/***************************************************************************
 * File:  ProfessorResource.java Course materials (23S) CST 8277
 * 
 * @author Rohan Kim
 * @date November 28, 2023
 * 
 * Updated by:  Group 45
 * 041081021 Moulayat Chawki
 * 04107092, Rohan, Kim
 * 041014922 Hoskol Mohamad
 * 041055255 Nikhil Krishnaa
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

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
import acmecollege.entity.Course;
import acmecollege.entity.Professor;

@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource { 
    
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    // Only an ‘ADMIN_ROLE’ user can associate a Professor and/or Course to a Student.
	// C
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
    	Response response = null;
    	service.persistProfessor(newProfessor);
    	LOG.debug("creating new professor" + newProfessor);
    	response = Response.ok(newProfessor).build();
    	return response;
    }
	// R
    @GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getProfessors() {
		LOG.debug("retrieving all professors ...");
		List<Professor> professors = service.getAll(Professor.class, Professor.ALL_PROFESSORS_QUERY);
		Response response = Response.ok(professors).build();
		return response;
	}
    
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving professor with id = {}", id);
        Response response = null;
        Professor professor = service.getProfessorById(id);
        if(professor == null) {
        	response = Response.status(Status.NOT_FOUND).build();
        } else {
        	response = Response.ok(professor).build();
        }
        return response;
    }
	
	// D
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
	public Response deleteProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
    	LOG.debug("deleting selected professor" + id);
    	Response response = null;
		service.deleteProfessorById(id);
		response = Response.ok().build();
		return response;
		
	}
}
