package edu.umich.its.spe;

/*
 * Class to hold properties available from the esb.yml file.
 * Spring will automatically go find the yml file. By default it can find
 * it in src/main/java/config directory.  Or many other places, see documentation.
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
//import org.springframework.stereotype.Component;

import lombok.Data;

//@Component
@Configuration
@ConfigurationProperties

// Look for esb properties.
@PropertySources({
	@PropertySource(value = "classpath:missing.properties", ignoreResourceNotFound=true),
	@PropertySource("file:config/security.yml")
})

//# ask for a token
//AT=$(curl --request POST \
//          -s \
//          --url ${URL_PREFIX}/oauth2/token \
//          --header 'accept: application/json' \
//          --header 'content-type: application/x-www-form-urlencoded' \
//          --data "grant_type=${GRANT_TYPE}&scope=${SCOPE}&client_id=${KEY}&client_secret=${SECRET}");
//
//# extract and squirrel the token away.
//ACCESS_TOKEN=$(echo ${AT} | perl -n -e'/access_token":"(.+)", "metadata.*/ && print "$1"' );
//}

@Data
public class EsbConfig {
	private String URLPREFIX;
	private String GRANTTYPE;
	private String KEY;
	private String SECRET;
	
}
