package edu.umich.its.spe;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

public class SPEMasterTest {

	
	@Autowired
	PersistString ps = null;
	
	@Autowired
	SPEMaster spe = null;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSPEMaster() {
		fail("Not yet implemented");
	}

	@Test
	public void testVerify() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSPEGrades() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutSPEGrade() {
		fail("Not yet implemented");
	}

	@Test
	public void testOrchestrator() {
		fail("Not yet implemented");
	}

	@Test
	public void testCloseUpShop() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutSPEGrades() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastGradeTransferTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetLastGradeTransferTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testConvertSPEGradesFromDataWarehouseJSON() {
		fail("Not yet implemented");
	}

}
