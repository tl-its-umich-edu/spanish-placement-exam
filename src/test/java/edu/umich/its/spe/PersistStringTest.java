package edu.umich.its.spe;

import static org.junit.Assert.*;

import java.time.Instant;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.umich.its.spe.PersistString;
import edu.umich.its.spe.PersistStringException;

public class PersistStringTest {
	//private static Log log = LogFactory.getLog(PersistStringTest.class);
	static final Logger log = LoggerFactory.getLogger(PersistStringTest.class);
	
	/*******************************/
	/* setup */
	// Automatically create / clean up temporary folder for each test.
	@Rule public TemporaryFolder tempFolder = new TemporaryFolder();

	PersistString ps = null;

	@Before
	public void setUp() throws Exception {
		ps = new PersistString(tempFolder.getRoot().toString());
	}

	@After
	public void tearDown() throws Exception {
	}

	String timeStamp() {
		return Instant.now().toString();
	}
	
	/********************************/
	/* constructor tests */

	public void testAbsolutePath() throws PersistStringException {
		log.error("temp folder: A "+tempFolder.getRoot());
		new PersistString("/here/is/a/reasonable/path");
	}

	@Test(expected=PersistStringException.class)
	public void testRelativePath() throws PersistStringException {
		log.error("temp folder: B "+tempFolder.getRoot());
		new PersistString("here/is/an/unreasonable/path");
	}
	
	/**********************************/
	/* IO tests */
	
	/******** fail (with exception) */
	
	@Test(expected=PersistStringException.class)
	public void testWriteStringBadPath() throws PersistStringException {
		ps = new PersistString("/tmp/test/here");
		ps.writeString("test string: "+timeStamp());
	}
	
	@Test(expected=PersistStringException.class)
	public void testWriteNullStringFAIL() throws PersistStringException {
		ps.writeString(null);
	}
	
	@Test(expected=PersistStringException.class)
	public void testReadMissingStringFAIL() throws PersistStringException {
			String s = ps.readString();
			assertNull("no string written",s);
	}
	
	
	/********* pass */
	
	@Test
	public void testWriteStringGoodPath() throws PersistStringException {
		ps.writeString("test string: "+timeStamp());
	}
	
	@Test
	public void testWriteEmptyStringOK() throws PersistStringException {
		ps.writeString("");
	}

	@Test
	public void testWriteReadString() throws PersistStringException {
		String testString = "test string: "+timeStamp();
		ps.writeString(testString);
		String s = ps.readString();
		assertEquals("string written and read are equal",testString,s);
	}
	
	@Test
	public void testModifyString() throws PersistStringException {
		String testStringA = "test string: A";
		String testStringB = "test string: B";
		
		ps.writeString(testStringA);
		assertEquals("string written and read are equal",testStringA,ps.readString());
		
		ps.writeString(testStringB);
		assertEquals("string written was replaced",testStringB,ps.readString());
	}
	
}
