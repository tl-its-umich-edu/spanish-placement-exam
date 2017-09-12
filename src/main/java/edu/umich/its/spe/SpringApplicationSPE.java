package edu.umich.its.spe;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@Autowired
	SPEProperties speproperties;

	static final Logger M_log = LoggerFactory.getLogger(SPEMaster.class);

	// The 'run' method will be called to start application code.
	@Override
	public void run(String... args) throws Exception {

		// find out if should actually run the script here
		HashMap<String,String> testproperties = speproperties.getTest();
		M_log.debug("skipRun: {}",testproperties.get("skipRun"));

		String skipRun = testproperties.get("skipRun");

		if (skipRun == null) {
			skipRun = "";
		}

		// to run or not to run, that's a great question.
		if (! "TRUE".equals(skipRun.toUpperCase())) {
			//spe.worker();
			spe.orchestrator();
		}
	}

	// This is static entry point required for Spring Boot.
	public static void main(String[] args) {
		SpringApplication.run(SpringApplicationSPE.class, args);
	}

}
