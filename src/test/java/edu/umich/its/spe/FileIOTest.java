package edu.umich.its.spe;

//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.*;
//import static org.mockito.Matchers.contains;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

@SpringBootTest
public class FileIOTest {

		  @Rule
		  public TemporaryFolder folder= new TemporaryFolder();

//		  @Test
//		  public void testUsingTempFolder() throws IOException {
//		      File createdFile= folder.newFile("myfile.txt");
//		      File createdFolder= folder.newFolder("subfolder");
//		      // ...
//		     }

	private static final Logger M_log = LoggerFactory.getLogger(FileIOTest.class);

	FileIO fio;
	SPEProperties props;

	final String date20170401 = "2017-04-01 18:00:00";

	@Before
	public void setUp() throws Exception {
		 fio = new FileIO();
		 props = new SPEProperties();
	}

	@After
	public void tearDown() throws Exception {
	}

	// set explicit value for sub piece of SPEProperties.
	protected HashMap<String, String> setSPEProperty(HashMap<String,String> subHash , String propertyName, String propertyValue) {
		// make sure there is a hash to use.
		if (subHash == null) {
			subHash = new HashMap<String,String>();
		}
		subHash.put(propertyName, propertyValue);
		return subHash;
	}

	/**** get grades ***/

	// @throws GradeIOException
	@Test(expected=GradeIOException.class)
	public void testGetGradesNullArgs() throws GradeIOException {
		WAPIResultWrapper result = fio.getGradesVia(null,null);
		fail("should have thrown exception");
	}

	@Test(expected=GradeIOException.class)
	public void testGetGradesNullProperties() throws GradeIOException {
		WAPIResultWrapper result = fio.getGradesVia(null,"HAPPY");
		fail("should have thrown exception");
	}

	@Test(expected=GradeIOException.class)
	public void testGetGradesNullTime() throws GradeIOException {
		WAPIResultWrapper result = fio.getGradesVia(props,null);
		fail("should have thrown exception");
	}

	//@Test(expected=GradeIOException.class)
	public void testGetGradesNot() throws GradeIOException {
		WAPIResultWrapper result = fio.getGradesVia(props,null);
		fail("should have thrown exception");
	}

//	@Test(expected=GradeIOException.class)
//	public void testGetGradesFromMissingFile() throws GradeIOException {
//		WAPIResultWrapper result = FileIO.getGradesFileStatic(props,date20170401);
//		fail("should have thrown exception");
//	}

	@Ignore

	@Test
	// deal with empty file
	public void testGetGradesFromEmptyFile() throws IOException, GradeIOException {

		// setup the empty file
		String fileName = "myfile.txt";
		File newEmptyFile = folder.newFile(fileName);
		String fullFileName = newEmptyFile.getPath();

		// Can override output file name for debugging.
		// If file isn't empty (e.g. reruns) then tests may fail.
		//fullFileName = "/tmp/SPE_A.txt";

		// add property value to esb properties
		String esbPropertyName = "getGradeIO";
		props.setIo(setSPEProperty(props.getIo(),esbPropertyName, fullFileName));

		WAPIResultWrapper wrapper = FileIO.getGradesFileStatic(props,date20170401);

		M_log.debug("emptyFile: [{}]",wrapper.toJson());
		assertEquals("read the file",200,wrapper.getStatus());
		assertEquals("got empty json object","{}",wrapper.getResult().toString());
	}

//	@Ignore
//	@Test
//	// deal with empty file
//	public void OLDtestGetGradesFromEmptyFile() throws IOException, GradeIOException {
//
//		// setup the empty file
//		String fileName = "myfile.txt";
//		File newEmptyFile = folder.newFile(fileName);
//		String fullFileName = newEmptyFile.getPath();
//		// add property value to esb properties
//		String esbPropertyName = "getGradeIO";
//		props.setEsb(setSPEProperty(props.getEsb(),esbPropertyName, fullFileName));
//
//		WAPIResultWrapper wrapper = FileIO.getGradesFileStatic(props,date20170401);
//
//		M_log.debug("emptyFile: [{}]",wrapper.toJson());
//		assertEquals("read the file",200,wrapper.getStatus());
//		assertEquals("got empty json object","{}",wrapper.getResult().toString());
//	}

