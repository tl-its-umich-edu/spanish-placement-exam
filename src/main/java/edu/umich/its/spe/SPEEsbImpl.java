package edu.umich.its.spe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import org.apache.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

/*
 * Implement esb calls to get / put grade information.
 * The information required is passed in a hashmap.  Some values come from the
 * properties files.
 */

// Spring: this makes the class discoverable for autowiring.
@Component

public class SPEEsbImpl implements GradeIO {

	protected static final String SKIP_GRADE_UPDATE = "SKIP GRADE UPDATE";

	static final Logger M_log = LoggerFactory.getLogger(SPEEsbImpl.class);

	@Autowired
	SPESummary spesummary;

	// No arg constructor for use in SpringApp.

	public SPEEsbImpl() {
		super();
	}

	// Constructor that allows injecting summary object.  That can
	// be useful in testing when don't want to automatically start application.

	public SPEEsbImpl(SPESummary spesummary) {
		super();
		this.spesummary = spesummary;
	}

	// Keys of properties that must be provided for our ESB queries.
	static final List<String> defaultKeys = (List<String>) Arrays.asList("tokenServer", "apiPrefix", "key", "secret",
			"scope", "x-ibm-client-id");

	/**************************
	 * get grades from data warehouse
	 *********************/

	protected HashMap<String, String> setupGetGradesCall(SPEProperties speproperties,String gradedAfterTime) {
		M_log.debug("spe properties: "+speproperties);
		HashMap<String,String> value = new HashMap<String,String>();
		value.putAll(speproperties.getEsb());
		value.putAll(speproperties.getGetgrades());
		value.put("gradedaftertime", gradedAfterTime);
		return value;
	}

	// Extend the properties list for the properties that get grade requests
	// require.
	public List<String> setupGetGradePropertyValues() {

		List<String> keys = new ArrayList<>(defaultKeys);

		// add gradedaftertime as a property for testing and defaulting.
		keys.add("gradedaftertime");
		keys.add("COURSEID");
		keys.add("ASSIGNMENTTITLE");

		return keys;
	}


	// Go get the SPE grades

	public WAPIResultWrapper getGradesViaESB(SPEProperties speproperties, String gradedAfterTime) throws GradeIOException {
		spesummary.setUseGradesLastRetrieved(gradedAfterTime);
		HashMap<String, String> values = setupGetGradesCall(speproperties,gradedAfterTime);

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("gradedAfterTime", values.get("gradedaftertime"));
		headers.put("x-ibm-client-id", values.get("x-ibm-client-id"));

		M_log.debug("spesummary: {}",spesummary);
		spesummary.setCourseId(values.get("COURSEID"));

		StringBuilder url = new StringBuilder();

		try {
			url.append(values.get("apiPrefix"))
			.append("/Unizin/data/CourseId/")
			.append(values.get("COURSEID"))
			.append("/AssignmentTitle/")
			.append(URLEncoder.encode(values.get("ASSIGNMENTTITLE"),"UTF-8"));

		} catch (UnsupportedEncodingException e) {
			M_log.error("encoding exception in getGrades"+e);
			throw(new GradeIOException("encoding exception in getGrades",e));
		}

		M_log.debug("getGrades: values:[" + values.toString() + "]");
		M_log.debug("getGrades: request url: [" + url.toString() + "]");
		M_log.debug("getGrades: headers: [" + headers.toString() + "]");

		WAPI wapi = new WAPI(values);

		WAPIResultWrapper wrappedResult = wapi.doRequest(url.toString(),headers);

		if (wrappedResult.getStatus() != HttpStatus.SC_OK && wrappedResult.getStatus() != HttpStatus.SC_NOT_FOUND) {
			String msg = "error in esb call to get grades: status: "+wrappedResult.getStatus()+" message: "+wrappedResult.getMessage();
			M_log.error(msg,wrappedResult.toString());
			throw(new GradeIOException(msg));
		}

		M_log.debug(wrappedResult.toJson());
		return wrappedResult;
	}

	/************************** put grades in MPathways *********************/

	// Add to the properties list the properties that put grade requests will
	// require.
	public List<String> setupPutGradePropertyValues() {

		List<String> keys = new ArrayList<>(defaultKeys);

		keys.add("UNIQNAME");
		keys.add("SCORE");

		return keys;
	}

	// Put a single grade in MPathways

	@Override
	public WAPIResultWrapper putGradeViaESB(HashMap<String, String> value) {
		HashMap<String, String> headers = new HashMap<String, String>();

		M_log.info("speproperties for esb: " + value.toString());

		headers.put("x-ibm-client-id", value.get("x-ibm-client-id"));

		StringBuilder url = new StringBuilder();
		url.append(value.get("apiPrefix"))
		.append("/Unizin/UniqName/")
		.append(value.get("UNIQNAME"))
		.append("/Score/")
		.append(value.get("SCORE"));

		M_log.debug("putGrades: value:[" + value.toString() + "]");
		M_log.debug("putGrades: request url: [" + url.toString() + "]");
		M_log.debug("putGrades: headers: [" + headers.toString() + "]");

		WAPI wapi = new WAPI(value);
		WAPIResultWrapper wrappedResult = null;

		// For testing allow skipping update of MPathways
		/// check for not true.
		M_log.debug("skipiGradeUpdate: {}",value.get("skipGradeUpdate"));
		if (value.get("skipGradeUpdate") == null || !"true".equals(value.get("skipGradeUpdate").toLowerCase())) {
			wrappedResult = wapi.doPutRequest(url.toString(), headers);
		} else {
			String msg = "{error: " + SKIP_GRADE_UPDATE + " for " + value.get("UNIQNAME") + "}";
			M_log.error("skip msg: " + msg);
			wrappedResult = new WAPIResultWrapper(WAPI.HTTP_UNKNOWN_ERROR, SKIP_GRADE_UPDATE, new JSONObject(msg));
		}

		M_log.info(wrappedResult.toJson());

		return wrappedResult;
	}


	/************************** Check that can access ESB successfully *********************/
	// All this does is renew the current token, but that is sufficient to check that the
	// ESB can be reached and do something requiring authorization.

	@Override
	public boolean verifyESBConnection(SPEProperties speproperties) {

		WAPI wapi = new WAPI(speproperties.getEsb());
		WAPIResultWrapper tokenRenewal = wapi.renewToken();
		Boolean success = false;

		if (tokenRenewal.getStatus() == HttpStatus.SC_OK) {
			success = true;
		} else {
			M_log.error("token renewal failed: status: {} message: {}", tokenRenewal.getStatus(),
					tokenRenewal.getMessage());
		}

		M_log.debug("verify esb: {}", success);
		return success;
	}

}
