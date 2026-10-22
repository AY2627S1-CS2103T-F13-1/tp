package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class LessonModelTest {
    private final Student student = new Student(new Name("Aiden"), new Phone("91234567"), null);
    private final Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));

    @Test
    public void enrolment_validatesReferencesAndDuplicates_notifiesObservers() {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addLesson(lesson);
        AtomicInteger changes = new AtomicInteger();
        model.getEnrolmentList().addListener((ListChangeListener<Enrolment>) change -> changes.incrementAndGet());
        model.enrolStudent(student, lesson);
        assertEquals(List.of(student), model.getAddressBook().getStudentsInLesson(lesson));
        assertEquals(List.of(lesson), model.getAddressBook().getLessonsForStudent(student));
        assertEquals(1, changes.get());
        assertThrows(IllegalArgumentException.class, () -> model.enrolStudent(student, lesson));
        assertThrows(IllegalArgumentException.class, () -> model.addLesson(new Lesson("math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0))));
        assertThrows(IllegalArgumentException.class, () -> new AddressBook()
                .addEnrolment(new Enrolment(UUID.randomUUID(), lesson.getId())));
        assertThrows(UnsupportedOperationException.class, () -> model.getEnrolmentList().clear());
        assertThrows(UnsupportedOperationException.class, () -> model.getFilteredLessonList().clear());
    }

    @Test
    public void unenrolment_removesOnlyLink_rejectsMissingLink() {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addLesson(lesson);
        model.filterStudentsByLesson(lesson);
        model.enrolStudent(student, lesson);
        assertEquals(List.of(student), model.getFilteredStudentList());
        model.unenrolStudent(student, lesson);
        assertTrue(model.getEnrolmentList().isEmpty());
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(List.of(student), model.getAddressBook().getStudentList());
        assertEquals(List.of(lesson), model.getAddressBook().getLessonList());
        assertThrows(IllegalArgumentException.class, () -> model.unenrolStudent(student, lesson));
    }

    @Test
    public void filteringAndDeletion_keepReferencesConsistent() {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addLesson(lesson);
        model.filterStudentsByLesson(lesson);
        assertTrue(model.getFilteredStudentList().isEmpty());
        model.enrolStudent(student, lesson);
        assertEquals(List.of(student), model.getFilteredStudentList());
        model.updateFilteredLessonList(l -> false);
        assertEquals(List.of(lesson), model.getAddressBook().getLessonsForStudent(student));
        model.deleteLesson(lesson);
        assertTrue(model.getEnrolmentList().isEmpty());
        assertEquals(List.of(student), model.getFilteredStudentList());
        model.addLesson(lesson);
        assertEquals(List.of(lesson), model.getFilteredLessonList());
        model.enrolStudent(student, lesson);
        model.deleteStudent(student);
        assertTrue(model.getEnrolmentList().isEmpty());
        assertEquals(List.of(lesson), model.getFilteredLessonList());
    }

    @Test
    public void filterStudentsByLesson_lessonAbsent_throwsIllegalArgumentException() {
        Model model = new ModelManager();

        assertThrows(IllegalArgumentException.class, () -> model.filterStudentsByLesson(lesson));
    }

    @Test
    public void setAddressBook_activeLessonFilter_refreshesEnrolledStudents() {
        Student ryan = new Student(new Name("Ryan"), new Phone("92345678"), null);
        AddressBook initial = new AddressBook();
        initial.addStudent(student);
        initial.addStudent(ryan);
        initial.addLesson(lesson);
        initial.addEnrolment(new Enrolment(student.getId(), lesson.getId()));
        Model model = new ModelManager(initial, new UserPrefs());
        model.filterStudentsByLesson(lesson);
        var filteredStudents = model.getFilteredStudentList();
        assertEquals(List.of(student), filteredStudents);

        AddressBook replacement = new AddressBook();
        replacement.addStudent(student);
        replacement.addStudent(ryan);
        replacement.addLesson(lesson);
        replacement.addEnrolment(new Enrolment(ryan.getId(), lesson.getId()));
        model.setAddressBook(replacement);

        assertEquals(List.of(ryan), filteredStudents);
        assertEquals(filteredStudents, model.getFilteredStudentList());
    }

    @Test
    public void reset_preservesLinksAndExistingObservableViews() {
        Model model = new ModelManager();
        var view = model.getFilteredLessonList();
        AddressBook replacement = new AddressBook();
        replacement.addStudent(student);
        replacement.addLesson(lesson);
        replacement.addEnrolment(new Enrolment(student.getId(), lesson.getId()));
        model.setAddressBook(replacement);
        assertEquals(List.of(lesson), view);
        assertTrue(model.getAddressBook().isEnrolled(student, lesson));
        model.filterStudentsByLesson(lesson);
        model.setAddressBook(new AddressBook());
        assertTrue(view.isEmpty());
        assertTrue(model.getEnrolmentList().isEmpty());
        model.addStudent(student);
        assertEquals(List.of(student), model.getFilteredStudentList());
    }

    @Test
    public void setLesson_preservesIdEnrolmentAndActiveFilters() {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addLesson(lesson);
        model.enrolStudent(student, lesson);
        model.updateFilteredLessonList(candidate -> candidate.getDay() == DayOfWeek.TUESDAY);
        model.filterStudentsByLesson(lesson);
        Lesson editedLesson = new Lesson(lesson.getId(), "Advanced Math", DayOfWeek.TUESDAY,
                LocalTime.of(17, 0), LocalTime.of(19, 0));

        model.setLesson(lesson, editedLesson);

        assertEquals(List.of(editedLesson), model.getFilteredLessonList());
        assertEquals(List.of(student), model.getFilteredStudentList());
        assertEquals(List.of(new Enrolment(student.getId(), lesson.getId())), model.getEnrolmentList());
        assertEquals(List.of(editedLesson), model.getAddressBook().getLessonsForStudent(student));
    }

    @Test
    public void setLesson_duplicateResult_throwsIllegalArgumentException() {
        Lesson science = new Lesson("Science", DayOfWeek.TUESDAY,
                LocalTime.of(14, 0), LocalTime.of(16, 0));
        Model model = new ModelManager();
        model.addLesson(lesson);
        model.addLesson(science);
        Lesson duplicateScience = new Lesson(lesson.getId(), science.getName(), science.getDay(),
                science.getStartTime(), science.getEndTime());

        assertThrows(IllegalArgumentException.class, () -> model.setLesson(lesson, duplicateScience));
        assertEquals(List.of(lesson, science), model.getAddressBook().getLessonList());
    }
}
