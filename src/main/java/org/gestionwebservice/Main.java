package org.gestionwebservice;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;


@QuarkusMain  
public class Main {

    public static void main(String ... args) {
        System.out.println("Running main method");
        Quarkus.run(args); 
    }

    public static class MyApp implements QuarkusApplication {
        @Override
        public int run(String... args) throws Exception {
            System.out.println("Hello do the logic here...");
            Quarkus.waitForExit();
            return 0;
        }
    }
}