	// wapi response for no classes{"ErrorResponse":{"responseCode":404,"responseDescription":"Please specify a valid search criteria"}}

	// speesb: {"Meta":{"Message":"COMPLETED","httpStatus":404},"Result":{"ErrorResponse":{"responseDescription":"Please specify a valid search criteria","responseCode":404}}}



	// example of put request
	//{"Meta":{"Message":"COMPLETED","httpStatus":200},"Result":{"putPlcExamScoreResponse":{"putPlcExamScoreResponse":{"Status":"SUCCESS","Form":7,"ID":"mattgra"},"@schemaLocation":"http://mais.he.umich.edu/schemas/putPlcExamScoreResponse.v1 http://csqa9ib.dsc.umich.edu/PSIGW/PeopleSoftServiceListeningConnector/putPlcExamScoreResponse.v1.xsd"}}}
	// example of get request
	//{"Meta":{"Message":"COMPLETED","httpStatus":200},"Result":{"AssignmentInfo":{"AssignmentData":[{"Score":22,"Published_Score":22,"User_Id":-365167227025976400,"Finished_At":"2017-06-26T12:09:29.107-04:00","Unique_Name":"kylepc"},{"Score":7,"Published_Score":7,"User_Id":380993646795706100,"Finished_At":"2017-06-26T09:41:25.171-04:00","Unique_Name":"savanndo"},{"Score":13,"Published_Score":13,"User_Id":-542332559889491260,"Finished_At":"2017-06-26T12:27:55.223-04:00","Unique_Name":"alissach"},{"Score":1416.3,"Published_Score":1416.3,"User_Id":369729299211462300,"Finished_At":"2017-06-26T13:53:55.117-04:00","Unique_Name":"sjsabuda"},{"Score":1421,"Published_Score":1421,"User_Id":-458875702890985000,"Finished_At":"2017-05-11T17:28:50.826-04:00","Unique_Name":"ferromic"},{"Score":4539,"Published_Score":4539,"User_Id":-450051484942599740,"Finished_At":"2017-05-11T20:53:59.367-04:00","Unique_Name":"kjacobso"},{"Score":1315,"Published_Score":1315,"User_Id":-397239884492243840,"Finished_At":"2017-05-11T17:16:08.126-04:00","Unique_Name":"xianyuel"},{"Score":5,"Published_Score":5,"User_Id":61686575085700130,"Finished_At":"2017-06-26T13:31:04.301-04:00","Unique_Name":"874cd94751fd96b5ef96ae9e5ef3c4c08a57e49c"},{"Score":22,"Published_Score":22,"User_Id":356779361291374850,"Finished_At":"2017-06-26T15:02:10.632-04:00","Unique_Name":"shardge"},{"Score":2337.2,"Published_Score":2337.2,"User_Id":-7602112577716947,"Finished_At":"2017-06-26T16:07:39.942-04:00","Unique_Name":"mattgra"},{"Score":19,"Published_Score":19,"User_Id":357695332757381200,"Finished_At":"2017-06-26T12:07:51.271-04:00","Unique_Name":"emwelch"},{"Score":1327,"Published_Score":1327,"User_Id":-99126766022424480,"Finished_At":"2017-05-12T16:42:40.891-04:00","Unique_Name":"ziegler"},{"Score":1419.2,"Published_Score":1419.2,"User_Id":-33819192538359004,"Finished_At":"2017-05-12T14:56:51.392-04:00","Unique_Name":"samoz"},{"Score":1717.7,"Published_Score":1717.7,"User_Id":-188543008998595500,"Finished_At":"2017-05-11T18:09:51.795-04:00","Unique_Name":"demonner"},{"Score":9,"Published_Score":9,"User_Id":508663127472936600,"Finished_At":"2017-06-26T16:27:53.736-04:00","Unique_Name":"itarpeh"},{"Score":1,"Published_Score":1,"User_Id":-182486954737622560,"Finished_At":"2017-06-26T16:05:57.523-04:00","Unique_Name":"rutag"},{"Score":1,"Published_Score":1,"User_Id":247378583477711140,"Finished_At":"2017-06-26T13:09:39.653-04:00","Unique_Name":"crouch"},{"Score":11,"Published_Score":11,"User_Id":427663952282038460,"Finished_At":"2017-05-12T10:41:37.061-04:00","Unique_Name":"jdiehl"},{"Score":1026,"Published_Score":1026,"User_Id":-173066613632549300,"Finished_At":"2017-05-16T11:11:41.993-04:00","Unique_Name":"jennlove"},{"Score":1326,"Published_Score":1326,"User_Id":80295732322489020,"Finished_At":"2017-05-16T14:45:14.833-04:00","Unique_Name":"otchiu"},{"Score":1519,"Published_Score":1519,"User_Id":-143657466902917380,"Finished_At":"2017-06-22T12:21:25.056-04:00","Unique_Name":"rkorosso"}]}}}

