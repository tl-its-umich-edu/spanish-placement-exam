package edu.umich.its.spe;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import java.io.IOException;

import java.io.InputStream;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPEEsbPropertiesTest {
	
	// Apply a global timeout to all tests.  Comment out when debugging a test.
    //@Rule
    //public Timeout globalTimeout = Timeout.seconds(10);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private static Logger M_log = LoggerFactory.getLogger(SPEEsbPropertiesTest.class);

	public Properties readTestProperties() throws IOException{
		String properties_file = "test.properties";
		Properties props = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(properties_file);
			props.load(in);
			in.close();
		}
		catch (Exception e) {
			M_log.error("getProps: properties file not opened : "+properties_file+ " "+e);
		}
		return props;
	}
	
	@Test
	public void checkPropertiesFile() throws IOException {
		Properties props = readTestProperties();
		assertNotNull(props);
	}

}
