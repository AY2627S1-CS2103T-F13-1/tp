package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.student.Student;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

/**
 * Wraps all data at the address-book level.
 * Duplicate persons and students are rejected using their respective identity checks.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObservableList<Student> readOnlyStudents = FXCollections.unmodifiableObservableList(students);

    private final ObservableList<Lesson> lessons = FXCollections.observableArrayList();
    private final ObservableList<Enrolment> enrolments = FXCollections.observableArrayList();
    private final ObservableList<Lesson> readOnlyLessons = FXCollections.unmodifiableObservableList(lessons);
    private final ObservableList<Enrolment> readOnlyEnrolments = FXCollections.unmodifiableObservableList(enrolments);

    public AddressBook() {}

    /**
     * Creates an AddressBook using the persons and students in {@code toBeCopied}.
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        // Validate the complete replacement before changing any observable list.
        AddressBook validated = new AddressBook();
        validated.setPersons(newData.getPersonList());
        newData.getStudentList().forEach(validated::addStudent);
        newData.getLessonList().forEach(validated::addLesson);
        newData.getEnrolmentList().forEach(validated::addEnrolment);
        setPersons(validated.getPersonList());
        students.setAll(validated.students);
        lessons.setAll(validated.lessons);
        enrolments.setAll(validated.enrolments);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /** Returns whether a duplicate student exists, using the student's name and phone numbers. */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.stream().anyMatch(existing -> existing.getId().equals(student.getId())
                || student.isSameStudent(existing));
    }

    /** Adds a student, rejecting duplicates. */
    public void addStudent(Student student) {
        if (hasStudent(student)) {
            throw new DuplicateStudentException();
        }
        students.add(student);
    }

    /**
     * Replaces {@code target} with {@code editedStudent} while retaining the record ID.
     * The target must exist, and the edited student must not duplicate another student.
     */
    public void setStudent(Student target, Student editedStudent) {
        requireAllNonNull(target, editedStudent);
        if (!target.getId().equals(editedStudent.getId())) {
            throw new IllegalArgumentException("Edited student must retain the original ID.");
        }

        int targetIndex = findStudentIndex(target.getId());
        boolean hasDuplicate = students.stream()
                .filter(student -> !student.getId().equals(target.getId()))
                .anyMatch(student -> student.getId().equals(editedStudent.getId())
                        || editedStudent.isSameStudent(student));
        if (hasDuplicate) {
            throw new DuplicateStudentException();
        }

        students.set(targetIndex, editedStudent);
    }

    /** Removes the student with exactly these details. */
    public void removeStudent(Student student) {
        requireNonNull(student);
        int index = students.indexOf(student);
        if (index < 0) {
            throw new StudentNotFoundException();
        }
        UUID studentId = students.get(index).getId();
        enrolments.removeIf(link -> link.studentId().equals(studentId));
        students.remove(index);
    }

    /** Returns the list index of the student with {@code studentId}. */
    private int findStudentIndex(UUID studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(studentId)) {
                return i;
            }
        }
        throw new StudentNotFoundException();
    }

    @Override
    public ObservableList<Student> getStudentList() {
        return readOnlyStudents;
    }

    /** Checks both ID collisions and duplicate lesson details. */
    public boolean hasLesson(Lesson lesson) {
        requireNonNull(lesson);
        return lessons.stream().anyMatch(existing -> existing.getId().equals(lesson.getId())
                || existing.isSameLesson(lesson));
    }

    /** Adds a lesson; throws IllegalArgumentException for a duplicate. */
    public void addLesson(Lesson lesson) {
        if (hasLesson(lesson)) {
            throw new IllegalArgumentException("This lesson already exists.");
        }
        lessons.add(lesson);
    }

    /**
     * Replaces {@code target} with {@code editedLesson} while retaining the record ID.
     * The target must exist, and the edited lesson must not duplicate another lesson.
     */
    public void setLesson(Lesson target, Lesson editedLesson) {
        requireAllNonNull(target, editedLesson);
        if (!target.getId().equals(editedLesson.getId())) {
            throw new IllegalArgumentException("Edited lesson must retain the original ID.");
        }

        int targetIndex = findLessonIndex(target.getId());
        boolean hasDuplicate = lessons.stream()
                .filter(lesson -> !lesson.getId().equals(target.getId()))
                .anyMatch(lesson -> lesson.getId().equals(editedLesson.getId())
                        || editedLesson.isSameLesson(lesson));
        if (hasDuplicate) {
            throw new IllegalArgumentException("This lesson already exists.");
        }

        lessons.set(targetIndex, editedLesson);
    }

    /** Deletes a lesson and its links, keeping all students. */
    public void removeLesson(Lesson lesson) {
        requireNonNull(lesson);
        if (!lessons.contains(lesson)) {
            throw new IllegalArgumentException("Lesson not found.");
        }
        enrolments.removeIf(link -> link.lessonId().equals(lesson.getId()));
        lessons.remove(lesson);
    }

    /** Returns the list index of the lesson with {@code lessonId}. */
    private int findLessonIndex(UUID lessonId) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getId().equals(lessonId)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Lesson not found.");
    }

    /** Adds a link after validating its references and uniqueness. */
    public void addEnrolment(Enrolment enrolment) {
        requireNonNull(enrolment);
        if (students.stream().noneMatch(s -> s.getId().equals(enrolment.studentId()))
                || lessons.stream().noneMatch(l -> l.getId().equals(enrolment.lessonId()))) {
            throw new IllegalArgumentException("Enrolment refers to a missing student or lesson.");
        }
        if (enrolments.contains(enrolment)) {
            throw new IllegalArgumentException("Student is already enrolled in this lesson.");
        }
        enrolments.add(enrolment);
    }

    @Override
    public ObservableList<Lesson> getLessonList() {
        return readOnlyLessons;
    }

    @Override
    public ObservableList<Enrolment> getEnrolmentList() {
        return readOnlyEnrolments;
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("students", students)
                .add("lessons", lessons)
                .add("enrolments", enrolments)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons) && students.equals(otherAddressBook.students)
                && lessons.equals(otherAddressBook.lessons) && enrolments.equals(otherAddressBook.enrolments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, students, lessons, enrolments);
    }
}
