package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.time.DayOfWeek;
import java.time.LocalTime;
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
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ListStudentsCommand}.
 */
public class ListStudentsCommandTest {
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student CHLOE = new Student(new Name("Chloe Tan"), new Phone("88888888"),
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);
    private static final Lesson MATH = new Lesson("Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));
    private static final Lesson PHYSICS = new Lesson("Physics", DayOfWeek.WEDNESDAY,
            LocalTime.of(19, 0), LocalTime.of(20, 30));

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
        model.updateFilteredStudentList(new StudentContainsKeywordsPredicate(List.of("Ryan"), List.of()));
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
    public void execute_withLessonIndex_showsStudentsInSelectedLesson() {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsAndEnrolments(each);
        }
        expectedModel.filterStudentsByLesson(MATH);

        assertCommandSuccess(new ListStudentsCommand(Index.fromOneBased(1)), model,
                "Listed students enrolled in " + MATH, expectedModel);
        assertEquals(List.of(AIDEN, CHLOE), model.getFilteredStudentList());
    }

    @Test
    public void execute_withLessonIndex_usesDisplayedLessonList() {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsAndEnrolments(each);
            each.updateFilteredLessonList(lesson -> lesson.equals(PHYSICS));
        }
        expectedModel.filterStudentsByLesson(PHYSICS);

        assertCommandSuccess(new ListStudentsCommand(Index.fromOneBased(1)), model,
                "Listed students enrolled in " + PHYSICS, expectedModel);
        assertEquals(List.of(RYAN), model.getFilteredStudentList());
    }

    @Test
    public void execute_lessonIndexOutsideDisplayedList_throwsCommandException() {
        addLessonsAndEnrolments(model);
        model.updateFilteredLessonList(lesson -> lesson.equals(MATH));

        List<Student> studentsBefore = List.copyOf(model.getFilteredStudentList());
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX, () ->
                new ListStudentsCommand(Index.fromOneBased(2)).execute(model));
        assertEquals(studentsBefore, model.getFilteredStudentList());
    }

    @Test
    public void execute_afterLessonFilterWithoutIndex_showsAllStudents() throws Exception {
        for (Model each : List.of(model, expectedModel)) {
            addLessonsAndEnrolments(each);
        }
        new ListStudentsCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(AIDEN, CHLOE), model.getFilteredStudentList());

        assertCommandSuccess(new ListStudentsCommand(), model, ListStudentsCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(AIDEN, CHLOE, RYAN), model.getFilteredStudentList());
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

    private static void addLessonsAndEnrolments(Model model) {
        model.addLesson(MATH);
        model.addLesson(PHYSICS);
        model.enrolStudent(AIDEN, MATH);
        model.enrolStudent(CHLOE, MATH);
        model.enrolStudent(RYAN, PHYSICS);
    }

    @Test
    public void equals_lessonFilter_comparesSelectedLesson() {
        var first = seedu.address.commons.core.index.Index.fromOneBased(1);
        var second = seedu.address.commons.core.index.Index.fromOneBased(2);
        var filtered = new ListStudentsCommand(first);
        assertEquals(filtered, new ListStudentsCommand(first));
        assertEquals(filtered.hashCode(), new ListStudentsCommand(first).hashCode());
        assertFalse(filtered.equals(new ListStudentsCommand(second)));
        assertFalse(filtered.equals(new ListStudentsCommand()));
        assertFalse(new ListStudentsCommand().equals(filtered));
    }

}
