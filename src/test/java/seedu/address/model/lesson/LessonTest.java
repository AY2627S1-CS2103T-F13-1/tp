package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

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

    @Test
    public void overlaps_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> lesson("Math", 16, 18).overlaps(null));
    }

    @Test
    public void overlaps_sameDay_trueOnlyIfTimeRangesOverlap() {
        Lesson math = lessonOn(DayOfWeek.MONDAY, "16:00", "18:00");

        assertTrue(math.overlaps(lessonOn(DayOfWeek.MONDAY, "17:00", "19:00"))); // overlaps the end
        assertTrue(math.overlaps(lessonOn(DayOfWeek.MONDAY, "15:00", "16:30"))); // overlaps the start
        assertTrue(math.overlaps(lessonOn(DayOfWeek.MONDAY, "16:30", "17:00"))); // inside
        assertTrue(math.overlaps(lessonOn(DayOfWeek.MONDAY, "15:00", "19:00"))); // contains it
        assertTrue(math.overlaps(lessonOn(DayOfWeek.MONDAY, "16:00", "18:00"))); // identical times

        // a lesson may start exactly when another ends
        assertFalse(math.overlaps(lessonOn(DayOfWeek.MONDAY, "18:00", "19:00")));
        assertFalse(math.overlaps(lessonOn(DayOfWeek.MONDAY, "14:00", "16:00")));
        assertFalse(math.overlaps(lessonOn(DayOfWeek.MONDAY, "19:00", "20:30")));
    }

    @Test
    public void overlaps_differentDays_returnsFalse() {
        Lesson math = lessonOn(DayOfWeek.MONDAY, "16:00", "18:00");

        assertFalse(math.overlaps(lessonOn(DayOfWeek.TUESDAY, "16:00", "18:00")));
        assertFalse(math.overlaps(lessonOn(DayOfWeek.SUNDAY, "16:00", "18:00")));
    }

    @Test
    public void overlaps_lessonPastMidnight_occupiesStartOfNextDay() {
        Lesson nightClass = lessonOn(DayOfWeek.FRIDAY, "22:00", "01:00");

        assertTrue(nightClass.overlaps(lessonOn(DayOfWeek.FRIDAY, "23:00", "23:30")));
        assertTrue(nightClass.overlaps(lessonOn(DayOfWeek.SATURDAY, "00:30", "02:00")));
        assertFalse(nightClass.overlaps(lessonOn(DayOfWeek.SATURDAY, "01:00", "02:00")));
        assertFalse(nightClass.overlaps(lessonOn(DayOfWeek.SATURDAY, "09:00", "11:00")));
        assertFalse(nightClass.overlaps(lessonOn(DayOfWeek.FRIDAY, "20:00", "22:00")));
    }

    @Test
    public void overlaps_lessonEndingAtMidnight_doesNotReachNextDay() {
        Lesson lateClass = lessonOn(DayOfWeek.MONDAY, "22:00", "00:00");

        assertTrue(lateClass.overlaps(lessonOn(DayOfWeek.MONDAY, "23:00", "23:59")));
        assertFalse(lateClass.overlaps(lessonOn(DayOfWeek.TUESDAY, "00:00", "01:00")));
    }

    @Test
    public void overlaps_sundayPastMidnight_wrapsIntoMonday() {
        Lesson sundayNight = lessonOn(DayOfWeek.SUNDAY, "23:00", "01:00");

        assertTrue(sundayNight.overlaps(lessonOn(DayOfWeek.MONDAY, "00:00", "00:30")));
        assertFalse(sundayNight.overlaps(lessonOn(DayOfWeek.MONDAY, "01:00", "02:00")));
        assertFalse(sundayNight.overlaps(lessonOn(DayOfWeek.SATURDAY, "23:00", "23:59")));
    }

    @Test
    public void overlaps_bothPastMidnight() {
        Lesson nightClass = lessonOn(DayOfWeek.FRIDAY, "22:00", "01:00");

        assertTrue(nightClass.overlaps(lessonOn(DayOfWeek.FRIDAY, "23:00", "02:00")));
        // runs from Thursday night until 2230 on Friday
        assertTrue(nightClass.overlaps(lessonOn(DayOfWeek.THURSDAY, "23:00", "22:30")));
        assertFalse(nightClass.overlaps(lessonOn(DayOfWeek.SATURDAY, "22:00", "01:00")));
    }

    @Test
    public void overlaps_isSymmetric() {
        List<Lesson> lessons = List.of(
                lessonOn(DayOfWeek.MONDAY, "16:00", "18:00"),
                lessonOn(DayOfWeek.MONDAY, "17:00", "19:00"),
                lessonOn(DayOfWeek.MONDAY, "18:00", "16:00"),
                lessonOn(DayOfWeek.TUESDAY, "00:00", "01:00"),
                lessonOn(DayOfWeek.SUNDAY, "23:00", "01:00"),
                lessonOn(DayOfWeek.MONDAY, "00:30", "02:00"));

        for (Lesson first : lessons) {
            for (Lesson second : lessons) {
                assertEquals(first.overlaps(second), second.overlaps(first), first + " and " + second);
            }
        }
    }

    private Lesson lesson(String name, int start, int end) {
        return new Lesson(name, DayOfWeek.MONDAY, LocalTime.of(start, 0), LocalTime.of(end, 0));
    }

    /** Creates a lesson named Math on {@code day}; times are given as {@code HH:mm}. */
    private Lesson lessonOn(DayOfWeek day, String startTime, String endTime) {
        return new Lesson("Math", day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }
}
