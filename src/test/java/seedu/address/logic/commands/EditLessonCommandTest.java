package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_LESSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_LESSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditLessonCommand.EditLessonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

/**
 * Contains integration and unit tests for {@code EditLessonCommand}.
 */
public class EditLessonCommandTest {
    private static final Lesson A_MATH = lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");
    private static final Lesson PHYSICS = lesson("Sec 4 Physics", DayOfWeek.WEDNESDAY, "19:00", "20:30");
    private static final Lesson CHEMISTRY = lesson("Sec 4 Chemistry", DayOfWeek.FRIDAY, "15:00", "16:30");
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getLessonAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditLessonCommand(null, descriptorWithName("Mathematics")));
    }

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditLessonCommand(INDEX_FIRST_LESSON, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EditLessonCommand command = new EditLessonCommand(
                INDEX_FIRST_LESSON, descriptorWithName("Mathematics"));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_allFieldsEdited_success() {
        EditLessonDescriptor descriptor = descriptor(
                "Advanced Mathematics", DayOfWeek.TUESDAY, "17:00", "19:00");
        Lesson editedLesson = lessonWithId(
                A_MATH, "Advanced Mathematics", DayOfWeek.TUESDAY, "17:00", "19:00");
        Model expectedModel = copyModel();
        expectedModel.setLesson(A_MATH, editedLesson);

        assertCommandSuccess(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor), model,
                getSuccessMessage(editedLesson), expectedModel);
        assertEquals(A_MATH.getId(), model.getFilteredLessonList().getFirst().getId());
        assertEquals(List.of(new Enrolment(AIDEN.getId(), A_MATH.getId())), model.getEnrolmentList());
        assertEquals(List.of(AIDEN), model.getAddressBook().getStudentsInLesson(editedLesson));
    }

    @Test
    public void execute_oneFieldEdited_unspecifiedFieldsRetained() {
        EditLessonDescriptor descriptor = descriptorWithName("Mathematics");
        Lesson editedLesson = lessonWithId(
                A_MATH, "Mathematics", DayOfWeek.MONDAY, "16:00", "18:00");
        Model expectedModel = copyModel();
        expectedModel.setLesson(A_MATH, editedLesson);

        assertCommandSuccess(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor), model,
                getSuccessMessage(editedLesson), expectedModel);
    }

    @Test
    public void execute_overnightLesson_success() {
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setStartTime(LocalTime.parse("22:00"));
        descriptor.setEndTime(LocalTime.parse("01:00"));
        Lesson editedLesson = lessonWithId(A_MATH, "Sec 3 A-Math", DayOfWeek.MONDAY, "22:00", "01:00");
        Model expectedModel = copyModel();
        expectedModel.setLesson(A_MATH, editedLesson);

        assertCommandSuccess(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor), model,
                getSuccessMessage(editedLesson), expectedModel);
    }

    @Test
    public void execute_startTimeBecomesEndTime_throwsCommandException() {
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setStartTime(A_MATH.getEndTime());

        assertEditFailure(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor),
                Lesson.MESSAGE_TIME_CONSTRAINTS);
    }

    @Test
    public void execute_invalidLessonIndex_throwsCommandException() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredLessonList().size() + 1);

        assertEditFailure(new EditLessonCommand(outOfBoundsIndex, descriptorWithName("Mathematics")),
                Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndReevaluatesFilter() {
        model.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.WEDNESDAY);
        Model expectedModel = copyModel();
        expectedModel.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.WEDNESDAY);
        assertEquals(List.of(PHYSICS), model.getFilteredLessonList());

        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setDay(DayOfWeek.TUESDAY);
        Lesson editedPhysics = lessonWithId(
                PHYSICS, "Sec 4 Physics", DayOfWeek.TUESDAY, "19:00", "20:30");
        expectedModel.setLesson(PHYSICS, editedPhysics);

        assertCommandSuccess(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor), model,
                getSuccessMessage(editedPhysics), expectedModel);
        assertTrue(model.getFilteredLessonList().isEmpty());
        assertEquals(List.of(A_MATH, editedPhysics, CHEMISTRY), model.getAddressBook().getLessonList());
    }

    @Test
    public void execute_duplicateLessonHiddenByFilter_throwsCommandException() {
        model.updateFilteredLessonList(lesson -> lesson.equals(A_MATH));
        EditLessonDescriptor duplicatePhysics = descriptor(
                "sec 4 physics", DayOfWeek.WEDNESDAY, "19:00", "20:30");

        assertEditFailure(new EditLessonCommand(INDEX_FIRST_LESSON, duplicatePhysics),
                EditLessonCommand.MESSAGE_DUPLICATE_LESSON);
        assertEquals(List.of(A_MATH), model.getFilteredLessonList());
    }

    @Test
    public void execute_sameValues_success() {
        EditLessonDescriptor descriptor = descriptor(
                A_MATH.getName(), A_MATH.getDay(), "16:00", "18:00");
        Model expectedModel = copyModel();
        Lesson editedLesson = lessonWithId(A_MATH, A_MATH.getName(), A_MATH.getDay(), "16:00", "18:00");
        expectedModel.setLesson(A_MATH, editedLesson);

        assertCommandSuccess(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor), model,
                getSuccessMessage(editedLesson), expectedModel);
    }

    @Test
    public void execute_studentsFilteredByEditedLesson_filterAndEnrolmentsRetained() {
        model.filterStudentsByLesson(A_MATH);
        Model expectedModel = copyModel();
        expectedModel.filterStudentsByLesson(A_MATH);
        EditLessonDescriptor descriptor = descriptorWithName("Mathematics");
        Lesson editedLesson = lessonWithId(A_MATH, "Mathematics", DayOfWeek.MONDAY, "16:00", "18:00");
        expectedModel.setLesson(A_MATH, editedLesson);

        assertCommandSuccess(new EditLessonCommand(INDEX_FIRST_LESSON, descriptor), model,
                getSuccessMessage(editedLesson), expectedModel);
        assertEquals(List.of(AIDEN), model.getFilteredStudentList());
        assertEquals(List.of(new Enrolment(AIDEN.getId(), editedLesson.getId())), model.getEnrolmentList());
    }

    @Test
    public void equals() {
        EditLessonDescriptor nameDescriptor = descriptorWithName("Mathematics");
        EditLessonDescriptor dayDescriptor = new EditLessonDescriptor();
        dayDescriptor.setDay(DayOfWeek.TUESDAY);
        EditLessonCommand command = new EditLessonCommand(INDEX_FIRST_LESSON, nameDescriptor);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new EditLessonCommand(INDEX_FIRST_LESSON, nameDescriptor)));
        assertFalse(command.equals(new EditLessonCommand(INDEX_SECOND_LESSON, nameDescriptor)));
        assertFalse(command.equals(new EditLessonCommand(INDEX_FIRST_LESSON, dayDescriptor)));
        assertFalse(command.equals(new DeleteLessonCommand(INDEX_FIRST_LESSON)));
        assertFalse(command.equals(null));
    }

    @Test
    public void descriptorEquals() {
        EditLessonDescriptor descriptor = descriptor(
                "Mathematics", DayOfWeek.TUESDAY, "17:00", "19:00");

        assertTrue(descriptor.equals(descriptor));
        assertTrue(descriptor.equals(new EditLessonDescriptor(descriptor)));
        assertFalse(descriptor.equals(descriptorWithName("Mathematics")));
        assertFalse(descriptor.equals(1));
        assertFalse(descriptor.equals(null));
    }

    @Test
    public void toStringMethod() {
        EditLessonDescriptor descriptor = descriptorWithName("Mathematics");
        EditLessonCommand command = new EditLessonCommand(INDEX_FIRST_LESSON, descriptor);
        String expectedDescriptor = EditLessonDescriptor.class.getCanonicalName()
                + "{name=Mathematics, day=null, startTime=null, endTime=null}";
        String expectedCommand = EditLessonCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_LESSON + ", editLessonDescriptor=" + expectedDescriptor + "}";

        assertEquals(expectedDescriptor, descriptor.toString());
        assertEquals(expectedCommand, command.toString());
    }

    /**
     * Executes {@code command} and confirms that it fails without changing model data or displayed lists.
     */
    private void assertEditFailure(EditLessonCommand command, String expectedMessage) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Lesson> expectedFilteredLessons = new ArrayList<>(model.getFilteredLessonList());
        List<Student> expectedFilteredStudents = new ArrayList<>(model.getFilteredStudentList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredLessons, model.getFilteredLessonList());
        assertEquals(expectedFilteredStudents, model.getFilteredStudentList());
    }

    private Model copyModel() {
        return new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    private static String getSuccessMessage(Lesson editedLesson) {
        return String.format(EditLessonCommand.MESSAGE_EDIT_LESSON_SUCCESS, editedLesson);
    }

    private static EditLessonDescriptor descriptorWithName(String name) {
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setName(name);
        return descriptor;
    }

    private static EditLessonDescriptor descriptor(
            String name, DayOfWeek day, String startTime, String endTime) {
        EditLessonDescriptor descriptor = descriptorWithName(name);
        descriptor.setDay(day);
        descriptor.setStartTime(LocalTime.parse(startTime));
        descriptor.setEndTime(LocalTime.parse(endTime));
        return descriptor;
    }

    private static Lesson lessonWithId(
            Lesson original, String name, DayOfWeek day, String startTime, String endTime) {
        return new Lesson(original.getId(), name, day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }

    private static Lesson lesson(String name, DayOfWeek day, String startTime, String endTime) {
        return new Lesson(name, day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }

    /**
     * Returns lessons with one enrolment, allowing edits to verify stable lesson links.
     */
    private static AddressBook getLessonAddressBook() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(AIDEN);
        List.of(A_MATH, PHYSICS, CHEMISTRY).forEach(addressBook::addLesson);
        addressBook.addEnrolment(new Enrolment(AIDEN.getId(), A_MATH.getId()));
        return addressBook;
    }
}
