package edu.umich.its.spe;

import java.io.File;
import java.io.IOException;

/*
 * Implement file IO for the grade input / output.  The implementation must allow configuration
 * so either get/put or both (or neither) use FileIO.  Any additional properties required for the IO
 * must either be provided on construction or come from the properties files.
 *
 *
 *	Throws GradeIOException if there is a problem.
 *
 * FORMAT will be the same as comes from the ESB.
 * TODO: verify that invoked correctly based on properties.
 * TODO: get relevant file names from properties, die if not there.
 * TODO: read from file
 * TODO: write to file
 */

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import edu.umich.ctools.esb.utils.WAPI;
import edu.umich.ctools.esb.utils.WAPIResultWrapper;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

@Component
@Qualifier("FileIO")

public class FileIO implements GradeIO {
	private static final Logger M_log = LoggerFactory.getLogger(FileIO.class);

	/******* Covers to call underlying static methods *********/
	@Override
	public WAPIResultWrapper getGradesViaESB(SPEProperties speproperties, String gradedAfterTime)
			throws GradeIOException {
		return getGradesFileStatic(speproperties,gradedAfterTime);
	}

	@Override
	public WAPIResultWrapper putGradeViaESB(SPEProperties speproperties, HashMap<?, ?> user) {
		return putGradeFileStatic(speproperties, user);
	}

	@Override
	public boolean verifyESBConnection(SPEProperties speproperties) {
		return verifyConnectionFileStatic(speproperties);
	}


	/********** Static methods ************/


	public static WAPIResultWrapper getGradesFileStatic(SPEProperties speproperties, String gradedAfterTime) throws GradeIOException {
		M_log.debug("getGrades FileIO: properties: {} gradedAfterTime: {}",speproperties,gradedAfterTime);
		if (speproperties == null || gradedAfterTime == null || gradedAfterTime.length() == 0) {
			throw new GradeIOException("FileIO Call invalid: speproperties: "+ speproperties+" gradedAfterTime: "+gradedAfterTime);
		}

		HashMap<String,String> ioProperties = speproperties.getEsb();
		String fileName = SPEUtils.safeGetPropertyValue(ioProperties,"getGradeIO");

		File file = new File(fileName);
		String gradesString;

		try {
			gradesString = FileUtils.readFileToString(file,"UTF-8");
		} catch (IOException e) {
			//throw new GradeIOException("FileIO IOException: file: "+fileName,e);
			String msg = "FileIO: exception: "+e.getMessage() + " file: "+fileName;
			M_log.info(msg);
			return new WAPIResultWrapper(WAPI.HTTP_NOT_FOUND,msg,new JSONObject("{}"));
		}

		M_log.debug("getGradesFileStatic gradesString: [{}]",gradesString);
		if (gradesString == null || gradesString.length() == 0) {
			gradesString = "{}";
		}

		JSONObject jo= new JSONObject(gradesString);
		M_log.info("jo: {}",jo);
		JSONObject jr = jo.getJSONObject("Result");
		M_log.info("jr: {}",jr);

		//return new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"returned from file: "+fileName,new JSONObject(gradesString));
		return new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"returned from file: "+fileName,jr);
	}

	public static WAPIResultWrapper putGradeFileStatic(SPEProperties speproperties, HashMap<?, ?> user) {
		M_log.debug("putGrade FileIO: properties: {} gradedAfterTime: {}",speproperties,user);
		String success_msg = "wrote user";

		HashMap<String,String> ioProperties = speproperties.getEsb();
		String fileName = SPEUtils.safeGetPropertyValue(ioProperties,"putGradeIO");
		M_log.info("putGradeFile: filename: {}",fileName);
		File file = new File(fileName);

		try {
			FileUtils.writeStringToFile(file, user.toString()+System.lineSeparator(), "UTF-8",true);
			//gradesString = FileUtils.readFileToString(file,"UTF-8");
		} catch (IOException e) {
			//throw new GradeIOException("FileIO IOException: file: "+fileName,e);
			String msg = "FileIO: exception: "+e.getMessage();
			M_log.info(msg);
			return new WAPIResultWrapper(WAPI.HTTP_NOT_FOUND,msg,new JSONObject("{}"));
		}
		return new WAPIResultWrapper(WAPI.HTTP_SUCCESS,success_msg, new JSONObject("{}"));
	}

	/* nop */
	public static boolean verifyConnectionFileStatic(SPEProperties speproperties) {
		M_log.debug("verifyESB: FileIO: is nop");
		return true;
	}


}
