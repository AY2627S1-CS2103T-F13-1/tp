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
        assertThrows(IllegalArgumentException.class, () -> lesson("Math", 16, 16));
        // an end time earlier than the start time means the lesson ends on the next day
        assertEquals("Math (Mon 2200-0100)", lesson("Math", 22, 1).toString());
        assertEquals("Math (Mon 1800-1600)", lesson("Math", 18, 16).toString());
        assertThrows(IllegalArgumentException.class, () -> new Lesson("Math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0, 1), LocalTime.of(18, 0)));
        assertThrows(NullPointerException.class, () -> lesson(null, 16, 18));
    }

    @Test
    public void constructor_timeFinerThanMinute_throwsIllegalArgumentException() {
        LocalTime start = LocalTime.of(16, 0);
        LocalTime end = LocalTime.of(18, 0);
        // seconds or nanoseconds on either the start time or the end time
        for (LocalTime invalid : new LocalTime[] {start.withSecond(1), start.withNano(1)}) {
            assertThrows(IllegalArgumentException.class, () -> new Lesson("Math", DayOfWeek.MONDAY, invalid, end));
        }
        for (LocalTime invalid : new LocalTime[] {end.withSecond(1), end.withNano(1)}) {
            assertThrows(IllegalArgumentException.class, () -> new Lesson("Math", DayOfWeek.MONDAY, start, invalid));
        }
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
