package edu.umich.its.spe;

/*
 * Example of using the Spring Boot test environment, so properties are loaded.
 * If use the (commented out) static configuration class then will not use the Spring
 * environment and will NOT get properties found and loaded.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan

public class SPEEsbTest {

	// Apply a global timeout to all tests.  Comment out when debugging a test.
	@Rule
	public Timeout globalTimeout = Timeout.seconds(10);

	@Before
	public void setUp() throws Exception {
		speesb = new SPEEsbImpl(spesummary);
	}

	@After
	public void tearDown() throws Exception {
	}

	private GradeIO speesb;

	@Autowired
	private SPEProperties speproperties;

	@Autowired
	private SPESummary spesummary;

	private static Logger M_log = LoggerFactory.getLogger(SPEEsbTest.class);

	String aprilFirst = "2017-04-01 18:00:00";

	// First call fails so make it easy to run test twice.
	protected void getGradesCommonMethod() throws GradeIOException {
		WAPIResultWrapper wrappedResult = speesb.getGradesVia(speproperties,aprilFirst);
		M_log.debug("grades: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		Boolean callOk = wrappedResult.getStatus() == HttpStatus.SC_OK || wrappedResult.getStatus() == HttpStatus.SC_NOT_FOUND;
		assertEquals("successful call",callOk, true);
	}

	@Test
	public void getGradesTest() throws IOException, GradeIOException {
		getGradesCommonMethod();
	}

	@Test
	public void getGradesTest2() throws IOException, GradeIOException {
		getGradesCommonMethod();
	}

	@Ignore
	@Test
	// fails SPEEsbTest.java
	public void putGradesTest() throws IOException {

		HashMap<String, String> user = new HashMap<String,String>();
		user.put("Unique_name","ABC");
		user.put("Score","1.1");

		WAPIResultWrapper wrappedResult = speesb.putGradeVia(speproperties,user);

		M_log.debug("update: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		assertEquals("successful call",HttpStatus.SC_OK,wrappedResult.getStatus());
	}

	@Test
	public void checkVerifyTest() throws IOException {

		Boolean verify_result = speesb.verifyConnection(speproperties);
		M_log.debug("update: {}",verify_result);
		assertTrue("successful verify",verify_result);
	}

}
