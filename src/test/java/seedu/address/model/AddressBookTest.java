package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.student.Student;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void setStudent_differentId_throwsIllegalArgumentException() {
        Student target = student("Aiden Tan", "91234567");
        Student editedStudent = student("Aiden Lee", "92345678");
        addressBook.addStudent(target);

        assertThrows(IllegalArgumentException.class, () -> addressBook.setStudent(target, editedStudent));
        assertEquals(List.of(target), addressBook.getStudentList());
    }

    @Test
    public void setStudent_targetNotFound_throwsStudentNotFoundException() {
        Student existingStudent = student("Chloe Tan", "93456789");
        Student missingStudent = student("Aiden Tan", "91234567");
        Student editedStudent = new Student(missingStudent.getId(), new Name("Aiden Lee"),
                new Phone("92345678"), null);
        addressBook.addStudent(existingStudent);

        assertThrows(StudentNotFoundException.class, () -> addressBook.setStudent(missingStudent, editedStudent));
        assertEquals(List.of(existingStudent), addressBook.getStudentList());
    }

    @Test
    public void setLesson_differentId_throwsIllegalArgumentException() {
        Lesson target = lesson("Mathematics", DayOfWeek.MONDAY, "16:00", "18:00");
        Lesson editedLesson = lesson("Advanced Mathematics", DayOfWeek.TUESDAY, "17:00", "19:00");
        addressBook.addLesson(target);

        assertThrows(IllegalArgumentException.class, () -> addressBook.setLesson(target, editedLesson));
        assertEquals(List.of(target), addressBook.getLessonList());
    }

    @Test
    public void setLesson_targetNotFound_throwsIllegalArgumentException() {
        Lesson existingLesson = lesson("Science", DayOfWeek.WEDNESDAY, "14:00", "16:00");
        Lesson missingLesson = lesson("Mathematics", DayOfWeek.MONDAY, "16:00", "18:00");
        Lesson editedLesson = new Lesson(missingLesson.getId(), "Advanced Mathematics", DayOfWeek.TUESDAY,
                LocalTime.parse("17:00"), LocalTime.parse("19:00"));
        addressBook.addLesson(existingLesson);

        assertThrows(IllegalArgumentException.class, () -> addressBook.setLesson(missingLesson, editedLesson));
        assertEquals(List.of(existingLesson), addressBook.getLessonList());
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", students=" + addressBook.getStudentList()
                + ", lessons=" + addressBook.getLessonList() + ", enrolments=" + addressBook.getEnrolmentList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    private static Student student(String name, String phone) {
        return new Student(new Name(name), new Phone(phone), null);
    }

    private static Lesson lesson(String name, DayOfWeek day, String startTime, String endTime) {
        return new Lesson(name, day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Lesson> getLessonList() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public ObservableList<Enrolment> getEnrolmentList() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public ObservableList<Student> getStudentList() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

}
