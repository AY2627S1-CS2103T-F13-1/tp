package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
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

        List<Student> newStudents = List.copyOf(newData.getStudentList());
        for (int i = 0; i < newStudents.size(); i++) {
            for (int j = i + 1; j < newStudents.size(); j++) {
                if (newStudents.get(i).isSameStudent(newStudents.get(j))) {
                    throw new DuplicateStudentException();
                }
            }
        }
        setPersons(newData.getPersonList());
        students.setAll(newStudents);
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
        return students.stream().anyMatch(student::isSameStudent);
    }

    /** Adds a student, rejecting duplicates. */
    public void addStudent(Student student) {
        if (hasStudent(student)) {
            throw new DuplicateStudentException();
        }
        students.add(student);
    }

    /** Removes the student with exactly these details. */
    public void removeStudent(Student student) {
        requireNonNull(student);
        if (!students.remove(student)) {
            throw new StudentNotFoundException();
        }
    }

    @Override
    public ObservableList<Student> getStudentList() {
        return readOnlyStudents;
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("students", students)
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

        return persons.equals(otherAddressBook.persons) && students.equals(otherAddressBook.students);
    }

    @Override
    public int hashCode() {
        return 31 * persons.hashCode() + students.hashCode();
    }
}
