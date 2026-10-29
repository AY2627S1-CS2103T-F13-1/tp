package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class UnenrolCommandTest {
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
        model.enrolStudent(aiden, math);
        model.enrolStudent(aiden, science);
        model.enrolStudent(beth, math);
    }

    @Test
    public void execute_filteredLists_usesDisplayedIndicesAndPreservesFilters() throws Exception {
        populate();
        model.updateFilteredStudentList(aiden::equals);
        model.updateFilteredLessonList(math::equals);
        assertEquals("Unenrolled Aiden Tan from Sec 3 A-Math (Mon 1600-1800)",
                parser.parseCommand("unenrol l/1 s/1").execute(model).getFeedbackToUser());
        assertEquals(List.of(beth), model.getAddressBook().getStudentsInLesson(math));
        assertEquals(List.of(science), model.getAddressBook().getLessonsForStudent(aiden));
        assertEquals(List.of(aiden), model.getFilteredStudentList());
        assertEquals(List.of(math), model.getFilteredLessonList());
        assertEquals(List.of(beth, aiden), model.getAddressBook().getStudentList());
        assertEquals(List.of(science, math), model.getAddressBook().getLessonList());
    }

    @Test
    public void execute_notEnrolled_throwsAndNoMutation() throws Exception {
        populate();
        assertThrows(CommandException.class, "Beth Tan is not enrolled in Science (Mon 1600-1800)", () ->
                parser.parseCommand("unenrol s/1 l/1").execute(model));
        assertEquals(3, model.getEnrolmentList().size());
        parser.parseCommand("unenrol s/2 l/1").execute(model);
        assertThrows(CommandException.class, "Aiden Tan is not enrolled in Science (Mon 1600-1800)", () ->
                parser.parseCommand("unenrol s/2 l/1").execute(model));
        assertEquals(2, model.getEnrolmentList().size());
    }

    @Test
    public void execute_studentsFilteredByLesson_removesStudentFromFilter() throws Exception {
        populate();
        parser.parseCommand("liststudents l/2").execute(model);
        assertEquals(List.of(beth, aiden), model.getFilteredStudentList());
        parser.parseCommand("unenrol s/2 l/2").execute(model);
        assertEquals(List.of(beth), model.getFilteredStudentList());
        parser.parseCommand("unenrol s/1 l/2").execute(model);
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(List.of(science, math), model.getFilteredLessonList());
    }

    @Test
    public void execute_invalidIndices_studentCheckedFirstAndNoMutation() throws Exception {
        populate();
        model.updateFilteredStudentList(aiden::equals);
        model.updateFilteredLessonList(math::equals);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                parser.parseCommand("unenrol s/2 l/2").execute(model));
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX, () ->
                parser.parseCommand("unenrol s/1 l/2").execute(model));
        assertEquals(3, model.getEnrolmentList().size());
    }

    @Test
    public void execute_lessonEndsNextDay_marksNextDayInMessages() throws Exception {
        model.addStudent(aiden);
        model.addLesson(latePhysics);
        model.enrolStudent(aiden, latePhysics);
        assertEquals("Unenrolled Aiden Tan from Late Physics (Fri 2300-0100) (next day)",
                parser.parseCommand("unenrol s/1 l/1").execute(model).getFeedbackToUser());
        assertThrows(CommandException.class,
                "Aiden Tan is not enrolled in Late Physics (Fri 2300-0100) (next day)", () ->
                        parser.parseCommand("unenrol s/1 l/1").execute(model));
    }

    @Test
    public void equals_comparesBothIndices() throws Exception {
        var command = parser.parseCommand("unenrol s/1 l/2");
        assertEquals(command, command);
        assertEquals(command, parser.parseCommand("unenrol l/2 s/1"));
        assertEquals(command.hashCode(), parser.parseCommand("unenrol l/2 s/1").hashCode());
        assertNotEquals(command, parser.parseCommand("unenrol s/2 l/2"));
        assertNotEquals(command, parser.parseCommand("unenrol s/1 l/1"));
        assertNotEquals(command, parser.parseCommand("enrol s/1 l/2"));
        assertNotEquals(command, null);
    }

    @Test
    public void toStringMethod() {
        Index studentIndex = Index.fromOneBased(1);
        Index lessonIndex = Index.fromOneBased(2);
        UnenrolCommand command = new UnenrolCommand(studentIndex, lessonIndex);
        String expected = UnenrolCommand.class.getCanonicalName()
                + "{studentIndex=" + studentIndex + ", lessonIndex=" + lessonIndex + "}";
        assertEquals(expected, command.toString());
    }
}
