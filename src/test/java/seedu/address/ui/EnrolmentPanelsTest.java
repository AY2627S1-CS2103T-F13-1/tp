package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
            HBox root = new HBox(students.getRoot(), lessons.getRoot());
            new Scene(root, 900, 600);
            root.applyCss();
            root.layout();
            model.enrolStudent(student, lesson);
            root.applyCss();
            root.layout();
            String studentText = students.getRoot().lookupAll(".label").stream()
                    .map(node -> ((Label) node).getText()).collect(Collectors.joining("\n"));
            String lessonText = lessons.getRoot().lookupAll(".label").stream()
                    .map(node -> ((Label) node).getText()).collect(Collectors.joining("\n"));
            assertTrue(studentText.contains("Math (Mon 1600-1800)"), studentText);
            assertTrue(lessonText.contains("Students"), lessonText);
            assertTrue(lessonText.contains("Aiden Tan"), lessonText);
            return null;
        });
    }

    @Test
    public void nullLessonCell_clearsGraphic() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Model model = new ModelManager();
            var items = javafx.collections.FXCollections.<Lesson>observableArrayList();
            items.add(null);
            LessonListPanel panel = new LessonListPanel(items, model.getAddressBook());
            @SuppressWarnings("unchecked")
            var view = (javafx.scene.control.ListView<Lesson>) panel.getRoot().lookup("#lessonListView");
            var cell = view.getCellFactory().call(view);
            cell.updateListView(view);
            cell.updateIndex(0);
            org.junit.jupiter.api.Assertions.assertNull(cell.getGraphic());
            org.junit.jupiter.api.Assertions.assertNull(cell.getText());
            return null;
        });
    }

}
