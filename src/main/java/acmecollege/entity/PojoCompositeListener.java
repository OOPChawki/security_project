/***************************************************************************
 * File:  PojoCompositeListener.java Course materials (23S) CST 8277
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

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@SuppressWarnings("unused")

public class PojoCompositeListener {

	// (DONE) TODO PCL01 - What annotation is used when we want to do something just before object is INSERT'd into database?
	@PrePersist
	public void setCreatedOnDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		LocalDateTime now = LocalDateTime.now();
		// (DONE) TODO PCL02 - What member field(s) do we wish to alter just before object is INSERT'd in the database?
		pojoBaseComposite.setCreated(now);
		pojoBaseComposite.setUpdated(now);
		
	}

	// (DONE) TODO PCL03 - What annotation is used when we want to do something just before object is UPDATE'd into database?
	@PreUpdate
	public void setUpdatedDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		// (DONE) TODO PCL04 - What member field(s) do we wish to alter just before object is UPDATE'd in the database?
		pojoBaseComposite.setUpdated(LocalDateTime.now());
	}

}
