package edu.umich.its.spe;

/*
 * Example of using the Spring Boot test environment, so properties are loaded.
 * If use the (commented out) static configuration class then will not use the Spring
 * environment and will NOT get properties found and loaded.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Instant;
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


import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;
import edu.umich.ctools.esb.utils.WAPI;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration

public class SPEEsbTest {

	// Apply a global timeout to all tests.  Comment out when debugging a test.
	//	@Rule
	//	public Timeout globalTimeout = Timeout.seconds(10);

	@MockBean
	PersistBlob persistblob;

	static {
		System.setProperty("skipRun", "TRUE");
	}

	@Before
	public void setUp() throws Exception {
		speesb = new SPEEsbImpl(spesummary);
	}

	@After
	public void tearDown() throws Exception {
	}

	private GradeIO speesb;

	// configure by hand (since there are nested values).
	private SPEProperties speproperties;

	@MockBean
	private PersistTimestamp persisttimestamp;

	@MockBean
	private SPESummary spesummary;

	private static Logger M_log = LoggerFactory.getLogger(SPEEsbTest.class);

	String aprilFirst = "2017-04-01 18:00:00";

	// First call fails so make it easy to run test twice.
	protected void getGradesCommonMethod() throws GradeIOException, PersistBlobException {

		when(persisttimestamp.readTestLastTakenTime()).thenReturn(Instant.now());
		when(persisttimestamp.ensureLastTestTakenTime()).thenReturn(Instant.now());

		// setup the mock properties
		SPEProperties speproperties = setupMockIOProperties();

		HashMap<String,String> getgrades = new HashMap<String,String>();
		getgrades.put("ASSIGNMENTTITLE","HOWDY");
		getgrades.put("COURSEID","187539");

		speproperties.setGetgrades(getgrades);

		// setup mock WAPI
		WAPI wapi = mock(WAPI.class);
		WAPIResultWrapper wrw = mock(WAPIResultWrapper.class);

		// deal with token renewal
		when(wapi.renewToken()).thenReturn(wrw);
		when(wrw.getStatus()).thenReturn(HttpStatus.SC_OK);

		// deal with request to wapi.
		when(wapi.doRequest(any(),any())).thenReturn(wrw);
		when(wrw.getStatus()).thenReturn(HttpStatus.SC_OK);

		SPEEsbImpl speesb = new SPEEsbImpl(wapi,spesummary);

		// now do something
		WAPIResultWrapper wrappedResult = speesb.getGradesVia(speproperties,aprilFirst);

		// check the results.
		M_log.debug("grades: {}",wrappedResult.toJson());
		assertNotNull("non-null result",wrappedResult);
		Boolean callOk = wrappedResult.getStatus() == HttpStatus.SC_OK || wrappedResult.getStatus() == HttpStatus.SC_NOT_FOUND;
		assertEquals("successful call",callOk, true);
	}

	public SPEProperties setupMockIOProperties() {
		SPEProperties speproperties = new SPEProperties();

		HashMap<String,String> tio = new HashMap<String,String>();
		tio.put("COURSEID", "Kangaroo");
		tio.put("esbGetScoreTemplate","%s/Scores/CourseId/%s/AssignmentTitle/%s");
		tio.put("esbPutScoreTemplate","%s/UniqName/%s/Score/%s");
		tio.put("key","OPENKEY");
		tio.put("secret","SECRETTHINGEE");
		tio.put("renewal","make me new again");
		tio.put("apiPrefix","https://apigw-tst.it.umich.edu/um/aa/Unizin");
		tio.put("tokenServer","https://apigw-tst.it.umich.edu/um/aa/oauth2/token");
		tio.put("x-ibm-client-id","ksldfjsldkj");
		tio.put("grant_type","hugh");
		tio.put("scope","spanishplacementscores");

		speproperties.setIo(tio);
		return speproperties;
	}

	@Test
	public void getGradesTest() throws IOException, GradeIOException, PersistBlobException {
		getGradesCommonMethod();
	}

	@Test
	public void getGradesTest2() throws IOException, GradeIOException, PersistBlobException {
		getGradesCommonMethod();
	}

	// put grades unit test not implemented
	@Ignore
	@Test
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

		SPEProperties speproperties = setupMockIOProperties();

		// mock the wapi calls.
		WAPI wapi = mock(WAPI.class);
		WAPIResultWrapper wrw = mock(WAPIResultWrapper.class);

		when(wapi.renewToken()).thenReturn(wrw);
		when(wrw.getStatus()).thenReturn(HttpStatus.SC_OK);

		SPEEsbImpl speesb = new SPEEsbImpl(wapi);

		// now do something
		Boolean verify_result = speesb.verifyConnection(speproperties);
		M_log.debug("update: {}",verify_result);
		assertTrue("successful verify",verify_result);
	}


	@Test
	public void testAssureWAPICreatesWAPI() {

		WAPI localWAPI = null;
		SPEProperties speproperties = setupMockIOProperties();

		SPEEsbImpl speesbimpl = new SPEEsbImpl(spesummary);

		localWAPI = speesbimpl.assureWAPI(localWAPI,speproperties.getIo());
		assertNotNull("create new wapi if current is null.",localWAPI);
	}


	@Test
	public void testAssureWAPIRecyclesWAPIByDefault() {

		WAPI localWAPI1 = null;
		SPEProperties speproperties = setupMockIOProperties();

		// Will test the impl.
		SPEEsbImpl speesbimpl = new SPEEsbImpl(spesummary);

		// make a wapi from nothing
		localWAPI1 = speesbimpl.assureWAPI(localWAPI1,speproperties.getIo());
		assertNotNull("create new wapi if current is null.",localWAPI1);

		// get the same wapi back if ask again.
		WAPI localWAPI2 = speesbimpl.assureWAPI(localWAPI1,speproperties.getIo());
		assertNotNull("return a wapi even if already set.",localWAPI2);

		assertEquals("assureWAPI returns same wapi multiple times.",localWAPI1,localWAPI2);
	}


	@Test
	public void testAssureWAPINeedNotRecycleWAPI() {

		WAPI localWAPI1 = null;
		SPEProperties speproperties = setupMockIOProperties();

		// need to test the implementation not the interface.

		SPEEsbImpl speesbimpl = new SPEEsbImpl(false);

		// make a wapi from nothing
		localWAPI1 = speesbimpl.assureWAPI(localWAPI1,speproperties.getIo());
		assertNotNull("create new wapi if current is null.",localWAPI1);

		// ask for another one
		WAPI localWAPI2 = speesbimpl.assureWAPI(localWAPI1,speproperties.getIo());
		assertNotNull("return a wapi even if already set.",localWAPI2);

		assertNotEquals("assureWAPI returns same wapi multiple times if requested.",localWAPI1,localWAPI2);
	}

}
