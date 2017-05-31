package edu.umich.its.spe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecutionListener;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;

//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
//import org.springframework.batch.item.database.JdbcBatchItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

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
// TODO: configuration should separate security and properties.
// TODO: test error handling
// TODO: test orcestration

//@EnableBatchProcessing
//@SpringBootApplication
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@Component
public class SPEMaster {

	static final Logger M_log = LoggerFactory.getLogger(SPEMaster.class);

	public SPEMaster() {
		super();
		M_log.error("starting SPEMaster");
		M_log.error("SPEMaster: this: "+this);
	}

	public SPEMaster(String[] args) {
		super();
		M_log.error("starting SPEMaster with args");
		M_log.error("args:" + args.toString());
	}
	
	@Autowired
	private SPEEsb speesb;
	
	@Autowired
	private PersistString persistString;
	
	@Autowired
	private EsbProperties esb;

//    public static void main(String[] args){
//       //SpringApplication.run(SPEMaster.class, args);
//       new SpringApplicationBuilder(SPEMaster.class)
//       .web(false).run("HOWDY");
//       //.web(false).run(args);
//       
//       M_log.error("in SPEMaster:main");
//       //SPEMaster.orchestrator();
//        //new SpringApplicationBuilder(SPEMaster.class)
//        //.web(false).run(args);
//   // 	SpringApplication(SPEMaster.class).setWebEnvironment(false).run(args);
//    	//SpringApplication.run(SPEMaster.class, args).setWebEnvironment(false);
//    }
//
//	public void init() {
//		M_log.error("SPEMaster: init");
//	}
    
	/* 
	 * Run sanity checks to make sure can talk to ESB and get data.
	 */
	public boolean verify() {
		// TODO: implement verify by renewing access token.
		return false;
	}

	/*
	 * Get grades as JSON string.  Only grades after the gradeAfterTime
	 * timestamp will be returned. An empty result is reasonable.
	 * The format of the time stamp is: 2017-04-01 18:00:00
	 */ 

	public String getSPEGrades(String gradedAfterTime) {
		// TODO: implement call to get grades using last update date.

		HashMap<String, String> values = setupGradesCall(gradedAfterTime);
		//WAPIResultWrapper grades = speesb.getGradesViaESB(values);
		WAPIResultWrapper grades = speesb.getGradesViaESB(esb.getEsb());
		return grades.getResult().toString();
		//return parseCanvasAssignmentJSON(grades.getResult().toString());
	}

	//HashMap<String,String> value = WAPI.getPropertiesInGroup(readTestProperties(), apiType,keys);
//	public static HashMap<String,String> getPropertiesInGroup(Properties props, String group, List<String> propertyNames) {
//		
//		HashMap<String, String> value = new HashMap<String, String>();
//		for(String key: propertyNames) {
//			String propertyValue = props.getProperty(group + "." +key);
//			if (propertyValue != null) {
//				value.put(key, propertyValue);
//			}
//		}
//		return value;
//	}
	
	// setup the values from the properties file.
	protected HashMap<String, String> setupGradesCall(String gradedAfterTime) {
		M_log.error("esb properties: "+esb);
		//Hashkeys = setupGetGradePropertyValues(esb);
		//List<String> keys = speesb.setupGetGradePropertyValues();
		//HashMap<String,String> value = WAPI.getPropertiesInGroup(esb, apiType,keys);
		//WAPIResultWrapper wrappedResult = speesb.getGradesViaESB(esb);
		return null;
	}

	/* 
	 * Put the grade for this user into MPathways.
	 */
	public boolean putSPEGrade(HashMap<?, ?> user) {
		// TODO: implement put of grades.
		//return false;
		M_log.error("user grade: {}",user);
		return true;
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
		
		M_log.error("Start orchestrator");

		//// Get the relevant grades.
		String lastUpdateTime = getLastGradeTransferTime();
		String assignmentsFromDW = getSPEGrades(lastUpdateTime);

		//// Extract grades from JSON the grades for insertion		
		ArrayList<HashMap<String,String>> SPEgradeMaps = null;
		
		try {
			SPEgradeMaps = convertSPEGradesFromDataWarehouseJSON(assignmentsFromDW);
		} catch (JSONException e) {
			M_log.error("exception in converting grades: "+e);
			e.printStackTrace();
		}

		//// Insert the grades.
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
	 * Use PersistString class to get a string with a timestamp representing the 
	 * last time the script updated the grades.
	 * 
	 * If the value isn't available or is corrupt use a default value.  We'll be careful
	 * but won't panic about only submitting grades once.
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

	// convert a single JSON version of an assignment to a grademap.
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
