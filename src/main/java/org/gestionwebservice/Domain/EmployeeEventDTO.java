package org.gestionwebservice.Domain;

public class EmployeeEventDTO {
    private String action;
    private String name;
    private String department;
    private String title;

    public EmployeeEventDTO(String action, String department, String name, String title) {
        this.action = action;
        this.department = department;
        this.name = name;
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
