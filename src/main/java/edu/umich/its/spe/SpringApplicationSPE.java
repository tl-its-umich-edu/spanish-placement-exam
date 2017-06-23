package edu.umich.its.spe;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Wrapper to allow driving the processing as a Spring Boot application.
 */

@SpringBootApplication
@EnableAutoConfiguration

// Specifically implement CommandLineRunner so can start up the application
// without web container and run exactly what we need to run on startup.

public class SpringApplicationSPE implements CommandLineRunner {

	@Autowired
	SPEMaster spe;

	// The 'run' method will be called to start application code.
	@Override
	public void run(String... args) throws Exception {
		spe.orchestrator();
	}

	// This is static entry point required for Spring Boot.
	public static void main(String[] args) {
		SpringApplication.run(SpringApplicationSPE.class, args);
	}

}
