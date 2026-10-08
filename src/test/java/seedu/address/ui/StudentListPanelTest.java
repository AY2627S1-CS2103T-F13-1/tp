package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class StudentListPanelTest {

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_observableList_bindsListAndTracksChanges() throws Exception {
        ObservableList<Student> students = FXCollections.observableArrayList(createStudent("Aiden Tan", "92345678"));

        JavaFxTestUtil.runOnFxThread(() -> {
            StudentListPanel panel = new StudentListPanel(students);
            ListView<Student> listView = getStudentListView(panel);
            assertSame(students, listView.getItems());

            students.add(createStudent("Chloe Lim", "93456789"));
            assertEquals(2, listView.getItems().size());
            return null;
        });
    }

    @Test
    public void listCell_studentAndEmptyItem_updatesGraphic() throws Exception {
        Student student = createStudent("Aiden Tan", "92345678");

        JavaFxTestUtil.runOnFxThread(() -> {
            StudentListPanel panel = new StudentListPanel(FXCollections.observableArrayList(student));
            StudentListPanel.StudentListViewCell cell = panel.new StudentListViewCell();

            cell.updateItem(student, false);
            assertNotNull(cell.getGraphic());
            assertEquals("Aiden Tan", ((Label) cell.getGraphic().lookup("#studentName")).getText());

            cell.updateItem(null, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static ListView<Student> getStudentListView(StudentListPanel panel) {
        return (ListView<Student>) panel.getRoot().lookup("#studentListView");
    }

    private static Student createStudent(String name, String phone) {
        return new Student(new Name(name), new Phone(phone), null);
    }
}
