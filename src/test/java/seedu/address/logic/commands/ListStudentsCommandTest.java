package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ListStudentsCommand}.
 */
public class ListStudentsCommandTest {
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student CHLOE = new Student(new Name("Chloe Tan"), new Phone("88888888"),
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListStudentsCommand().execute(null));
    }

    @Test
    public void execute_listIsNotFiltered_showsAllStudents() {
        assertCommandSuccess(new ListStudentsCommand(), model, ListStudentsCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_listIsFiltered_showsAllStudents() {
        model.updateFilteredStudentList(new StudentNameContainsKeywordsPredicate(List.of("Ryan"), List.of()));
        assertEquals(List.of(RYAN), model.getFilteredStudentList());

        assertCommandSuccess(new ListStudentsCommand(), model, ListStudentsCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_noStudents_success() {
        model = new ModelManager();
        expectedModel = new ModelManager();
        assertCommandSuccess(new ListStudentsCommand(), model, ListStudentsCommand.MESSAGE_SUCCESS, expectedModel);
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void execute_personListFiltered_personFilterUnchanged() {
        AddressBook addressBook = getTypicalAddressBook();
        getStudentAddressBook().getStudentList().forEach(addressBook::addStudent);
        model = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredPersonList(person -> false);
        expectedModel = new ModelManager(addressBook, new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);

        assertCommandSuccess(new ListStudentsCommand(), model, ListStudentsCommand.MESSAGE_SUCCESS, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getFilteredStudentList());
        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void equals() {
        ListStudentsCommand listStudentsCommand = new ListStudentsCommand();

        // same object -> returns true
        assertTrue(listStudentsCommand.equals(listStudentsCommand));

        // same type -> returns true
        assertTrue(listStudentsCommand.equals(new ListStudentsCommand()));
        assertEquals(listStudentsCommand.hashCode(), new ListStudentsCommand().hashCode());

        // different types -> returns false
        assertFalse(listStudentsCommand.equals(1));
        assertFalse(listStudentsCommand.equals(new ListCommand()));

        // null -> returns false
        assertFalse(listStudentsCommand.equals(null));
    }

    private static AddressBook getStudentAddressBook() {
        AddressBook addressBook = new AddressBook();
        for (Student student : List.of(AIDEN, CHLOE, RYAN)) {
            addressBook.addStudent(student);
        }
        return addressBook;
    }
}
