package edu.umich.its.spe;

//import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;
//import static org.hamcrest.Matchers.equalTo;

/*
 * SPE Summary holds data to be used in summary reporting.
 * 
 * Will skip basic setter getter testing for Lombok fields.
 */

import static org.junit.Assert.*;

import java.util.List;
//import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SPESummaryTest {

	@Before
	public void setUp() throws Exception {
		ss = new SPESummary();
	}

	@After
	public void tearDown() throws Exception {
	}

	SPESummary ss = null;
	

	// One sanity check test of simple string setter getter.  We'll trust lombok 
	// but will do a little verification.
	@Test
	public void checkLastDateFound() {
		ss.setStoredGradesLastRetrieved("LASTDATE");
		assertEquals("saved date","LASTDATE",ss.getStoredGradesLastRetrieved());
	}
	
	@Test 
	public void checkEmptyUserList() {
		List<Pair<String,Boolean>> users = ss.getUsers();
		assertNotNull("got some list from users",users);
		assertEquals("empty list",0,users.size());
	}
	
	@Test
	public void addOneUser() {
		ss.appendUser("user 1",true);
		assertEquals("list of 1",1,ss.getUsers().size());
	}
	
	@Test
	public void addThreeUsers() {
		ss.appendUser("user 1",true);
		ss.appendUser("user 2",false);
		ss.appendUser("user 3",true);
		assertEquals("list of 3",3,ss.getUsers().size());
	}
	
	@Test
	public void addThreeUsersFluent() {
		ss.appendUser("user 1",true).appendUser("user 2",false).appendUser("user 3",true);
		assertEquals("list of 3",3,ss.getUsers().size());
	}
	
//	@Test
//	public void checkToStringWithUsers() {
//		ss.appendUser("user 1",true).appendUser("user 2",true).appendUser("user 3",true);
//		String tos = ss.toString();
//		assertNotNull("some string",tos);
//	}
	
//	@Test
//	public void checkToStringFormatLastDateFoundEmpty() {
//		ss.appendUser("user 1",true).appendUser("user 2",true).appendUser("user 3",true);
//		String tos = ss.getStoredGradesLastRetrieved().toString();
//		assertEquals("what?","",tos);
//	}
	
	@Test
	public void checkToStringDateField() {
		ss.setUseGradesLastRetrieved("MALTA");
		String tos = ss.getUseGradesLastRetrieved().toString();
		assertEquals("expected string","MALTA",tos);
	}
	
	@Test
	public void checkDateString() {
		ss.setStoredGradesLastRetrieved("HI");
		ss.setUseGradesLastRetrieved("NOW");
		ss.setUpdatedGradesLastRetrieved("BYE");
		
		String tos = ss.toString();

		assertThat("find storedGradesLastRetrieved",tos,containsString("storedGradesLastRetrieved: HI"));
		assertThat("find updatedGradesLastRetrieved",tos,containsString("updatedGradesLastRetrieved: BYE"));
		assertThat("find useGradesLastRetrieved",tos,containsString("useGradesLastRetrieved: NOW"));
	}
	
	@Test
	public void checkUserFormatting() {
		ss.appendUser("user 1",true).appendUser("user 2",false).appendUser("user 3",true);
		
		String tos = ss.toString();

		System.out.println("tos: "+tos);
		assertThat("find user 1",tos,containsString("user: user 1 success: true"));
		assertThat("find user 2",tos,containsString("user: user 2 success: false"));
		assertThat("find user 3",tos,containsString("user: user 3 success: true"));
		assertThat("added 2 users",tos,containsString("users added: 2"));
		assertThat("failed 1 user",tos,containsString("errors: 1"));
	}
	
	@Test
	public void checkCombinedFormatting() {
		
		ss.appendUser("user 2",false);
		ss.setUpdatedGradesLastRetrieved("BYE");
		String tos = ss.toString();

		assertThat("find user 2",tos,containsString("user: user 2 success: false"));
		assertThat("find updatedGradesLastRetrieved",tos,containsString("updatedGradesLastRetrieved: BYE"));
	}
	
	
}
