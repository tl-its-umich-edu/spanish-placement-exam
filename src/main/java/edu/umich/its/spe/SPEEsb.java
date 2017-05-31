package edu.umich.its.spe;

import java.util.HashMap;
import java.util.List;

import edu.umich.ctools.esb.utils.WAPIResultWrapper;

public interface SPEEsb {

	List<String> setupGetGradePropertyValues();
	
	List<String> setupPutGradePropertyValues();

	WAPIResultWrapper getGradesViaESB(HashMap<String, String> value);

	WAPIResultWrapper putGradeViaESB(HashMap<String, String> value);

}