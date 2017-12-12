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

public class SPEUtilsTest {

	private static Logger M_log = LoggerFactory.getLogger(SPEUtilsTest.class);

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

	////////// test normalizeStringTimestamp
	@Test
	public void testNormalizeStringTimestampNull() {
		String nts = SPEUtils.normalizeStringTimestamp(null);
		assertNull("null string time stamp is null",nts);
	}

	@Test
	public void testNormalizeStringTimestampEmpty() {
		String nts = SPEUtils.normalizeStringTimestamp("");
		assertNull("null string time stamp is null",nts);
	}

	// from unix timestamp, 2010-01-01 12:00:00
	// Instant fromUnixTimestamp = Instant.ofEpochSecond(1262347200);

	@Test
	public void testInstantToUTC() {
		// formatTimestampToUTC
		String zdt = SPEUtils.formatTimestampInstantToImplicitUTC(early2010Instant);
		assertNotNull("normalized timestamp",zdt);
		assertEquals("normalized timestamp with zone offset","2010-01-01T12:00:00",zdt.toString());
	}


	@Test
	public void testTimeStampStringToUTC() {
		Instant i = SPEUtils.generateNewQueryTime(early2010Instant);
		String zdt = SPEUtils.formatTimestampInstantToImplicitUTC(i);
		assertNotNull("normalized timestamp",zdt);
		assertEquals("normalized timestamp with zone offset","2010-01-01T12:00:01",zdt.toString());
	}

	@Test
	public void testNewQueryTime() {
		Instant i = SPEUtils.generateNewQueryTime(early2010Instant);
		assertEquals("proper incremented time",early2010StringZPlus1,i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantZ() {
		Instant i = SPEUtils.convertTimeStampStringToInstant(early2010StringZ);
		assertEquals("early2010 with Z",early2010StringZ,i.toString());
	}

	@Test
	public void testConvertTimeStampWithMilliWithoutZToInstantZ() {
		Instant i = SPEUtils.convertTimeStampStringToInstant("2017-09-08T20:35:38.918");
		assertEquals("deleted fractional seconds add Z","2017-09-08T20:35:38"+"Z",i.toString());
	}

	@Test
	public void testConvertTimeStampWithMilliWithZToInstantZ() {
		Instant i = SPEUtils.convertTimeStampStringToInstant("2017-09-08T20:35:38.918Z");
		assertEquals("deleted fractional seconds add Z","2017-09-08T20:35:38"+"Z",i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantNoTNoZ() {
		String input = "2017-09-08 16:35:38";
		String correct = input.replaceFirst(" ","T")+"Z";
		Instant i = SPEUtils.convertTimeStampStringToInstant(input);
		assertEquals("parse no T",correct,i.toString());
	}

	@Test
	public void testConvertTimeStampWithOffsetToInstant() {
		// Note this takes into account the time change to UTC.
		String withOffset = "2017-09-08T15:47:11-04:00";
		String withoutOffset = "2017-09-08T19:47:11Z";
		Instant i = SPEUtils.convertTimeStampStringToInstant(withOffset);
		assertEquals("parse with offset",
				withoutOffset,
				i.toString());
	}

	@Test
	public void testConvertTimeStampWithOffsetNoColonToInstant() {
		// Note this takes into account the time change to UTC.
		String withOffset = "2017-09-08T15:47:11-0400";
		String withoutOffset = "2017-09-08T19:47:11Z";
		Instant i = SPEUtils.convertTimeStampStringToInstant(withOffset);
		assertEquals("parse with offset",
				withoutOffset,
				i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantNoTWithZ() {
		String noT = "2017-09-08 16:35:38Z";
		Instant i = SPEUtils.convertTimeStampStringToInstant(noT);
		assertEquals("parse no T","2017-09-08T16:35:38Z",i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantWithTWithZ() {
		String noT = "2017-09-08T16:35:38Z";
		Instant i = SPEUtils.convertTimeStampStringToInstant(noT);
		assertEquals("parse no T",noT,i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantWithTWorks() {
		//String hasT = "2017-01-01T01:01:01";
		 String hasT =  "2010-01-01T12:00:00Z";
		//early2010StringZ
		Instant i = SPEUtils.convertTimeStampStringToInstant(hasT);
		assertEquals("parse with T",hasT,i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantWithT() {
		String hasT = "2017-09-08T16:35:38";
		Instant i = SPEUtils.convertTimeStampStringToInstant(hasT);
		assertEquals("parse with T",hasT+"Z",i.toString());
	}

	@Test
	public void testConvertTimeStampToInstantNoZ() {
		Instant i = SPEUtils.convertTimeStampStringToInstant(early2017);
		assertEquals("early2017 without Z",early2017+"Z",i.toString());
	}

	@Test
	public void testNormalizeTSWithoutOffset() {
		String zdt = SPEUtils.normalizeStringTimestamp("2017-04-01T11:12:13");
		assertNotNull("normalized timestamp",zdt);
		assertEquals("normalized timestamp with zone offset","2017-04-01T11:12:13Z",zdt.toString());
	}

	@Test
	public void testNormalizeTSWithoutOffWithoutT() {
		String zdt = SPEUtils.normalizeStringTimestamp("2017-04-01 11:12:13");
		assertNotNull("normalized timestamp",zdt);
		assertEquals("normalized timestamp with zone offset","2017-04-01T11:12:13Z",zdt.toString());
	}

	@Test
	public void testNormalizeTSWithOffset() {
		String zdt = SPEUtils.normalizeStringTimestamp("2017-04-01T11:12:13+00:00");
		assertNotNull("normalized timestamp",zdt);
		assertEquals("normalized timestamp with zone offset","2017-04-01T11:12:13+00:00",zdt.toString());
	}

}
