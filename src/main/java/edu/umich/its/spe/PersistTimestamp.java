package edu.umich.its.spe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistTimestamp {

	/***************** manage the timestamp data with PersistString ***********/
	/*
	 * Use PersistString class to get a string with a timestamp representing the
	 * last time the script updated the grades.
	 *
	 * If the value isn't available or is corrupt use a default value.  We'll be careful
	 * but won't panic about only submitting grades once.
	 */

	static final Logger M_log = LoggerFactory.getLogger(PersistTimestamp.class);

	private static final long WEEKS_OFFSET_DEFAULT = 4;

	@Autowired
	private PersistBlob persistString;

	@Autowired
	private SPEProperties speproperties;

	@Autowired
	private SPESummary spesummary;

	private LocalDateTime startingTime = null;

	// Format an internal timestamp into the expected string.
	protected static String formatTimestamp(LocalDateTime ts) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String s = ts.format(formatter);
		return s;
	}

	public String readLastGradeTransferTime() throws PersistBlobException {
		String blob = persistString.readBlob();
		if (blob == null) {
			blob = "";
		}
		spesummary.setStoredGradesLastRetrieved(blob);
		return blob;
	}

	// save a specific time
	public String writeGradeTransferTime(LocalDateTime timestamp) throws PersistBlobException {
		return writeGradeTransferTime(formatTimestamp(timestamp));
	}

	// Save a specific time.
	public String writeGradeTransferTime(String timestamp) throws PersistBlobException {
		spesummary.setUpdatedGradesLastRetrieved(timestamp);
		persistString.writeBlob(timestamp);
		return timestamp;
	}

	public String writeCurrentGradeTransferTime() throws PersistBlobException {
		return writeGradeTransferTime(PersistTimestamp.formatTimestamp(startingTime));
	}

	// Make sure that a reasonable last transfer time has been written.  It will be read
	public String ensureLastGradeTransferTime() throws PersistBlobException, GradeIOException {

		String useTransferTime;

		// if a particular time was specified by a property use that.
		// Property can be overridden from command line.
		useTransferTime = speproperties.getGetgrades().get("gradedaftertime");

//		if (useTransferTime == null) {
//			useTransferTime = "";
//		}

		// if no property then read from persisted blob
		if (useTransferTime == null || useTransferTime.length() == 0) {
			useTransferTime = readLastGradeTransferTime();
			// Avoid reading and re-writing the same value.
			if (useTransferTime.length() > 0) {
				spesummary.setUseGradesLastRetrieved(useTransferTime);
				return useTransferTime;
			}
		}

		// if nothing is persisted then get the default property value.
		if (useTransferTime == null || useTransferTime.length() == 0) {
			useTransferTime = speproperties.getGetgrades().get("gradedaftertimedefault");
		}

//		if (useTransferTime == null) {
//			useTransferTime = "";
//		}
		// if no default value in properties then use the last month.
		if (useTransferTime == null || useTransferTime.length() == 0) {
			useTransferTime = formatTimestamp(LocalDateTime.now().minusWeeks(WEEKS_OFFSET_DEFAULT));
		}

		if (useTransferTime == null || useTransferTime == "") {
			M_log.error("Unable to compute last grade transfer time");
			return useTransferTime;
		}

		// Save the computed time.
		writeGradeTransferTime(useTransferTime);
		spesummary.setUseGradesLastRetrieved(useTransferTime);
		return useTransferTime;
	}

}
