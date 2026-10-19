package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Student> filteredStudents;
    private final ObservableList<Student> readOnlyFilteredStudents;
    private final FilteredList<Lesson> filteredLessons;
    private final ObservableList<Lesson> readOnlyFilteredLessons;
    private UUID studentLessonFilter;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredStudents = new FilteredList<>(this.addressBook.getStudentList());
        readOnlyFilteredStudents = FXCollections.unmodifiableObservableList(filteredStudents);
        filteredLessons = new FilteredList<>(this.addressBook.getLessonList());
        readOnlyFilteredLessons = FXCollections.unmodifiableObservableList(filteredLessons);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        if (studentLessonFilter != null) {
            if (this.addressBook.getLessonList().stream().noneMatch(l -> l.getId().equals(studentLessonFilter))) {
                updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
            } else {
                refreshStudentLessonFilter();
            }
        }
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public boolean hasStudent(Student student) {
        return addressBook.hasStudent(student);
    }

    @Override
    public void addStudent(Student student) {
        addressBook.addStudent(student);
        updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
    }

    @Override
    public void setStudent(Student target, Student editedStudent) {
        addressBook.setStudent(target, editedStudent);
    }

    @Override
    public void deleteStudent(Student student) {
        addressBook.removeStudent(student);
    }

    @Override
    public ObservableList<Student> getFilteredStudentList() {
        return readOnlyFilteredStudents;
    }

    @Override
    public void updateFilteredStudentList(Predicate<Student> predicate) {
        requireNonNull(predicate);
        studentLessonFilter = null;
        filteredStudents.setPredicate(predicate);
    }

    @Override
    public boolean hasLesson(Lesson lesson) {
        return addressBook.hasLesson(lesson);
    }

    @Override
    public void addLesson(Lesson lesson) {
        addressBook.addLesson(lesson);
        updateFilteredLessonList(PREDICATE_SHOW_ALL_LESSONS);
    }

    @Override
    public void setLesson(Lesson target, Lesson editedLesson) {
        addressBook.setLesson(target, editedLesson);
    }

    @Override
    public void deleteLesson(Lesson lesson) {
        addressBook.removeLesson(lesson);
        if (lesson.getId().equals(studentLessonFilter)) {
            updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        }
    }

    @Override
    public void enrolStudent(Student student, Lesson lesson) {
        requireAllNonNull(student, lesson);
        addressBook.addEnrolment(new Enrolment(student.getId(), lesson.getId()));
        refreshStudentLessonFilter();
    }

    @Override
    public ObservableList<Lesson> getFilteredLessonList() {
        return readOnlyFilteredLessons;
    }

    @Override
    public void updateFilteredLessonList(Predicate<Lesson> predicate) {
        filteredLessons.setPredicate(requireNonNull(predicate));
    }

    @Override
    public void filterStudentsByLesson(Lesson lesson) {
        requireNonNull(lesson);
        if (!addressBook.getLessonList().contains(lesson)) {
            throw new IllegalArgumentException("Lesson not found.");
        }
        studentLessonFilter = lesson.getId();
        refreshStudentLessonFilter();
    }

    private void refreshStudentLessonFilter() {
        if (studentLessonFilter != null) {
            UUID lessonId = studentLessonFilter;
            filteredStudents.setPredicate(s -> addressBook.getEnrolmentList()
                    .contains(new Enrolment(s.getId(), lessonId)));
        }
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredStudents.equals(otherModelManager.filteredStudents)
                && filteredLessons.equals(otherModelManager.filteredLessons)
                && Objects.equals(studentLessonFilter, otherModelManager.studentLessonFilter);
    }

}
