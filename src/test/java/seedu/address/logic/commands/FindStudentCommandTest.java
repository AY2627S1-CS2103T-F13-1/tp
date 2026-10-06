package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW;
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
    private static final Student WEI_JIE = new Student(new Name("Tan Wei Jie"), new Phone("81112222"),
            new Guardian(new Name("Ong Siew Hoon"), new Phone("82223333")));

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindStudentCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FindStudentCommand command = new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of()));
        assertThrows(NullPointerException.class, () -> command.execute(null));
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
        assertTrue(findFirstCommand.equals(new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("first"), List.of()))));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));
        assertFalse(findFirstCommand.equals(firstPredicate));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_noMatchingStudent_noStudentFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Zack"), List.of()), List.of());
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Zack")), List.of());
    }

    @Test
    public void execute_emptyStudentList_noStudentFound() {
        model = new ModelManager();
        expectedModel = new ModelManager();
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of("Tan")), List.of());
    }

    @Test
    public void execute_studentNameKeyword_singleStudentFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Ryan"), List.of()), List.of(RYAN));
    }

    @Test
    public void execute_studentNameKeywords_multipleStudentsFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Tan", "Ryan"), List.of()),
                List.of(AIDEN, CHLOE, RYAN, WEI_JIE));
    }

    @Test
    public void execute_keywordOrder_resultsKeepStudentListOrder() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Joel", "Aiden"), List.of()),
                List.of(AIDEN, JOEL));
    }

    @Test
    public void execute_mixedCaseKeywords_studentsFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("cHLOE"), List.of("tAN")),
                List.of(CHLOE));
    }

    @Test
    public void execute_partialWordKeywords_noStudentFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Ai", "Chlo"), List.of()), List.of());
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ta")), List.of());
    }

    @Test
    public void execute_guardianNameKeywords_studentsOfGuardianFound() {
        // RYAN has the same surname but no guardian, so only students whose guardian matches are found
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Lim")), List.of(JOEL));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ling")),
                List.of(AIDEN, CHLOE));
    }

    @Test
    public void execute_wordInStudentAndGuardianNames_searchesOnlyRequestedName() {
        // "Wei" is in WEI_JIE's own name and in JOEL's guardian's name
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Wei"), List.of()), List.of(WEI_JIE));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Wei")), List.of(JOEL));

        // "Ong" is in JOEL's own name and in WEI_JIE's guardian's name
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Ong"), List.of()), List.of(JOEL));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ong")), List.of(WEI_JIE));
    }

    @Test
    public void execute_studentAndGuardianNameKeywords_studentsMatchingBothFound() {
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Chloe", "Joel"), List.of("Ling")),
                List.of(CHLOE));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Tan"), List.of("Tan")),
                List.of(AIDEN, CHLOE));
    }

    @Test
    public void execute_studentAndGuardianNameKeywordsMatchDifferentStudents_noStudentFound() {
        // RYAN matches the student name and JOEL matches the guardian name, but neither matches both
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Ryan"), List.of("Lim")), List.of());
    }

    @Test
    public void execute_previousFilter_replacedNotNarrowed() {
        model.updateFilteredStudentList(student -> student.equals(RYAN));
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of()), List.of(AIDEN));
    }

    @Test
    public void execute_repeatedSearch_sameResult() {
        StudentNameContainsKeywordsPredicate predicate =
                new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ling"));
        assertFindSuccess(predicate, List.of(AIDEN, CHLOE));
        assertFindSuccess(predicate, List.of(AIDEN, CHLOE));
    }

    @Test
    public void execute_studentsAndPersons_onlyStudentListFiltered() {
        AddressBook addressBook = getTypicalAddressBook();
        getStudentAddressBook().getStudentList().forEach(addressBook::addStudent);
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());

        // "Kurz" and "Meier" are names of typical persons, not students
        assertFindSuccess(new StudentNameContainsKeywordsPredicate(List.of("Aiden", "Kurz", "Meier"), List.of()),
                List.of(AIDEN));
        assertEquals(getTypicalPersons(), model.getFilteredPersonList());
        assertEquals(addressBook, model.getAddressBook());
    }

    @Test
    public void execute_studentAddedAfterSearch_searchCanFindNewStudent() {
        StudentNameContainsKeywordsPredicate predicate =
                new StudentNameContainsKeywordsPredicate(List.of("Hannah"), List.of());
        assertFindSuccess(predicate, List.of());

        Student hannah = new Student(new Name("Hannah Goh"), new Phone("87654321"), null);
        model.addStudent(hannah);
        expectedModel.addStudent(hannah);
        assertFindSuccess(predicate, List.of(hannah));
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
        for (Student student : List.of(AIDEN, CHLOE, RYAN, JOEL, WEI_JIE)) {
            addressBook.addStudent(student);
        }
        return addressBook;
    }
}
