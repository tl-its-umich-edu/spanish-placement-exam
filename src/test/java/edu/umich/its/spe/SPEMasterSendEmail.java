package edu.umich.its.spe;

/* Check that the formatting of the email summary string is as expected. */

/* Use mockito explicitly since automatic configuration of SPEMaster and use of
 * mocks didn't play well together.
 */

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

public class SPEMasterSendEmail {

	static final Logger log = LoggerFactory.getLogger(SPEMasterSendEmail.class);

	@After
	public void tearDown() throws Exception {
	}

	@MockBean
	private SPESummary spesummary;

	@MockBean
	private PersistTimestamp persisttimestamp;

	@MockBean
	private SPEProperties speproperties;

	@MockBean
	private SPEConfiguration speconfiguration;

	@MockBean
	private GradeIO gradeio;

	@MockBean
	private SimpleJavaEmail sjm;

	private SPEMaster spe;

	@Test
	public void testSummaryReportZeroZero() throws PersistBlobException, GradeIOException {

		setupMockitoExpectations();

		given(this.spesummary.getAdded()).willReturn(0);
		given(this.spesummary.getErrors()).willReturn(0);

		callSendSummaryEmail();

		verify(sjm).sendSimpleMessage(eq(null),eq(null),contains("(0/0) Test Subject"),anyString());
	}

	@Test
	public void testSummaryReportTwoZero() throws PersistBlobException, GradeIOException {

		setupMockitoExpectations();

		given(this.spesummary.getAdded()).willReturn(2);
		given(this.spesummary.getErrors()).willReturn(0);

		callSendSummaryEmail();

		verify(sjm).sendSimpleMessage(eq(null),eq(null),contains("(2/0) Test Subject"),anyString());
	}

	@Test
	public void testSummaryReportZeroTwo() throws PersistBlobException, GradeIOException {

		// setup mockito expectations
		setupMockitoExpectations();

		given(this.spesummary.getAdded()).willReturn(0);
		given(this.spesummary.getErrors()).willReturn(2);

		callSendSummaryEmail();

		verify(sjm).sendSimpleMessage(eq(null),eq(null),contains("(0/2) Test Subject"),anyString());
	}

	public void callSendSummaryEmail() {
		// use explicit injection constructor so can mock.
		spe = new SPEMaster(gradeio, persisttimestamp,speproperties,sjm,spesummary);
		// usually called automatically by Spring, but not with the explicit construction.
		spe.globalSettingsFromProperties();
		spe.sendSummaryEmail();
	}


	public void setupMockitoExpectations() throws PersistBlobException, GradeIOException {
		given(this.persisttimestamp.ensureLastTestTakenTime()).willReturn(Instant.now());

		@SuppressWarnings("serial")
		HashMap<String,String> myMap = new HashMap<String,String>(){
			{
				put("alwaysMailReport", "TRUE");
				put("subject","Test Subject");
			}};

			when(this.speproperties.getEmail()).thenReturn(myMap);
	}

}
