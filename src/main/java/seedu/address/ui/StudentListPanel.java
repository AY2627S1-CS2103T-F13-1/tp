package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.student.Student;

/**
 * Panel containing the list of students.
 */
public class StudentListPanel extends UiPart<Region> {

    private static final String FXML = "StudentListPanel.fxml";

    private final ReadOnlyAddressBook addressBook;

    @FXML
    private ListView<Student> studentListView;

    /**
     * Creates a {@code StudentListPanel} backed by the given observable student list.
     */
    public StudentListPanel(ObservableList<Student> studentList) {
        this(studentList, null);
    }

    /**
     * Creates a panel that refreshes when membership or lesson details change.
     */
    public StudentListPanel(ObservableList<Student> students, ReadOnlyAddressBook addressBook) {
        super(FXML);
        this.addressBook = addressBook;
        studentListView.setItems(requireNonNull(students));
        studentListView.setCellFactory(listView -> new StudentListViewCell());

        if (addressBook != null) {
            InvalidationListener refresh = observable -> studentListView.refresh();
            addressBook.getEnrolmentList().addListener(refresh);
            addressBook.getLessonList().addListener(refresh);
        }
    }

    /**
     * Displays each student using a {@link StudentCard}.
     */
    class StudentListViewCell extends ListCell<Student> {
        StudentListViewCell() {
            // size the cell to the list rather than its content, so long names wrap instead of scrolling sideways
            setPrefWidth(0);
        }

        @Override
        protected void updateItem(Student student, boolean empty) {
            super.updateItem(student, empty);

            if (empty || student == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new StudentCard(student, getIndex() + 1,
                        addressBook == null ? List.of() : addressBook.getLessonsForStudent(student)).getRoot());
            }
        }
    }
}
