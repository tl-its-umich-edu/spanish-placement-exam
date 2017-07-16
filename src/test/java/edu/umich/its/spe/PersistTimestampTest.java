package edu.umich.its.spe;

/*
 * Test master methods.  For properties have the properties injected and then reset them for the tests.
 * Better to mock them if figure out how to deal with the startup issue with SPE injection.
 */

import static org.assertj.core.api.Assertions.*;

import static org.mockito.BDDMockito.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan

public class PersistTimestampTest {

	private static Logger M_log = LoggerFactory.getLogger(PersistTimestampTest.class);

	@Autowired
	SPEMaster spe = null;

	@Autowired
	SPEProperties speproperties = null;

	// PRACTICAL MAGIC: You can mock beans injected into the bean under test.
	// PersistTimestamp has a PersistBlob bean injected.  @MockBean
	// makes that into a mock and behavior can be set here.
	@MockBean
	private PersistBlob persistblob;

	@Autowired
	PersistTimestamp persisttimestamp;

	final String early2017 = "2017-01-01 01:01:01";

	@Before
	public void setUp() throws Exception {
		// reset the properties before each run
		speproperties.getGetgrades().put("gradedaftertime","");
		speproperties.getGetgrades().put("gradedaftertimeoverride","");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTimeStampFormat() {
		LocalDateTime ts = LocalDateTime.now();
		assertThat("valid year",PersistTimestamp.formatTimestamp(ts),containsString("2017-"));
		assertThat("valid year",PersistTimestamp.formatTimestamp(ts),not(containsString("XXX")));
	}


	////////////////// setUp will set both gradedaftertime and gradedaftertimeoverride to empty string.

	// check that override is used if set
	@Test
	public void testEnsureLastGradeTransferTimeOverride() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertime","GAT");
		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		M_log.error("defaultTime: [{}]",useTime);
		assertNotNull("default time",useTime);
	}

	// check that default is used if there is no value for gradedaftertime
	@Test
	public void testEnsureLastGradeTransferTimeDefault() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertimedefault","DEFAULT");
		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		M_log.error("useTime: [{}]",useTime);
		assertNotNull("useTime ",useTime);
	}

	// verify that use gradedaftertime if both set
	@Test
	public void testEnsureLastGradeTransferTimeBoth() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertime","GAT");
		speproperties.getGetgrades().put("gradedaftertimedefault","DEFAULT");
		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		M_log.error("defaultTime: [{}]",useTime);
		assertNotNull("default time",useTime);
		assertThat("defaultTime",useTime,is("GAT"));

	}

	@Test
	public void testEnsureLastGradeTransferTimeNoPropertiesUseBlob() throws PersistBlobException, GradeIOException {
		given(this.persistblob.readBlob()).willReturn(early2017);
		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		assertEquals("got persist blob value",early2017,useTime);
	}

	@Test
	public void testEnsureLastGradeTransferTimeOnlyDefaultPropertiesUseBlob() throws PersistBlobException, GradeIOException {
		given(this.persistblob.readBlob()).willReturn(early2017);
		speproperties.getGetgrades().put("gradedaftertimedefault","DEFAULT");
		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		assertEquals("got persist blob value",early2017,useTime);
	}

	@Test
	public void testEnsureLastGradeTransferTimePropertyAndBlob() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertime","GAT");
		given(this.persistblob.readBlob()).willReturn(early2017);

		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		assertThat("useTime",useTime,is("GAT"));
	}

	@Test
	public void testEnsureLastGradeTransferTimeLastMonth() throws PersistBlobException, GradeIOException {
		// no properties and nothing stored so make a time up.
		speproperties.getGetgrades().put("gradedaftertimedefault","");
		M_log.error("speproperties.getGrades: {}",speproperties.getGetgrades());
		String useTime = persisttimestamp.ensureLastGradeTransferTime();
		// not a great test, but the length of a time stamp is 19 so this is
		// necessary if not sufficient.  Figure out how to deal with a variable
		// time check when/if it is necessary.
		assertThat("useTime",useTime.length(),is(19));
	}

}

