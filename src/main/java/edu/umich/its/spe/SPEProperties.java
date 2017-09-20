package edu.umich.its.spe;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

/*
 * Spring will inject values from properties file into property / value hash maps.
 * Entries are put in same hash if they have the same starting prefix.  We only need to know the
 * value of the first name element explicitly.
 * This only applies to one level of naming.  This isn't configured to provide nested property maps.
 */

// Spring: These annotations make the properties class visible to Spring for filling and injection.
@Component
@Configuration
@ConfigurationProperties

/* May want property sources so can use multiple property files */
/* Nice to have file name with the properties.  Not so nice if need to change them in several files. */
@PropertySource("file:config/application.properties")

@Data
public class SPEProperties {
	private HashMap<String,String> io;
	private HashMap<String,String> persist;
	private HashMap<String,String> getgrades;
	private HashMap<String,String> putgrades;
	private HashMap<String,String> unirest;
	private HashMap<String,String> test;
	private HashMap<String,String> repeat;
	private HashMap<String,String> email;
}
