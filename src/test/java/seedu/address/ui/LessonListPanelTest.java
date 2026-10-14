package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Lesson;

public class LessonListPanelTest {

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_observableList_bindsListAndTracksChanges() throws Exception {
        ModelManager model = new ModelManager();
        ObservableList<Lesson> lessons = FXCollections.observableArrayList(createLesson("Math"));

        JavaFxTestUtil.runOnFxThread(() -> {
            LessonListPanel panel = new LessonListPanel(lessons, model.getAddressBook());
            ListView<Lesson> listView = getLessonListView(panel);
            assertSame(lessons, listView.getItems());

            lessons.add(createLesson("Physics"));
            assertEquals(2, listView.getItems().size());
            return null;
        });
    }

    @Test
    public void listCell_lessonAndEmptyItem_updatesGraphic() throws Exception {
        ModelManager model = new ModelManager();
        Lesson lesson = createLesson("Math");

        JavaFxTestUtil.runOnFxThread(() -> {
            LessonListPanel panel = new LessonListPanel(FXCollections.observableArrayList(lesson),
                    model.getAddressBook());
            LessonListPanel.LessonListViewCell cell = panel.new LessonListViewCell();

            cell.updateItem(lesson, false);
            assertNotNull(cell.getGraphic());
            assertEquals("Math", ((Label) cell.getGraphic().lookup("#lessonName")).getText());

            cell.updateItem(null, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static ListView<Lesson> getLessonListView(LessonListPanel panel) {
        return (ListView<Lesson>) panel.getRoot().lookup("#lessonListView");
    }

    private static Lesson createLesson(String name) {
        return new Lesson(name, DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
    }
}
