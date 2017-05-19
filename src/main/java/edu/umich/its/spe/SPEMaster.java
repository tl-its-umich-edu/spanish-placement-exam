package edu.umich.its.spe;

import org.json.JSONObject;

/*
 * Class to drive the SPE processing  
 * Will verify can reach ESB.
 */

// TTD:
// - get configuration (inject via Spring?)
// - method to verify connection (e.g. get token successfully)
// - method to getGrades
// - method to put grades.
// - needs flexible sibling to allow file read / write testing.

public class SPEMaster {
	
	public SPEMaster() {
		super();
	}
	
	/* 
	 * Run sanity checks to make sure can talk to ESB and get data.
	 */
	public boolean verify() {
		return false;
	}
	
	/*
	* Get grades as json.  Only grades after the gradeAfterTime
	* timestamp will be returned.  There may be no new tests.
	* The format of the time stamp is: 2017-04-01 18:00:00
	*/ 
	public JSONObject getSPEGrades(String gradeAfterTime) {
		return null;
	}
	
	/* 
	 * Put the grade for this user into MPathways.
	 */
	public boolean putSPEGrade(JSONObject user) {
		return false;
	}
	
	/*
	 * Orchestrate the script.
	 * - read configuration.
	 * - get gradeAfterTime.
	 * - get grades.
	 * - translate to MPathways format.
	 * - send to MPathways.
	 * 
	 * Also error handling and logging.
	 */
	public void invoker () {
		
	}
	
}
