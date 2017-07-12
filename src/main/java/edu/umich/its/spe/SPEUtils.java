package edu.umich.its.spe;

import java.util.HashMap;

public class SPEUtils {

	// get a string property and default it to empty string.
	static protected String safeGetPropertyValue(HashMap<String, String> testproperties, String propertyKey) {
		if (testproperties == null) {
			return "";
		}
		String propertyValue = (testproperties.get(propertyKey) == null ? "" :  testproperties.get(propertyKey));
		return propertyValue;
	}

}
