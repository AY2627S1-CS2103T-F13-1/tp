package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

/**
 * Contains integration and unit tests for {@code ListLessonsCommand}.
 */
public class ListLessonsCommandTest {
    private static final Lesson MONDAY_MATH = new Lesson("Monday Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));
    private static final Lesson WEDNESDAY_PHYSICS = new Lesson("Wednesday Physics", DayOfWeek.WEDNESDAY,
            LocalTime.of(19, 0), LocalTime.of(20, 30));
    private static final Lesson MONDAY_CHEMISTRY = new Lesson("Monday Chemistry", DayOfWeek.MONDAY,
            LocalTime.of(14, 0), LocalTime.of(15, 30));
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = getLessonAddressBook();
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListLessonsCommand().execute(null));
    }

    @Test
    public void execute_unfiltered_showsAllLessons() {
        assertCommandSuccess(new ListLessonsCommand(), model, ListLessonsCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(MONDAY_CHEMISTRY, MONDAY_MATH, WEDNESDAY_PHYSICS),
                model.getFilteredLessonList());
    }

    @Test
    public void execute_filteredWithoutDay_showsAllLessons() {
        model.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.WEDNESDAY);

        assertCommandSuccess(new ListLessonsCommand(), model, ListLessonsCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(MONDAY_CHEMISTRY, MONDAY_MATH, WEDNESDAY_PHYSICS),
                model.getFilteredLessonList());
    }

    @Test
    public void execute_withDay_showsMatchingLessonsInTimetableOrder() {
        expectedModel.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.MONDAY);

        assertCommandSuccess(new ListLessonsCommand(DayOfWeek.MONDAY), model,
                String.format(ListLessonsCommand.MESSAGE_SUCCESS_BY_DAY, "Monday"), expectedModel);
        assertEquals(List.of(MONDAY_CHEMISTRY, MONDAY_MATH), model.getFilteredLessonList());
    }

    @Test
    public void execute_withDayWithoutMatches_showsEmptyList() {
        expectedModel.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.FRIDAY);

        assertCommandSuccess(new ListLessonsCommand(DayOfWeek.FRIDAY), model,
                String.format(ListLessonsCommand.MESSAGE_SUCCESS_BY_DAY, "Friday"), expectedModel);
        assertTrue(model.getFilteredLessonList().isEmpty());
    }

    @Test
    public void execute_noLessons_success() {
        model = new ModelManager();
        expectedModel = new ModelManager();

        assertCommandSuccess(new ListLessonsCommand(DayOfWeek.MONDAY), model,
                String.format(ListLessonsCommand.MESSAGE_SUCCESS_BY_DAY, "Monday"), expectedModel);
        assertTrue(model.getFilteredLessonList().isEmpty());
    }

    @Test
    public void execute_studentListFiltered_studentFilterUnchanged() {
        for (Model each : List.of(model, expectedModel)) {
            each.addStudent(AIDEN);
            each.updateFilteredStudentList(student -> false);
        }
        expectedModel.updateFilteredLessonList(lesson -> lesson.getDay() == DayOfWeek.MONDAY);

        assertCommandSuccess(new ListLessonsCommand(DayOfWeek.MONDAY), model,
                String.format(ListLessonsCommand.MESSAGE_SUCCESS_BY_DAY, "Monday"), expectedModel);
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(List.of(AIDEN), model.getAddressBook().getStudentList());
    }

    @Test
    public void equals() {
        ListLessonsCommand listAll = new ListLessonsCommand();
        ListLessonsCommand listMonday = new ListLessonsCommand(DayOfWeek.MONDAY);

        assertTrue(listAll.equals(listAll));
        assertEquals(listAll, new ListLessonsCommand());
        assertEquals(listAll.hashCode(), new ListLessonsCommand().hashCode());
        assertEquals(listMonday, new ListLessonsCommand(DayOfWeek.MONDAY));
        assertEquals(listMonday.hashCode(), new ListLessonsCommand(DayOfWeek.MONDAY).hashCode());
        assertFalse(listMonday.equals(new ListLessonsCommand(DayOfWeek.TUESDAY)));
        assertFalse(listMonday.equals(listAll));
        assertFalse(listAll.equals(1));
        assertFalse(listAll.equals(null));
    }

    private static AddressBook getLessonAddressBook() {
        AddressBook addressBook = new AddressBook();
        for (Lesson lesson : List.of(MONDAY_MATH, WEDNESDAY_PHYSICS, MONDAY_CHEMISTRY)) {
            addressBook.addLesson(lesson);
        }
        return addressBook;
    }
}
