package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.student.Student;

/**
 * A UI component that displays a student's and guardian's contact details.
 */
public class StudentCard extends UiPart<Region> {

    private static final String FXML = "StudentListCard.fxml";

    public final Student student;

    @FXML
    private Label id;
    @FXML
    private Label studentName;
    @FXML
    private Label studentPhone;
    @FXML
    private Label guardianName;
    @FXML
    private Label guardianPhone;

    /**
     * Creates a {@code StudentCard} with the given student and displayed index.
     */
    public StudentCard(Student student, int displayedIndex) {
        super(FXML);
        this.student = student;
        StudentCardDetails details = StudentCardDetails.from(student);

        id.setText(displayedIndex + ". ");
        studentName.setText(details.studentName());
        studentPhone.setText(details.studentPhone());
        guardianName.setText(details.guardianName());
        guardianPhone.setText(details.guardianPhone());
    }
}
