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

	// Call the update implementation and return a wrapped result.
	WAPIResultWrapper getGradesVia(SPEProperties speproperties, String gradedAfterTime) throws GradeIOException;
	WAPIResultWrapper putGradeVia(SPEProperties speproperties,HashMap<?, ?> user);

	// check that can access the ESB.
	boolean verifyConnection(SPEProperties speproperties);

}