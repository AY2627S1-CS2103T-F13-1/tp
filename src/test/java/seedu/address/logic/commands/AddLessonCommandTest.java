package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_LESSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code AddLessonCommand}.
 */
public class AddLessonCommandTest {
    private static final Lesson A_MATH = lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");
    private static final Lesson PHYSICS = lesson("Sec 4 Physics", DayOfWeek.WEDNESDAY, "19:00", "20:30");
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        expectedModel = new ModelManager();
    }

    @Test
    public void constructor_nullLesson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddLessonCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddLessonCommand(A_MATH).execute(null));
    }

    @Test
    public void execute_newLesson_addSuccessful() {
        // the full message is spelled out once to pin its exact wording
        String expectedMessage = "New lesson added: Sec 3 A-Math (Mon 1600-1800)";
        expectedModel.addLesson(A_MATH);

        assertCommandSuccess(new AddLessonCommand(A_MATH), model, expectedMessage, expectedModel);
        assertEquals(List.of(A_MATH), model.getFilteredLessonList());
        assertTrue(model.getEnrolmentList().isEmpty());
    }

    @Test
    public void execute_lessonPastMidnight_addSuccessful() {
        Lesson nightClass = lesson("Night Class", DayOfWeek.FRIDAY, "22:00", "01:00");
        expectedModel.addLesson(nightClass);

        assertCommandSuccess(new AddLessonCommand(nightClass), model,
                "New lesson added: Night Class (Fri 2200-0100)", expectedModel);
    }

    @Test
    public void execute_otherLessonsExist_addedAtEnd() {
        model.addLesson(PHYSICS);
        expectedModel.addLesson(PHYSICS);
        expectedModel.addLesson(A_MATH);

        assertCommandSuccess(new AddLessonCommand(A_MATH), model, getSuccessMessage(A_MATH), expectedModel);
        assertEquals(List.of(PHYSICS, A_MATH), model.getFilteredLessonList());
    }

    @Test
    public void execute_sameLesson_throwsCommandException() {
        model.addLesson(A_MATH);

        // the full message is spelled out once to pin its exact wording
        assertAddFailure(new AddLessonCommand(A_MATH), "This lesson already exists");
    }

    @Test
    public void execute_sameDetailsNewId_throwsCommandException() {
        model.addLesson(A_MATH);
        Lesson typedAgain = lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");

        assertAddFailure(new AddLessonCommand(typedAgain), AddLessonCommand.MESSAGE_DUPLICATE_LESSON);
    }

    @Test
    public void execute_sameDetailsDifferentCaseOrSpacing_throwsCommandException() {
        model.addLesson(A_MATH);

        for (String name : List.of("sec 3 a-math", "SEC 3 A-MATH", "  Sec   3  A-Math  ")) {
            Lesson duplicate = lesson(name, DayOfWeek.MONDAY, "16:00", "18:00");
            assertAddFailure(new AddLessonCommand(duplicate), AddLessonCommand.MESSAGE_DUPLICATE_LESSON);
        }
    }

    @Test
    public void execute_duplicateHiddenByLessonFilter_throwsCommandException() {
        model.addLesson(A_MATH);
        model.updateFilteredLessonList(lesson -> false);
        Lesson typedAgain = lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");

        // a lesson the tutor cannot currently see is still a duplicate, and the filter is left alone
        assertAddFailure(new AddLessonCommand(typedAgain), AddLessonCommand.MESSAGE_DUPLICATE_LESSON);
        assertTrue(model.getFilteredLessonList().isEmpty());
    }

    @Test
    public void execute_sameCommandTwice_secondThrowsCommandException() throws Exception {
        AddLessonCommand command = new AddLessonCommand(A_MATH);
        command.execute(model);

        assertAddFailure(command, AddLessonCommand.MESSAGE_DUPLICATE_LESSON);
        assertEquals(List.of(A_MATH), model.getAddressBook().getLessonList());
    }

    @Test
    public void execute_sameNameDifferentSchedule_addSuccessful() {
        model.addLesson(A_MATH);
        expectedModel.addLesson(A_MATH);
        List<Lesson> otherSchedules = List.of(
                lesson("Sec 3 A-Math", DayOfWeek.TUESDAY, "16:00", "18:00"), // different day
                lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:01", "18:00"), // different start time
                lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "17:59"), // different end time
                lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "18:00", "16:00")); // times swapped: ends the next day

        for (Lesson other : otherSchedules) {
            expectedModel.addLesson(other);
            assertCommandSuccess(new AddLessonCommand(other), model, getSuccessMessage(other), expectedModel);
        }
        assertEquals(5, model.getAddressBook().getLessonList().size());
    }

    @Test
    public void execute_similarNameSameSchedule_addSuccessful() {
        model.addLesson(A_MATH);
        expectedModel.addLesson(A_MATH);

        // only case and extra spaces are ignored; punctuation and spelling still tell lessons apart
        for (String name : List.of("Sec 3 A Math", "Sec 3 AMath", "Sec 3 A-Maths", "Sec3 A-Math")) {
            Lesson other = lesson(name, DayOfWeek.MONDAY, "16:00", "18:00");
            expectedModel.addLesson(other);
            assertCommandSuccess(new AddLessonCommand(other), model, getSuccessMessage(other), expectedModel);
        }
    }

    @Test
    public void execute_clashingTimes_addSuccessful() {
        model.addLesson(A_MATH);
        expectedModel.addLesson(A_MATH);
        List<Lesson> clashes = List.of(
                lesson("Sec 4 Physics", DayOfWeek.MONDAY, "16:00", "18:00"), // same slot
                lesson("Sec 2 Science", DayOfWeek.MONDAY, "17:00", "19:00"), // overlaps the end
                lesson("Sec 1 Math", DayOfWeek.MONDAY, "16:30", "17:00")); // inside the slot

        // clashes are not checked, so the tutor decides whether they are a mistake
        for (Lesson clash : clashes) {
            expectedModel.addLesson(clash);
            assertCommandSuccess(new AddLessonCommand(clash), model, getSuccessMessage(clash), expectedModel);
        }
    }

    @Test
    public void execute_lessonListFiltered_showsAllLessonsAfterAdd() {
        model.addLesson(PHYSICS);
        model.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.FRIDAY);
        assertTrue(model.getFilteredLessonList().isEmpty());
        expectedModel.addLesson(PHYSICS);
        expectedModel.addLesson(A_MATH);

        // otherwise a lesson on a hidden day would seem not to have been added
        assertCommandSuccess(new AddLessonCommand(A_MATH), model, getSuccessMessage(A_MATH), expectedModel);
        assertEquals(List.of(PHYSICS, A_MATH), model.getFilteredLessonList());
    }

    @Test
    public void execute_studentListFiltered_studentFilterKept() {
        for (Model each : List.of(model, expectedModel)) {
            each.addStudent(AIDEN);
            each.addStudent(RYAN);
            each.updateFilteredStudentList(student -> student.equals(RYAN));
        }
        expectedModel.addLesson(A_MATH);

        assertCommandSuccess(new AddLessonCommand(A_MATH), model, getSuccessMessage(A_MATH), expectedModel);
        assertEquals(List.of(RYAN), model.getFilteredStudentList());
        assertEquals(List.of(AIDEN, RYAN), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_studentsShownByLesson_viewAndEnrolmentsKept() {
        for (Model each : List.of(model, expectedModel)) {
            each.addStudent(AIDEN);
            each.addStudent(RYAN);
            each.addLesson(PHYSICS);
            each.enrolStudent(AIDEN, PHYSICS);
            each.filterStudentsByLesson(PHYSICS);
        }
        expectedModel.addLesson(A_MATH);

        assertCommandSuccess(new AddLessonCommand(A_MATH), model, getSuccessMessage(A_MATH), expectedModel);
        assertEquals(List.of(AIDEN), model.getFilteredStudentList());
        assertEquals(List.of(new Enrolment(AIDEN.getId(), PHYSICS.getId())), model.getEnrolmentList());
        assertTrue(model.getAddressBook().getStudentsInLesson(A_MATH).isEmpty());
    }

    @Test
    public void execute_sameDetailsAsDeletedLesson_startsWithNoStudents() throws Exception {
        model.addStudent(AIDEN);
        model.addLesson(A_MATH);
        model.enrolStudent(AIDEN, A_MATH);
        model.deleteLesson(A_MATH);
        Lesson typedAgain = lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");

        new AddLessonCommand(typedAgain).execute(model);

        assertEquals(List.of(typedAgain), model.getAddressBook().getLessonList());
        assertTrue(model.getEnrolmentList().isEmpty());
        assertTrue(model.getAddressBook().getStudentsInLesson(typedAgain).isEmpty());
    }

    @Test
    public void execute_deletedLessonWithSameId_startsWithNoStudents() throws Exception {
        model.addStudent(AIDEN);
        model.addLesson(A_MATH);
        model.enrolStudent(AIDEN, A_MATH);
        model.deleteLesson(A_MATH);

        // even with its old ID, a deleted lesson must not bring its old students back
        new AddLessonCommand(A_MATH).execute(model);

        assertEquals(List.of(A_MATH), model.getAddressBook().getLessonList());
        assertTrue(model.getAddressBook().getStudentsInLesson(A_MATH).isEmpty());
        assertTrue(model.getAddressBook().getLessonsForStudent(AIDEN).isEmpty());
    }

    @Test
    public void equals() {
        AddLessonCommand addMathCommand = new AddLessonCommand(A_MATH);

        // same object -> returns true
        assertTrue(addMathCommand.equals(addMathCommand));

        // same lesson -> returns true
        assertTrue(addMathCommand.equals(new AddLessonCommand(A_MATH)));

        // same details with another generated ID -> returns true, as every parse generates a new ID
        assertTrue(addMathCommand.equals(new AddLessonCommand(
                lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00"))));

        // different types -> returns false
        assertFalse(addMathCommand.equals(1));
        assertFalse(addMathCommand.equals(new DeleteLessonCommand(INDEX_FIRST_LESSON)));

        // null -> returns false
        assertFalse(addMathCommand.equals(null));

        // different name -> returns false, even if only the case differs, as the stored name would differ
        assertFalse(addMathCommand.equals(new AddLessonCommand(PHYSICS)));
        assertFalse(addMathCommand.equals(new AddLessonCommand(
                lesson("sec 3 a-math", DayOfWeek.MONDAY, "16:00", "18:00"))));

        // different day -> returns false
        assertFalse(addMathCommand.equals(new AddLessonCommand(
                lesson("Sec 3 A-Math", DayOfWeek.TUESDAY, "16:00", "18:00"))));

        // different start time -> returns false
        assertFalse(addMathCommand.equals(new AddLessonCommand(
                lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:01", "18:00"))));

        // different end time -> returns false
        assertFalse(addMathCommand.equals(new AddLessonCommand(
                lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:01"))));
    }

    @Test
    public void toStringMethod() {
        AddLessonCommand command = new AddLessonCommand(A_MATH);
        String expected = AddLessonCommand.class.getCanonicalName() + "{toAdd=" + A_MATH + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Executes {@code command} and confirms that it fails with {@code expectedMessage}, leaving the
     * address book, the filtered lesson list and the filtered student list unchanged.
     */
    private void assertAddFailure(AddLessonCommand command, String expectedMessage) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Lesson> expectedFilteredLessons = new ArrayList<>(model.getFilteredLessonList());
        List<Student> expectedFilteredStudents = new ArrayList<>(model.getFilteredStudentList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredLessons, model.getFilteredLessonList());
        assertEquals(expectedFilteredStudents, model.getFilteredStudentList());
    }

    private static String getSuccessMessage(Lesson addedLesson) {
        return String.format(AddLessonCommand.MESSAGE_SUCCESS, addedLesson);
    }

    /** Creates a lesson with a newly generated ID; times are given as {@code HH:mm}. */
    private static Lesson lesson(String name, DayOfWeek day, String startTime, String endTime) {
        return new Lesson(name, day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }
}
