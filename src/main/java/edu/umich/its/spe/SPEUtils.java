package edu.umich.its.spe;

/*
 * Cross class utilities.  Mostly for handling timestamps consistently. By convention Canvas
 * (UDW) dates are UTC but that isn't explicit in the data and needs to be clearly understood
 * in the code.  Methods are centralized to prevent conversion / parsing / misunderstandings
 * errors from creeping in.
 *
 * Internally times are represented as an Instant (epoch time).  This is always UTC.
 *
 * All code modifying a grade related timestamp should be in here.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPEUtils {

	static final Logger M_log = LoggerFactory.getLogger(SPEUtils.class);

	// match end of string that looks like fractional seconds with or without a Z
	public static final String FRACTIONAL_SECONDS_REGEX = "\\.\\d\\d\\d[zZ]?$";
	// match timezone offset with or without an embedded ':'
	public static String OFFSET_REGEX = "[-+]\\d\\d:?\\d\\d$";

	static Pattern p_fractional_seconds = Pattern.compile(FRACTIONAL_SECONDS_REGEX);
	static Pattern p_offset = Pattern.compile(OFFSET_REGEX);

	// get a string property and default it to empty string.
	static protected String safeGetPropertyValue(HashMap<String, String> testproperties, String propertyKey) {
		if (testproperties == null) {
			return "";
		}
		String propertyValue = (testproperties.get(propertyKey) == null ? "" :  testproperties.get(propertyKey));
		return propertyValue;
	}

	static String getISO8601StringForDate(Date date) {
		// Get the current time (with time zone).
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return dateFormat.format(date);
	}

	// Take out minor variations in a timestamp string.  This makes parsing
	// much, much easier.

	public static String normalizeStringTimestamp(String stringTimestamp) {

		M_log.debug("normalizeStringTimestamp: input: {}",stringTimestamp);

		if (stringTimestamp == null || stringTimestamp.length() == 0) {
			return null;
		}

		//////// eliminate any fractional seconds.  Note this truncates, it does NOT round.
		Matcher m = p_fractional_seconds.matcher(stringTimestamp);
		if (m.find()) {
			//M_log.debug("normalizeStringTimestamp: FOUND fractional seconds");
			stringTimestamp = stringTimestamp.replaceFirst(FRACTIONAL_SECONDS_REGEX,"");
		}

		M_log.debug("normalizeStringTimestamp: without fractional seconds {}",stringTimestamp);

		// Make sure has explicit timezone information.  Either it has an offset or ends with 'Z' (UTC).

		Matcher mo = p_offset.matcher(stringTimestamp);

		if (mo.find()) {
			// found this has an offset, so make sure it has the embedded colon.
			if (! ":".equals(String.valueOf(stringTimestamp.substring(stringTimestamp.length()-3,
					stringTimestamp.length()-2)))) {
				stringTimestamp = new StringBuffer(stringTimestamp).insert(stringTimestamp.length()-2,":").toString();
			}
			M_log.debug("normalizeStringTimestamp: standarized colon in offset {}",stringTimestamp);
		}
		else {
			// no offset so make sure there is (only 1) Z at the end.
			if (! "Z".equalsIgnoreCase(String.valueOf(stringTimestamp.charAt(stringTimestamp.length()-1)))) {
				M_log.debug("normalizeStringTimestamp: adding Z to {}",stringTimestamp);
				stringTimestamp += "Z";
			}
		}

		// make sure the ISO 8601 T is in there.
		stringTimestamp = stringTimestamp.replaceFirst( " ", "T" );

		M_log.debug("normalizeStringTimestamp output: {}",stringTimestamp);
		return stringTimestamp;
	}

	// Returns as plain date time string assumed to be in UTC.  There is no offset or Z.
	public static String formatTimestampInstantToImplicitUTC(Instant timeStampInstant) {
		// by default this converts to UTC in ISO8601.  We format without time zone information.
		// User must assume this is in UTC.
		return timeStampInstant.toString().replace("Z","");
	}

	// Get a long value from a string use default if the string is null or empty.
	public static long longFromStringWithDefault(String longString, Long defaultValue) {
		long longValue = (longString == null ? defaultValue : Long.parseLong(longString));
		M_log.debug("longFromString: {} {}",longString,longValue);
		return longValue;
	}

	// get an existing offset time and return a new string in UTC that is one second later.
	public static Instant generateNewQueryTime(Instant priorUpdateTime) {

		Instant newQueryTime = priorUpdateTime.plusSeconds(1);
		M_log.debug("generating new query time: original: {} new {}",priorUpdateTime,newQueryTime);

		return newQueryTime;
	}

	public static Instant convertTimeStampStringToInstant(String timestamp) {

		// This puts the string in a standard format that should be parsable.
		String s = SPEUtils.normalizeStringTimestamp(timestamp);

		Matcher mo = p_offset.matcher(timestamp);

		if (mo.find()) {
			M_log.debug("found offset in timestamp: {} so trying OffsetDateTime",timestamp);
			try {
				OffsetDateTime odt = OffsetDateTime.parse(s);
				M_log.debug("Offset date time for: {} is {}",s,odt);
				//return Instant.parse(s,DateTimeFormatter.ISO_OFFSET_DATE_TIME);
				return odt.toInstant();
			} catch(DateTimeParseException exp) {
				M_log.info("OffsetDateTime.parse: Catch {} for timestamp: {}",exp,s);
			}
		}

		// try default Instant parse.
		M_log.debug("try Instant");
		try {
			return Instant.parse(s);
		} catch(DateTimeParseException exp){
			M_log.warn("Instant.parse: Catch {} for timestamp: {}",exp,s);
		}

		M_log.error("timestamp to instant parsing failed: {}",s);
		//TODO: throw exception?
		return null;
	}

}
