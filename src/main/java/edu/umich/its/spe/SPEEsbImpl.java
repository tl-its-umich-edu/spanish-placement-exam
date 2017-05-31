package edu.umich.its.spe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

//@Configuration
@Component
//@ComponentScan

//@Bean
public class SPEEsbImpl implements SPEEsb {

	static final Logger M_log = LoggerFactory.getLogger(SPEEsbImpl.class);
	
	EsbProperties esb;
	
	// Properties that must be provided for our ESB queries.
	static final List<String> defaultKeys =  (List<String>) Arrays.asList("tokenServer","apiPrefix","key","secret","scope","x-ibm-client-id");

	// Add to properties list the properties that get grade requests will require.
	@Override
	public List<String> setupGetGradePropertyValues() {

		List<String> keys = new ArrayList<>(defaultKeys);
		
		// add gradedaftertime as a property is only for testing.
		keys.add("gradedaftertime");
		keys.add("COURSEID");
		keys.add("ASSIGNMENTTITLE");
		
		return keys;
	}
	
	 @Override
	public WAPIResultWrapper getGradesViaESB(HashMap<String, String> value) {
		HashMap<String,String> headers = new HashMap<String,String>();
	
		//headers.put("Accept", "json");
		headers.put("gradedaftertime",value.get("gradedaftertime"));
		headers.put("x-ibm-client-id",value.get("x-ibm-client-id"));
	
		//INFO: doRequest: https://apigw-tst.it.umich.edu:444/aa/sandbox/aa/Unizin/data/CourseId/159923/AssignmentTitle/'Spanish%20Placement%20Exam' headers: {x-ibm-client-id=ac54652b-5f59-4a9c-a39f-33f76567597b, GRADEDAFTERTIME=null}
		//--url "${URL_PREFIX}/Unizin/data/CourseId/${COURSEID}/AssignmentTitle/${ASSIGNMENTTITLE}" 
	
		StringBuilder url = new StringBuilder();
		url.append(value.get("apiPrefix"))
		.append("/Unizin/data/CourseId/")
		.append(value.get("COURSEID"))
		.append("/AssignmentTitle/")
		.append(value.get("ASSIGNMENTTITLE"));
	
		M_log.info("getGrades: value:["+value.toString()+"]");
		M_log.info("getGrades: request url: ["+url.toString()+"]");
		M_log.info("getGrades: headers: ["+headers.toString()+"]");
	
		WAPI wapi = new WAPI(value);
	
		WAPIResultWrapper wrappedResult = wapi.doRequest(url.toString(),headers);
	
		M_log.info(wrappedResult.toJson());
		return wrappedResult;
	}

//	curl --request PUT \
//	  --url https://apigw-tst.it.umich.edu:444/aa/sandbox/aa/Unizin/UniqName/REPLACE_UNIQNAME/Score/REPLACE_SCORE \
//	  --header 'accept: application/json' \
//	  --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
//	  --header 'x-ibm-client-id: REPLACE_THIS_KEY'
	
	// Add to properties list the properties that put grade requests will require.
	 public List<String> setupPutGradePropertyValues() {

		List<String> keys = new ArrayList<>(defaultKeys);

		keys.add("UNIQNAME");
		keys.add("SCORE");

		return keys;
	}
	
	 @Override
	public WAPIResultWrapper putGradeViaESB(HashMap<String, String> value) {
		HashMap<String,String> headers = new HashMap<String,String>();
		
		headers.put("x-ibm-client-id",value.get("x-ibm-client-id"));
	
		StringBuilder url = new StringBuilder();
		url.append(value.get("apiPrefix"))
		.append("/Unizin/UniqName/")
		.append(value.get("UNIQNAME"))
		.append("/Score/")
		.append(value.get("SCORE"));
	
		M_log.info("getGrades: value:["+value.toString()+"]");
		M_log.info("getGrades: request url: ["+url.toString()+"]");
		M_log.info("getGrades: headers: ["+headers.toString()+"]");
	
		WAPI wapi = new WAPI(value);
		
		WAPIResultWrapper wrappedResult = wapi.doPutRequest(url.toString(),headers);
	
		M_log.info(wrappedResult.toJson());
		return wrappedResult;
	}
	
}
