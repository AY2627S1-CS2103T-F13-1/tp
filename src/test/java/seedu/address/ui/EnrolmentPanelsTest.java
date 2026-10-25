package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class EnrolmentPanelsTest {
    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void enrolmentChange_existingPanelsRefreshBothMembershipLabels() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Model model = new ModelManager();
            Student student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
            Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
            model.addStudent(student);
            model.addLesson(lesson);
            StudentListPanel students = new StudentListPanel(model.getFilteredStudentList(), model.getAddressBook());
            LessonListPanel lessons = new LessonListPanel(model.getFilteredLessonList(), model.getAddressBook());
            HBox root = showPanels(students, lessons);

            model.enrolStudent(student, lesson);
            root.applyCss();
            root.layout();

            String studentText = students.getRoot().lookupAll(".label").stream()
                    .map(node -> ((Label) node).getText()).collect(Collectors.joining("\n"));
            String lessonText = lessons.getRoot().lookupAll(".label").stream()
                    .map(node -> ((Label) node).getText()).collect(Collectors.joining("\n"));
            assertTrue(studentText.contains("Math — Monday, 16:00 – 18:00"), studentText);
            assertTrue(lessonText.contains("Students"), lessonText);
            assertTrue(lessonText.contains("Aiden Tan"), lessonText);
            return null;
        });
    }

    @Test
    public void deleteLesson_existingStudentCardRemovesMembership() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Model model = new ModelManager();
            Student student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
            Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
            model.addStudent(student);
            model.addLesson(lesson);
            model.enrolStudent(student, lesson);
            StudentListPanel students = new StudentListPanel(model.getFilteredStudentList(), model.getAddressBook());
            LessonListPanel lessons = new LessonListPanel(model.getFilteredLessonList(), model.getAddressBook());
            HBox root = showPanels(students, lessons);

            model.deleteLesson(lesson);
            root.applyCss();
            root.layout();

            assertEquals("-", getLabelText(students.getRoot(), "lessons"));
            return null;
        });
    }

    @Test
    public void deleteStudent_existingLessonCardRemovesMembership() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Model model = new ModelManager();
            Student student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
            Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
            model.addStudent(student);
            model.addLesson(lesson);
            model.enrolStudent(student, lesson);
            StudentListPanel students = new StudentListPanel(model.getFilteredStudentList(), model.getAddressBook());
            LessonListPanel lessons = new LessonListPanel(model.getFilteredLessonList(), model.getAddressBook());
            HBox root = showPanels(students, lessons);

            model.deleteStudent(student);
            root.applyCss();
            root.layout();

            assertEquals("-", getLabelText(lessons.getRoot(), "enrolledStudents"));
            return null;
        });
    }

    @Test
    public void editStudent_existingLessonCardShowsNewName() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Model model = new ModelManager();
            Student student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
            Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
            model.addStudent(student);
            model.addLesson(lesson);
            model.enrolStudent(student, lesson);
            StudentListPanel students = new StudentListPanel(model.getFilteredStudentList(), model.getAddressBook());
            LessonListPanel lessons = new LessonListPanel(model.getFilteredLessonList(), model.getAddressBook());
            HBox root = showPanels(students, lessons);

            model.setStudent(student, new Student(student.getId(), new Name("Aiden Lim"), new Phone("91234567"),
                    null));
            root.applyCss();
            root.layout();

            assertEquals("Aiden Lim", getLabelText(lessons.getRoot(), "enrolledStudents"));
            return null;
        });
    }

    @Test
    public void editLesson_existingStudentCardShowsNewLesson() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Model model = new ModelManager();
            Student student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
            Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
            model.addStudent(student);
            model.addLesson(lesson);
            model.enrolStudent(student, lesson);
            StudentListPanel students = new StudentListPanel(model.getFilteredStudentList(), model.getAddressBook());
            LessonListPanel lessons = new LessonListPanel(model.getFilteredLessonList(), model.getAddressBook());
            HBox root = showPanels(students, lessons);

            model.setLesson(lesson, new Lesson(lesson.getId(), "Physics", DayOfWeek.TUESDAY, LocalTime.of(16, 0),
                    LocalTime.of(18, 0)));
            root.applyCss();
            root.layout();

            assertEquals("Physics — Tuesday, 16:00 – 18:00", getLabelText(students.getRoot(), "lessons"));
            return null;
        });
    }

    private static HBox showPanels(StudentListPanel students, LessonListPanel lessons) {
        HBox root = new HBox(students.getRoot(), lessons.getRoot());
        new Scene(root, 900, 600);
        root.applyCss();
        root.layout();
        return root;
    }

    private static String getLabelText(Parent root, String labelId) {
        return ((Label) root.lookup("#" + labelId)).getText();
    }

}