	// get grades
	 String testZeroGrade = "{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":404},\"Result\":{\"ErrorResponse\":{\"responseDescription\":\"Please specify a valid search criteria\",\"responseCode\":404}}}";
	 String testFiveGrade = "{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":200},\"Result\":{\"AssignmentInfo\":{\"AssignmentData\":[{\"Score\":22,\"Published_Score\":22,\"User_Id\":-365167227025976400,\"Finished_At\":\"2017-06-26T12:09:29.107-04:00\",\"Unique_Name\":\"kylepc\"},{\"Score\":7,\"Published_Score\":7,\"User_Id\":380993646795706100,\"Finished_At\":\"2017-06-26T09:41:25.171-04:00\",\"Unique_Name\":\"savanndo\"},{\"Score\":13,\"Published_Score\":13,\"User_Id\":-542332559889491260,\"Finished_At\":\"2017-06-26T12:27:55.223-04:00\",\"Unique_Name\":\"alissach\"},{\"Score\":1416.3,\"Published_Score\":1416.3,\"User_Id\":369729299211462300,\"Finished_At\":\"2017-06-26T13:53:55.117-04:00\",\"Unique_Name\":\"sjsabuda\"},{\"Score\":1421,\"Published_Score\":1421,\"User_Id\":-458875702890985000,\"Finished_At\":\"2017-05-11T17:28:50.826-04:00\",\"Unique_Name\":\"ferromic\"}]}}}";
	 String testOneGrade = "{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":200},\"Result\":{\"AssignmentInfo\":{\"AssignmentData\":["
			 +"{\"Score\":22,\"Published_Score\":22,\"User_Id\":-365167227025976400,\"Finished_At\":\"2017-06-26T12:09:29.107-04:00\",\"Unique_Name\":\"kylepc\"}"
			 + "]}}}";


	String getScoresExample = "{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":200},\"Result\":{\"AssignmentInfo\":{\"AssignmentData\":[{\"Score\":22,\"Published_Score\":22,\"User_Id\":-365167227025976400,\"Finished_At\":\"2017-06-26T12:09:29.107-04:00\",\"Unique_Name\":\"kylepc\"},{\"Score\":7,\"Published_Score\":7,\"User_Id\":380993646795706100,\"Finished_At\":\"2017-06-26T09:41:25.171-04:00\",\"Unique_Name\":\"savanndo\"},{\"Score\":13,\"Published_Score\":13,\"User_Id\":-542332559889491260,\"Finished_At\":\"2017-06-26T12:27:55.223-04:00\",\"Unique_Name\":\"alissach\"},{\"Score\":1416.3,\"Published_Score\":1416.3,\"User_Id\":369729299211462300,\"Finished_At\":\"2017-06-26T13:53:55.117-04:00\",\"Unique_Name\":\"sjsabuda\"},{\"Score\":1421,\"Published_Score\":1421,\"User_Id\":-458875702890985000,\"Finished_At\":\"2017-05-11T17:28:50.826-04:00\",\"Unique_Name\":\"ferromic\"},{\"Score\":4539,\"Published_Score\":4539,\"User_Id\":-450051484942599740,\"Finished_At\":\"2017-05-11T20:53:59.367-04:00\",\"Unique_Name\":\"kjacobso\"},{\"Score\":1315,\"Published_Score\":1315,\"User_Id\":-397239884492243840,\"Finished_At\":\"2017-05-11T17:16:08.126-04:00\",\"Unique_Name\":\"xianyuel\"},{\"Score\":5,\"Published_Score\":5,\"User_Id\":61686575085700130,\"Finished_At\":\"2017-06-26T13:31:04.301-04:00\",\"Unique_Name\":\"874cd94751fd96b5ef96ae9e5ef3c4c08a57e49c\"},{\"Score\":22,\"Published_Score\":22,\"User_Id\":356779361291374850,\"Finished_At\":\"2017-06-26T15:02:10.632-04:00\",\"Unique_Name\":\"shardge\"},{\"Score\":2337.2,\"Published_Score\":2337.2,\"User_Id\":-7602112577716947,\"Finished_At\":\"2017-06-26T16:07:39.942-04:00\",\"Unique_Name\":\"mattgra\"},{\"Score\":19,\"Published_Score\":19,\"User_Id\":357695332757381200,\"Finished_At\":\"2017-06-26T12:07:51.271-04:00\",\"Unique_Name\":\"emwelch\"},{\"Score\":1327,\"Published_Score\":1327,\"User_Id\":-99126766022424480,\"Finished_At\":\"2017-05-12T16:42:40.891-04:00\",\"Unique_Name\":\"ziegler\"},{\"Score\":1419.2,\"Published_Score\":1419.2,\"User_Id\":-33819192538359004,\"Finished_At\":\"2017-05-12T14:56:51.392-04:00\",\"Unique_Name\":\"samoz\"},{\"Score\":1717.7,\"Published_Score\":1717.7,\"User_Id\":-188543008998595500,\"Finished_At\":\"2017-05-11T18:09:51.795-04:00\",\"Unique_Name\":\"demonner\"},{\"Score\":9,\"Published_Score\":9,\"User_Id\":508663127472936600,\"Finished_At\":\"2017-06-26T16:27:53.736-04:00\",\"Unique_Name\":\"itarpeh\"},{\"Score\":1,\"Published_Score\":1,\"User_Id\":-182486954737622560,\"Finished_At\":\"2017-06-26T16:05:57.523-04:00\",\"Unique_Name\":\"rutag\"},{\"Score\":1,\"Published_Score\":1,\"User_Id\":247378583477711140,\"Finished_At\":\"2017-06-26T13:09:39.653-04:00\",\"Unique_Name\":\"crouch\"},{\"Score\":11,\"Published_Score\":11,\"User_Id\":427663952282038460,\"Finished_At\":\"2017-05-12T10:41:37.061-04:00\",\"Unique_Name\":\"jdiehl\"},{\"Score\":1026,\"Published_Score\":1026,\"User_Id\":-173066613632549300,\"Finished_At\":\"2017-05-16T11:11:41.993-04:00\",\"Unique_Name\":\"jennlove\"}]}}}";


