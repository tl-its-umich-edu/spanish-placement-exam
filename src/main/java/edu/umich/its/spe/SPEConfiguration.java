package edu.umich.its.spe;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.umich.its.spe.PersistBlob;

/*
 * Spring default processing won't work with constructor aruments so
 * implement explicit bean factory method for persistString.
 */

// Spring: Mark as configuration so that will be used during automatic context construction.
@Configuration
public class SPEConfiguration {

	static final Logger M_log = LoggerFactory.getLogger(SPEConfiguration.class);

	@Autowired
	private SPEProperties speproperties;

	// Explicitly expose a bean with constructor method.
	@Bean(name = "persistString")
	public PersistBlob persistString() throws PersistBlobException {

		HashMap<String,String> persistProperties = speproperties.getPersist();
		String path = persistProperties.get("persistPath");
		M_log.debug("persist path: {}",path);
		return new PersistBlobImpl(path);
	}
}
