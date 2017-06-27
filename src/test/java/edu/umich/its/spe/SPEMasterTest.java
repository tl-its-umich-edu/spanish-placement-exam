package edu.umich.its.spe;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
//import static org.mockito.Matchers.matches;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ComponentScan

public class SPEMasterTest {

	private static Logger M_log = LoggerFactory.getLogger(SPEMasterTest.class);
	
//	@Autowired
	SPEMaster spe = null;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testSPEMaster() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testTimeStampFormat() {
		LocalDateTime ts = LocalDateTime.now();
		assertThat("valid year",SPEMaster.formatPersistTimestamp(ts),containsString("2017-"));
		assertThat("valid year",SPEMaster.formatPersistTimestamp(ts),not(containsString("XXX")));
	}
	
//	@Test
//	public void testWriteLastGradeTransferTime() throws PersistStringException {
//		spe.writeLastGradeTransferTime();
//	}
//	

}
