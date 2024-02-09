/**
 * File:  ACMEColegeService.java
 * Course materials (23S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 45
 * 041081021 Moulayat Chawki
 * 04107092, Rohan, Kim
 * 041014922 Hoskol Mohamad
 * 041055255 Nikhil Krishnaa
 */
package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2023-11-26T23:22:04.300-0500")
@StaticMetamodel(ClubMembership.class)
public class ClubMembership_ extends PojoBase_ {
	public static volatile SingularAttribute<ClubMembership, StudentClub> club;
	public static volatile SingularAttribute<ClubMembership, MembershipCard> card;
	public static volatile SingularAttribute<ClubMembership, DurationAndStatus> durationAndStatus;
}
