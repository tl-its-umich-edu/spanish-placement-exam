package edu.umich.its.spe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.Unirest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/*
 * Master class to run the Spanish Placement Exam script.
 * 
 * See junit test files for sample data.
 */

// TTD: (informal)

// TASKS: (if not listed inline below)
// TODO: implement message logging / reporting
// TODO: figure out logging configuration. how set log level?
// TODO: configuration should separate security and properties.
// TODO: test error handling

// Spring: make visible for auto wiring.
@Component
public class SPEMaster {

	static final Logger M_log = LoggerFactory.getLogger(SPEMaster.class);
	static HashMap<String,String> unirest;

	// Let Spring inject the properties, the esb service, and the persistString implementation.
	@Autowired
	private SPEEsb speesb;

	@Autowired
	private PersistString persistString;

	@Autowired
	private SPEProperties speproperties;

	@Autowired
	private SPESummary spesummary;
	
	// If no other timestamp is specified use the current time.
	private LocalDateTime startingTime = LocalDateTime.now();

	public SPEMaster() {
		super();
		M_log.info("SPEMaster: this: "+this.toString());
	}

	// Set global timeouts for EBS calls 
	protected void setUnirestGlobalValues() {
		
		M_log.info("setupUnirestGlobalValues");
		unirest = speproperties.getUnirest();
		M_log.info("unirest properties: {}",unirest);
		
		// Setup values for request timeouts.
		long ct = longFromStringWithDefault(unirest.get("connectionTimeout"), 10000l);
		long st = longFromStringWithDefault(unirest.get("socketTimeout"), 10000l);
		
		M_log.info("unirest timeouts: connectionTimeout: {} socketTimeout: {}",ct,st);
		
		Unirest.setTimeouts(ct,st);
	}

	// Get a long value from a string use default if the string is null or empty.
	protected long longFromStringWithDefault(String longString, Long defaultValue) {
		long longValue = (longString == null ? defaultValue : Long.parseLong(longString));
		M_log.info("longFromString: {} {}",longString,longValue);
		return longValue;
	}

	/*********************** Orchestrate the script. *****************/ 
	
	/* 
	 * - configuration is read and injected by spring
	 * - get value gradeAfterTime from persist string (default if necessary).
	 * - get grades.
	 * - translate to format for updates.
	 * - send to MPathways.
	 * 
	 */
	
	/* Organize the task and handle errors */
	
	public void orchestrator () throws PersistStringException, SPEEsbException {
	
		M_log.info("Start SPE orchestrator");
	
		setUnirestGlobalValues();
		
		// sanity check by renewing token to ensure we can connect to the database.
		
		if (!verifyESB()) {
			M_log.error("Unable to connect to esb"); 
			throw new SPEEsbException("Unable to connect to ESB");
		}
		
		//// Get the relevant grades.
		String lastUpdateTime = readLastGradeTransferTime();
		M_log.error("fake lastUpdateTime");
		lastUpdateTime = "2017-03-08 15:38:21";
		M_log.info("lastUpdateTime: {}",lastUpdateTime);
		String assignmentsFromDW;
	
		try {
			assignmentsFromDW = getSPEGrades(lastUpdateTime);
	
			//// Extract grades from JSON the grades for insertion	
	
			ArrayList<HashMap<String,String>> SPEgradeMaps = null;
			try {
				SPEgradeMaps = convertSPEGradesFromDataWarehouseJSON(assignmentsFromDW);
			} catch (JSONException e) {
				M_log.error("exception in converting grades: "+e);
			}
	
			//// Insert the grades.
			M_log.info("Grade count since {} is {}.",lastUpdateTime,SPEgradeMaps.size());
			putSPEGrades(SPEgradeMaps);
	
		} catch (SPEEsbException e1) {
			M_log.error("Exception processing SPE grades:",e1);
		}
		finally {
			// Finish up, save data, send reports.
			closeUpShop();
		}
	}

	/************ Close down processing, create summary ************/
	public void closeUpShop() {
		// TODO: implement task shutdown.
		// Just print summary.
		System.out.println(spesummary.toString());
		M_log.error("Implement close up shop");
	}