//	@Test
//	public void testGetWrappedGradesFromString() {
//		M_log.error("getScoresExample: [{}]",getScoresExample);
//
//
//		//new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"returned from file: "+fileName,new JSONObject(gradesString));
//		WAPIResultWrapper result = new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"grades from string",new JSONObject(getScoresExample));
//		M_log.error("wrapped getScoresExample: "+result.toJson());
//		M_log.error("wrapped getScoresExample status: "+result.getStatus());
//		M_log.error("wrapped getScoresExample result: "+result.getResult().toString());
//		assertEquals("successful json parse from string",WAPI.HTTP_SUCCESS,result.getStatus());
//		fail("under development");
//	}

//	@Test
//	public void testGetGradesFromNotFound() {
//		M_log.debug("testGGFNF: testZeroGrade: {}",testZeroGrade);
//		fail("not yet implemented");
//	}


	@Test
	public void testGetGrades1() {

		JSONObject gradeObj = new JSONObject(testOneGrade);
		gradeObj = gradeObj.getJSONObject("Result");
		WAPIResultWrapper result = new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"grades from string",gradeObj);

		JSONArray grades = SPEMaster.parseCanvasAssignmentJSON(result.getResult().toString());

		assertEquals("GG1: successful json parse from string",WAPI.HTTP_SUCCESS,result.getStatus());
		assertEquals("GG1: got grade array length 1",1,grades.length());
	}


	@Test
	public void testGetGrades5() {

		JSONObject gradeObj = new JSONObject(testFiveGrade);
		gradeObj = gradeObj.getJSONObject("Result");
		WAPIResultWrapper result = new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"grades from string",gradeObj);

		JSONArray grades = SPEMaster.parseCanvasAssignmentJSON(result.getResult().toString());

		assertEquals("successful json parse from string",WAPI.HTTP_SUCCESS,result.getStatus());
		assertEquals("got grade array length 5",5,grades.length());
	}


	/********* put grades ***********/

	@Test
	public void testPutGradeViaESBOne() throws IOException {

		//HashMap<?, ?> user = SPEMaster.createGradeMap("1", "barney");

		String useFileName = "putGradesOne";
		String fullFileName = folder.getRoot()+useFileName;

		// Can override output file name for debugging.
		// If file isn't empty (e.g. reruns) then tests may fail.
		//fullFileName = "/tmp/SPE_put.txt";

		M_log.debug("fullFileName putOne: [{}]",fullFileName);

		// add property value to esb properties
		String esbPropertyName = "putGradeIO";
		props.setIo(setSPEProperty(props.getIo(),esbPropertyName, fullFileName));

		writeUserGradeToFile(props,"1", "barney");

		M_log.error("output file: {}",fullFileName);
		File file = new File(fullFileName);
		String gradesString = FileUtils.readFileToString(file,"UTF-8");
		M_log.error("putGradeOutput: {}",gradesString);
		//System.out.println("putGradeOutput: "+gradesString);
	}

	@Test
	public void testPutGradeViaESBSeveral() throws IOException {

		String useFileName = "putGradesOne";
		String fullFileName = folder.getRoot()+useFileName;

		// Can override output file name for debugging.
		// If file isn't empty (e.g. reruns) then tests may fail.
		//fullFileName = "/tmp/SPE_put_N.txt";

		M_log.debug("fullFileName putOne: [{}]",fullFileName);

		// add property value to esb properties
		String esbPropertyName = "putGradeIO";
		props.setIo(setSPEProperty(props.getIo(),esbPropertyName, fullFileName));

		HashMap<?, ?> user;

		writeUserGradeToFile(props ,"1", "barney");
		writeUserGradeToFile(props ,"2", "rubble");
		writeUserGradeToFile(props ,"3", "wilma");

		M_log.error("output file was: {}",fullFileName);
		File file = new File(fullFileName);
		String gradesString = FileUtils.readFileToString(file,"UTF-8");
		M_log.error("putGradeOutput: {}",gradesString);

		int len = gradesString.split(System.getProperty("line.separator")).length;

		assertEquals("multiple lines",3,len);

		assertThat("multiple grades",gradesString, containsString("barney"));
		assertThat("multiple grades",gradesString, containsString("rubble"));
		assertThat("multiple grades",gradesString, containsString("wilma"));
	}

	public void writeUserGradeToFile(SPEProperties props,String grade, String name) {
		HashMap<?, ?> user;
		user = SPEMaster.createGradeMap(grade, name);
		WAPIResultWrapper result = FileIO.putGradeFileStatic(props, user);
		M_log.error("putGradeOne: user: {} json: {}",name,result.toJson());
	}

	@Ignore
	@Test
	public void testVerifyESBConnection() {
		fail("Not yet implemented");
	}

}
