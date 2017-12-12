package edu.umich.its.spe;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistTimestamp {

	/***************** manage the timestamp data with PersistString ***********/
	/*
	 * Use PersistString class to get a timestamp representing the
	 * last time a user finished the test.
	 *
	 * The underlying blob implementation is just storing a string, so we convert the
	 * ZonedDateTime timestamps in and out of string format.
	 *
	 * If the timestamp value isn't available or is corrupt we'll use a default value.
	 */

	static final Logger M_log = LoggerFactory.getLogger(PersistTimestamp.class);

	private static final long WEEKS_OFFSET_DEFAULT = 4;

	@Autowired
	private PersistBlob persistString;

	@Autowired
	private SPEProperties speproperties;

	@Autowired
	private SPESummary spesummary;

	public Instant readTestLastTakenTime() throws PersistBlobException {
		String blob = persistString.readBlob();
		blob = blob.trim();
		if (blob == null) {
			blob = "";
		}

		M_log.info("Timestamp from disk: {}",blob);
		spesummary.setStoredTestLastTakenTime(blob);

		return SPEUtils.convertTimeStampStringToInstant(blob);
	}

	// Write out instant as UTC timestamp
	public String writeLastTestTakenTime(Instant useLastTestTakenTime) throws PersistBlobException {
		return writeLastTestTakenTime(SPEUtils.formatTimestampInstantToImplicitUTC(useLastTestTakenTime));
	}

	// Save a specific time from a string.
	protected String writeLastTestTakenTime(String timestamp) throws PersistBlobException {
		timestamp = SPEUtils.normalizeStringTimestamp(timestamp);
		spesummary.setUpdatedTestLastTakenTime(timestamp);
		persistString.writeBlob(timestamp);
		return timestamp;
	}

	public Instant ensureLastTestTakenTime() throws PersistBlobException, GradeIOException {

		Instant useLastTestTakenTime = null;

		// if a particular time was specified by a property use that.
		// Property can be overridden from command line.
		String getGradedAfterTimeProperty = speproperties.getGetgrades().get("gradedaftertime");
		M_log.debug("getgradedaftertime",getGradedAfterTimeProperty);
		if (getGradedAfterTimeProperty != null) {
			useLastTestTakenTime = SPEUtils.convertTimeStampStringToInstant(speproperties.getGetgrades().get("gradedaftertime"));
		}

		// if no property then read from persisted blob
		if (useLastTestTakenTime == null) {
			useLastTestTakenTime = readTestLastTakenTime();
			// Avoid reading and re-writing the same value.
			// If found a saved value then use it.
			if (useLastTestTakenTime != null) {
				spesummary.setUseTestLastTakenTime(SPEUtils.normalizeStringTimestamp(useLastTestTakenTime.toString()));
				return useLastTestTakenTime;
			}
		}

		// No saved value so come up with a default.
		// if nothing is persisted then get the default property value.
		if (useLastTestTakenTime == null) {
			useLastTestTakenTime =
					SPEUtils.convertTimeStampStringToInstant(speproperties.getGetgrades().get("gradedaftertimedefault"));
		}

		// if no default value in properties then go back a month.
		if (useLastTestTakenTime == null) {
			useLastTestTakenTime = Instant.now().minus(WEEKS_OFFSET_DEFAULT,ChronoUnit.WEEKS);
		}

		M_log.debug("uselastTestTakenTime: UTC: {}",useLastTestTakenTime);

		// Save the computed time.
		String timeStampAsString = writeLastTestTakenTime(useLastTestTakenTime);
		spesummary.setUseTestLastTakenTime(timeStampAsString);
		return useLastTestTakenTime;
	}

}