	/********* esb verify **********/
	// Run sanity check to make sure can talk to ESB and get data.
	
	/*
	 * Setup the hash of values needed for the call to verify ESB connection.
	 */
	protected HashMap<String, String> setupESBVerifyCall() {
		//M_log.debug("spe properties: "+speproperties);
		HashMap<String,String> value = new HashMap<String,String>();
		value.putAll(speproperties.getEsb());
		return value;
	}

	public Boolean verifyESB() {
		// TODO: implement verify by renewing access token.
		M_log.error("verify is not yet used by SPEMaster");
		HashMap<String,String> value = setupESBVerifyCall();
		return speesb.verify(value);	
	}

	/*************** get grades via ESB **************/

	/*
	 * Setup the hash of values needed for the call to get grades.
	 */
	protected HashMap<String, String> setupGetGradesCall(String gradedAfterTime) {
		M_log.debug("spe properties: "+speproperties);
		HashMap<String,String> value = new HashMap<String,String>();
		value.putAll(speproperties.getEsb());
		value.putAll(speproperties.getGetgrades());
		value.put("gradedaftertime", gradedAfterTime);
		return value;
	}

	/*
	 * Get grades as JSON string.  Only grades after the gradeAfterTime
	 * timestamp will be returned. An empty result is reasonable.
	 * The format of the time stamp is: 2017-04-01 18:00:00.  
	 * TODO: timestamp default value?
	 */ 

	public String getSPEGrades(String gradedAfterTime) throws SPEEsbException {

		spesummary.setUseGradesLastRetrieved(gradedAfterTime);
		HashMap<String, String> values = setupGetGradesCall(gradedAfterTime);

		WAPIResultWrapper grades = speesb.getGradesViaESB(values);
		// check for possibility of no new grades.
		if (grades.getStatus() == 404) {
			return "[]";
		}

		return grades.getResult().toString();
	}

	//{"Meta":{"Message":"COMPLETED","httpStatus":200},"Result":{"putPlcExamScoreResponse":{"putPlcExamScoreResponse":{"Status":"SUCCESS","Form":7,"ID":"abc"},"@schemaLocation":"http://mais.he.umich.edu/schemas/putPlcExamScoreResponse.v1 http://csqa9ib.dsc.umich.edu/PSIGW/PeopleSoftServiceListeningConnector/putPlcExamScoreResponse.v1.xsd"}}}

	//{"putPlcExamScoreResponse":{"putPlcExamScoreResponse":{"Status":"SUCCESS","Form":7,"ID":"abc"

	/*********** format assignments from the data warehouse ********/
	
	// After getting the JSON from the data warehouse format the SPE grades.
	protected ArrayList<HashMap<String,String>> convertSPEGradesFromDataWarehouseJSON(String assignmentJSON) {
	
		M_log.debug("cSGFDWJ: assignmentJSON: {}",assignmentJSON);
		// if nothing in it then return an empty list.
		if (assignmentJSON == null || assignmentJSON.length() <=2) {
			ArrayList<HashMap<String,String>> emptyArrayList = new ArrayList<HashMap<String,String>>();
			return emptyArrayList;
		}
	
		JSONArray assignments = parseCanvasAssignmentJSON(assignmentJSON);
		ArrayList<HashMap<String,String>> gradeMapList = convertAssignmentsToGradeMaps(assignments);
		M_log.debug("cSGFDWJ: gradeMaplist: {}",gradeMapList);
		return gradeMapList;
	}

	// convert the list of DataWarehouse assignments to a list of grade maps for MPathways.
	protected ArrayList<HashMap<String, String>> convertAssignmentsToGradeMaps(JSONArray canvasAssignments) {
		ArrayList<HashMap<String,String>> gradeMapList = new ArrayList<HashMap<String, String>>();
	
		// JSONArray is not iterable so can't use the nice "for each" syntax.
		for(int i = 0; i<canvasAssignments.length(); i++) {
			JSONObject assignment = canvasAssignments.getJSONObject(i);
			HashMap<String, String> grademap = convertAssignmentToGradeMap(assignment);
			gradeMapList.add(grademap);
		}
	
		return gradeMapList;
	}

