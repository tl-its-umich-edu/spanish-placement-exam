package edu.umich.its.spe;

// INTEGRATION TEST FOR TESTING GETTING GRADES.

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
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

import edu.umich.its.spe.TestingUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan

public class SPEEsbGETGradesIntegrationTest {

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

	GradeIO speesb;

	@Autowired
	SPESummary spesummary;

	@Autowired
	SPEProperties speproperties;

	private static Logger M_log = LoggerFactory.getLogger(SPEEsbGETGradesIntegrationTest.class);

	String aprilFirst = "2017-04-01 18:00:00";

	@Test
	public void checkPropertiesFile() throws IOException {
		Properties props = TestingUtils.readTestProperties(speproperties);
		assertNotNull(props);
	}

	@Test
	public void getGradesTest() throws IOException, GradeIOException {

		WAPIResultWrapper wrappedResult = speesb.getGradesVia(speproperties,aprilFirst);
		M_log.debug("grades: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		Boolean callOk = wrappedResult.getStatus() == HttpStatus.SC_OK || wrappedResult.getStatus() == HttpStatus.SC_NOT_FOUND;
		assertEquals("successful call",callOk, true);
	}

}
