const apiBase = "http://localhost:8083/employees";
const employeeTable = document.getElementById("employeeTable");
const employeeForm = document.getElementById("employeeForm");
const resetButton = document.getElementById("resetButton");

let reconnectAttempts = 0;
const maxReconnectAttempts = 5;

// Helper function to retrieve headers with Authorization token
function getAuthHeaders() {
    const token = localStorage.getItem("jwt");
    if (!token) {
        alert("No token found. Please log in.");
        window.location.href = "/login.html";
        return {};
    }
    return {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };
}

function connectWebSocket() {
    const socket = new WebSocket("ws://localhost:8083/employee-events");

    socket.onopen = () => {
        console.log("WebSocket connected.");
        reconnectAttempts = 0; 
    };

    socket.onmessage = (event) => {
        const notification = JSON.parse(event.data);
        const { action } = notification;

        let message;
        switch (action) {
            case "CREATE":
                message = `Employee Created`;
                break;
            case "UPDATE":
                message = `Employee Updated`;
                break;
            case "DELETE":
                message = `Employee Deleted`;
                break;
            default:
                message = "Unknown action received.";
        }

        alert(message);
        console.log(`WebSocket Notification: ${message}`);
        fetchEmployees();
    };

    socket.onclose = () => {
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++;
            console.log(`WebSocket connection closed. Reconnecting... (Attempt ${reconnectAttempts})`);
            setTimeout(connectWebSocket, 5000);
        } else {
            console.error("Max WebSocket reconnection attempts reached.");
        }
    };

    socket.onerror = (error) => console.error("WebSocket error:", error);
}

connectWebSocket();

function fetchEmployees() {
    fetch(apiBase, {
        headers: getAuthHeaders()
    })
        .then((res) => {
            if (!res.ok) throw new Error("Failed to fetch employees");
            return res.json();
        })
        .then((data) => {
            employeeTable.innerHTML = "";
            data.forEach((employee) => {
                const row = document.createElement("tr");
                row.setAttribute("data-id", employee.id); // Add ID for future references
                row.innerHTML = `
                    <td>${employee.name}</td>
                    <td>${employee.department}</td>
                    <td>${employee.title}</td>
                    <td class="actions">
                        <button class="edit" onclick="editEmployee(${employee.id})">Edit</button>
                        <button class="delete" onclick="deleteEmployee(${employee.id})">Delete</button>
                    </td>
                `;
                employeeTable.appendChild(row);
            });
        })
        .catch((err) => {
            console.error("Error fetching employees:", err);
            alert("Failed to fetch employees. Please try again.");
        });
}

function createEmployee(employee) {
    fetch(apiBase, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(employee)
    })
        .then(() => fetchEmployees())
        .catch((err) => {
            console.error("Error creating employee:", err);
            alert("Failed to create employee. Please try again.");
        });
}

function updateEmployee(id, employee) {
    fetch(`${apiBase}/${id}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify(employee)
    })
        .then(() => fetchEmployees())
        .catch((err) => {
            console.error("Error updating employee:", err);
            alert("Failed to update employee. Please try again.");
        });
}

function deleteEmployee(id) {
    fetch(`${apiBase}/${id}`, {
        method: "DELETE",
        headers: getAuthHeaders()
    })
        .then(() => fetchEmployees())
        .catch((err) => {
            console.error("Error deleting employee:", err);
            alert("Failed to delete employee. Please try again.");
        });
}

function editEmployee(id) {
    fetch(`${apiBase}/${id}`, {
        headers: getAuthHeaders()
    })
        .then((res) => {
            if (!res.ok) throw new Error("Failed to fetch employee details");
            return res.json();
        })
        .then((data) => {
            document.getElementById("employeeId").value = data.id;
            document.getElementById("name").value = data.name;
            document.getElementById("department").value = data.department;
            document.getElementById("title").value = data.title;
        })
        .catch((err) => {
            console.error("Error fetching employee details:", err);
            alert("Failed to fetch employee details.");
        });
}

employeeForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const name = document.getElementById("name").value.trim();
    const department = document.getElementById("department").value.trim();
    const title = document.getElementById("title").value.trim();

    if (!name || !department || !title) {
        alert("All fields are required.");
        return;
    }

    const id = document.getElementById("employeeId").value;
    const employee = { name, department, title };
    if (id) {
        updateEmployee(id, employee);
    } else {
        createEmployee(employee);
    }
    employeeForm.reset();
});

resetButton.addEventListener("click", () => {
    employeeForm.reset();
});

// Fetch initial employees
fetchEmployees();
