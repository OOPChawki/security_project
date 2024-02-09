/***************************************************************************
 * File:  MembershipCardResource.java Course materials (23S) CST 8277
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
import acmecollege.entity.MembershipCard;
import acmecollege.entity.PojoBase;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

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
    @Path("/{studentId}")
    public Response addMembershipCard(@PathParam("studentId") int studentId, MembershipCard newMembershipCard) {
        LOG.debug("Adding a new membership card = {}", newMembershipCard);
        MembershipCard membershipCard = service.persistMembershipCard(newMembershipCard, studentId);
        return Response.ok(membershipCard).build();
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
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{membershipCardId}")
    public Response getMembershipCardById(@PathParam("membershipCardId") int membershipCardId) {
        LOG.debug("Retrieving membership card with id = {}", membershipCardId);
        MembershipCard membershipCard;
        Response response;
        if (sc.isCallerInRole(ADMIN_ROLE)) {
                membershipCard = service.getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, membershipCardId);
                response = Response.ok(membershipCard).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {          
                membershipCard = service.getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, membershipCardId);
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();

            if (!student.getMembershipCards().stream().map(PojoBase::getId).toList().contains(membershipCardId)) {
                throw new ForbiddenException("User trying to access resource it does not own");
            } else {
                response = Response.ok(membershipCard).build();
            }

        } else {
            response = Response.status(Response.Status.BAD_REQUEST).build();
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
