package edu.umich.its.spe;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SPEMasterProcessGradeTest {

	static final Logger log = LoggerFactory.getLogger(SPEMasterProcessGradeTest.class);

	String esbAssignmentJSON = "{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":200},\"Result\":{\"AssignmentInfo\":{\"AssignmentData\":[{\"Score\":1721.5,\"Published_Score\":1721.5,\"User_Id\":137734728828958800,\"Finished_At\":\"2017-04-26T14:38:46.809-04:00\",\"Unique_Name\":\"studentc\"},{\"Score\":1320,\"Published_Score\":1320,\"User_Id\":-296951127543716860,\"Finished_At\":\"2017-04-26T14:10:54.188-04:00\",\"Unique_Name\":\"studentd\"},{\"Score\":107.2,\"Published_Score\":107.2,\"User_Id\":80295732322489020,\"Finished_At\":\"2017-04-27T15:34:24.638-04:00\",\"Unique_Name\":\"otchiu\"},{\"Score\":1520.5,\"Published_Score\":1520.5,\"User_Id\":271815845968802940,\"Finished_At\":\"2017-04-26T15:03:53.929-04:00\",\"Unique_Name\":\"studenta\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":247378583477711140,\"Finished_At\":\"2017-04-26T11:52:42.184-04:00\",\"Unique_Name\":\"crouch\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":-527815610591606100,\"Finished_At\":\"2017-04-28T16:02:20.856-04:00\",\"Unique_Name\":\"2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21\"},{\"Score\":1613.3,\"Published_Score\":1613.3,\"User_Id\":392571521045807040,\"Finished_At\":\"2017-04-25T15:42:26.797-04:00\",\"Unique_Name\":\"studentb\"},{\"Score\":1220.5,\"Published_Score\":1220.5,\"User_Id\":173106618602968540,\"Finished_At\":\"2017-04-27T14:38:28.160-04:00\",\"Unique_Name\":\"zewang\"}]}}}";
	String esbAssignmentResultJSON = "{\"AssignmentInfo\":{\"AssignmentData\":[{\"Score\":1721.5,\"Published_Score\":1721.5,\"User_Id\":137734728828958800,\"Finished_At\":\"2017-04-26T14:38:46.809-04:00\",\"Unique_Name\":\"studentc\"},{\"Score\":1320,\"Published_Score\":1320,\"User_Id\":-296951127543716860,\"Finished_At\":\"2017-04-26T14:10:54.188-04:00\",\"Unique_Name\":\"studentd\"},{\"Score\":107.2,\"Published_Score\":107.2,\"User_Id\":80295732322489020,\"Finished_At\":\"2017-04-27T15:34:24.638-04:00\",\"Unique_Name\":\"otchiu\"},{\"Score\":1520.5,\"Published_Score\":1520.5,\"User_Id\":271815845968802940,\"Finished_At\":\"2017-04-26T15:03:53.929-04:00\",\"Unique_Name\":\"studenta\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":247378583477711140,\"Finished_At\":\"2017-04-26T11:52:42.184-04:00\",\"Unique_Name\":\"crouch\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":-527815610591606100,\"Finished_At\":\"2017-04-28T16:02:20.856-04:00\",\"Unique_Name\":\"2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21\"},{\"Score\":1613.3,\"Published_Score\":1613.3,\"User_Id\":392571521045807040,\"Finished_At\":\"2017-04-25T15:42:26.797-04:00\",\"Unique_Name\":\"studentb\"},{\"Score\":1220.5,\"Published_Score\":1220.5,\"User_Id\":173106618602968540,\"Finished_At\":\"2017-04-27T14:38:28.160-04:00\",\"Unique_Name\":\"zewang\"}]}}";
	String assignmentListJSON = "[{\"Score\":1721.5,\"Published_Score\":1721.5,\"User_Id\":137734728828958800,\"Finished_At\":\"2017-04-26T14:38:46.809-04:00\",\"Unique_Name\":\"studentc\"},{\"Score\":1320,\"Published_Score\":1320,\"User_Id\":-296951127543716860,\"Finished_At\":\"2017-04-26T14:10:54.188-04:00\",\"Unique_Name\":\"studentd\"},{\"Score\":107.2,\"Published_Score\":107.2,\"User_Id\":80295732322489020,\"Finished_At\":\"2017-04-27T15:34:24.638-04:00\",\"Unique_Name\":\"otchiu\"},{\"Score\":1520.5,\"Published_Score\":1520.5,\"User_Id\":271815845968802940,\"Finished_At\":\"2017-04-26T15:03:53.929-04:00\",\"Unique_Name\":\"studenta\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":247378583477711140,\"Finished_At\":\"2017-04-26T11:52:42.184-04:00\",\"Unique_Name\":\"crouch\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":-527815610591606100,\"Finished_At\":\"2017-04-28T16:02:20.856-04:00\",\"Unique_Name\":\"2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21\"},{\"Score\":1613.3,\"Published_Score\":1613.3,\"User_Id\":392571521045807040,\"Finished_At\":\"2017-04-25T15:42:26.797-04:00\",\"Unique_Name\":\"studentb\"},{\"Score\":1220.5,\"Published_Score\":1220.5,\"User_Id\":173106618602968540,\"Finished_At\":\"2017-04-27T14:38:28.160-04:00\",\"Unique_Name\":\"zewang\"}]";

	String assignmentJSON_A = "{\"Score\":1721.5,\"Published_Score\":1721.5,\"User_Id\":137734728828958800,\"Finished_At\":\"2017-04-26T14:38:46.809-04:00\",\"Unique_Name\":\"studentc\"}";
	String assignmentJSON_B = "{\"Score\":1320,\"Published_Score\":1320,\"User_Id\":-296951127543716860,\"Finished_At\":\"2017-04-26T14:10:54.188-04:00\",\"Unique_Name\":\"studentd\"}";

	SPEMaster spe = null;

	@Before
	public void setUp() throws Exception {
		spe = new SPEMaster();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertSPEGradesFromDataWarehouseJSON() throws JSONException{
		ArrayList<HashMap<String,String>> gradedFromDW = spe.convertSPEGradesFromDataWarehouseJSON(esbAssignmentResultJSON);

		assertEquals("correct number of assignments",8,gradedFromDW.size());
		
		for(HashMap<String, String> e : gradedFromDW) {
			assertThat("proper type",e,instanceOf(HashMap.class));
			assertThat("Score exists and is string",e.get("Score"),instanceOf(String.class));
			assertThat("Unique_Name exists and is string",e.get("Unique_Name"),instanceOf(String.class));
		}
	}

	@Test
	public void testConvertAssignmentAToGradeMap() throws JSONException {
		JSONObject jo = new JSONObject(assignmentJSON_A);
		HashMap<String, String> gradeMap = SPEMaster.convertAssignmentToGradeMap(jo);
		assertEquals("score:","1721.5",gradeMap.get("Score"));
		assertEquals("uniqname:","studentc",gradeMap.get("Unique_Name"));
	}

	@Test
	public void testConvertAssignmentBToGradeMap() throws JSONException {
		JSONObject jo = new JSONObject(assignmentJSON_B);
		HashMap<String, String> gradeMap = SPEMaster.convertAssignmentToGradeMap(jo);
		assertEquals("score:","1320",gradeMap.get("Score"));
		assertEquals("uniqname:","studentd",gradeMap.get("Unique_Name"));
	}

	@Test
	public void testJSONObjectParse() throws JSONException {
		// verify esb JSON is in expected format.
		JSONObject jo = new JSONObject(esbAssignmentJSON);
		JSONObject ja = jo.getJSONObject("Result");
		JSONObject jAI = ja.getJSONObject("AssignmentInfo");
		JSONArray jAD = jAI.getJSONArray("AssignmentData");

		for(int i = 0; i<jAD.length(); i++) {

			JSONObject grade = jAD.getJSONObject(i);
			assertThat("proper type",grade,instanceOf(JSONObject.class));
			assertThat("Score exists and is string",JSONObject.valueToString(grade.get("Score")),instanceOf(String.class));
			assertThat("Unique_Name exists and is string",grade.getString("Unique_Name"),instanceOf(String.class));
		}
	}

	@Ignore
	@Test
	public void testVerify() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testInvoker() {
		fail("Not yet implemented");
	}

}

