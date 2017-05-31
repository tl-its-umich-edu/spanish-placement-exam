package edu.umich.its.spe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Configuration
@ConfigurationProperties

//@ConfigurationProperties(prefix="esb")
@Component
//@PropertySource(value = "application.properties")
//@PropertySource("file:config/security.yml")
@PropertySource("file:config/application.properties")

//@EnableConfigurationProperties ??
//public class ApplicationProperties {
//    private Map<String, String> pathMapper;
//
//    //get and set for pathMapper are important
//}

@Data
public class EsbProperties {
	private HashMap<String,String> esb;
}

//@Data
//public class EsbProperties {
//  private List<String> esb = new ArrayList<String>();
//
//  public List<String> esb() {
//      return this.esb;
//  }
//}


//@Data
//public class EsbProperties {
//	String tokenServer;
//	String apiPrefix;
//	String key;
//	String x_ibm_client_id;
//	String secret;
//	String grant_type;
//	String scope;
//}

//public class Config {
//
//    private List<String> servers = new ArrayList<String>();
//
//    public List<String> getServers() {
//        return this.servers;
//    }
//}

//################## DEV API settings
//
//dev.tokenServer=https://apigw-tst.it.umich.edu:444/aa/sandbox/aa/oauth2/token
//dev.apiPrefix=https://apigw-tst.it.umich.edu:444/aa/sandbox/aa
//dev.key=ac54652b-5f59-4a9c-a39f-33f76567597b
//dev.x-ibm-client-id=ac54652b-5f59-4a9c-a39f-33f76567597b
//dev.secret=bT1dL8fE1bG3tI1xU2yS3lB2uN8nS6vX4tA1nB7kD4eT7aE7kE
//## added as overrides
//dev.grant_type=client_credentials
//dev.scope=unizingrades
//## skip for the moment.
//dev.header_accept='accept: application/json'
//dev.header_content-type='content-type: application/x-www-form-urlencoded'
//
//############# test settings
//## for getting grades.
//dev.COURSEID=159923
//dev.ASSIGNMENTTITLE=Spanish Placement Exam
//dev.gradedaftertime=2017-04-01 18:00:00
//
//dev.persistPath=/tmp
//
//## for putting grades
//dev.UNIQNAME=abc
//dev.SCORE=954.2
