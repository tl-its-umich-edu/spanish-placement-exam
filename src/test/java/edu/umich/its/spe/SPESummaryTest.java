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

import org.apache.commons.lang3.tuple.Triple;
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
//	@Test
//	public void checkLastDateFound() {
//		//ss.setStoredGradesLastRetrieved("LASTDATE");
//		//assertEquals("saved date","LASTDATE",ss.getStoredGradesLastRetrieved());
//	}

	@Test
	public void checkEmptyUserList() {
		List<Triple<String,String,Boolean>> users = ss.getUsers();
		assertNotNull("got some list from users",users);
		assertEquals("empty list",0,users.size());
	}

	@Test
	public void addOneUser() {
		ss.appendUser("user 1","NOW",true);
		assertEquals("list of 1",1,ss.getUsers().size());
	}

	@Test
	public void addThreeUsers() {
		ss.appendUser("user 1","NOW",true);
		ss.appendUser("user 2","NOW",false);
		ss.appendUser("user 3","THEN",true);
		assertEquals("list of 3",3,ss.getUsers().size());
	}

	@Test
	public void addThreeUsersFluent() {
		ss.appendUser("user 1","THEN",true).appendUser("user 2","THEN",false).appendUser("user 3","THEN",true);
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
		//ss.setUseGradesLastRetrieved("MALTA");
		ss.setUseTestLastTakenTime("MALTA"); //setUseGradesLastRetrieved("MALTA");
		//String tos = ss.getUseGradesLastRetrieved().toString();
		String tos = ss.getUseTestLastTakenTime().toString();
		assertEquals("expected string","MALTA",tos);
	}

	@Test
	public void checkDateString() {

		ss.setStoredTestLastTakenTime("HI");
		ss.setUseTestLastTakenTime("NOW");
		ss.setUpdatedTestLastTakenTime("BYE");

		String tos = ss.toString();

		assertThat("find storedTestLastTakenTime",tos,containsString("storedTestLastTakenTime: HI"));

		assertThat("find updatedTestLastTakenTime",tos,containsString("updatedTestLastTakenTime: BYE"));
		assertThat("find useTestLastTakenTime",tos,containsString("useTestLastTakenTime: NOW"));
	}

	@Test
	public void checkUserFormatting() {
		ss.appendUser("user 1","THEN",true).appendUser("user 2","THEN",false).appendUser("user 3","NOW",true);

		String tos = ss.toString();

		// Uncomment if want to see the report.
		//System.out.println(tos);
		assertThat("find user 1",tos,containsString("user: user 1 success: true finished at: THEN"));
		assertThat("find user 2",tos,containsString("user: user 2 success: false finished at: THEN"));
		assertThat("find user 3",tos,containsString("user: user 3 success: true finished at: NOW"));
		assertThat("added 2 users",tos,containsString("users added: 2"));
		assertThat("failed 1 user",tos,containsString("errors: 1"));
	}

	@Test
	public void checkCombinedFormatting() {

		ss.appendUser("user 2","NOW",false);
		//ss.setUpdatedGradesLastRetrieved("BYE");
		ss.setUpdatedTestLastTakenTime("BYE");
		String tos = ss.toString();

		assertThat("find user 2",tos,containsString("user: user 2 success: false finished at: NOW"));
		assertThat("find updatedTestLastTakenTime",tos,containsString("updatedTestLastTakenTime: BYE"));
	}

	/////////////////// Check that sort list of user results by user name.

	@Test
	public void checkSortUsersEmpty() {
		List<Triple<String, String, Boolean>> sortedUsers;

		assertEquals("list of 0",0,ss.getUsers().size());

		sortedUsers = ss.sortedUsers();
		assertEquals("sorted list of 0",0,sortedUsers.size());
	}

	@Test
	public void checkSortUsers1() {
		List<Triple<String, String, Boolean>> sortedUsers;

		ss.appendUser("user C","NOW",true);
		assertEquals("list of 1",1,ss.getUsers().size());

		sortedUsers = ss.sortedUsers();
		assertThat("user C in sorted order",sortedUsers.get(0).getLeft(),equalTo("user C"));
	}

	@Test
	public void checkSortUsers3() {
		List<Triple<String, String, Boolean>> sortedUsers;

		ss.appendUser("user C","THEN",true);
		ss.appendUser("user B","THEN",false);
		ss.appendUser("user A","NOW",true);
		assertEquals("list of 3",3,ss.getUsers().size());

		sortedUsers = ss.sortedUsers();
		assertThat("user A in sorted order",sortedUsers.get(0).getLeft(),equalTo("user A"));
		assertThat("user A in sorted order",sortedUsers.get(0).getMiddle(),equalTo("NOW"));
		assertThat("user B in sorted order",sortedUsers.get(1).getLeft(),equalTo("user B"));
		assertThat("user A in sorted order",sortedUsers.get(1).getMiddle(),equalTo("THEN"));
		assertThat("user C in sorted order",sortedUsers.get(2).getLeft(),equalTo("user C"));
		assertThat("user A in sorted order",sortedUsers.get(2).getMiddle(),equalTo("THEN"));
	}
}