//INFO: {"Meta":{"Message":"COMPLETED","httpStatus":200},"Result":{"AssignmentInfo":{"AssignmentData":[{"Score":1721.5,"Published_Score":1721.5,"User_Id":137734728828958800,"Finished_At":"2017-04-26T14:38:46.809-04:00","Unique_Name":"studentc"},{"Score":1320,"Published_Score":1320,"User_Id":-296951127543716860,"Finished_At":"2017-04-26T14:10:54.188-04:00","Unique_Name":"studentd"},{"Score":107.2,"Published_Score":107.2,"User_Id":80295732322489020,"Finished_At":"2017-04-27T15:34:24.638-04:00","Unique_Name":"otchiu"},{"Score":1520.5,"Published_Score":1520.5,"User_Id":271815845968802940,"Finished_At":"2017-04-26T15:03:53.929-04:00","Unique_Name":"studenta"},{"Score":0,"Published_Score":0,"User_Id":247378583477711140,"Finished_At":"2017-04-26T11:52:42.184-04:00","Unique_Name":"crouch"},{"Score":0,"Published_Score":0,"User_Id":-527815610591606100,"Finished_At":"2017-04-28T16:02:20.856-04:00","Unique_Name":"2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21"},{"Score":1613.3,"Published_Score":1613.3,"User_Id":392571521045807040,"Finished_At":"2017-04-25T15:42:26.797-04:00","Unique_Name":"studentb"},{"Score":1220.5,"Published_Score":1220.5,"User_Id":173106618602968540,"Finished_At":"2017-04-27T14:38:28.160-04:00","Unique_Name":"zewang"}]}}}
//{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":200},\"Result\":{\"AssignmentInfo\":{\"AssignmentData\":[{\"Score\":1721.5,\"Published_Score\":1721.5,\"User_Id\":137734728828958800,\"Finished_At\":\"2017-04-26T14:38:46.809-04:00\",\"Unique_Name\":\"studentc\"},{\"Score\":1320,\"Published_Score\":1320,\"User_Id\":-296951127543716860,\"Finished_At\":\"2017-04-26T14:10:54.188-04:00\",\"Unique_Name\":\"studentd\"},{\"Score\":107.2,\"Published_Score\":107.2,\"User_Id\":80295732322489020,\"Finished_At\":\"2017-04-27T15:34:24.638-04:00\",\"Unique_Name\":\"otchiu\"},{\"Score\":1520.5,\"Published_Score\":1520.5,\"User_Id\":271815845968802940,\"Finished_At\":\"2017-04-26T15:03:53.929-04:00\",\"Unique_Name\":\"studenta\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":247378583477711140,\"Finished_At\":\"2017-04-26T11:52:42.184-04:00\",\"Unique_Name\":\"crouch\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":-527815610591606100,\"Finished_At\":\"2017-04-28T16:02:20.856-04:00\",\"Unique_Name\":\"2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21\"},{\"Score\":1613.3,\"Published_Score\":1613.3,\"User_Id\":392571521045807040,\"Finished_At\":\"2017-04-25T15:42:26.797-04:00\",\"Unique_Name\":\"studentb\"},{\"Score\":1220.5,\"Published_Score\":1220.5,\"User_Id\":173106618602968540,\"Finished_At\":\"2017-04-27T14:38:28.160-04:00\",\"Unique_Name\":\"zewang\"}]}}}

