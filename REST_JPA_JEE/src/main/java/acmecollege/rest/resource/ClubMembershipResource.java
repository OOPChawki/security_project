/***************************************************************************
 * File:  ClubMembershipResource.java Course materials (23S) CST 8277
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
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
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
import javax.ws.rs.ForbiddenException;
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
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {
	
	 private static final Logger LOG = LogManager.getLogger();

	    @EJB
	    protected ACMECollegeService service;

	    @Inject
	    protected SecurityContext sc;

	    // R: READ
	    @GET
	    @RolesAllowed({ADMIN_ROLE, USER_ROLE}) // Any user can retrieve the list of ClubMembership
	    public Response getClubMemberships() {
	        LOG.debug("retrieving all clubmemberships ...");
	        List<ClubMembership> clubMemberships = service.getAll(ClubMembership.class, ClubMembership.FIND_ALL);
	        Response response = Response.ok(clubMemberships).build();
	        return response;
	    }
	    
	    
	    // R: READ by id
	    @GET
	    @Path(RESOURCE_PATH_ID_PATH)
	    @RolesAllowed({ADMIN_ROLE})
	    public Response getClubMembershipById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
	        LOG.debug("try to retrieve specific clubmembership " + id);
	        Response response = null;
	        ClubMembership clubMembership = null;

	        clubMembership = service.getClubMembershipById(id);
	        if(clubMembership == null) {
	        	response = Response.status(Status.NOT_FOUND).build();
	        } else {
	        	response = response.ok(clubMembership).build();
	        }
	        return response;
	    }
	    
	    // R: Create
	    @POST
	    @RolesAllowed({ADMIN_ROLE})
	    public Response addClubMembership(ClubMembership newClubMembership) {
	    	Response response = null;
	    	service.persistClubMembership(newClubMembership);
	    	response =Response.ok(newClubMembership).build();
	    	return response;
	    }
	    
	    // R: Delete
	    @DELETE
	    @RolesAllowed({ADMIN_ROLE})
	    public Response deleteClubMembershipResponse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
	    	Response response = null;
	    	service.deleteClubMembershipById(id);
	    	response = Response.ok().build();
	    	return response;
	    }
	    
}
