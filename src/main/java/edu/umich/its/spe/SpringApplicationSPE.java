package edu.umich.its.spe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})

public class SpringApplicationSPE implements CommandLineRunner {

	@Autowired
	SPEMaster spe;
	
	@Autowired
	EsbProperties esb;
	
	@Override
	public void run(String... args) throws Exception {
		spe.orchestrator();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringApplicationSPE.class, args);
	}

}
