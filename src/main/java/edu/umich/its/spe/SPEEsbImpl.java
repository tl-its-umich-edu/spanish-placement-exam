package edu.umich.its.spe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Qualifier("ESBIO")

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
	// be useful in testing when don't want to require using Spring injection.

	public SPEEsbImpl(SPESummary spesummary) {
		super();
		this.spesummary = spesummary;
	}

	/******************** Get grades from ESB (Unizin Data Warehouse) ******************/

	protected HashMap<String, String> setupGetGradesCall(SPEProperties speproperties,String gradedAfterTime) {
		M_log.debug("spe properties: "+speproperties);
		HashMap<String,String> value = new HashMap<String,String>();
		value.putAll(speproperties.getIo());
		value.putAll(speproperties.getGetgrades());
		value.put("gradedaftertime", gradedAfterTime);
		return value;
	}

	public WAPIResultWrapper getGradesVia(SPEProperties speproperties, String gradedAfterTime) throws GradeIOException {

		// gradedAfterTime must be in date time in UTC but without explicit timezone information.
		// That is the format expected by the ESB API.

		M_log.debug("incoming gradedAfterTime: {}",gradedAfterTime);
		gradedAfterTime = gradedAfterTime.replaceFirst("T"," ");
		M_log.info("use gradedAfterTime: {}",gradedAfterTime);

		HashMap<String, String> values = setupGetGradesCall(speproperties,gradedAfterTime);
		M_log.debug("getGrades: values:[" + values.toString() + "]");

		spesummary.setUseTestLastTakenTime(SPEUtils.normalizeStringTimestamp(gradedAfterTime));
		spesummary.setCourseId(values.get("COURSEID"));

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("gradedAfterTime", values.get("gradedaftertime"));
		headers.put("x-ibm-client-id", values.get("x-ibm-client-id"));
		M_log.debug("getGrades: headers: [" + headers.toString() + "]");

		String url = formatGetURLTemplate(values);
		M_log.debug("getGrades: request url: [" + url.toString() + "]");

		WAPI wapi = new WAPI(values);

		WAPIResultWrapper wrappedResult = wapi.doRequest(url.toString(),headers);

		if (wrappedResult.getStatus() != HttpStatus.SC_OK && wrappedResult.getStatus() != HttpStatus.SC_NOT_FOUND) {
			String msg = "error in esb call to get grades: status: "+wrappedResult.getStatus()+" message: "
						+wrappedResult.getMessage()
						+" URL: "+ url.toString()
						+" Headers: "+headers;
			M_log.error(msg,wrappedResult.toString());
			throw(new GradeIOException(msg));
		}

		if(M_log.isDebugEnabled()) {
			M_log.debug(wrappedResult.toJson());
		}

		return wrappedResult;
	}

	/* Put together the URL to get grades */

	// Ensure the assignment title is ok and then really put the url together
	public String formatGetURLTemplate(HashMap<String, String> values) throws GradeIOException {
		try {
			values.put("ASSIGNMENTTITLE",
					URLEncoder.encode(values.get("ASSIGNMENTTITLE"),"UTF-8").replaceAll("\\+", "%20"));
		} catch (UnsupportedEncodingException e) {
			M_log.error("encoding exception in getGrades"+e);
			throw(new GradeIOException("encoding exception in getGrades",e));
		}
		return formatGetURLTemplateSimple(values);
	}

	// Template is set as an external io property.
	public String formatGetURLTemplateSimple(HashMap<String, String> values) throws GradeIOException {
		return String.format(
				values.get("esbGetScoreTemplate"),
				new Object[]{
						values.get("apiPrefix"),
						values.get("COURSEID"),
						values.get("ASSIGNMENTTITLE")
				});
	}


	/************************** put grades in MPathways *********************/

	protected HashMap<String, String> setupPutGradeCall(SPEProperties speproperties,HashMap<?, ?> user) {
		M_log.debug("spe properties: "+speproperties);
		M_log.debug("sPGC: user: {}",user);

		HashMap<String,String> values = new HashMap<String,String>();
		values.putAll(speproperties.getIo());

		// if no user then use default values (for testing).
		if (user == null || user.isEmpty()) {
			values.putAll(speproperties.getPutgrades());
		}
		else {
			values.put("SCORE",(String) user.get("Score"));
			values.put("UNIQNAME",(String) user.get("Unique_Name"));
		}

		return values;
	}

	// Put a single score in MPathways

	@Override
	public WAPIResultWrapper putGradeVia(SPEProperties speproperties,HashMap<?, ?> user) {
		M_log.debug("user to update: " + user.toString());

		HashMap<String,String> values = setupPutGradeCall(speproperties,user);
		M_log.debug("putGrades: value:[" + values.toString() + "]");

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("x-ibm-client-id", values.get("x-ibm-client-id"));
		M_log.debug("putGrades: headers: [" + headers.toString() + "]");

		String url = formatPutURLTemplate(values);
		M_log.debug("putGrades: request url: [" + url.toString() + "]");

		WAPI wapi = new WAPI(values);
		WAPIResultWrapper wrappedResult = null;

		wrappedResult = wapi.doPutRequest(url.toString(), headers);

		M_log.debug("putGrade result: {}",wrappedResult.toJson());

		return wrappedResult;
	}

	// Template is set as an external io property.
	public String formatPutURLTemplate(HashMap<String, String> values) {;
		return String.format(
				values.get("esbPutScoreTemplate"),
				new Object[]{
						values.get("apiPrefix"),
						values.get("UNIQNAME"),
						values.get("SCORE")
				});
	}

	/************************** Check that can access ESB successfully *********************/
	// All this does is renew the current token, but that is sufficient to check that the
	// ESB can be reached and do something requiring authorization.

	@Override
	public boolean verifyConnection(SPEProperties speproperties) {

		WAPI wapi = new WAPI(speproperties.getIo());
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
