package edu.umich.its.spe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	String path = "";

	public PersistString(String path) throws PersistStringException {
		// TODO: path needs to be a complete path to avoid ambiguity.
		System.out.println("PS constructor string: "+path);
		this.path = path;
		if (! "/".equals(path.substring(0,1))) {
			throw new PersistStringException("Path provided must be an absolute path.");
		}
		//verify that path exists
	}

	// Read the string for this path.
	public String readString() throws PersistStringException {

		throw new PersistStringException("implement read string.");
		//throw new PersistStringException("problem reading string at: "+this.path);
	}

	/* Replace the current data for this path by the 
	 * contents of this new string.
	 */

	public void writeString(String contents) throws PersistStringException {
		// TODO: make sure directory exists
		//throw new PersistStringException("problem writing string at: "+this.path);
		try {
			Files.write(Paths.get(this.path+"/persisted.txt"), contents.getBytes());
			return;
		} catch (RuntimeException |  IOException e) {
			// TODO Auto-generated catch block
			throw new PersistStringException("Wrapped Exception in PersistString",e);
		}
		throw new PersistStringException("implement write string.");
	}

	//Files.write(Paths.get("./duke.txt"), msg.getBytes());
	
}
