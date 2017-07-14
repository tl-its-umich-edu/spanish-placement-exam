package edu.umich.its.spe;

/*
 * Test master methods.  For properties have the properties injected and then reset them for the tests.
 * Better to mock them if figure out how to deal with the startup issue with SPE injection.
 */

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
//import static org.mockito.Matchers.matches;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan

public class SPEMasterTest {

	private static Logger M_log = LoggerFactory.getLogger(SPEMasterTest.class);

	@Autowired
	SPEMaster spe = null;

	@Autowired
	SPEProperties speproperties = null;

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
		assertThat("valid year",SPEMaster.formatPersistTimestamp(ts),containsString("2017-"));
		assertThat("valid year",SPEMaster.formatPersistTimestamp(ts),not(containsString("XXX")));
	}


	////////////////// setUp will set both gradedaftertime and gradedaftertimeoverride to empty string.

	// check that override is used of there is no value for gradedaftertime
	@Test
	public void testEnsureLastGradeTransferTimeOverride() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertimeoverride","DKJ");
		String defaultTime = spe.ensureLastGradeTransferTime(null);
		M_log.error("defaultTime: [{}]",defaultTime);
		assertNotNull("default time",defaultTime);
		assertThat("defaultTime",defaultTime,is("DKJ"));

	}

	// verify that use gradedaftertime from gradedaftertime property if it is set and override property is empty.
	@Test
	public void testEnsureLastGradeTransferTimeDefault() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertime","GAT");
		String defaultTime = spe.ensureLastGradeTransferTime(null);
		M_log.error("defaultTime: [{}]",defaultTime);
		assertNotNull("default time",defaultTime);
		assertThat("defaultTime",defaultTime,is("GAT"));

	}

	// verify that use gradedaftertime from override if properties for override is set and gradedaftertime is empty.
	@Test
	public void testZZZEnsureLastGradeTransferTimegradedaftertimeOverrideOnly() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertimeoverride","OVERRIDE");
		String defaultTime = spe.ensureLastGradeTransferTime(null);
		M_log.error("defaultTime: [{}]",defaultTime);
		assertNotNull("default time",defaultTime);
		assertThat("defaultTime",defaultTime,is("OVERRIDE"));
	}


	// verify that set gradedaftertime from override if properties for both override and gradedaftertime are set.
	@Test
	public void testEnsureLastGradeTransferTimegradedaftertimeOverride() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertime","GAT");
		speproperties.getGetgrades().put("gradedaftertimeoverride","OVERRIDE");
		String defaultTime = spe.ensureLastGradeTransferTime(null);
		M_log.error("defaultTime: [{}]",defaultTime);
		assertNotNull("default time",defaultTime);
		assertThat("defaultTime",defaultTime,is("OVERRIDE"));
	}

	// verify get exception if no gradedaftertime property is set.
	@Test(expected = GradeIOException.class)
	public void testEnsureLastGradeTransferTimeNoPropertySet() throws PersistBlobException, GradeIOException {
		String defaultTime = spe.ensureLastGradeTransferTime(null);
		M_log.error("defaultTime: [{}]",defaultTime);
		assertNotNull("default time",defaultTime);
		assertThat("defaultTime",defaultTime,is("OVERRIDE"));
	}

}

