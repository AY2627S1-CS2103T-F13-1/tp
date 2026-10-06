package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

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
 * Contains integration tests (interaction with the Model) for {@code FindStudentCommand}.
 */
public class FindStudentCommandTest {
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student CHLOE = new Student(new Name("Chloe Tan"), new Phone("88888888"),
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);
    private static final Student JOEL = new Student(new Name("Joel Ong"), null,
            new Guardian(new Name("Lim Wei"), new Phone("98765432")));

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
    }

    @Test
    public void equals() {
        StudentNameContainsKeywordsPredicate firstPredicate =
                new StudentNameContainsKeywordsPredicate(List.of("first"), List.of());
        StudentNameContainsKeywordsPredicate secondPredicate =
                new StudentNameContainsKeywordsPredicate(List.of(), List.of("second"));

        FindStudentCommand findFirstCommand = new FindStudentCommand(firstPredicate);
        FindStudentCommand findSecondCommand = new FindStudentCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        assertTrue(findFirstCommand.equals(new FindStudentCommand(firstPredicate)));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_noMatchingStudent_noStudentFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Zack"), List.of()), List.of());
    }

    @Test
    public void execute_studentNameKeywords_multipleStudentsFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Tan", "Ryan"), List.of()),
                List.of(AIDEN, CHLOE, RYAN));
    }

    @Test
    public void execute_guardianNameKeywords_studentsOfGuardianFound() {
        // RYAN has the same surname but no guardian, so only students whose guardian matches are found
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Lim")), List.of(JOEL));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ling")),
                List.of(AIDEN, CHLOE));
    }

    @Test
    public void execute_studentAndGuardianNameKeywords_studentsMatchingBothFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Chloe", "Joel"), List.of("Ling")),
                List.of(CHLOE));
    }

    @Test
    public void execute_previousFilter_replacedNotNarrowed() {
        model.updateFilteredStudentList(student -> student.equals(RYAN));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of()), List.of(AIDEN));
    }

    @Test
    public void toStringMethod() {
        StudentNameContainsKeywordsPredicate predicate =
                new StudentNameContainsKeywordsPredicate(List.of("keyword"), List.of());
        FindStudentCommand findStudentCommand = new FindStudentCommand(predicate);
        String expected = FindStudentCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findStudentCommand.toString());
    }

    private void assertFindSuccess(StudentNameContainsKeywordsPredicate predicate, List<Student> expectedStudents) {
        String expectedMessage = String.format(MESSAGE_STUDENTS_LISTED_OVERVIEW, expectedStudents.size());
        expectedModel.updateFilteredStudentList(predicate);
        assertCommandSuccess(new FindStudentCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(expectedStudents, model.getFilteredStudentList());
    }

    private static AddressBook getStudentAddressBook() {
        AddressBook addressBook = new AddressBook();
        for (Student student : List.of(AIDEN, CHLOE, RYAN, JOEL)) {
            addressBook.addStudent(student);
        }
        return addressBook;
    }
}
