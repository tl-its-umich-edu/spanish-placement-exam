package edu.umich.its.spe;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingUtils {

	private static Logger M_log = LoggerFactory.getLogger(TestingUtils.class);

	// Merge all the different properties together for testing.
	static public Properties readTestProperties(SPEProperties speproperties) {
		Properties props = new Properties();
		props.putAll(speproperties.getPersist());
		props.putAll(speproperties.getEsb());
		props.putAll(speproperties.getGetgrades());
		props.putAll(speproperties.getPutgrades());
		props.putAll(speproperties.getTest());
		M_log.debug("testing properties: {}",props);
		return props;
	}

}
