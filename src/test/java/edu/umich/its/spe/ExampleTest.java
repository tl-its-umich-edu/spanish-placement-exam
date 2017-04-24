package edu.umich.its.spe;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ExampleTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIDontDoMuchButSayHello() {
		assertEquals("Say Hello", "HELLO", Example.iDontDoMuchButSayHello());
	}

}
