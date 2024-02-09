/**
 * File:  TestACMECollegeSystem.java
 * Course materials (23S) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
* Updated by:  Group 45
 * 041081021 Moulayat Chawki
 * 04107092, Rohan, Kim
 * 041014922 Hoskol Mohamad
 * 041055255 Nikhil Krishnaa
 */
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME ;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_PROFESSOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Student;
import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.PojoBase;
import acmecollege.entity.Professor;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.StudentClub;
@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    private final int ID=2;
    private static Course courseSample;
    private static final String COURSE_CODE="CST8116";
    private static final String COURSE_TITLE="DATASTRUCTURE";
    private static final int YEAR =2023;
    private static final String SEMESTER="SPRING";
    private static final int CREDIT_UNITS=3;
    private static final byte ONLINE=0B0;
    private static int sampleId = 10;
    private static Student studentSample;
    private static Student studentSampleUpdate;
    private static final String S_LNAME="LNAME";
    private static final String S_FNAME="FNAME";
    private static final String S_LNAMEUPDATE="LNAMEUPDATE";
    private int studentId=2;    
    private static Professor prof;
    private static final String P_LNAME="LNAME";
    private static final String P_FNAME="FNAME";
    private static final String P_DEPARTMENT="DEPARTMENT";
    
    private static StudentClub clubSample;
    private static final String C_NAME="NAME4";
    private static int id;
 
    		
    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
        
        courseSample= new Course(COURSE_CODE,COURSE_TITLE,YEAR,SEMESTER,CREDIT_UNITS,ONLINE);
        studentSample = new Student();
        studentSample.setFirstName(S_FNAME);
        studentSample.setLastName(S_LNAME);
