package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteStudentCommand}.
 */
public class DeleteStudentCommandTest {
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student CHLOE = new Student(new Name("Chloe Tan"), new Phone("88888888"),
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    private static final Lesson A_MATH = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));
    private static final Lesson PHYSICS = new Lesson("Sec 4 Physics", DayOfWeek.WEDNESDAY,
            LocalTime.of(19, 0), LocalTime.of(20, 30));

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteStudentCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteStudentCommand(INDEX_FIRST_PERSON).execute(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        // the full message is spelled out once to pin its exact wording
        String expectedMessage = "Deleted Student: Aiden Tan; Phone: -; Guardian: Tan Mei Ling (91234567)";
        expectedModel.deleteStudent(AIDEN);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);
        // later students move up one index
        assertEquals(List.of(CHLOE, RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_lastIndexUnfilteredList_success() {
        String expectedMessage = getSuccessMessage(RYAN);
        expectedModel.deleteStudent(RYAN);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_THIRD_PERSON), model, expectedMessage, expectedModel);
        assertEquals(List.of(AIDEN, CHLOE), model.getFilteredStudentList());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        assertDeleteFailure(new DeleteStudentCommand(outOfBoundIndex),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyStudentList_throwsCommandException() {
        model = new ModelManager();
        assertDeleteFailure(new DeleteStudentCommand(INDEX_FIRST_PERSON),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_successAndFilterKept() {
        model.updateFilteredStudentList(student -> !student.equals(AIDEN));
        expectedModel.updateFilteredStudentList(student -> !student.equals(AIDEN));
        expectedModel.deleteStudent(RYAN);

        // index 2 of the filtered list [CHLOE, RYAN] is RYAN, not CHLOE
        String expectedMessage = getSuccessMessage(RYAN);
        assertCommandSuccess(new DeleteStudentCommand(INDEX_SECOND_PERSON), model, expectedMessage, expectedModel);
        assertEquals(List.of(CHLOE), model.getFilteredStudentList());
        assertEquals(List.of(AIDEN, CHLOE), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        model.updateFilteredStudentList(student -> student.equals(RYAN));

        // index 2 is within the full list but not the filtered list
        assertDeleteFailure(new DeleteStudentCommand(INDEX_SECOND_PERSON),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_sameNameDifferentStudents_deletesOnlyChosenOne() {
        Student otherRyan = new Student(new Name("Ryan Lim"), new Phone("81234567"), null);
        model.addStudent(otherRyan);
        expectedModel.addStudent(otherRyan);
        expectedModel.deleteStudent(otherRyan);
        Index otherRyanIndex = Index.fromOneBased(model.getFilteredStudentList().size());

        assertCommandSuccess(new DeleteStudentCommand(otherRyanIndex), model, getSuccessMessage(otherRyan),
                expectedModel);
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_enrolledStudent_removesOnlyTheirEnrolments() {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsWithAidenAndChloeInMath(each);
            each.enrolStudent(AIDEN, PHYSICS);
        }
        expectedModel.deleteStudent(AIDEN);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_FIRST_PERSON), model, getSuccessMessage(AIDEN),
                expectedModel);

        // Chloe stays in A-Math, and Physics is kept as a lesson with no students
        assertEquals(List.of(new Enrolment(CHLOE.getId(), A_MATH.getId())), model.getEnrolmentList());
        assertEquals(List.of(A_MATH, PHYSICS), model.getAddressBook().getLessonList());
        assertEquals(List.of(CHLOE), model.getAddressBook().getStudentsInLesson(A_MATH));
        assertTrue(model.getAddressBook().getStudentsInLesson(PHYSICS).isEmpty());
    }

    @Test
    public void execute_studentWithoutLessons_enrolmentsUnchanged() {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsWithAidenAndChloeInMath(each);
        }
        List<Enrolment> enrolmentsBefore = new ArrayList<>(model.getEnrolmentList());
        expectedModel.deleteStudent(RYAN);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_THIRD_PERSON), model, getSuccessMessage(RYAN),
                expectedModel);
        assertEquals(enrolmentsBefore, model.getEnrolmentList());
    }

    @Test
    public void execute_studentsShownByLesson_viewKeptAfterDelete() {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsWithAidenAndChloeInMath(each);
            each.filterStudentsByLesson(A_MATH);
        }
        expectedModel.deleteStudent(AIDEN);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_FIRST_PERSON), model, getSuccessMessage(AIDEN),
                expectedModel);
        // Ryan, who is not in the lesson, stays hidden
        assertEquals(List.of(CHLOE), model.getFilteredStudentList());
    }

    @Test
    public void execute_lastStudentShownByLesson_emptyViewAndLessonKept() {
        for (Model each : List.of(model, expectedModel)) {
            each.addLesson(PHYSICS);
            each.enrolStudent(CHLOE, PHYSICS);
            each.filterStudentsByLesson(PHYSICS);
        }
        expectedModel.deleteStudent(CHLOE);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_FIRST_PERSON), model, getSuccessMessage(CHLOE),
                expectedModel);
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(List.of(PHYSICS), model.getAddressBook().getLessonList());
        assertEquals(List.of(AIDEN, RYAN), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_lessonListFiltered_lessonFilterKept() {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsWithAidenAndChloeInMath(each);
            each.updateFilteredLessonList(lesson -> lesson.equals(PHYSICS));
        }
        expectedModel.deleteStudent(AIDEN);

        assertCommandSuccess(new DeleteStudentCommand(INDEX_FIRST_PERSON), model, getSuccessMessage(AIDEN),
                expectedModel);
        assertEquals(List.of(PHYSICS), model.getFilteredLessonList());
    }

    @Test
    public void execute_studentAddedAgainAfterDelete_hasNoLessons() throws Exception {
        addLessonsWithAidenAndChloeInMath(model);
        new DeleteStudentCommand(INDEX_FIRST_PERSON).execute(model);
        Student aidenAgain = new Student(new Name("Aiden Tan"), null,
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));

        new AddStudentCommand(aidenAgain).execute(model);

        // a returning student is a new record and is not put back into their old lessons
        assertTrue(model.getAddressBook().getLessonsForStudent(aidenAgain).isEmpty());
        assertEquals(List.of(CHLOE), model.getAddressBook().getStudentsInLesson(A_MATH));
    }

    @Test
    public void equals() {
        DeleteStudentCommand deleteFirstCommand = new DeleteStudentCommand(INDEX_FIRST_PERSON);
        DeleteStudentCommand deleteSecondCommand = new DeleteStudentCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        assertTrue(deleteFirstCommand.equals(new DeleteStudentCommand(INDEX_FIRST_PERSON)));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));
        assertFalse(deleteFirstCommand.equals(new DeleteCommand(INDEX_FIRST_PERSON)));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        DeleteStudentCommand deleteStudentCommand = new DeleteStudentCommand(INDEX_FIRST_PERSON);
        String expected = DeleteStudentCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, deleteStudentCommand.toString());
    }

    /**
     * Executes {@code command} and confirms that it fails with {@code expectedMessage}, leaving the
     * address book and the filtered student list unchanged.
     */
    private void assertDeleteFailure(DeleteStudentCommand command, String expectedMessage) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Student> expectedFilteredList = new ArrayList<>(model.getFilteredStudentList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredList, model.getFilteredStudentList());
    }

    /**
     * Adds A-Math and Physics to {@code target}, and enrols Aiden and Chloe in A-Math.
     */
    private static void addLessonsWithAidenAndChloeInMath(Model target) {
        target.addLesson(A_MATH);
        target.addLesson(PHYSICS);
        target.enrolStudent(AIDEN, A_MATH);
        target.enrolStudent(CHLOE, A_MATH);
    }

    private static String getSuccessMessage(Student deletedStudent) {
        return String.format(DeleteStudentCommand.MESSAGE_DELETE_STUDENT_SUCCESS, Messages.format(deletedStudent));
    }

    private static AddressBook getStudentAddressBook() {
        AddressBook addressBook = new AddressBook();
        for (Student student : List.of(AIDEN, CHLOE, RYAN)) {
            addressBook.addStudent(student);
        }
        return addressBook;
    }
}
