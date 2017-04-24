package edu.umich.its.spe;

/*
 * Class to hold properties available from the default application.yml file.
 * Spring will automatically go find the yml file. By default it can find
 * it in src/main/java/config directory.  Or many other places, see documentation.
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Configuration
@ConfigurationProperties

@Data
public class Config {
	private String type;

}
