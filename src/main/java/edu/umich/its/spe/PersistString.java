package edu.umich.its.spe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 
 * This will read or write a UTF-8 string identified by a specific path.  
 * The contents of the string are undefined and have no specific limit.
 * This is useful for implementing tiny bits of persistent storage when the
 * need for persistent storage is real but small.
 * 
 * The initial implementation will only work on a single server.  Anticipated volume
 * is low enough to make that feasible.
 */

/*
 * For initial implementation the path will be a disk directory path.
 * The path should be complete path.
 * 
 * In the future there could be a better solution.  E.g. a protected string server 
 * with authn and authz.
 * 
 */

/*
 * Possible extensions:
 * - authn, authz
 * - version control - keep old version, allow restoring old versions.
 * - sanity checking for simultanious access.
 * - atomic read / write.
 */
public class PersistString {

//	./src/main/java/edu/umich/its/cpm/Migration.java:3:import org.slf4j.Logger;
//	./src/main/java/edu/umich/its/cpm/Migration.java:4:import org.slf4j.LoggerFactory;
	//./src/main/java/edu/umich/its/cpm/Migration.java:26:	
	private static final Logger log = LoggerFactory.getLogger(PersistString.class);
	
	String path = "";

	public PersistString(String path) throws PersistStringException {
		// TODO: path needs to be a complete path to avoid ambiguity.
		//log.error("PS constructor string: "+path);
		log.info("PS constructor string: "+path);
		this.path = path;
		if (! "/".equals(path.substring(0,1))) {
			throw new PersistStringException("Path provided must be an absolute path.");
		}
		//verify that path exists
	}

	// Read the string for this path.
	public String readString() throws PersistStringException {
		log.info("readString: ");
		String usePath = this.path+"/persisted.txt";
		try {
			String contents = FileUtils.readFileToString(new File(usePath), "UTF-8");
			log.info("returned string: "+contents);
			return contents;
		} catch (IOException e) {
			throw new PersistStringException("can not read string",e);
		}

	}

	/* Replace the current data for this path by the 
	 * contents of this new string.
	 */

	public void writeString(String contents) throws PersistStringException {
		// TODO: make sure directory exists
		//throw new PersistStringException("problem writing string at: "+this.path);
		log.info("writeString: [{}]",contents);
		try {
			Files.write(Paths.get(this.path+"/persisted.txt"), contents.getBytes());
			return;
		} catch (RuntimeException |  IOException e) {
			throw new PersistStringException("Wrapped Exception in PersistString",e);
		}
	}

	//Files.write(Paths.get("./duke.txt"), msg.getBytes());
	
}
