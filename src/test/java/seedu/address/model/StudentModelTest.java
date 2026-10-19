package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

public class StudentModelTest {
    private final Student aiden = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
    private final Student chloe = new Student(new Name("Chloe Tan"), new Phone("91234567"), null);

    @Test
    public void studentList_observableAndUnmodifiable() {
        Model model = new ModelManager();
        ObservableList<Student> displayed = model.getFilteredStudentList();
        AtomicInteger changes = new AtomicInteger();
        displayed.addListener((ListChangeListener<Student>) change -> changes.incrementAndGet());
        model.addStudent(aiden);
        assertEquals(List.of(aiden), displayed);
        assertTrue(changes.get() > 0);
        assertThrows(UnsupportedOperationException.class, () -> displayed.add(chloe));
        assertThrows(UnsupportedOperationException.class, () -> model.getAddressBook().getStudentList().add(chloe));
        assertThrows(DuplicateStudentException.class, () -> model.addStudent(aiden));
    }

    @Test
    public void filtering_deletePreservesFilter_addClearsFilter() {
        Model model = new ModelManager();
        model.addStudent(aiden);
        model.addStudent(chloe);
        model.updateFilteredStudentList(student -> student.equals(aiden));
        assertEquals(List.of(aiden), model.getFilteredStudentList());
        model.deleteStudent(aiden);
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(List.of(chloe), model.getAddressBook().getStudentList());
        assertThrows(StudentNotFoundException.class, () -> model.deleteStudent(aiden));
        model.addStudent(aiden);
        assertEquals(List.of(chloe, aiden), model.getFilteredStudentList());
    }

    @Test
    public void copyAndReset_preserveStudentsAndNotifyExistingView() {
        AddressBook original = new AddressBook();
        original.addStudent(aiden);
        Model model = new ModelManager(original, new UserPrefs());
        ObservableList<Student> displayed = model.getFilteredStudentList();
        original.addStudent(chloe);
        assertEquals(List.of(aiden), displayed);
        model.setAddressBook(original);
        assertEquals(List.of(aiden, chloe), displayed);
        assertEquals(original, model.getAddressBook());
        model.setAddressBook(new AddressBook());
        assertTrue(displayed.isEmpty());
    }

    @Test
    public void setStudent_preservesIdEnrolmentAndActiveFilter() {
        Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0));
        Model model = new ModelManager();
        model.addStudent(aiden);
        model.addLesson(lesson);
        model.enrolStudent(aiden, lesson);
        model.updateFilteredStudentList(student -> student.getName().equals(new Name("Aiden Lim")));
        Student editedAiden = new Student(aiden.getId(), new Name("Aiden Lim"), new Phone("92345678"), null);

        model.setStudent(aiden, editedAiden);

        assertEquals(List.of(editedAiden), model.getFilteredStudentList());
        assertEquals(List.of(new Enrolment(aiden.getId(), lesson.getId())), model.getEnrolmentList());
        assertEquals(List.of(lesson), model.getAddressBook().getLessonsForStudent(editedAiden));
    }

    @Test
    public void setStudent_duplicateResult_throwsDuplicateStudentException() {
        Model model = new ModelManager();
        model.addStudent(aiden);
        model.addStudent(chloe);
        Student duplicateChloe = new Student(aiden.getId(), chloe.getName(), new Phone("91234567"), null);

        assertThrows(DuplicateStudentException.class, () -> model.setStudent(aiden, duplicateChloe));
        assertEquals(List.of(aiden, chloe), model.getAddressBook().getStudentList());
    }
}
