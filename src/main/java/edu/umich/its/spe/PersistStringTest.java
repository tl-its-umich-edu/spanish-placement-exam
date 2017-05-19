package edu.umich.its.spe;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.umich.its.spe.PersistString;
import edu.umich.its.spe.PersistStringException;

public class PersistStringTest {

	@Rule public TemporaryFolder tempFolder = new TemporaryFolder();

	PersistString ps = null;

	@Before
	public void setUp() throws Exception {
		
		ps = new PersistString("/tmp/test/here");
	}

	@After
	public void tearDown() throws Exception {
	}

	String timeStamp() {
		return Instant.now().toString();
	}
	
	/*********************/
	/* constructor tests */

	public void testAbsolutePath() throws PersistStringException {
		System.out.println("temp folder: A "+tempFolder.getRoot());
		new PersistString("/here/is/a/reasonable/path");
	}

	@Test(expected=PersistStringException.class)
	public void testRelativePath() throws PersistStringException {
		System.out.println("temp folder: B "+tempFolder.getRoot());
		new PersistString("here/is/an/unreasonable/path");
	}
	
	/*********************/
	/* IO tests */
	@Test(expected=PersistStringException.class)
	public void testWriteString() throws PersistStringException {
		ps.writeString("test string: "+timeStamp());
	}

	@Test(expected=PersistStringException.class)
	public void testReadString() throws PersistStringException {
		String s = null;
			s = ps.readString();
	}

}
