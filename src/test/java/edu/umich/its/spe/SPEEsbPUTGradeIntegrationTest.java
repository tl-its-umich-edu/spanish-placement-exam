package edu.umich.its.spe;

// INTEGRATION TEST FOR TESTING PUTTING GRADES.
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

public class SPEEsbPUTGradeIntegrationTest {

	@Before
	public void setUp() throws Exception {
		speesb = new SPEEsbImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	SPEEsb speesb;

	@Autowired
	SPEProperties speproperties;

	private static Logger M_log = LoggerFactory.getLogger(SPEEsbPUTGradeIntegrationTest.class);

	@Test
	public void checkPropertiesFile() throws IOException {
		Properties props = TestingUtils.readTestProperties(speproperties);
		assertNotNull(props);
	}


	@Test
	// fails when run from SPEEsbTest.java
	public void putGradesTestSPEEsbTest() throws IOException {

		List<String> keys = speesb.setupPutGradePropertyValues();
		HashMap<String,String> value = WAPI.getPropertiesWithKeys(TestingUtils.readTestProperties(speproperties), keys);

		WAPIResultWrapper wrappedResult = speesb.putGradeViaESB(value);
		M_log.debug("update: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		assertEquals("successful call",HttpStatus.SC_OK,wrappedResult.getStatus());
	}

	@Test
	public void putGradesTest() throws IOException {

		List<String> keys = speesb.setupPutGradePropertyValues();
		HashMap<String,String> value = WAPI.getPropertiesWithKeys(TestingUtils.readTestProperties(speproperties), keys);

		WAPIResultWrapper wrappedResult = speesb.putGradeViaESB(value);
		M_log.debug("update: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		assertEquals("successful call",HttpStatus.SC_OK,wrappedResult.getStatus());
	}

}
