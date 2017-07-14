package edu.umich.its.spe;

/*
 * Example of using the Spring Boot test environment, so properties are loaded.
 * If use the (commented out) static configuration class then will not use the Spring
 * environment and will NOT get properties found and loaded.
 */

import static org.junit.Assert.*;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.io.IOException;


import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan

public class SPEEsbTest {

	// Apply a global timeout to all tests.  Comment out when debugging a test.
	//@Rule
	//public Timeout globalTimeout = Timeout.seconds(10);


	@Before
	public void setUp() throws Exception {
		speesb = new SPEEsbImpl(spesummary);
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Autowired
	SPEEsb speesb;


	@Autowired
	private SPEProperties speproperties;

	@Autowired
	private SPESummary spesummary;

	private static Logger M_log = LoggerFactory.getLogger(SPEEsbTest.class);

	@Test
	public void getGradesTest() throws IOException, SPEEsbException {

		List<String> keys = speesb.setupGetGradePropertyValues();
		HashMap<String,String> value = WAPI.getPropertiesWithKeys(TestingUtils.readTestProperties(speproperties), keys);

		WAPIResultWrapper wrappedResult = speesb.getGradesViaESB(value);
		M_log.debug("grades: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		Boolean callOk = wrappedResult.getStatus() == HttpStatus.SC_OK || wrappedResult.getStatus() == HttpStatus.SC_NOT_FOUND;
		assertEquals("successful call",callOk, true);
	}

	@Test
	// fails SPEEsbTest.java
	public void putGradesTest() throws IOException {

		List<String> keys = speesb.setupPutGradePropertyValues();
		HashMap<String,String> value = WAPI.getPropertiesWithKeys(TestingUtils.readTestProperties(speproperties), keys);

		WAPIResultWrapper wrappedResult = speesb.putGradeViaESB(value);
		M_log.debug("update: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		assertEquals("successful call",HttpStatus.SC_OK,wrappedResult.getStatus());
	}

	@Test
	public void checkVerifyTest() throws IOException {

		List<String> keys = speesb.setupPutGradePropertyValues();
		HashMap<String,String> value = WAPI.getPropertiesWithKeys(TestingUtils.readTestProperties(speproperties), keys);

		//WAPIResultWrapper wrappedResult = speesb.verify(value);
		Boolean verify_result = speesb.verifyESBConnection(value);
		M_log.debug("update: {}",verify_result);
		assertTrue("successful verify",verify_result);
	}

}
