package edu.umich.its.spe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import edu.umich.its.spe.Config;

@ComponentScan
@RestController
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(Config.class)


// TODO: implement multiple input files.
/*

@PropertySources({
@PropertySource(value = "classpath:missing.properties", ignoreResourceNotFound=true),
@PropertySource("classpath:config.properties")
        })
*/

// public

// private final FooProperties properties;

// @Autowired
// public MyService(FooProperties properties) {
// this.properties = properties;
// }

public class Example {

	static final Logger log = LoggerFactory.getLogger(Example.class);

	private Config config;

	@Autowired
	public void MyConfig(Config config) {
		log.debug("in MyConfig");
		this.config = config;
	}

	// TODO: get the generic ones works (e.g. endpoints.enabled)

	@RequestMapping("/")
	String home() {
		log.debug("home()");
		return "Hello World!" + " config.type: " + config.getType();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Example.class, args);
	}

	public static String iDontDoMuchButSayHello() {
		return "HELLO";
	}

}
