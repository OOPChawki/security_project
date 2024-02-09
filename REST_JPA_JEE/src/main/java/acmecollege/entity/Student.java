/***************************************************************************
 * File:  Student.java Course materials (23S) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 * Updated by:  Group 45
 *   041070929, Rohan, Kim
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   
 */
package acmecollege.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the student database table.
 */
@SuppressWarnings("unused")
// (DONE) TODO ST01 - Add the missing annotations.
@Entity
@Table(name = "student")
@NamedQuery(name = Student.ALL_STUDENTS_QUERY_NAME, 
			query = "SELECT s FROM Student s LEFT JOIN FETCH s.membershipCards LEFT JOIN FETCH s.courseRegistrations")
@NamedQuery(name = Student.QUERY_STUDENT_BY_ID, 
			query = "SELECT s FROM Student s LEFT JOIN FETCH s.membershipCards LEFT JOIN FETCH s.courseRegistrations where s.id = :param1")
// (DONE) TODO ST02 - Do we need a mapped super class? If so, which one?
public class Student extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public static final String ALL_STUDENTS_QUERY_NAME = "Student.findAll";
    public static final String QUERY_STUDENT_BY_ID = "Student.findAllByID";

    public Student() {
    	super();
    }

    // (DONE) TODO ST03 - Add annotation
    @Basic(optional = false)
    @Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	// (DONE) TODO ST04 - Add annotation
    @Basic(optional = false)
    @Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	// (DONE) TODO ST05 - Add annotations for 1:M relation.  Changes should not cascade.
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "owner")
    @JsonIgnore
	private Set<MembershipCard> membershipCards = new HashSet<>();

	// (DONE) TODO ST06 - Add annotations for 1:M relation.  Changes should not cascade.
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "student")
    @JsonIgnore
	private Set<CourseRegistration> courseRegistrations = new HashSet<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    // Simplify JSON body, skip MembershipCards
    @JsonIgnore
    public Set<MembershipCard> getMembershipCards() {
		return membershipCards;
	}

	public void setMembershipCards(Set<MembershipCard> membershipCards) {
		this.membershipCards = membershipCards;
	}

    // Simplify JSON body, skip CourseRegistrations
    @JsonIgnore
    public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

	public void setFullName(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
