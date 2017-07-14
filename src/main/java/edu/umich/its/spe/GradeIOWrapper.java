package edu.umich.its.spe;

/*
 * Wrapper to check properties and send the request to the right implementation.  This
 * The implementations for getting grades and settings grades can be set separately.
 * The implementations will get any required connection details from the properties.
 */

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;

// Mark as Spring component and indicate this is the implementation of GradeIO to use by default.

@Component
@Primary

public class GradeIOWrapper implements GradeIO {

	static final Logger M_log = LoggerFactory.getLogger(GradeIOWrapper.class);

	// Get the two real implementations injected.  @Qualifier is used to disambiguate multiple
	// implementations of the GradeIO interface and allow them to be injected automatically.

	@Autowired
	@Qualifier("ESBIO")
	GradeIO speesb;

	@Autowired
	@Qualifier("FileIO")
	GradeIO fileIO;

	@Autowired
	SPEProperties speproperties;

	// Hold (potentially) different implementations for get and put.
	GradeIO getGradesIO;
	GradeIO putGradeIO;

	/*
	 * The injected values are only available AFTER construction, so
	 * put the setup code in a @PostConstruct method.
	 */

	@PostConstruct
	public void setupGradeIOWrapper() {

		M_log.info("setting up GradeIOWrapper");
		M_log.info("speproperties: {}",speproperties);

		HashMap<String,String> testproperties = speproperties.getIo();

		String getGradeIOProperty = SPEUtils.safeGetPropertyValue(testproperties,"getGradeIO");
		if (getGradeIOProperty.length() == 0 || "ESBIO".equals(getGradeIOProperty.toUpperCase())) {
			getGradesIO = speesb;
		} else {
			getGradesIO = fileIO;
		}

		String putGradeIOProperty = SPEUtils.safeGetPropertyValue(testproperties,"putGradeIO");
		if (putGradeIOProperty.length() == 0 || "ESBIO".equals(putGradeIOProperty.toUpperCase())) {
			putGradeIO = speesb;
		} else if (putGradeIOProperty.length() > 0) {
			putGradeIO = fileIO;
		}

		M_log.info("getGradesIO: {} putGradeIO: {}",getGradesIO,putGradeIO);

	}

	// delegate to appropriate implementation.
	@Override
	public WAPIResultWrapper getGradesVia(SPEProperties speproperties, String gradedAfterTime)
			throws GradeIOException {
		return getGradesIO.getGradesVia(speproperties, gradedAfterTime);
	}

	// delegate to appropriate implementation.
	@Override
	public WAPIResultWrapper putGradeVia(SPEProperties speproperties, HashMap<?, ?> user) {
		return putGradeIO.putGradeVia(speproperties, user);
	}

	// Verify the IO connection.  Currently a NO OP.
	@Override
	public boolean verifyConnection(SPEProperties speproperties) {
		M_log.error("verifyConnection: FIX ME: always returns true right now.");
		//return speesb.verifyESBConnection(speproperties);
		return true;
	}

}
