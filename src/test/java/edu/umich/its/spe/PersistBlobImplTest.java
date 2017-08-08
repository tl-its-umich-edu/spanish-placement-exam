package edu.umich.its.spe;

import static org.junit.Assert.*;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.umich.its.spe.PersistBlobImpl;
import edu.umich.its.spe.PersistBlobException;

public class PersistBlobImplTest {
	static final Logger log = LoggerFactory.getLogger(PersistBlobImplTest.class);

	/*******************************/
	/* setup */
	// Automatically create / clean up temporary folder for each test.
	@Rule public TemporaryFolder tempFolder = new TemporaryFolder();

	PersistBlob ps = null;

	@Before
	public void setUp() throws Exception {
		ps = new PersistBlobImpl(tempFolder.getRoot().toString());
	}

	@After
	public void tearDown() throws Exception {
	}

	String timeStamp() {
		return Instant.now().toString();
	}

	/** @throws IOException **************************/
	/* constructor tests */

	@Test
	public void testAbsolutePath() throws PersistBlobException {
		String tmpFolder = tempFolder.getRoot().toString();
		new PersistBlobImpl(tmpFolder);
	}

	@Test
	public void testAbsolutePathNewFolder() throws PersistBlobException {
		String tmpNewFolder = tempFolder.getRoot().toString()+"/newFolder";
		new PersistBlobImpl(tmpNewFolder);
	}

	@Test
	public void testAbsolutePathNewNestedFolders() throws PersistBlobException {
		String tmpNewFolder = tempFolder.getRoot().toString()+"/newFolder/level1/level2";
		new PersistBlobImpl(tmpNewFolder);
	}

	@Test
	public void testAbsolutePathExistingFolder() throws PersistBlobException {
		String tmpNewFolder = tempFolder.getRoot().toString()+"/newFolder/level1/level2";
		// make it.
		new PersistBlobImpl(tmpNewFolder);
		// make it again.
		new PersistBlobImpl(tmpNewFolder);
	}

	@Test(expected=PersistBlobException.class)
	public void testRelativePath() throws PersistBlobException {
		new PersistBlobImpl("here/is/an/unreasonable/path");
	}

	/**********************************/
	/* IO tests */

	/******** fail (with exception) @throws IOException */

	@Test(expected=PersistBlobException.class)
	public void testWriteNullStringFAIL() throws PersistBlobException {
		ps.writeBlob(null);
	}

	/********* pass ***********/

	@Test
	public void testWriteStringGoodPath() throws PersistBlobException {
		ps.writeBlob("test string: "+timeStamp());
		String s = ps.readBlob();
		assertNotNull("wrote string",s);
	}

	@Test
	public void testWriteEmptyStringOK() throws PersistBlobException {
		ps.writeBlob("");
	}

	@Test
	public void testWriteReadString() throws PersistBlobException {
		String testString = "test string: "+timeStamp();
		ps.writeBlob(testString);
		String s = ps.readBlob();
		assertEquals("string written and read are equal",testString,s);
	}

	@Test
	public void testModifyBlob() throws PersistBlobException {
		String testBlobA = "test string: A";
		String testBlobB = "test string: B";

		ps.writeBlob(testBlobA);
		assertEquals("string written and read are equal",testBlobA,ps.readBlob());

		ps.writeBlob(testBlobB);
		assertEquals("string written was replaced",testBlobB,ps.readBlob());
	}

}