	// convert a single JSON version of an assignment to a grademap.
	static protected HashMap<String, String> convertAssignmentToGradeMap(JSONObject assignment) throws JSONException {
		HashMap<String,String> grademap = new HashMap<String,String>();
		// Score may be read as a number instead of a string so pull out as an object and convert to a string.
		grademap.put("Score",JSONObject.valueToString(assignment.get("Score")));
		grademap.put("Unique_Name", assignment.getString("Unique_Name"));
		return grademap;
	}

	// Take the assignment JSON string from the data warehouse and return the array of grades as a parsed JSON array.
	static protected JSONArray parseCanvasAssignmentJSON(String gradeJSON) throws JSONException {
		JSONObject jo = new JSONObject(gradeJSON);
		JSONObject jAI = jo.getJSONObject("AssignmentInfo");
	
		// Get the data as a JSON array.
		JSONArray jAD = jAI.optJSONArray("AssignmentData");
	
		// If there is a single grade it may appear as a single object, so deal with that.
		if (jAD == null) {
			jAD = new JSONArray();
			jAD.put(jAI.getJSONObject("AssignmentData"));
		}
	
		return jAD;
	}

	
	/***************** put grades ************/
	
	/*
	 * Setup the hash of values needed for the call to get grades.
	 */
	
	protected HashMap<String, String> setupPutGradeCall(HashMap<?, ?> user) {
		M_log.debug("spe properties: "+speproperties);
		HashMap<String,String> value = new HashMap<String,String>();
		value.putAll(speproperties.getEsb());
		// if no user that use default values (for testing).
		if (user == null || user.isEmpty()) {
			value.putAll(speproperties.getPutgrades());
		}
		else {
			value.put("SCORE",(String) user.get("Score"));
			value.put("UNIQNAME",(String) user.get("Unique_Name"));
		}
		return value;
	}

	//{"Meta":{"Message":"COMPLETED","httpStatus":200},"Result":{"putPlcExamScoreResponse":{"putPlcExamScoreResponse":{"Status":"SUCCESS","Form":7,"ID":"abc"},"@schemaLocation":"http://mais.he.umich.edu/schemas/putPlcExamScoreResponse.v1 http://csqa9ib.dsc.umich.edu/PSIGW/PeopleSoftServiceListeningConnector/putPlcExamScoreResponse.v1.xsd"}}}
	
	//{"putPlcExamScoreResponse":{"putPlcExamScoreResponse":{"Status":"SUCCESS","Form":7,"ID":"abc"
	
	// Print out user for updated grade.
	static protected void logPutGrade(WAPIResultWrapper putGrade)  {
		try {
			JSONObject jo = new JSONObject(putGrade);
			JSONObject jAI = jo.getJSONObject("result");
			JSONObject pPESR = jAI.getJSONObject("putPlcExamScoreResponse").getJSONObject("putPlcExamScoreResponse");
			String user = pPESR.getString("ID");
			M_log.info("putGrade: updated MPathways for user: "+user);
		} catch (JSONException e) {
			M_log.error("error extracting grade update user for: "+putGrade.toJson());
		}
	}

	/********* put in the grades **********/
	
	// Send a list of grades to MPathways.
	public void putSPEGrades(ArrayList<HashMap<String, String>> SPEgradeMaps) {
		int gradesAdded = 0;
		int gradesAttempted = 0;
		Boolean success;
		for(HashMap<String, String> singleUser : SPEgradeMaps) {
			gradesAttempted ++;
			success = putSPEGrade(singleUser);
			if (success) {
				gradesAdded++;
			}
		}

		M_log.info("adding grades: attempted: {} successful: {}",gradesAttempted,gradesAdded);
	}
	
	// Send a single grade to MPathways.
	public boolean putSPEGrade(HashMap<?, ?> user) {
		HashMap<String,String> value = setupPutGradeCall(user);
		boolean success = false;

		WAPIResultWrapper wrappedResult = speesb.putGradeViaESB(value);

		M_log.debug("update: {}",wrappedResult.toJson());

		if (wrappedResult.getStatus() == 200) {
			logPutGrade(wrappedResult);
			//return true;
			success = true;
		}

		spesummary.appendUser(value.get("UNIQNAME"), success);
		M_log.error("error updating grade: "+wrappedResult.toJson());
		//return false;
		return success;
	}