//        studentSample.setId(sampleId);
        studentSampleUpdate = new Student();
        studentSampleUpdate.setFirstName(S_LNAMEUPDATE);
        studentSampleUpdate.setLastName(S_LNAME);
        
        prof = new Professor();
        prof.setFirstName(P_FNAME);
        prof.setLastName(P_LNAME);
        prof.setDepartment(P_DEPARTMENT);
        
        
        clubSample =  new AcademicStudentClub();
        clubSample.setName(C_NAME);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }

    @Test
    public void test01_allstudents_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
     
    }
    @Test
    public void test02_retrieve_all_students_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }

    @Test
    public void test03_all_courses_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
    
    }
    @Test
    public void test04_retrieve_all_courses_with_admin_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth) // Use adminAuth for administrator privileges
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();

        assertThat(response.getStatus(), is(200)); 
       
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));  
    }
    @Test
    public void test05_retrieve_all_courses_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }
    @Test
    public void test06_all_membershipcards_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();

        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test07_retrieve_all_membershipcards_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }
    @Test
    public void test8_retrieve_all_courseregistrations_with_admin_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .request()
            .get();

        assertThat(response.getStatus(), is(200)); 

        
        List<CourseRegistration> courseRegistrations = response.readEntity(new GenericType<List<CourseRegistration>>(){});
        assertThat(courseRegistrations, is(not(empty()))); 
    }
    @Test
    public void test9_retrieve_all_courseregistrations_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403)); 
    }
    @Test
    public void test10_all_studentclubs_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
       
    }

    @Test
    public void test11_retrieve_all_studentclubs_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); 
    }
    @Test

    public void test12_courseById_with_userRole() throws JsonMappingException, JsonProcessingException {
    	 Response response = webTarget
    	            .register(userAuth)
    	            .path(COURSE_RESOURCE_NAME+'/'+ID)
    	            .request()
    	            .get();
    
    	 assertThat(response.getStatus(), is(403));
    }
    @Test
    public void test13_addCourse_with_adminRole() throws JsonMappingException, JsonProcessingException {
   	 
    	Entity<Course> add= Entity.json(courseSample);
    	
    	Response response = webTarget
   	            .register(adminAuth)
   	            .path(COURSE_RESOURCE_NAME)
   	            .request()
   	            .post(add);
   	 
   	 
   	 Course course=response.readEntity(new GenericType<Course>() {});
   	 sampleId=course.getId();
   	 
   	 
   	 assertThat(course, is(not(equals(null))));
   	 assertThat(response.getStatus(), is(200));
   	 
   }
    @Test
	public void test14_delete_courseById_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME+ "/"+ID)
                .request()
                .delete();  
	    assertThat(response.getStatus(), is(200));
	}
    @Test
	public void test15_delete_courseById_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
                .register(userAuth)
                .path(COURSE_RESOURCE_NAME+ "/"+ID)
                .request()
                .delete(); 
			    assertThat(response.getStatus(), is(403));
	}
    @Test
    public void test16_addStudent_with_adminRole() throws JsonMappingException, JsonProcessingException {
    	Student studentSample = new Student();
        studentSample.setFirstName("namesq");
        studentSample.setLastName("Lnamas");
        Entity<Student> add = Entity.json(studentSample);
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(add);

        assertThat(response.getStatus(), is(200)); 
        Student newStudent = response.readEntity(new GenericType<Student>(){});
        assertThat(newStudent.getId(), is(not(0)));
    }
    @Test
	 public void test17_delete_studentsById_with_adminrole() throws JsonMappingException, JsonProcessingException {
	        Response response =webTarget
	                .register(adminAuth)
	                .path(STUDENT_RESOURCE_NAME+ "/"+ID)
	                .request()
	                .delete();   
	        assertEquals(200, response.getStatus());
	    }
	    
    @Test
    public void test18_addStudent_with_userRole() throws JsonMappingException, JsonProcessingException {
    	Student studentSample = new Student();
        studentSample.setFirstName("name");
        studentSample.setLastName("Lnamasos");
        Entity<Student> add = Entity.json(studentSample);
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(add);

        assertThat(response.getStatus(), is(403)); 
    }


    @Test
    public void test19_retriveStudent_WithID_with_adminRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget
   			
	            .register(adminAuth)
	            .path(STUDENT_RESOURCE_NAME +'/'+ 1)
	            .request()
	            .get();
	 assertThat(response.getStatus(), is(200)); 
	 Student student=response.readEntity(new GenericType<Student>() {});
	 assertThat(student.getId(), is(1));
    }
    
    
    @Test
    public void test20_updateStudent_with_adminRole() throws JsonMappingException, JsonProcessingException{
    	Entity<Student> update= Entity.json(studentSampleUpdate);
   	 	Response response = webTarget
   			
	            .register(adminAuth)
	            .path(STUDENT_RESOURCE_NAME+STUDENT_COURSE_PROFESSOR_RESOURCE_PATH)
	            .resolveTemplate("studentId", 3)
	            .resolveTemplate("courseId", 3)
	            .resolveTemplate("professor", prof)
	            .request()
	            .put(update);
	 assertThat(response.getStatus(), is(200)); 
    }
    
    @Test
    public void test21_retriveClub_WithID_with_adminRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget		
	            .register(adminAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME +'/'+ 1)
	            .request()
	            .get();
	 assertEquals(response.getStatus(), 200); 

	 }

    @Test
    public void test22_addStudent_with_userRole() throws JsonMappingException, JsonProcessingException{
    	Entity<Student> add= Entity.json(studentSample);
   	 	Response response = webTarget
   			
	            .register(userAuth)
	            .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .post(add);
   	 assertThat(response.getStatus(), is(403));;
    }    
    @Test
    public void test23_retriveStudent_WithID_with_userRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget
   			
	            .register(userAuth)
	            .path(STUDENT_RESOURCE_NAME +'/'+ 1)
	            .request()
	            .get();
   	 assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test24_retriveClub_WithID_with_userRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget		
	            .register(userAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME +'/'+ 1)
	            .request()
	            .get();
   	 assertEquals(response.getStatus(), 200);
 
    }
    @Test
    public void test25_updateStudent_with_userRole() throws JsonMappingException, JsonProcessingException{
    	Entity<Student> update= Entity.json(studentSampleUpdate);
   	 	Response response = webTarget
   			
	            .register(userAuth)
	            .path(STUDENT_RESOURCE_NAME+STUDENT_COURSE_PROFESSOR_RESOURCE_PATH)
	            .resolveTemplate("studentId", 1)
	            .resolveTemplate("courseId", 1)
	            .resolveTemplate("professor", prof)
	            .request()
	            .put(update);
	 assertThat(response.getStatus(), is(403)); 
    }
    @Test
    public void test26_addClub_with_adminRole() throws JsonMappingException, JsonProcessingException{
    	AcademicStudentClub clubSample =  new AcademicStudentClub();
        clubSample.setName("Theclubs");
		Entity<StudentClub> add= Entity.json(clubSample );
   	 	Response response = webTarget
   	 		
	            .register(adminAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME)
	            .request()
	            .post(add);
   	 assertEquals(response.getStatus(), 200);	 
   	 StudentClub club=response.readEntity(new GenericType<StudentClub>() {});
	 assertThat(club.getName(), is("Theclubs"));
    }
    @Test
    public void test27_deleteClub_with_adminRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget
   			
	            .register(adminAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME+'/'+2)
	            .request()
	            .delete();
   	 assertEquals(response.getStatus(), 200);    }
    
    @Test
    public void test28_updateclub_with_adminRole() throws JsonMappingException, JsonProcessingException{
    	clubSample.setName(C_NAME+"UPDATE");
    	Entity<StudentClub> update= Entity.json(clubSample);
   	 	Response response = webTarget
   			
	            .register(adminAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME+'/'+1)
	            .request()
	            .put(update);
	 assertThat(response.getStatus(), is(200)); 
	  }
    @Test
    public void test29_deleteClub_with_userRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget
   			
	            .register(userAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME+'/'+ID)
	            .request()
	            .delete();
	 assertThat(response.getStatus(), is(403)); 
    }
    @Test
    public void test30_addClub_with_userRole() throws JsonMappingException, JsonProcessingException{
    	
		Entity<StudentClub> add= Entity.json(clubSample );
   	 	Response response = webTarget
   	 		
	            .register(userAuth)
	            .path(STUDENT_CLUB_RESOURCE_NAME)
	            .request()
	            .post(add);
	 assertThat(response.getStatus(), is(403)); 
	 
    }
     
    @Test
	public void test31_get_deleted_course_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    Response response = webTarget
	        .register(adminAuth)
	        .path(COURSE_RESOURCE_NAME + "/" + sampleId)
	        .request()
	        .get();
	    assertThat(response.getStatus(), is(200));
	}
    @Test
	public void test32_get_updated_course_with_userrole() throws JsonMappingException, JsonProcessingException {
	    Response response = webTarget
	        .register(userAuth)
	        .path(COURSE_RESOURCE_NAME + "/" + sampleId)
	        .request()
	        .get();
	    assertEquals(response.getStatus(), 403);

	}
    @Test
	public void test33_update_course_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    Course updatedCourse = new Course(COURSE_CODE, COURSE_TITLE, YEAR, SEMESTER, 4, ONLINE);
	    Response response = webTarget
	        .register(adminAuth)
	        .path(COURSE_RESOURCE_NAME + "/" + sampleId)
	        .request()
	        .put(Entity.json(updatedCourse));
	    assertEquals(response.getStatus(), 405);
	}
    @Test
	public void test34_get_course_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    Response response = webTarget
	        .register(adminAuth)
	        .path(COURSE_RESOURCE_NAME + "/" + sampleId)
	        .request()
	        .get();
	    assertThat(response.getStatus(), is(200));
	    Course course = response.readEntity(Course.class);
	    assertThat(course.getCourseCode(), is(COURSE_CODE));
	    assertThat(course.getCreditUnits(), is(CREDIT_UNITS));
	}
    
    

    @Test
    public void test35_all_professors_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response =webTarget
 		        .register(adminAuth)
 		        .path(PROFESSOR_SUBRESOURCE_NAME)
 		        .request()
 		        .get(); 
        assertEquals(200, response.getStatus());
        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
    }
    
    @Test
    public void test36_all_professors_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response =webTarget
 		        .register(userAuth)
 		        .path(PROFESSOR_SUBRESOURCE_NAME)
 		        .request()
 		        .get(); 
    	assertEquals(403, response.getStatus());
    }
    
     
    @Test
    public void test37_professorById_with_userrole() throws JsonMappingException, JsonProcessingException {
    	 Response response = webTarget
 		        .register(userAuth)
 		        .path(PROFESSOR_SUBRESOURCE_NAME+ "/"+ID)
 		        .request()
 		        .get();
    	 assertEquals(403, response.getStatus());
    }

	@Test
	public void test38_delete_professorById_userrole() throws JsonMappingException, JsonProcessingException {
	    Response response = webTarget
                .register(userAuth)
                .path(PROFESSOR_SUBRESOURCE_NAME+ "/"+ID)
                .request()
                .delete();

	    assertThat(response.getStatus(), is(403));
	    
	}
	 
	  @Test
	  public void test39_delete_studentsById_with_userrole() throws JsonMappingException, JsonProcessingException {
	        Response response =webTarget
	                .register(userAuth)
	                .path(STUDENT_RESOURCE_NAME+ "/"+ID)
	                .request()
	                .delete();  
	        assertEquals(403, response.getStatus());
	    }
	  @Test
	    public void test40_retriveClub_WithID_with_adminAdmin() throws JsonMappingException, JsonProcessingException{
	   	 	Response response = webTarget		
		            .register(adminAuth)
		            .path(STUDENT_CLUB_RESOURCE_NAME +'/'+ 1)
		            .request()
		            .get();
		 assertEquals(response.getStatus(), 200); 

		 }  
	  
}