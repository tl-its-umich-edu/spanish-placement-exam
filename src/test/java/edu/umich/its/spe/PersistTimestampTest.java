package edu.umich.its.spe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Instant;

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

	final String early2017 = "2017-01-01T01:01:01";
	final String early2017Z = "2017-01-01T01:01:01Z";
	final String early2016Z = "2016-01-01T01:01:01Z";
	final String early2015Z = "2015-01-01T01:01:01Z";

	final String early2010StringZ      = "2010-01-01T12:00:00Z";
	final String early2010StringZPlus1 = "2010-01-01T12:00:01Z";
	final Instant early2010Instant = Instant.ofEpochSecond(1262347200);

	@Before
	public void setUp() throws Exception {
		// reset the properties before each run
		speproperties.getGetgrades().put("gradedaftertime","");
		speproperties.getGetgrades().put("gradedaftertimeoverride","");
	}

	@After
	public void tearDown() throws Exception {
	}

	////////////////// setUp will set both gradedaftertime and gradedaftertimeoverride to empty string.

	// check that override is used if set
	@Test
	public void testEnsureLastTestTakenTimeOverride() throws PersistBlobException, GradeIOException {
		speproperties.getGetgrades().put("gradedaftertime",early2016Z);

		Instant useTime = persisttimestamp.ensureLastTestTakenTime();
		M_log.info("defaultTime: [{}]",useTime);
		assertNotNull("default time",useTime);
		assertEquals("force stop","2016-01-01T01:01:01Z",useTime.toString());
	}

}
