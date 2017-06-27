package edu.umich.its.spe;

/*
 * Interface for object to read/write a persistent string based only on resource path name.
 * This is meant to provide a very simple way to store very small amounts of data.
 */
public interface PersistBlob {

	// Read the string for this path.
	String readBlob() throws PersistBlobException;

	// write a string to this path.
	void writeBlob(String contents) throws PersistBlobException;

	// empty out string if it exists.
	void zapBlob() throws PersistBlobException;

}