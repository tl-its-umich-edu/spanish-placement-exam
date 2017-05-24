package edu.umich.its.spe;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/*
 * Class to drive the SPE processing  
 * 
 * See junit test files for sample data.
 */

// TTD: (informal)

// TASKS: (if not listed inline below)
// TODO: implement message logging / reporting
// TODO: figure out logging configuration. how set log level?
// TODO: esb verification (e.g. get token renewed)
// TODO: configuration should separate security and properties.
// TODO: test error handling

public class SPEMaster {

	static final Logger log = LoggerFactory.getLogger(SPEMaster.class);

	public SPEMaster() {
		super();
	}

	/* 
	 * Run sanity checks to make sure can talk to ESB and get data.
	 */
	public boolean verify() {
		// TODO: implement verify by renewing access token.
		return false;
	}

	/*
	 * Get grades as JSON (in string format).  Only grades after the gradeAfterTime
	 * timestamp will be returned.  There may be no new tests.
	 * The format of the time stamp is: 2017-04-01 18:00:00
	 */ 

	public String getSPEGrades(String gradedAfterTime) {
		// TODO: implement call to get grades using last update date.
		return null;
	}

	/* 
	 * Put the grade for this user into MPathways.
	 */
	public boolean putSPEGrade(HashMap<?, ?> user) {
		// TODO: implement put of grades.
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
	
	
	/* Organize the task and handle errors */
	
	public void orchestrator () {
		
		//// Get the relevant grades.
		String lastUpdateTime = getLastGradeTransferTime();
		
		String assignmentsFromDW = getSPEGrades(lastUpdateTime);
		
		//// Format the grades for insertion		
		ArrayList<HashMap<String,String>> SPEgradeMaps = null;
		try {
			SPEgradeMaps = convertSPEGradesFromDataWarehouseJSON(assignmentsFromDW);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//// Insert those grades.
		putSPEGrades(SPEgradeMaps);
		
		// Finish up, save data, send reports.
		closeUpShop();
		
	}

	public void closeUpShop() {
		// TODO: implement task shutdown.
		
	}

	//// Put each of the grades in separately.
	public void putSPEGrades(ArrayList<HashMap<String, String>> SPEgradeMaps) {
		for(HashMap<String, String> singleUser : SPEgradeMaps) {
			putSPEGrade(singleUser);
		}
	}

	/*
	* Get string representing the last time the script updated the grades.  If necessary
	* default the value to a reasonable one.  Will not worry about never resubmitting grades.
	* Use PersistantString class.
	*/
	
	public String getLastGradeTransferTime() {
		// TODO: implement get  last transfer time
		return null;
	}
	
	public String setLastGradeTransferTime() {
		// TODO: implement  update last transfer time
		return null;
	}

	/*********** get and format assignments ********/
	// After getting the DW JSON format the SPE grades from the data warehouse.
	public ArrayList<HashMap<String,String>> convertSPEGradesFromDataWarehouseJSON(String assignmentJSON) throws JSONException {

		JSONArray assignments = parseCanvasAssignmentJSON(assignmentJSON);
		ArrayList<HashMap<String,String>> gradeMapList = convertAssignmentsToGradeMaps(assignments);

		return gradeMapList;
	}

	// convert all the DataWarehouse assignments to a list of grade maps for MPathways.
	protected ArrayList<HashMap<String, String>> convertAssignmentsToGradeMaps(JSONArray canvasAssignments) throws JSONException {
		ArrayList<HashMap<String,String>> gradeMapList = new ArrayList<HashMap<String, String>>();

		// JSONArray is not iterable so can't use for each format.
		for(int i = 0; i<canvasAssignments.length(); i++) {
			JSONObject assignment = canvasAssignments.getJSONObject(i);
			HashMap<String, String> grademap = convertAssignmentToGradeMap(assignment);
			gradeMapList.add(grademap);
		}

		return gradeMapList;
	}

	// convert a single JSON version of an assigment to a grademap.
	static protected HashMap<String, String> convertAssignmentToGradeMap(JSONObject assignment) throws JSONException {
		HashMap<String,String> grademap = new HashMap<String,String>();
		grademap.put("Score",assignment.getString("Score"));
		grademap.put("Unique_Name", assignment.getString("Unique_Name"));
		return grademap;
	}

	// Take the assignment JSON string and return the array of grades as a parsed JSON array.
	static protected JSONArray parseCanvasAssignmentJSON(String gradeJSON) throws JSONException {
		JSONObject jo = new JSONObject(gradeJSON);
		JSONObject ja = jo.getJSONObject("Result");
		JSONObject jAI = ja.getJSONObject("AssignmentInfo");
		JSONArray jAD = jAI.getJSONArray("AssignmentData");
		return jAD;
	}

}