	/***************** manage the timestamp data with PersistString ***********/
	/*
	 * Use PersistString class to get a string with a timestamp representing the 
	 * last time the script updated the grades.
	 * 
	 * If the value isn't available or is corrupt use a default value.  We'll be careful
	 * but won't panic about only submitting grades once.
	 */
	
	// Format an internal timestamp into the expected string.
	public static String formatPersistTimestamp(LocalDateTime ts) {
		return formatTimestamp(ts);
	}
	
	public static String formatTimestamp(LocalDateTime ts) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String s = ts.format(formatter);
		return s;
	}
	
	public String readLastGradeTransferTime() throws PersistStringException, SPEEsbException {
		String lastTransferTime = persistString.readString();

		lastTransferTime = ensureLastGradeTransferTime(lastTransferTime);
		writeLastGradeTransferTime(speproperties.getGetgrades().get("gradedaftertime"));
		spesummary.setStoredGradesLastRetrieved(lastTransferTime);
		return lastTransferTime;
	}
	
	// Compute a reasonable last grade transfer time.
	protected String ensureLastGradeTransferTime(String lastTransferTime) throws PersistStringException, SPEEsbException {
		
		String useTransferTime;
		
		// if specify a particular override time use that.  This is a separate property to make it easy to 
		// override from command line.
		useTransferTime = speproperties.getGetgrades().get("gradedaftertimeoverride");
		if (useTransferTime != null && useTransferTime.length() > 0) {
			return useTransferTime;
		}
		
		// if a time was saved use that.
		//useTransferTime = readLastGradeTransferTime();
		if (useTransferTime != null && useTransferTime.length() > 0) {
			return useTransferTime;
		}
		
		// if a time was saved use that.
		useTransferTime = speproperties.getGetgrades().get("gradedaftertime");
		if (useTransferTime != null && useTransferTime.length() > 0) {
			return useTransferTime;
		}
		
		M_log.error("Can not determine last graded time");
		throw  new SPEEsbException("Can not determine last graded time");
		
	}
	
	// Compute a reasonable last grade transfer time.
	protected String ensureLastGradeTransferTimeOLD(String lastTransferTime) throws PersistStringException, SPEEsbException {

		String useTransferTime;
		
		// if specify specific override time use that.  This is a separate property to make it easy to 
		// override from command line.
		String gradeDateOverride = speproperties.getGetgrades().get("gradedateoverride");
		if (gradeDateOverride != null && gradeDateOverride.length() > 0) {
			return gradeDateOverride;
		}
		
		// if a time was saved use that.
		//useTransferTime = readLastGradeTransferTime();
		useTransferTime = persistString.readString();
		if (useTransferTime != null && useTransferTime.length() > 0) {
			return useTransferTime;
		}
		
		// if a time was saved use that.
		useTransferTime = speproperties.getGetgrades().get("gradedaftertime");
		if (useTransferTime != null && useTransferTime.length() > 0) {
			return useTransferTime;
		}
		
		M_log.error("Can not determine last graded time");
		throw  new SPEEsbException("Can not determine last graded time");
		

//		if (lastTransferTime == null || lastTransferTime.isEmpty()) {
//			//M_log.info("default gradedaftertime: {}",speproperties.getGetgrades().get("gradedaftertime"));
//			M_log.info("persisted gradedaftertime was empty");
//			
//		//	writeLastGradeTransferTime(speproperties.getGetgrades().get("gradedaftertime"));
//		//	return readLastGradeTransferTime();
//			lastTransferTime = readLastGradeTransferTime();
//		}
		//return lastTransferTime;
	}

	// If no time is specified used the current script starting time.
	public String writeLastGradeTransferTime() throws PersistStringException {
		return writeLastGradeTransferTime(formatPersistTimestamp(startingTime));
	}

	// Save a specific time.
	public String writeLastGradeTransferTime(String timestamp) throws PersistStringException {
		spesummary.setUpdatedGradesLastRetrieved(timestamp);
		persistString.writeString(timestamp);
		return timestamp;
	}

}
