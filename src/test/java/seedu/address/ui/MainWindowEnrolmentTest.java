package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowEnrolmentTest {
    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void commandBox_enrol_updatesBothPanelsAndPersistsMembership() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            var model = new ModelManager();
            var student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
            var lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
            model.addStudent(student);
            model.addLesson(lesson);
            var dataPath = temporaryFolder.resolve("addressbook.json");
            var storage = new JsonAddressBookStorage(dataPath);
            var logic = new LogicManager(model, new StorageManager(storage,
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, dataPath);
                window.fillInnerParts();
                stage.show();
                Parent root = stage.getScene().getRoot();
                root.applyCss();
                root.layout();
                TextField commandBox = (TextField) root.lookup("#commandTextField");
                commandBox.setText("enrol s/1 l/1");
                commandBox.fireEvent(new ActionEvent());
                root.applyCss();
                root.layout();
                assertEquals("", commandBox.getText());
                String students = labels((Parent) root.lookup("#studentListPanelPlaceholder"));
                String lessons = labels((Parent) root.lookup("#lessonListPanelPlaceholder"));
                assertTrue(students.contains("Math (Mon 1600-1800)"), students);
                assertTrue(lessons.contains("Students: Aiden Tan"), lessons);
                assertTrue(storage.readAddressBook().orElseThrow().isEnrolled(student, lesson));
            } finally {
                stage.close();
            }
            return null;
        });
    }

    private static String labels(Parent root) {
        return root.lookupAll(".label").stream().map(node -> ((Label) node).getText())
                .collect(Collectors.joining("\n"));
    }
}
