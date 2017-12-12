package edu.umich.its.spe;

/*
 * Store summary of processing information for distribution.
 * Include the grade retrieval dates (stored, used, and new),
 * users processed (with error status) and elapsed time.
 *
 * This doesn't replace logging.  It is to provide a report
 * suitable for distribution. The report is generated by toString.
 *
 * Elapsed time is calculated based on when summary is created and when
 * the string version is created.
 */

import java.time.Duration;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.apache.commons.lang3.time.DurationFormatUtils;

import lombok.Data;

@Component

@Data
public class SPESummary {

	static final Logger M_log = LoggerFactory.getLogger(SPEMaster.class);

	private static final String LINE_RETURN = "\n";

	private Instant startTime = Instant.now();
	private Instant endTime;

	// Save the grade retrieval timestamps for:
	// - the value that was already stored by a previous run (if any),
	// - the, possibly computed, value that was actually used for the query,
	// - the value to be stored for next time.

	// String is a suitable format for this timestamp since these are only used in the report.

	private String storedTestLastTakenTime = new String();
	private String useTestLastTakenTime = new String();
	private String updatedTestLastTakenTime = new String();

	// id of the canvas course used as grade source
	private String courseId = new String();

	// Keep list of user names, processing success status, and time they user finished the test.

	private List<Triple<String, String, Boolean>> users  = new ArrayList<Triple<String,String,Boolean>>();
	private int added = 0;
	private int errors = 0;

	// Empty the object so it can be used anew for the next run.  Used auto-wiring to
	// get the same object everywhere it is needed.  May not be the best choice if
	// if using the internal wait and restart approach to cron jobs.

	public void reset() {
		// Instant is based on linux epoch time so it is unambigiously
		// interpreted as UTC.
		startTime = Instant.now();
		endTime = null;

		storedTestLastTakenTime = new String();
		useTestLastTakenTime = new String();
		updatedTestLastTakenTime = new String();

		courseId = new String();

		users  = new ArrayList<Triple<String,String,Boolean>>();
		added = 0;
		errors = 0;

	}

	public SPESummary appendUser(String name,String time,Boolean success) {
		users.add(Triple.of(name,time,success));
		if (success) {
			added++;
		} else {
			errors++;
		};

		return this;
	}

	// Return a copy of the user list sorted by the name string. The original user list is unchanged.
	// This method is separate for easy testing.

	protected List<Triple<String, String, Boolean>> sortedUsers() {
		// use Java 8 streams and lambda
		return users.stream()
				.sorted((u1, u2) -> u1.getLeft().compareTo(u2.getLeft()))
				.collect(Collectors.toList());
	}

	// Produce a processing summary report in a single string.
	public String toString() {

		// Automatically set the report end time to the time when generate report string.

		endTime = Instant.now();
		StringBuffer result = new StringBuffer();

		Duration dur = Duration.between(startTime, endTime);

		// make sure there is a printable value for course id even if used file IO to get users so no course
		// was involved.

		String courseIdString = courseId.toString().length() > 0 ? courseId.toString() : "[none]";

		result.append(LINE_RETURN);
		result.append(LINE_RETURN);

		result.append("starting time: ").append(SPEUtils.formatTimestampInstantToImplicitUTC(startTime)).append(LINE_RETURN);
		result.append("end time: ").append(SPEUtils.formatTimestampInstantToImplicitUTC(endTime)).append(LINE_RETURN);

		result.append("elapsed time: ").append(DurationFormatUtils.formatDurationHMS(dur.toMillis())).append(LINE_RETURN);
		result.append(LINE_RETURN);

		result.append("storedTestLastTakenTime: ").append(storedTestLastTakenTime.toString()).append(LINE_RETURN);
		result.append("useTestLastTakenTime: ").append(useTestLastTakenTime.toString()).append(LINE_RETURN);
		result.append("updatedTestLastTakenTime: ").append(updatedTestLastTakenTime.toString()).append(LINE_RETURN);
		result.append(LINE_RETURN);

		result.append("courseId: ").append(courseIdString).append(LINE_RETURN);
		result.append(LINE_RETURN);

		result.append("users added: ").append(added).append(" errors: ").append(errors).append(LINE_RETURN);
		result.append(LINE_RETURN);


		// for each user (in sorted order) add an entry to the result string.
		sortedUsers()
		.forEach((u) -> result
				.append("user: ").append(u.getLeft())
				.append(" success: ").append(u.getRight())
				.append(" finished at: ").append(u.getMiddle())
				.append(LINE_RETURN));

		return result.toString();
	}

}
