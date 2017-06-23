package edu.umich.its.spe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 
 * This will read or write a UTF-8 string identified by a specific resource path.
 * The contents of the string are restricted and have no specific limit.
 * This is useful for implementing tiny bits of persistent storage when the
 * need for persistent storage is real but small.
 * 
 * The initial implementation requires disk storage and will only work on a single server.  
 * Anticipated volume for SPE is low enough to make that feasible.
 */

/*
 * For initial implementation the path will be an absolute disk directory path.
 * Only the directory path is required.  The internal file name is supplied automatically.
 * 
 * Future implementations may be more powerful when more power is required.
 * 
 * Possible extensions are:
 * - authn, authz
 * - version control - keep old version, allow restoring old versions.
 * - sanity checking for simultaneous access.
 * - compatibility with multiple servers.
 * - atomic read / write.
 */

// Spring: Don't use @Component for autowiring since this need a constructor argument.
// See SPEConfiguration for that implementation.

public class PersistStringImpl implements PersistString {

	private static final Logger log = LoggerFactory.getLogger(PersistStringImpl.class);

	String path = "";
	String persistant_file_name = "persisted.txt";
	String full_file_name = "";
	
	// Path passed in must be an absolute disk path to avoid problems finding the location.
	public PersistStringImpl(String path) throws PersistStringException {

		log.info("PS constructor string: "+path);
		this.path = path;
		this.full_file_name = path+persistant_file_name;
		
		// Make sure the file can be used and has at least a trivial value.
		
		if (! "/".equals(path.substring(0,1))) {
			throw new PersistStringException("Path provided must be an absolute path.");
		}

		if (Files.notExists(Paths.get(path))) {
			throw new PersistStringException("Path does not exist: ["+path+"]");
		}
		
		if (!Files.isWritable(Paths.get(path))) {
			throw new PersistStringException("Path is not writeable.");
		}
		
		if (Files.notExists(Paths.get(full_file_name))) {
			writeString("");
		}
	}

	// Read the string for this path.
	@Override
	public String readString() throws PersistStringException {
		log.debug("readString: ");
		try {
			String contents = FileUtils.readFileToString(new File(full_file_name), "UTF-8");
			log.debug("returned string: "+contents);
			return contents;
		} catch (IOException e) {
			throw new PersistStringException("can not read string",e);
		}

	}

	/* 
	 * Replace the current data for this path by the 
	 * contents of this new string.
	 */

	@Override
	public void writeString(String contents) throws PersistStringException {
		log.debug("writeString: [{}]",contents);
		try {
			Files.write(Paths.get(full_file_name), contents.getBytes());
			return;
		} catch (RuntimeException |  IOException e) {
			throw new PersistStringException("Wrapped Exception in PersistString",e);
		}
	}

	/*
	 * Convenience function to empty the stored value.
	 * 
	 */
	@Override
	public void zapString() throws PersistStringException {
		writeString("");		
	}

}
