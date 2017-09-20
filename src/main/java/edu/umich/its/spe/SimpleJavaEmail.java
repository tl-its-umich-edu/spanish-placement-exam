package edu.umich.its.spe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Create generator of java mail sender objects.  Use this to deal with custom javamail property handling.
 */

import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class SimpleJavaEmail {

	static final Logger M_log = LoggerFactory.getLogger(SimpleJavaEmail.class);

	Properties mailProperties;

	// Create and store the desired properties given a hashmap.

	// Default the prefix.
	SimpleJavaEmail(HashMap<String,String> inputProperties) {
		this(inputProperties,"mail.");
	}

	SimpleJavaEmail(HashMap<String,String> inputProperties,String prefix) {
		mailProperties = extractPrefixedProperties(prefix,inputProperties);
		M_log.debug("mailProperties: {}",mailProperties);
	}

	// Utility to pull out the properties with specific prefix.
	private Properties extractPrefixedProperties(String prefix,HashMap<String,String> map) {
		Properties extractedProperties = new Properties();

		//TODO: use streams
		for (String key : map.keySet()) {
			if (key.startsWith(prefix)) {
				extractedProperties.put(key, map.get(key));
				M_log.debug("extracted key: {} value: {}",key,map.get(key));
			}
		}

		M_log.info("final extracted properties: {}",extractedProperties);
		return extractedProperties;
	}

	// Just return a new sender with the right properties.
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl	sender = new JavaMailSenderImpl();
		sender.setJavaMailProperties(mailProperties);
		return sender;
	}

	public void sendSimpleMessage(String from, String to, String subject, String text) {

		SimpleMailMessage message = new SimpleMailMessage();
		JavaMailSender sender = getJavaMailSender();

		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		sender.send(message);
	}

	static String getISO8601StringForDate(Date date) {
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.getDefault());
		                      //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.getDefault());
		//dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

}