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


@Component
// There are multiple implementations so make it clear this is the one to use.
@Primary

public class GradeIOWrapper implements GradeIO {

	static final Logger M_log = LoggerFactory.getLogger(GradeIOWrapper.class);

	// Get the two implementations injected.  Qualifier is used to allow multiple
	// implementations of the GradeIO interface to be injected automatically.
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
	 * put the setup in a @PostConstruct method.
	 */

	@PostConstruct
	public void setupGradeIOWrapper() {

		M_log.info("setting up GradeIOWrapper");

		M_log.debug("speesb: {}",speesb);
		M_log.debug("fileIO: {}",fileIO);
		M_log.debug("speproperties: {}",speproperties);
		M_log.debug("getGradesIO: {}",getGradesIO);

		HashMap<String,String> testproperties = speproperties.getTest();

		String getGradeIOProperty = safeGetPropertyValue(testproperties,"getGradeIO");
		if (getGradeIOProperty.length() == 0 || "ESBIO".equals(getGradeIOProperty.toUpperCase())) {
			getGradesIO = speesb;
		} else {
			getGradesIO = fileIO;
		}

		String putGradeIOProperty = safeGetPropertyValue(testproperties,"putGradeIO");
		if (putGradeIOProperty.length() == 0 || "ESBIO".equals(getGradeIOProperty.toUpperCase())) {
			putGradeIO = speesb;
		} else if (putGradeIOProperty.length() > 0) {
			putGradeIO = fileIO;
		}

		M_log.info("getGradesIO: {} putGradeIO: {}",getGradesIO,putGradeIO);

	}

	// get a string property and default it to empty string.
	protected String safeGetPropertyValue(HashMap<String, String> testproperties, String propertyKey) {
		String propertyValue = (testproperties.get(propertyKey) == null ? "" :  testproperties.get(propertyKey));
		return propertyValue;
	}


	// get a grade (eventually not by esb only)
	@Override
	public WAPIResultWrapper getGradesViaESB(SPEProperties speproperties, String gradedAfterTime)
			throws GradeIOException {
		return getGradesIO.getGradesViaESB(speproperties, gradedAfterTime);
	}

	// put a grade (eventually not by esb only)
	@Override
	public WAPIResultWrapper putGradeViaESB(SPEProperties speproperties, HashMap<?, ?> user) {
		return putGradeIO.putGradeViaESB(speproperties, user);
	}

	// Verify the IO connection.  Currently always check the esb.
	@Override
	public boolean verifyESBConnection(SPEProperties speproperties) {
		return speesb.verifyESBConnection(speproperties);
	}

}
