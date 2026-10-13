package seedu.address.ui;

import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.lesson.Lesson;

/** Displays lessons and their enrolled students using the displayed lesson indices. */
public class LessonListPanel extends UiPart<Region> {
    @FXML
    private ListView<Lesson> lessonListView;

    /** Creates a panel whose membership labels follow changes in the complete data. */
    public LessonListPanel(ObservableList<Lesson> lessons, ReadOnlyAddressBook addressBook) {
        super("LessonListPanel.fxml");
        lessonListView.setItems(lessons);
        lessonListView.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Lesson lesson, boolean empty) {
                super.updateItem(lesson, empty);
                setText(null);
                if (empty || lesson == null) {
                    setGraphic(null);
                    return;
                }
                Label title = new Label((getIndex() + 1) + ". " + lesson);
                title.getStyleClass().add("cell_big_label");
                title.setWrapText(true);
                String names = addressBook.getStudentsInLesson(lesson).stream()
                        .map(student -> student.getName().fullName).collect(Collectors.joining("\n"));
                Label students = new Label("Students: " + (names.isEmpty() ? "-" : names));
                students.getStyleClass().add("cell_small_label");
                students.setWrapText(true);
                VBox card = new VBox(4, title, students);
                card.setStyle("-fx-padding: 10 12 10 12;");
                setGraphic(card);
            }
        });
        InvalidationListener refresh = observable -> lessonListView.refresh();
        addressBook.getEnrolmentList().addListener(refresh);
        addressBook.getStudentList().addListener(refresh);
    }
}
