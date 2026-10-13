package seedu.address.ui;

import java.util.stream.Collectors;

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

    private ReadOnlyAddressBook addressBook;

    @FXML
    private ListView<Student> studentListView;

    /**
     * Creates a {@code StudentListPanel} backed by the given observable student list.
     */
    public StudentListPanel(ObservableList<Student> studentList) {
        super(FXML);
        studentListView.setItems(studentList);
        studentListView.setCellFactory(listView -> new StudentListViewCell());
    }

    /** Creates a panel that refreshes when membership or lesson details change. */
    public StudentListPanel(ObservableList<Student> students, ReadOnlyAddressBook addressBook) {
        this(students);
        this.addressBook = addressBook;
        InvalidationListener refresh = observable -> studentListView.refresh();
        addressBook.getEnrolmentList().addListener(refresh);
        addressBook.getLessonList().addListener(refresh);
    }

    /**
     * Displays each student using a {@link StudentCard}.
     */
    class StudentListViewCell extends ListCell<Student> {
        @Override
        protected void updateItem(Student student, boolean empty) {
            super.updateItem(student, empty);

            if (empty || student == null) {
                setGraphic(null);
                setText(null);
            } else {
                String lessons = addressBook == null ? "" : addressBook.getLessonsForStudent(student).stream()
                        .map(Object::toString).collect(Collectors.joining("\n"));
                setGraphic(new StudentCard(student, getIndex() + 1, lessons.isEmpty() ? "-" : lessons).getRoot());
            }
        }
    }
}
