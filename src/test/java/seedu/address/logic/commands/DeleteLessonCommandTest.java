package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_LESSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_LESSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_LESSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteLessonCommand}.
 */
public class DeleteLessonCommandTest {
    private static final Lesson A_MATH = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));
    private static final Lesson PHYSICS = new Lesson("Sec 4 Physics", DayOfWeek.WEDNESDAY,
            LocalTime.of(19, 0), LocalTime.of(20, 30));
    private static final Lesson CHEMISTRY = new Lesson("Sec 4 Chemistry", DayOfWeek.FRIDAY,
            LocalTime.of(14, 0), LocalTime.of(15, 30));

    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student CHLOE = new Student(new Name("Chloe Tan"), new Phone("88888888"),
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getLessonAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getLessonAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteLessonCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteLessonCommand(INDEX_FIRST_LESSON).execute(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        // the full message is spelled out once to pin its exact wording
        String expectedMessage = "Deleted lesson: Sec 3 A-Math (Mon 1600-1800)\n"
                + "Unenrolled 2 student(s): Aiden Tan, Chloe Tan";
        expectedModel.deleteLesson(A_MATH);

        assertCommandSuccess(new DeleteLessonCommand(INDEX_FIRST_LESSON), model, expectedMessage, expectedModel);
        // later lessons move up one index
        assertEquals(List.of(PHYSICS, CHEMISTRY), model.getFilteredLessonList());
    }

    @Test
    public void execute_lastIndexUnfilteredList_success() {
        expectedModel.deleteLesson(CHEMISTRY);

        assertCommandSuccess(new DeleteLessonCommand(INDEX_THIRD_LESSON), model, getSuccessMessage(CHEMISTRY),
                expectedModel);
        assertEquals(List.of(A_MATH, PHYSICS), model.getFilteredLessonList());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredLessonList().size() + 1);
        assertDeleteFailure(new DeleteLessonCommand(outOfBoundIndex), Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);

        Index largestIndex = Index.fromOneBased(Integer.MAX_VALUE);
        assertDeleteFailure(new DeleteLessonCommand(largestIndex), Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_noLessons_throwsCommandException() {
        model = new ModelManager();
        assertDeleteFailure(new DeleteLessonCommand(INDEX_FIRST_LESSON),
                Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_indexValidOnlyForStudentList_throwsCommandException() {
        model.deleteLesson(PHYSICS);
        model.deleteLesson(CHEMISTRY);

        // 3 students but only 1 lesson are shown: the index must be read against the lesson list
        assertDeleteFailure(new DeleteLessonCommand(INDEX_SECOND_LESSON),
                Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_lessonWithStudents_removesOnlyItsEnrolments() {
        expectedModel.deleteLesson(A_MATH);

        assertCommandSuccess(new DeleteLessonCommand(INDEX_FIRST_LESSON), model,
                getSuccessMessage(A_MATH, AIDEN, CHLOE), expectedModel);

        ReadOnlyAddressBook addressBook = model.getAddressBook();
        // every student is kept, including Aiden who was only in the deleted lesson
        assertEquals(List.of(AIDEN, CHLOE, RYAN), addressBook.getStudentList());
        assertTrue(addressBook.getLessonsForStudent(AIDEN).isEmpty());
        // Chloe keeps her other lesson
        assertEquals(List.of(new Enrolment(CHLOE.getId(), PHYSICS.getId())), addressBook.getEnrolmentList());
        assertEquals(List.of(PHYSICS), addressBook.getLessonsForStudent(CHLOE));
        assertEquals(List.of(CHLOE), addressBook.getStudentsInLesson(PHYSICS));
    }

    @Test
    public void execute_lessonWithOneStudent_listsThatStudent() {
        expectedModel.deleteLesson(PHYSICS);

        assertCommandSuccess(new DeleteLessonCommand(INDEX_SECOND_LESSON), model,
                "Deleted lesson: Sec 4 Physics (Wed 1900-2030)\nUnenrolled 1 student(s): Chloe Tan", expectedModel);
    }

    @Test
    public void execute_lessonWithoutStudents_enrolmentsUnchanged() {
        List<Enrolment> enrolmentsBefore = new ArrayList<>(model.getEnrolmentList());
        expectedModel.deleteLesson(CHEMISTRY);

        // no one was unenrolled, so there is no second line
        assertCommandSuccess(new DeleteLessonCommand(INDEX_THIRD_LESSON), model,
                "Deleted lesson: Sec 4 Chemistry (Fri 1400-1530)", expectedModel);
        assertEquals(enrolmentsBefore, model.getEnrolmentList());
    }

    @Test
    public void execute_studentsEnrolledOutOfOrder_listedInStudentListOrder() {
        for (Model each : List.of(model, expectedModel)) {
            each.enrolStudent(RYAN, CHEMISTRY);
            each.enrolStudent(AIDEN, CHEMISTRY);
        }
        expectedModel.deleteLesson(CHEMISTRY);

        // the same order as on the lesson card, not the order of enrolment
        assertCommandSuccess(new DeleteLessonCommand(INDEX_THIRD_LESSON), model,
                getSuccessMessage(CHEMISTRY, AIDEN, RYAN), expectedModel);
    }

    @Test
    public void execute_lessonPastMidnight_marksNextDay() {
        Lesson nightClass = new Lesson("Night Class", DayOfWeek.FRIDAY, LocalTime.of(22, 0), LocalTime.of(1, 0));
        for (Model each : List.of(model, expectedModel)) {
            each.addLesson(nightClass);
        }
        expectedModel.deleteLesson(nightClass);
        Index nightClassIndex = Index.fromOneBased(model.getFilteredLessonList().size());

        assertCommandSuccess(new DeleteLessonCommand(nightClassIndex), model,
                "Deleted lesson: Night Class (Fri 2200-0100) (next day)", expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_successAndFilterKept() {
        model.updateFilteredLessonList(lesson -> !lesson.equals(A_MATH));
        expectedModel.updateFilteredLessonList(lesson -> !lesson.equals(A_MATH));
        expectedModel.deleteLesson(CHEMISTRY);

        // index 2 of the filtered list [PHYSICS, CHEMISTRY] is CHEMISTRY, not PHYSICS
        assertCommandSuccess(new DeleteLessonCommand(INDEX_SECOND_LESSON), model, getSuccessMessage(CHEMISTRY),
                expectedModel);
        assertEquals(List.of(PHYSICS), model.getFilteredLessonList());
        assertEquals(List.of(A_MATH, PHYSICS), model.getAddressBook().getLessonList());
        // the hidden lesson keeps its students
        assertEquals(List.of(AIDEN, CHLOE), model.getAddressBook().getStudentsInLesson(A_MATH));
    }

    @Test
    public void execute_dayFilterActive_deletesDisplayedLessonAndKeepsFilter() {
        for (Model each : List.of(model, expectedModel)) {
            new ListLessonsCommand(DayOfWeek.WEDNESDAY).execute(each);
        }
        expectedModel.deleteLesson(PHYSICS);

        assertCommandSuccess(new DeleteLessonCommand(INDEX_FIRST_LESSON), model, getSuccessMessage(PHYSICS, CHLOE),
                expectedModel);
        assertTrue(model.getFilteredLessonList().isEmpty());
        assertEquals(List.of(A_MATH, CHEMISTRY), model.getAddressBook().getLessonList());
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        model.updateFilteredLessonList(lesson -> lesson.equals(CHEMISTRY));

        // index 2 is within the full list but not the filtered list
        assertDeleteFailure(new DeleteLessonCommand(INDEX_SECOND_LESSON),
                Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_noLessonsShown_throwsCommandException() {
        model.updateFilteredLessonList(lesson -> false);

        assertDeleteFailure(new DeleteLessonCommand(INDEX_FIRST_LESSON),
                Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        assertEquals(List.of(A_MATH, PHYSICS, CHEMISTRY), model.getAddressBook().getLessonList());
    }

    @Test
    public void execute_studentsShownByDeletedLesson_showsAllStudents() {
        model.filterStudentsByLesson(A_MATH);
        assertEquals(List.of(AIDEN, CHLOE), model.getFilteredStudentList());
        expectedModel.deleteLesson(A_MATH);

        // the lesson being viewed no longer exists, so an empty students panel would mislead
        assertCommandSuccess(new DeleteLessonCommand(INDEX_FIRST_LESSON), model,
                getSuccessMessage(A_MATH, AIDEN, CHLOE), expectedModel);
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_studentsShownByOtherLesson_studentFilterKept() {
        model.filterStudentsByLesson(PHYSICS);
        expectedModel.filterStudentsByLesson(PHYSICS);
        expectedModel.deleteLesson(A_MATH);

        assertCommandSuccess(new DeleteLessonCommand(INDEX_FIRST_LESSON), model,
                getSuccessMessage(A_MATH, AIDEN, CHLOE), expectedModel);
        assertEquals(List.of(CHLOE), model.getFilteredStudentList());
    }

    @Test
    public void execute_studentSearchActive_studentFilterKept() {
        model.updateFilteredStudentList(student -> student.equals(RYAN));
        expectedModel.updateFilteredStudentList(student -> student.equals(RYAN));
        expectedModel.deleteLesson(A_MATH);

        // students hidden by the search are still reported, as they were unenrolled too
        assertCommandSuccess(new DeleteLessonCommand(INDEX_FIRST_LESSON), model,
                getSuccessMessage(A_MATH, AIDEN, CHLOE), expectedModel);
        assertEquals(List.of(RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_sameNameDifferentSchedule_deletesOnlyChosenOne() {
        Lesson thursdayMath = new Lesson("Sec 3 A-Math", DayOfWeek.THURSDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0));
        for (Model each : List.of(model, expectedModel)) {
            each.addLesson(thursdayMath);
            each.enrolStudent(RYAN, thursdayMath);
        }
        expectedModel.deleteLesson(thursdayMath);
        // Thursday comes after Wednesday but before Friday in timetable order
        Index thursdayMathIndex = Index.fromOneBased(3);

        assertCommandSuccess(new DeleteLessonCommand(thursdayMathIndex), model,
                getSuccessMessage(thursdayMath, RYAN), expectedModel);
        assertEquals(List.of(A_MATH, PHYSICS, CHEMISTRY), model.getFilteredLessonList());
        assertEquals(List.of(AIDEN, CHLOE), model.getAddressBook().getStudentsInLesson(A_MATH));
        assertTrue(model.getAddressBook().getLessonsForStudent(RYAN).isEmpty());
    }

    @Test
    public void execute_firstLessonRepeatedly_removesAllLessonsAndEnrolments() throws Exception {
        DeleteLessonCommand deleteFirstCommand = new DeleteLessonCommand(INDEX_FIRST_LESSON);
        int lessonCount = model.getFilteredLessonList().size();

        for (int i = 0; i < lessonCount; i++) {
            deleteFirstCommand.execute(model);
        }

        assertTrue(model.getAddressBook().getLessonList().isEmpty());
        assertTrue(model.getEnrolmentList().isEmpty());
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getAddressBook().getStudentList());
        assertDeleteFailure(deleteFirstCommand, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteLessonCommand deleteFirstCommand = new DeleteLessonCommand(INDEX_FIRST_LESSON);
        DeleteLessonCommand deleteSecondCommand = new DeleteLessonCommand(INDEX_SECOND_LESSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        assertTrue(deleteFirstCommand.equals(new DeleteLessonCommand(INDEX_FIRST_LESSON)));

        // different types -> returns false, even for another delete command with the same index
        assertFalse(deleteFirstCommand.equals(1));
        assertFalse(deleteFirstCommand.equals(new DeleteStudentCommand(INDEX_FIRST_LESSON)));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        DeleteLessonCommand deleteLessonCommand = new DeleteLessonCommand(INDEX_FIRST_LESSON);
        String expected = DeleteLessonCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_LESSON + "}";
        assertEquals(expected, deleteLessonCommand.toString());
    }

    /**
     * Executes {@code command} and confirms that it fails with {@code expectedMessage}, leaving the
     * address book, the filtered lesson list and the filtered student list unchanged.
     */
    private void assertDeleteFailure(DeleteLessonCommand command, String expectedMessage) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Lesson> expectedFilteredLessons = new ArrayList<>(model.getFilteredLessonList());
        List<Student> expectedFilteredStudents = new ArrayList<>(model.getFilteredStudentList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredLessons, model.getFilteredLessonList());
        assertEquals(expectedFilteredStudents, model.getFilteredStudentList());
    }

    /**
     * Returns the message for deleting {@code deletedLesson}, listing {@code unenrolledStudents} on a second
     * line if there are any.
     */
    private static String getSuccessMessage(Lesson deletedLesson, Student... unenrolledStudents) {
        String message = String.format(DeleteLessonCommand.MESSAGE_DELETE_LESSON_SUCCESS,
                Messages.format(deletedLesson));
        if (unenrolledStudents.length == 0) {
            return message;
        }
        String names = Arrays.stream(unenrolledStudents)
                .map(student -> student.getName().fullName)
                .collect(Collectors.joining(", "));
        return message + "\n" + String.format(DeleteLessonCommand.MESSAGE_UNENROLLED_STUDENTS,
                unenrolledStudents.length, names);
    }

    /**
     * Returns an address book with three students and three lessons, where Aiden and Chloe are in
     * A-Math, Chloe is also in Physics, and Chemistry has no students.
     */
    private static AddressBook getLessonAddressBook() {
        AddressBook addressBook = new AddressBook();
        List.of(AIDEN, CHLOE, RYAN).forEach(addressBook::addStudent);
        List.of(A_MATH, PHYSICS, CHEMISTRY).forEach(addressBook::addLesson);
        addressBook.addEnrolment(new Enrolment(AIDEN.getId(), A_MATH.getId()));
        addressBook.addEnrolment(new Enrolment(CHLOE.getId(), A_MATH.getId()));
        addressBook.addEnrolment(new Enrolment(CHLOE.getId(), PHYSICS.getId()));
        return addressBook;
    }
}
