package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class EnrolCommandTest {
    private final Model model = new ModelManager();
    private final AddressBookParser parser = new AddressBookParser();
    private final Student aiden = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
    private final Student beth = new Student(new Name("Beth Tan"), new Phone("92345678"), null);
    private final Lesson math = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));
    private final Lesson science = new Lesson("Science", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));
    private final Lesson latePhysics = new Lesson("Late Physics", DayOfWeek.FRIDAY,
            LocalTime.of(23, 0), LocalTime.of(1, 0));

    private void populate() {
        model.addStudent(beth);
        model.addStudent(aiden);
        model.addLesson(science);
        model.addLesson(math);
    }

    @Test
    public void execute_filteredLists_usesDisplayedIndicesAndPreservesFilters() throws Exception {
        populate();
        model.updateFilteredStudentList(aiden::equals);
        model.updateFilteredLessonList(math::equals);
        assertEquals("Enrolled Aiden Tan in Sec 3 A-Math (Mon 1600-1800)",
                parser.parseCommand("enrol l/1 s/1").execute(model).getFeedbackToUser());
        assertEquals(List.of(aiden), model.getAddressBook().getStudentsInLesson(math));
        assertEquals(List.of(math), model.getAddressBook().getLessonsForStudent(aiden));
        assertEquals(List.of(aiden), model.getFilteredStudentList());
        assertEquals(List.of(math), model.getFilteredLessonList());
        assertThrows(CommandException.class, "Aiden Tan is already enrolled in Sec 3 A-Math (Mon 1600-1800)", () ->
                 parser.parseCommand("enrol s/1 l/1").execute(model));
        assertEquals(1, model.getEnrolmentList().size());
        parser.parseCommand("liststudents l/1").execute(model);
        assertEquals(List.of(aiden), model.getFilteredStudentList());
        parser.parseCommand("liststudents").execute(model);
        assertEquals(List.of(beth, aiden), model.getFilteredStudentList());
        assertEquals(List.of(math), model.getFilteredLessonList());
    }

    @Test
    public void execute_invalidIndices_studentCheckedFirstAndNoMutation() throws Exception {
        populate();
        model.updateFilteredStudentList(aiden::equals);
        model.updateFilteredLessonList(math::equals);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                 parser.parseCommand("enrol s/2 l/2").execute(model));
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX, () ->
                 parser.parseCommand("enrol s/1 l/2").execute(model));
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX, () ->
                 parser.parseCommand("liststudents l/2").execute(model));
        assertTrue(model.getEnrolmentList().isEmpty());
        model.updateFilteredStudentList(student -> false);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                 parser.parseCommand("enrol s/1 l/1").execute(model));
    }

    @Test
    public void execute_manyToManyAndClashingLessons_accepted() throws Exception {
        populate();
        parser.parseCommand("enrol s/1 l/1").execute(model);
        parser.parseCommand("enrol s/1 l/2").execute(model);
        parser.parseCommand("enrol s/2 l/2").execute(model);
        assertEquals(3, model.getEnrolmentList().size());
        assertEquals(List.of(science, math), model.getAddressBook().getLessonsForStudent(beth));
        assertEquals(List.of(beth, aiden), model.getAddressBook().getStudentsInLesson(math));
    }

    @Test
    public void execute_lessonEndsNextDay_marksNextDayInMessages() throws Exception {
        model.addStudent(aiden);
        model.addLesson(latePhysics);
        assertEquals("Enrolled Aiden Tan in Late Physics (Fri 2300-0100) (next day)",
                parser.parseCommand("enrol s/1 l/1").execute(model).getFeedbackToUser());
        assertThrows(CommandException.class,
                "Aiden Tan is already enrolled in Late Physics (Fri 2300-0100) (next day)", () ->
                        parser.parseCommand("enrol s/1 l/1").execute(model));
    }

    @Test
    public void equals_comparesBothIndices() throws Exception {
        var command = parser.parseCommand("enrol s/1 l/2");
        assertEquals(command, command);
        assertEquals(command, parser.parseCommand("enrol l/2 s/1"));
        org.junit.jupiter.api.Assertions.assertNotEquals(command, parser.parseCommand("enrol s/2 l/2"));
        org.junit.jupiter.api.Assertions.assertNotEquals(command, parser.parseCommand("enrol s/1 l/1"));
        org.junit.jupiter.api.Assertions.assertNotEquals(command, new ListStudentsCommand());
        org.junit.jupiter.api.Assertions.assertNotEquals(command, null);
    }

}
