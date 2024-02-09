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
package acmecollege.ejb;

import static acmecollege.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME;
import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
import static acmecollege.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
            DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        SecurityRole userRole = null;
        // (DONE) TODO ACMECS01 - Use NamedQuery on SecurityRole to find USER_ROLE
        try {
			userRole = em.createNamedQuery(SecurityRole.SECURITY_ROLE_BY_NAME_QUERY, SecurityRole.class).setParameter(PARAM1, USER_ROLE)
					.getSingleResult();	
		} catch (NoResultException e) {
			LOG.error("No SecurityRole found for USER_ROLE", e);
		}
        if (userRole != null) {
            userForNewStudent.getRoles().add(userRole);
            userRole.getUsers().add(userForNewStudent);
        }

        em.persist(userForNewStudent);
    }

    @Transactional
    public Professor setProfessorForStudentCourse(int studentId, int courseId, Professor newProfessor) {
        Student studentToBeUpdated = em.find(Student.class, studentId);
        if (studentToBeUpdated != null) { // Student exists
            Set<CourseRegistration> courseRegistrations = studentToBeUpdated.getCourseRegistrations();
            courseRegistrations.forEach(c -> {
                if (c.getCourse().getId() == courseId) {
                    if (c.getProfessor() != null) { // Professor exists
                        Professor prof = em.find(Professor.class, c.getProfessor().getId());
                        prof.setProfessor(newProfessor.getFirstName(),
                        				  newProfessor.getLastName(),
                        				  newProfessor.getDepartment());
                        em.merge(prof);
                    }
                    else { // Professor does not exist
                        c.setProfessor(newProfessor);
                        em.merge(studentToBeUpdated);
                    }
                }
            });
            return newProfessor;
        }
        else return null;  // Student doesn't exists
    }

    /**
     * To update a student
     * 
     * @param id - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
        Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentToBeUpdated;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public void deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            // (DONE) TODO ACMECS02 - Use NamedQuery on SecurityRole to find this related Student
            // so that when we remove it, the relationship from SECURITY_USER table
            // is not dangling
            TypedQuery<SecurityUser> findUser = em.createNamedQuery(SecurityUser.SECURITY_USER_BY_STUDENT_ID_QUERY, SecurityUser.class);     
            findUser.setParameter(PARAM1, id);
            SecurityUser sUser = findUser.getSingleResult();
            sUser.getRoles().clear();
            sUser.setStudent(null);
            em.remove(sUser);
            em.remove(student);
        }    
    }
    
    public List<StudentClub> getAllStudentClubs() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
        cq.select(cq.from(StudentClub.class));
        return em.createQuery(cq).getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_STUDENT_CLUB_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
    public StudentClub getStudentClubById(int id) {
        TypedQuery<StudentClub> specificStudentClubQuery = em.createNamedQuery(SPECIFIC_STUDENT_CLUB_QUERY_NAME, StudentClub.class);
        specificStudentClubQuery.setParameter(PARAM1, id);
        return specificStudentClubQuery.getSingleResult();
    }
    
    // These methods are more generic.

    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        return allQuery.getSingleResult();
    }

    @Transactional
    public StudentClub deleteStudentClub(int id) {
        
    	StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, id);
        if (sc != null) {
            Set<ClubMembership> memberships = sc.getClubMemberships();
            List<ClubMembership> list = new LinkedList<>();
            memberships.forEach(list::add);
            list.forEach(m -> {
                if (m.getCard() != null) {
                    MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, m.getCard().getId());
                    mc.setClubMembership(null);
                }
                m.setCard(null);
                em.merge(m);
            });
            em.remove(sc);
            return sc;
        }
        return null;
    }
    
    // Please study & use the methods below in your test suites
    
    public boolean isDuplicated(StudentClub newStudentClub) {
        TypedQuery<Long> allStudentClubsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allStudentClubsQuery.setParameter(PARAM1, newStudentClub.getName());
        return (allStudentClubsQuery.getSingleResult() >= 1);
    }

    @Transactional
    public StudentClub persistStudentClub(StudentClub newStudentClub) {
        em.persist(newStudentClub);
        return newStudentClub;
    }

    @Transactional
    public StudentClub updateStudentClub(int id, StudentClub updatingStudentClub) {
    	StudentClub studentClubToBeUpdated = getStudentClubById(id);
        if (studentClubToBeUpdated != null) {
            em.refresh(studentClubToBeUpdated);
            studentClubToBeUpdated.setName(updatingStudentClub.getName());
            em.merge(studentClubToBeUpdated);
            em.flush();
        }
        return studentClubToBeUpdated;
    }
    
    // R: ClubMembership - CREATE
    @Transactional
	public ClubMembership persistClubMembership(ClubMembership newClubMembership, int studentClubId,
			int membershipCardId) {
		StudentClub studentClub = getStudentClubById(studentClubId);
		MembershipCard membershipCard = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME,
				membershipCardId);

		newClubMembership.setStudentClub(studentClub);
		newClubMembership.setCard(membershipCard);
		em.persist(newClubMembership);

		studentClub.getClubMemberships().add(newClubMembership);
		membershipCard.setClubMembership(newClubMembership);

		em.merge(studentClub);
		em.merge(membershipCard);
		return newClubMembership;
	}
    // R: ClubMembership - READ by ID
    public ClubMembership getClubMembershipById(int cmId) {
        TypedQuery<ClubMembership> allClubMembershipQuery = em.createNamedQuery(ClubMembership.FIND_BY_ID, ClubMembership.class);
        allClubMembershipQuery.setParameter(PARAM1, cmId);
        return allClubMembershipQuery.getSingleResult();
    }
    // R: ClubMembership - UPDATE
    @Transactional
    public ClubMembership updateClubMembership(int id, ClubMembership clubMembershipWithUpdates) {
    	ClubMembership clubMembershipToBeUpdated = getClubMembershipById(id);
        if (clubMembershipToBeUpdated != null) {
            em.refresh(clubMembershipToBeUpdated);
            em.merge(clubMembershipWithUpdates);
            em.flush();
        }
        return clubMembershipToBeUpdated;
    }


    // R: Add method for each resource ////////////////////////////////////////////////////////////////////
    
    // R: ClubMembership - DELETE
    @Transactional
	public void deleteClubMembershipById(int id) {
		ClubMembership clubMembership = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, id);
		MembershipCard membershipCard = clubMembership.getCard();
		StudentClub studentClub = clubMembership.getStudentClub();
		if (clubMembership != null) {
			Set<ClubMembership> newMembership = studentClub.getClubMemberships();
			newMembership.remove(clubMembership);
			studentClub.setClubMembership(newMembership);
			em.merge(studentClub);

			membershipCard.setClubMembership(null);
			em.merge(membershipCard);

			em.remove(clubMembership);
		} else {
			LOG.warn("Club membership was null");
		}
	}	
    @Transactional
	public CourseRegistration getCourseRegistrationById(int studentId, int courseId) {
		TypedQuery<CourseRegistration> query = em.createNamedQuery(CourseRegistration.FIND_BY_ID,
				CourseRegistration.class);
		query.setParameter(PARAM1, studentId);
		query.setParameter("param2", courseId);
		return query.getSingleResult();
	}
    @Transactional
	public CourseRegistration persistCourseRegistration(CourseRegistration newCourseRegistration, int studentId,
			int courseId) {
		Student student = getStudentById(studentId);
		Course course = getById(Course.class, Course.GET_COURSE_BY_ID_QUERY, courseId);

		newCourseRegistration.setCourse(course);
		newCourseRegistration.setStudent(student);
		em.persist(newCourseRegistration);

		return newCourseRegistration;
	}
    @Transactional
	public CourseRegistration persistCourseRegistration(CourseRegistration newCourseRegistration, int studentId,
			int courseId, int professorId) {
		Student student = getStudentById(studentId);
		Course course = getById(Course.class, Course.GET_COURSE_BY_ID_QUERY, courseId);
		Professor professor = getById(Professor.class, Professor.GET_PROFESSOR_BY_ID_QUERY_NAME, professorId);

		newCourseRegistration.setCourse(course);
		newCourseRegistration.setStudent(student);
		newCourseRegistration.setProfessor(professor);
		em.persist(newCourseRegistration);

		return newCourseRegistration;
	}
    @Transactional
	public void deleteCourseRegistration(int studentId, int courseId) {
		CourseRegistration courseRegistration = getCourseRegistrationById(studentId, courseId);
		Student student = courseRegistration.getStudent();
		Course course = courseRegistration.getCourse();
		Professor professor = courseRegistration.getProfessor();

		if (courseRegistration != null) {
			if (professor != null) {
				professor.getCourseRegistrations().remove(courseRegistration);
				em.merge(professor);
			}

			student.getCourseRegistrations().remove(courseRegistration);
			em.merge(student);

			course.getCourseRegistrations().remove(courseRegistration);
			em.merge(course);

			em.remove(courseRegistration);
		} else {
			LOG.warn("Course registration was null");
		}
	}
	// R: Course - CREATE
	@Transactional
	public Course persistCourse(Course newCourse) {
        em.persist(newCourse);
        return newCourse;
	}
	
	// R: Course - READ by ID
	public Course getCourseById(int id) {
        TypedQuery<Course> allCourseQuery = em.createNamedQuery(Course.FIND_BY_ID, Course.class);
        allCourseQuery.setParameter(PARAM1, id);
        return allCourseQuery.getSingleResult();
	}

	
	// R: Course - DELETE
	@Transactional
	public void deleteCourseById(int id) {
        Course course = getById(Course.class, Course.FIND_BY_ID, id);    
        if (course != null) {
        	em.refresh(course);
            em.remove(course);
        }	
	}
	
	// R: Professor - CREATE
	@Transactional
	public Professor persistProfessor(Professor newProfessor) {
        em.persist(newProfessor);
        return newProfessor;
	}
	
	// R: Professor - READ by Id
	@Transactional
	public Professor getProfessorById(int id) {
        Professor professor = em.find(Professor.class, id);
        return professor;
    }
	
	// R: Professor - DELETE
	@Transactional
	public void deleteProfessorById(int id) {
        Professor professor = getById(Professor.class, Professor.QUERY_PROFESSORS_BY_ID, id);
        if (professor != null) {
        	em.refresh(professor);
            em.remove(professor);
        }
    }
	
	// R: MembershipCard - CREATE
	@Transactional
	public MembershipCard persistMembershipCard(MembershipCard newEntity, int studentId) {
		Student student = getStudentById(studentId);

		student.getMembershipCards().add(newEntity);
		newEntity.setOwner(student);
		em.merge(student);
		return newEntity;
	}
	// R: MembershipCard - DELETE
	@Transactional
	public void deleteMembershipCardById(int id) {
		MembershipCard membershipCard = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, id);
		if (membershipCard != null) {
			if (membershipCard.getClubMembership() != null) {
				deleteClubMembershipById(membershipCard.getClubMembership().getId());
				em.refresh(membershipCard);
			}

			em.remove(membershipCard);
		}
	}



    
    
}