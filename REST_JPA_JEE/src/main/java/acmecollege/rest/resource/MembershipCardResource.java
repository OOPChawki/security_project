/***************************************************************************
 * File:  MembershipCardResource.java Course materials (23S) CST 8277
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
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
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
import acmecollege.entity.MembershipCard;

@Path(MEMBERSHIP_CARD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    // Only an ‘ADMIN_ROLE’ user can apply CRUD to one or all MembershipCard
	// C
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMembershipCard(MembershipCard newMembershipCard) {
    	Response response = null;
    	service.persistMembershipCard(newMembershipCard);
    	LOG.debug("creating new membershipCard" + newMembershipCard);
    	response = Response.ok(newMembershipCard).build();
    	return response;
    }
	// R
    @GET
    @RolesAllowed({ADMIN_ROLE})
	public Response getMembershipCards() {
		LOG.debug("retrieving all membershipCards ...");
		List<MembershipCard> membershipCards = service.getAll(MembershipCard.class, MembershipCard.ALL_CARDS_QUERY_NAME);
		Response response = Response.ok(membershipCards).build();
		return response;
	}
    @GET
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ADMIN_ROLE, USER_ROLE}) // Only a ‘USER_ROLE’ user can read their own MembershipCard
    public Response getMembershipCardById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Retrieving membershipCard with id = {}", id);
        Response response = null;
        MembershipCard membershipCard = service.getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, id);
        if(membershipCard == null) {
        	response = Response.status(Status.NOT_FOUND).build();
        } else {
        	response = response.ok(membershipCard).build();
        }
        return response;
    }
    	
	// D
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
	public Response deleteMembershipCard(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
    	LOG.debug("deleting selected membershipCard" + id);
    	Response response = null;
		service.deleteMembershipCardById(id);
		response = Response.ok().build();
		return response;	
	}
}
