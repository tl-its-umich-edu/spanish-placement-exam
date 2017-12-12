package edu.umich.its.spe;

/*
 * Example of using the Spring Boot test environment, so properties are loaded.
 * If use the (commented out) static configuration class then will not use the Spring
 * environment and will NOT get properties found and loaded.
 */


import static org.junit.Assert.*;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan


public class SPEEsbTokenTest {

	// Apply a global timeout to all tests.  Comment out when debugging a test.
	//@Rule
	//public Timeout globalTimeout = Timeout.seconds(10);



	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Autowired
	GradeIO speesb;

	@Autowired
	private SPEProperties speproperties;

	private static Logger M_log = LoggerFactory.getLogger(SPEEsbTokenTest.class);

	protected void testESBVerify() {
		Boolean verify_result = speesb.verifyConnection(speproperties);
		M_log.debug("update: {}",verify_result);
		assertTrue("successful verify",verify_result);
	}

	@Test
	public void checkVerifyTest1() throws IOException {
		testESBVerify();
	}

	@Test
	public void checkVerifyTest2() throws IOException {
		testESBVerify();
	}

	@Test
	public void checkVerifyTest3() throws IOException {
		testESBVerify();
	}

	@Test
	public void checkVerifyTest4() throws IOException {
		testESBVerify();
	}

	@Test
	public void checkVerifyTest5() throws IOException {
		testESBVerify();
	}

}
