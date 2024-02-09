/**
 * File:  TestACMECollegeSystem.java
 * Course materials (23S) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Student Name
 */
package acmecollege;

import static acmecollege.utility.MyConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import static org.hamcrest.Matchers.*;
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

import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.Course;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;
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
    private static Course courseSample;
    private final static int ID=1;
    private static final String COURSE_CODE="CST8101";
    private static final String COURSE_TITLE="Computer Essentials";
    private static final int YEAR =2022;
    private static final String SEMESTER="WINTER";
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
    private static final String C_NAME="NAME";
 

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
    @Order(1)
    public void test01_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
        assertThat(students.size() >=1, is(true));
    }
    @Test
    @Order(2)
    public void test02_retrieve_all_students_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }
    @Test
    @Order(3)
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
    @Order(4)
    public void test04_retrieve_all_courses_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403)); 
    }
    @Test
    @Order(5)
    public void test05_all_membershipcards_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        
    }
    @Test
    @Order(6)
    public void test06_retrieve_all_membershipcards_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }
    @Test
    @Order(7)
    public void test7_retrieve_all_courseregistrations_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_REGISTRATION_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(404)); 
    }
    @Test
    @Order(8)
    public void test8_all_studentclubs_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
       
    }

    @Test
    @Order(9)
    public void test9_retrieve_all_studentclubs_with_user_role() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }
    @Test
    @Order(10)
    public void test10_courseById_with_userRole() throws JsonMappingException, JsonProcessingException {
    	 Response response = webTarget
    	            .register(userAuth)
    	            .path(COURSE_RESOURCE_NAME+'/'+ID)
    	            .request()
    	            .get();
    
    	 assertThat(response.getStatus(), is(403));
    }
    
    @Test
    @Order(11)
    public void test11_addStudent_with_adminRole() throws JsonMappingException, JsonProcessingException{

    	Entity<Student> add= Entity.json(studentSample);
   	 	Response response = webTarget
   			
	            .register(adminAuth)
	            .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .post(add);
   	 	
	 assertThat(response.getStatus(), is(200)); 	 
//	 Student newStudent = response.readEntity(new GenericType<Student>(){});
//	 assertThat("The student ID should be greater than 0", newStudent.getId(), greaterThan(0));
    }    
    @Test
    @Order(12)
    public void test12_retriveStudent_WithID_with_adminRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget
   			
	            .register(adminAuth)
	            .path(STUDENT_RESOURCE_NAME +'/'+ ID)
	            .request()
	            .get();
	 assertThat(response.getStatus(), is(200)); 
	 Student student=response.readEntity(new GenericType<Student>() {});
	 assertThat(student.getId(), is(ID));
    }
    
    
    @Test
    @Order(13)
    public void test13_addStudent_with_userRole() throws JsonMappingException, JsonProcessingException{
    	Entity<Student> add= Entity.json(studentSample);
   	 	Response response = webTarget
   			
	            .register(userAuth)
	            .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .post(add);
   	 assertThat(response.getStatus(), is(403));;
    }    
    @Test
    @Order(14)
    public void test14_retriveStudent_WithID_with_userRole() throws JsonMappingException, JsonProcessingException{
   	 	Response response = webTarget
   			
	            .register(userAuth)
	            .path(STUDENT_RESOURCE_NAME +'/'+ ID)
	            .request()
	            .get();
   	 assertThat(response.getStatus(), is(200));
    }
    @Test
    @Order(15)
    public void test15_addCourse_with_adminRole() throws JsonMappingException, JsonProcessingException {
   	 
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
}