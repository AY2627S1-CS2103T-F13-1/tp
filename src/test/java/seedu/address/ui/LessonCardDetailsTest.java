package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class LessonCardDetailsTest {

    private static final Student AIDEN = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
    private static final Student CHLOE = new Student(new Name("Chloe Lim"), new Phone("92345678"), null);

    @Test
    public void from_lessonWithStudents_formatsScheduleAndNames() {
        Lesson lesson = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0));

        LessonCardDetails details = LessonCardDetails.from(lesson, List.of(AIDEN, CHLOE));

        assertEquals("Sec 3 A-Math", details.lessonName());
        assertEquals("Monday", details.day());
        assertEquals("16:00 – 18:00", details.time());
        assertEquals("Aiden Tan\nChloe Lim", details.students());
    }

    @Test
    public void from_overnightLessonWithoutStudents_marksNextDayAndUsesPlaceholder() {
        Lesson lesson = new Lesson("Late Physics", DayOfWeek.FRIDAY,
                LocalTime.of(23, 0), LocalTime.of(1, 0));

        LessonCardDetails details = LessonCardDetails.from(lesson, List.of());

        assertEquals("23:00 – 01:00 (next day)", details.time());
        assertEquals("-", details.students());
    }
}
