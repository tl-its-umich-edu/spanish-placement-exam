package edu.umich.its.spe;

/*
 * Test of static methods in SPEEsbImpl.
 */

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.mock.mockito.MockBean;

public class SPEEsbImplURLFormatTest {

	// Apply a global timeout to all tests.  Comment out when debugging a test.
	@Rule
	public Timeout globalTimeout = Timeout.seconds(10);

	SPEEsbImpl speesb;

	@After
	public void tearDown() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		speesb = new SPEEsbImpl(spesummary);
	}

	// Allows calling constructor and avoiding autowiring.
	@MockBean
	private SPESummary spesummary;

	@SuppressWarnings("unused")
	private static Logger M_log = LoggerFactory.getLogger(SPEEsbImplURLFormatTest.class);

	@Test
	public void formatPutURLWordTest() {
		HashMap<String, String> values = new HashMap<String, String>();

		values.put("esbPutScoreTemplate","%s/UniqName/%s/Score/%s");
		values.put("apiPrefix","FIRST");
		values.put("UNIQNAME","SUGAROIL");
		values.put("SCORE","TOUCHDOWN");

		String url = speesb.formatPutURLTemplate(values);

		assertEquals("puturl format","FIRST/UniqName/SUGAROIL/Score/TOUCHDOWN",url);
	}

	@Test
	public void formatPutURLHTTPTest() {
		HashMap<String, String> values = new HashMap<String, String>();
		//final static String esbPutScoreTemplate= "%s/UniqName/%s/Score/%s";

		values.put("esbPutScoreTemplate","%s/UniqName/%s/Score/%s");
		values.put("apiPrefix","https:666//ME.YOU");
		values.put("UNIQNAME","SUGAROIL");
		values.put("SCORE","TOUCHDOWN");

		String url = speesb.formatPutURLTemplate(values);

		assertEquals("puturl format","https:666//ME.YOU/UniqName/SUGAROIL/Score/TOUCHDOWN",url);
	}

	@Test
	public void formatGetURLWordTest() throws GradeIOException {
		HashMap<String, String> values = new HashMap<String, String>();

		values.put("esbGetScoreTemplate","%s/data/CourseId/%s/AssignmentTitle/%s");
		values.put("apiPrefix","FIRST");
		values.put("COURSEID","PracticalMagic");
		values.put("ASSIGNMENTTITLE","GO AWAY");

		String url = speesb.formatGetURLTemplate(values);

		assertEquals("geturl format","FIRST/data/CourseId/PracticalMagic/AssignmentTitle/GO%20AWAY",url);
	}

	@Test
	public void setupPutGradeCallUserInfoTest() throws GradeIOException {
		HashMap<String, String> user = new HashMap<String, String>();

		SPEProperties speprop = new SPEProperties();
		HashMap<String, String> io = new HashMap<String,String>();
		speprop.setIo(io);

		// store information under standard names and retrieve under expected name for the put grade.
		// This fails if the right names are used where expected.

		user.put(SPEMaster.SCORE,"INFINITE");
		user.put(SPEMaster.UNIQUE_NAME,"HowdyDuty");

		HashMap<String, String> putInfo = speesb.setupPutGradeCall(speprop,user);

		assertEquals("uniqname","HowdyDuty",putInfo.get("UNIQNAME"));
		assertEquals("score","INFINITE",putInfo.get("SCORE"));
	}




}
