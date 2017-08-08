package edu.umich.its.spe;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.Set;

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
 * The directory will be created if it doesn't exist.
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

public class PersistBlobImpl implements PersistBlob {

	private static final Logger log = LoggerFactory.getLogger(PersistBlobImpl.class);

	String path = "";
	String persistant_file_name = "persisted.txt";
	String full_file_name = "";

	// If need to create directories use these permissions.
	String directoryPerms = "rwxr-xr-x";

	// Path passed in must be an absolute disk path to avoid problems finding the location.
	public PersistBlobImpl(String path) throws PersistBlobException {

		log.info("PS constructor string: "+path);
		this.path = path;
		this.full_file_name = path+"/"+persistant_file_name;

		// Make sure the file can be used and has at least a trivial value.

		if (! "/".equals(path.substring(0,1))) {
			throw new PersistBlobException("Path provided must be an absolute path.");
		}

		// if directory doesn't exist then try to make it.
		if (Files.notExists(Paths.get(path))) {
			handleMissingDirectory(path);
		}

		if (!Files.isWritable(Paths.get(path))) {
			throw new PersistBlobException("Path is not writeable.");
		}

		if (Files.notExists(Paths.get(full_file_name))) {
			writeBlob("");
		}
	}

	// Make directories if necessary
	public void handleMissingDirectory(String path_string) throws PersistBlobException {
		Path path = Paths.get(path_string);
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString(directoryPerms);
		FileAttribute<Set<PosixFilePermission>> attr =	PosixFilePermissions.asFileAttribute(perms);
		try {
			Files.createDirectories(path, attr);
		} catch (IOException e) {
			throw new PersistBlobException("Unable to create path: ["+path+"]",e);
		}
		log.info("PersistBlobImpl: created directory: {}",path_string);
	}

	// Read the string for this path.
	@Override
	public String readBlob() throws PersistBlobException {
		log.debug("readString: ");
		try {
			String contents = FileUtils.readFileToString(new File(full_file_name), "UTF-8");
			log.debug("returned string: "+contents);
			return contents;
		} catch (IOException e) {
			throw new PersistBlobException("can not read string",e);
		}

	}

	/*
	 * Replace the current data for this path by the
	 * contents of this new string.
	 */

	@Override
	public void writeBlob(String contents) throws PersistBlobException {
		log.debug("writeString: [{}]",contents);
		try {
			Files.write(Paths.get(full_file_name), contents.getBytes());
			return;
		} catch (RuntimeException |  IOException e) {
			throw new PersistBlobException("Wrapped Exception in PersistString",e);
		}
	}

	/*
	 * Convenience function to empty the stored value.
	 *
	 */
	@Override
	public void zapBlob() throws PersistBlobException {
		writeBlob("");
	}

}
