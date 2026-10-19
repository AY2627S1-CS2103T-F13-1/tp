package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /** Returns an unmodifiable observable list of students, with no duplicates. */
    ObservableList<Student> getStudentList();

    /** Returns the unmodifiable observable lesson list. */
    ObservableList<Lesson> getLessonList();

    /** Returns the unmodifiable observable enrolment list. */
    ObservableList<Enrolment> getEnrolmentList();

    /** Returns whether the student is enrolled in the lesson. */
    default boolean isEnrolled(Student student, Lesson lesson) {
        return getEnrolmentList().contains(new Enrolment(student.getId(), lesson.getId()));
    }

    /** Returns a snapshot in student insertion order, independent of display filters. */
    default List<Student> getStudentsInLesson(Lesson lesson) {
        return getStudentList().stream().filter(s -> isEnrolled(s, lesson)).toList();
    }

    /** Returns a snapshot in lesson insertion order, independent of display filters. */
    default List<Lesson> getLessonsForStudent(Student student) {
        return getLessonList().stream().filter(l -> isEnrolled(student, l)).toList();
    }

    /**
     * Returns a snapshot, in lesson insertion order and independent of display filters, of the lessons whose times
     * overlap {@code lesson}'s. The lesson with {@code lesson}'s ID is left out, so an edited lesson does not clash
     * with its own old times.
     */
    default List<Lesson> getClashingLessons(Lesson lesson) {
        requireNonNull(lesson);
        return getLessonList().stream()
                .filter(other -> !other.getId().equals(lesson.getId()) && other.overlaps(lesson))
                .toList();
    }

}
