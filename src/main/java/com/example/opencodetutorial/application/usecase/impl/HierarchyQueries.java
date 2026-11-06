package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.domain.exception.CenterNotFoundException;
import com.example.opencodetutorial.domain.exception.CourseNotFoundException;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.exception.ProfessorNotFoundException;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.exception.SubjectNotFoundException;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.domain.port.CenterRepository;
import com.example.opencodetutorial.domain.port.CourseRepository;
import com.example.opencodetutorial.domain.port.GroupRepository;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.stereotype.Component;

/**
 * Resolves the {@code center -> course -> group} chain taken from the canonical URL and
 * turns every broken link into the matching {@code *NotFoundException}, so the REST
 * layer answers 404 without each use case repeating the same lookups.
 *
 * <p>A resource that exists but belongs to another parent is reported as not found too:
 * from the caller's point of view it is not reachable under that URL.
 */
@Component
public class HierarchyQueries {

    private final CenterRepository centerRepository;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Builds the component with its required collaborators.
     *
     * @param centerRepository   the center repository, must not be null
     * @param courseRepository   the course repository, must not be null
     * @param groupRepository    the group repository, must not be null
     * @param studentRepository  the student repository, must not be null
     * @param professorRepository the professor repository, must not be null
     * @param subjectRepository  the subject repository, must not be null
     */
    public HierarchyQueries(CenterRepository centerRepository,
                            CourseRepository courseRepository,
                            GroupRepository groupRepository,
                            StudentRepository studentRepository,
                            ProfessorRepository professorRepository,
                            SubjectRepository subjectRepository) {
        this.centerRepository = centerRepository;
        this.courseRepository = courseRepository;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * Loads the center, failing with 404 semantics when it does not exist.
     *
     * @param centerId the center identifier, must not be null
     * @return the existing center
     */
    public Center requireCenter(CenterId centerId) {
        return centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterNotFoundException(centerId));
    }

    /**
     * Loads a course that belongs to the given center.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @return the existing course
     */
    public Course requireCourse(CenterId centerId, CourseId courseId) {
        requireCenter(centerId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        if (!course.centerId().equals(centerId)) {
            throw new CourseNotFoundException(courseId);
        }
        return course;
    }

    /**
     * Loads a group that belongs to the given course, verifying the whole chain.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @param groupId  the group identifier, must not be null
     * @return the existing group
     */
    public Group requireGroup(CenterId centerId, CourseId courseId, GroupId groupId) {
        requireCourse(centerId, courseId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
        if (!group.courseId().equals(courseId)) {
            throw new GroupNotFoundException(groupId);
        }
        return group;
    }

    /**
     * Loads a subject that belongs to the given course, verifying the whole chain.
     *
     * @param centerId  the center identifier, must not be null
     * @param courseId  the course identifier, must not be null
     * @param subjectId the subject identifier, must not be null
     * @return the existing subject
     */
    public Subject requireSubject(CenterId centerId, CourseId courseId, SubjectId subjectId) {
        requireCourse(centerId, courseId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));
        if (!subject.courseId().equals(courseId)) {
            throw new SubjectNotFoundException(subjectId);
        }
        return subject;
    }

    /**
     * Loads a student that belongs to the given center.
     *
     * @param centerId  the center identifier, must not be null
     * @param studentId the student identifier, must not be null
     * @return the existing student
     */
    public Student requireStudent(CenterId centerId, StudentId studentId) {
        requireCenter(centerId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        if (!student.centerId().equals(centerId)) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }

    /**
     * Loads a professor that belongs to the given center.
     *
     * <p>A professor of another center is reported as not found too: from the caller's
     * point of view it is not reachable under that URL.
     *
     * @param centerId    the center identifier, must not be null
     * @param professorId the professor identifier, must not be null
     * @return the existing professor
     */
    public Professor requireProfessor(CenterId centerId, ProfessorId professorId) {
        requireCenter(centerId);
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ProfessorNotFoundException(professorId));
        if (!professor.centerId().equals(centerId)) {
            throw new ProfessorNotFoundException(professorId);
        }
        return professor;
    }

    /**
     * Loads the course a group belongs to.
     *
     * @param group the group whose course is needed, must not be null
     * @return the course that owns the group
     */
    public Course requireCourseOf(Group group) {
        return courseRepository.findById(group.courseId())
                .orElseThrow(() -> new CourseNotFoundException(group.courseId()));
    }

    /**
     * Loads a group whose course must belong to the given center. Used when a payload
     * carries bare group identifiers instead of a canonical URL chain: each group is
     * resolved through its own course, which is then checked against the center.
     *
     * @param centerId the expected owning center, must not be null
     * @param groupId  the group identifier, must not be null
     * @return the existing group, whose course belongs to {@code centerId}
     * @throws CourseNotFoundException if the group's course belongs to another center
     */
    public Group requireGroupOfCenter(CenterId centerId, GroupId groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
        requireCourse(centerId, group.courseId());
        return group;
    }
}