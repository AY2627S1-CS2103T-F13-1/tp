package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.lesson.Lesson;

/**
 * Panel containing the list of lessons.
 */
public class LessonListPanel extends UiPart<Region> {

    private static final String FXML = "LessonListPanel.fxml";

    private final ReadOnlyAddressBook addressBook;

    @FXML
    private ListView<Lesson> lessonListView;

    /**
     * Creates a lesson panel backed by the given observable list and complete membership data.
     */
    public LessonListPanel(ObservableList<Lesson> lessons, ReadOnlyAddressBook addressBook) {
        super(FXML);
        this.addressBook = requireNonNull(addressBook);
        lessonListView.setItems(requireNonNull(lessons));
        lessonListView.setCellFactory(view -> new LessonListViewCell());

        InvalidationListener refresh = observable -> lessonListView.refresh();
        addressBook.getEnrolmentList().addListener(refresh);
        addressBook.getStudentList().addListener(refresh);
    }

    /**
     * Displays each lesson using a {@link LessonCard}.
     */
    class LessonListViewCell extends ListCell<Lesson> {
        @Override
        protected void updateItem(Lesson lesson, boolean empty) {
            super.updateItem(lesson, empty);

            if (empty || lesson == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new LessonCard(lesson, getIndex() + 1,
                        addressBook.getStudentsInLesson(lesson)).getRoot());
            }
        }
    }
}
