package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * A UI component that displays a lesson's schedule and enrolled students.
 */
public class LessonCard extends UiPart<Region> {

    private static final String FXML = "LessonListCard.fxml";

    public final Lesson lesson;

    @FXML
    private Label id;
    @FXML
    private Label lessonName;
    @FXML
    private Label lessonDay;
    @FXML
    private Label lessonTime;
    @FXML
    private Label enrolledStudents;

    /**
     * Creates a lesson card for the given displayed index and membership snapshot.
     */
    public LessonCard(Lesson lesson, int displayedIndex, List<Student> students) {
        super(FXML);
        this.lesson = lesson;
        LessonCardDetails details = LessonCardDetails.from(lesson, students);

        id.setText(displayedIndex + ". ");
        lessonName.setText(details.lessonName());
        lessonDay.setText(details.day());
        lessonTime.setText(details.time());
        enrolledStudents.setText(details.students());
    }
}
