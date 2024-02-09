/***************************************************************************
 * File:  ClubMembershipResource.java Course materials (23S) CST 8277
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
import acmecollege.entity.MembershipCard;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

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
	        	response = Response.ok(clubMembership).build();
	        }
	        return response;
	    }
	    
	    // R: Create
	    @POST
		@RolesAllowed({ ADMIN_ROLE })
		@Path("/{membershipCardId}/{studentClubId}")
		public Response addClubMembership(@PathParam("membershipCardId") int membershipCardId,
				@PathParam("studentClubId") int studentClubId, ClubMembership newClubMembership) {
			LOG.debug("Adding a new club membership = {}", newClubMembership);
			MembershipCard membershipCard;
			StudentClub studentClub;

			try {
				membershipCard = service.getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, membershipCardId);
			} catch (Exception e) {
				e.printStackTrace();
				HttpErrorResponse err = new HttpErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(),
						String.format("No membership card found with id %d", membershipCardId));
				return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
			}

			try {
				studentClub = service.getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME,
						studentClubId);
			} catch (Exception e) {
				e.printStackTrace();
				HttpErrorResponse err = new HttpErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(),
						String.format("No student club found with id %d", studentClubId));
				return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
			}

			service.persistClubMembership(newClubMembership, studentClubId, membershipCardId);

			return Response.ok(newClubMembership).build();
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
