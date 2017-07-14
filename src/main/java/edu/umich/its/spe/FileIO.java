package edu.umich.its.spe;

import java.io.File;
import java.io.IOException;

/*
 * Implement file IO for the grade input / output.  The implementation must allow configuration
 * so either get/put or both (or neither) use FileIO.  Any additional properties required for the IO
 * must either be provided on construction or come from the properties files.
 *
 * Methods are static but calls based covers are provided to be consistent with calls
 * to the ESB implementation.
 *
 * Input file format is string version of JSON WAPI wrapped response.  However line breaks are permitted.
 *
 *	Throws GradeIOException if there is a problem.
 *
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

	/******* Class covers to call underlying static methods *********/
	@Override
	public WAPIResultWrapper getGradesVia(SPEProperties speproperties, String gradedAfterTime)
			throws GradeIOException {
		return getGradesFileStatic(speproperties,gradedAfterTime);
	}

	@Override
	public WAPIResultWrapper putGradeVia(SPEProperties speproperties, HashMap<?, ?> user) {
		return putGradeFileStatic(speproperties, user);
	}

	@Override
	public boolean verifyConnection(SPEProperties speproperties) {
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
			// allow input file to have line breaks.
			gradesString = FileUtils.readFileToString(file,"UTF-8").replaceAll("\\r|\\n", " ");
		} catch (IOException e) {
			String msg = "FileIO: exception: "+e.getMessage() + " file: "+fileName;
			M_log.info(msg);
			return new WAPIResultWrapper(WAPI.HTTP_NOT_FOUND,msg,new JSONObject("{}"));
		}

		M_log.debug("getGradesFileStatic gradesString: [{}]",gradesString);
		if (gradesString == null || gradesString.length() == 0) {
			gradesString = "{}";
		}

		JSONObject jo= new JSONObject(gradesString);
		M_log.debug("jo: {}",jo);
		JSONObject jr = jo.getJSONObject("Result");
		M_log.debug("jr: {}",jr);

		return new WAPIResultWrapper(WAPI.HTTP_SUCCESS,"returned from file: "+fileName,jr);
	}

	public static WAPIResultWrapper putGradeFileStatic(SPEProperties speproperties, HashMap<?, ?> user) {
		M_log.debug("putGrade FileIO: properties: {} gradedAfterTime: {}",speproperties,user);
		String success_msg = "wrote user";

		HashMap<String,String> ioProperties = speproperties.getEsb();
		String fileName = SPEUtils.safeGetPropertyValue(ioProperties,"putGradeIO");
		M_log.info("putGrade: updated {} for user: {}",fileName,user.toString());
		File file = new File(fileName);

		try {
			FileUtils.writeStringToFile(file, user.toString()+System.lineSeparator(), "UTF-8",true);
		} catch (IOException e) {
			String msg = "FileIO: exception: "+e.getMessage();
			M_log.info(msg);
			return new WAPIResultWrapper(WAPI.HTTP_UNKNOWN_ERROR,msg,new JSONObject("{}"));
		}
		return new WAPIResultWrapper(WAPI.HTTP_SUCCESS,success_msg, new JSONObject("{}"));
	}

	/* NO OP */
	public static boolean verifyConnectionFileStatic(SPEProperties speproperties) {
		M_log.debug("verifyESB: FileIO: is NOP");
		return true;
	}


}
