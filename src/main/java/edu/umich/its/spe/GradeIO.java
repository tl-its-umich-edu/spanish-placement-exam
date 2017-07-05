package edu.umich.its.spe;

import java.util.HashMap;
import java.util.List;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;

/*
 * Interface describing the local interface to the ESB calls.  This is used
 * so it is easy to mock the calls.  Methods should only depend on
 * values required semantically for a request and a generic properties map.
 */

public interface GradeIO {

	// Set up the values required to do get (put) calls for grades via the ESB.
	List<String> setupPutGradePropertyValues();

	// Call the esb and return a wrapped result.
	WAPIResultWrapper getGradesViaESB(SPEProperties speproperties, String gradedAfterTime) throws GradeIOException;
	WAPIResultWrapper putGradeViaESB(HashMap<String, String> value);

	// check that can access the ESB.
	boolean verifyESBConnection(SPEProperties speproperties);

}