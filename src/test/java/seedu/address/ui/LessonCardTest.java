package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class LessonCardTest {

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_lessonAndStudents_loadsFxmlAndDisplaysDetails() throws Exception {
        Lesson lesson = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0));
        Student student = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);

        List<String> displayedText = JavaFxTestUtil.runOnFxThread(() -> {
            LessonCard card = new LessonCard(lesson, 2, List.of(student));
            return List.of(
                    getLabelText(card, "id"),
                    getLabelText(card, "lessonName"),
                    getLabelText(card, "lessonDay"),
                    getLabelText(card, "lessonTime"),
                    getLabelText(card, "enrolledStudents"));
        });

        assertEquals(List.of("2. ", "Sec 3 A-Math", "Monday", "16:00 – 18:00", "Aiden Tan"), displayedText);
    }

    private static String getLabelText(LessonCard card, String labelId) {
        return ((Label) card.getRoot().lookup("#" + labelId)).getText();
    }
}
