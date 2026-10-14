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
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

public class StudentCardTest {

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_allDetailsPresent_loadsFxmlAndDisplaysDetails() throws Exception {
        Student student = new Student(new Name("Aiden Tan"), new Phone("92345678"),
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));

        List<String> displayedText = JavaFxTestUtil.runOnFxThread(() -> {
            StudentCard card = new StudentCard(student, 2);
            return List.of(
                    getLabelText(card, "id"),
                    getLabelText(card, "studentName"),
                    getLabelText(card, "studentPhone"),
                    getLabelText(card, "guardianName"),
                    getLabelText(card, "guardianPhone"));
        });

        assertEquals(List.of("2. ", "Aiden Tan", "92345678", "Tan Mei Ling", "91234567"), displayedText);
    }

    @Test
    public void constructor_optionalDetailsMissing_displaysPlaceholders() throws Exception {
        Student student = new Student(new Name("Aiden Tan"), null,
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));

        List<String> displayedText = JavaFxTestUtil.runOnFxThread(() -> {
            StudentCard card = new StudentCard(student, 1);
            return List.of(getLabelText(card, "studentPhone"), getLabelText(card, "guardianName"));
        });

        assertEquals(List.of("-", "Tan Mei Ling"), displayedText);
    }

    @Test
    public void constructor_enrolledLessons_displaysMemberships() throws Exception {
        Student student = new Student(new Name("Aiden Tan"), new Phone("92345678"), null);
        Lesson lesson = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0));

        String displayedLessons = JavaFxTestUtil.runOnFxThread(() -> {
            StudentCard card = new StudentCard(student, 1, List.of(lesson));
            return getLabelText(card, "lessons");
        });

        assertEquals("Sec 3 A-Math — Monday, 16:00 – 18:00", displayedLessons);
    }

    private static String getLabelText(StudentCard card, String labelId) {
        return ((Label) card.getRoot().lookup("#" + labelId)).getText();
    }
}