//String JSONtest = "{\"Meta\":{\"Message\":\"COMPLETED\",\"httpStatus\":200},\"Result\":{\"AssignmentInfo\":{\"AssignmentData\":[{\"Score\":1721.5,\"Published_Score\":1721.5,\"User_Id\":137734728828958800,\"Finished_At\":\"2017-04-26T14:38:46.809-04:00\",\"Unique_Name\":\"studentc\"},{\"Score\":1320,\"Published_Score\":1320,\"User_Id\":-296951127543716860,\"Finished_At\":\"2017-04-26T14:10:54.188-04:00\",\"Unique_Name\":\"studentd\"},{\"Score\":107.2,\"Published_Score\":107.2,\"User_Id\":80295732322489020,\"Finished_At\":\"2017-04-27T15:34:24.638-04:00\",\"Unique_Name\":\"otchiu\"},{\"Score\":1520.5,\"Published_Score\":1520.5,\"User_Id\":271815845968802940,\"Finished_At\":\"2017-04-26T15:03:53.929-04:00\",\"Unique_Name\":\"studenta\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":247378583477711140,\"Finished_At\":\"2017-04-26T11:52:42.184-04:00\",\"Unique_Name\":\"crouch\"},{\"Score\":0,\"Published_Score\":0,\"User_Id\":-527815610591606100,\"Finished_At\":\"2017-04-28T16:02:20.856-04:00\",\"Unique_Name\":\"2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21\"},{\"Score\":1613.3,\"Published_Score\":1613.3,\"User_Id\":392571521045807040,\"Finished_At\":\"2017-04-25T15:42:26.797-04:00\",\"Unique_Name\":\"studentb\"},{\"Score\":1220.5,\"Published_Score\":1220.5,\"User_Id\":173106618602968540,\"Finished_At\":\"2017-04-27T14:38:28.160-04:00\",\"Unique_Name\":\"zewang\"}]}}}";

