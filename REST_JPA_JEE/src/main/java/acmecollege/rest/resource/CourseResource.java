/***************************************************************************
 * File:  CourseResource.java Course materials (23S) CST 8277
 * 
 * @author Rohan Kim
 * @date November 28, 2023
 * 
 * Updated by:  Group 45
 *   041070929, Rohan, Kim
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.Professor;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
	// C
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
    	Response response = null;
    	service.persistCourse(newCourse);
    	LOG.debug("creating new course" + newCourse);
    	response = Response.ok(newCourse).build();
    	return response;
    }
	// R
    @GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getCourses() {
		LOG.debug("retrieving all courses ...");
		List<Course> courses = service.getAll(Course.class, Course.ALL_COURSES_QUERY);
		Response response = Response.ok(courses).build();
		return response;
	}
    
    @GET
    @RolesAllowed({ADMIN_ROLE}) 
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific course " + id);
        Course course = service.getCourseById(id);
        if (course == null)
            return Response.status(Status.NOT_FOUND).build();
        return Response.ok(course).build();
    }
    
    	
	// D
    @DELETE
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ADMIN_ROLE})
	public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
    	LOG.debug("deleting selected course" + id);
    	Response response = null;
		service.deleteCourseById(id);
		response = Response.ok().build();
		return response;	
	}

}
