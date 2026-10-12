package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class LessonTest {
    @Test
    public void constructor_validatesNameAndTimes() {
        Lesson lesson = lesson("Sec 3  A-Math", 16, 18);
        assertEquals("Sec 3 A-Math (Mon 1600-1800)", lesson.toString());
        for (String name : new String[] {"", " ", "Math/Science", "-Math", "a".repeat(51)}) {
            assertThrows(IllegalArgumentException.class, () -> lesson(name, 16, 18));
        }
        assertThrows(IllegalArgumentException.class, () -> lesson("Math", 18, 16));
        assertThrows(IllegalArgumentException.class, () -> lesson("Math", 16, 16));
        assertThrows(IllegalArgumentException.class, () -> new Lesson("Math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0, 1), LocalTime.of(18, 0)));
        assertThrows(NullPointerException.class, () -> lesson(null, 16, 18));
    }

    @Test
    public void duplicateCheck_ignoresCaseAndSpacingButIncludesSchedule() {
        Lesson lesson = lesson("Sec 3 A-Math", 16, 18);
        assertTrue(lesson.isSameLesson(lesson("sec 3  a-math", 16, 18)));
        assertFalse(lesson.isSameLesson(lesson("Sec 3 A-Math", 17, 19)));
        Lesson restored = new Lesson(lesson.getId(), lesson.getName(), lesson.getDay(),
                lesson.getStartTime(), lesson.getEndTime());
        assertEquals(lesson, restored);
        assertEquals(lesson.hashCode(), restored.hashCode());
    }

    private Lesson lesson(String name, int start, int end) {
        return new Lesson(name, DayOfWeek.MONDAY, LocalTime.of(start, 0), LocalTime.of(end, 0));
    }
}
