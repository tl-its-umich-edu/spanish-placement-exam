package edu.umich.its.spe;

import java.util.HashMap;
import java.util.List;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;

/* 
 * Interface describing the local interface to the ESB calls.  This is used
 * so it is easy to mock the calls.
 */

public interface SPEEsb {

	// Set up the values required to do get (put) calls for grades via the ESB.
	List<String> setupGetGradePropertyValues();
	List<String> setupPutGradePropertyValues();

	// Call the esb and return a wrapped result.
	WAPIResultWrapper getGradesViaESB(HashMap<String, String> value) throws SPEEsbException;
	WAPIResultWrapper putGradeViaESB(HashMap<String, String> value);
	
	// check that can access the ESB.
	boolean verify(HashMap<String, String> value);

}