//
//{
//	"AssignmentInfo": {
//		"AssignmentData": [{
//			"Score": 1721.5,
//			"Published_Score": 1721.5,
//			"User_Id": 137734728828958800,
//			"Finished_At": "2017-04-26T14:38:46.809-04:00",
//			"Unique_Name": "studentc"
//		}, {
//			"Score": 1320,
//			"Published_Score": 1320,
//			"User_Id": -296951127543716860,
//			"Finished_At": "2017-04-26T14:10:54.188-04:00",
//			"Unique_Name": "studentd"
//		}, {
//			"Score": 107.2,
//			"Published_Score": 107.2,
//			"User_Id": 80295732322489020,
//			"Finished_At": "2017-04-27T15:34:24.638-04:00",
//			"Unique_Name": "otchiu"
//		}, {
//			"Score": 1520.5,
//			"Published_Score": 1520.5,
//			"User_Id": 271815845968802940,
//			"Finished_At": "2017-04-26T15:03:53.929-04:00",
//			"Unique_Name": "studenta"
//		}, {
//			"Score": 0,
//			"Published_Score": 0,
//			"User_Id": 247378583477711140,
//			"Finished_At": "2017-04-26T11:52:42.184-04:00",
//			"Unique_Name": "crouch"
//		}, {
//			"Score": 0,
//			"Published_Score": 0,
//			"User_Id": -527815610591606100,
//			"Finished_At": "2017-04-28T16:02:20.856-04:00",
//			"Unique_Name": "2bf01bfed59660f85cee2dc7fddbd9e8d9fb1b21"
//		}, {
//			"Score": 1613.3,
//			"Published_Score": 1613.3,
//			"User_Id": 392571521045807040,
//			"Finished_At": "2017-04-25T15:42:26.797-04:00",
//			"Unique_Name": "studentb"
//		}, {
//			"Score": 1220.5,
//			"Published_Score": 1220.5,
//			"User_Id": 173106618602968540,
//			"Finished_At": "2017-04-27T14:38:28.160-04:00",
//			"Unique_Name": "zewang"
//		}]
//	}
//}
//}