package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(addressBookStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonAddressBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonAddressBookStorageTest} class.
         */
        AddressBook original = getTypicalAddressBook();
        storageManager.saveAddressBook(original);
        ReadOnlyAddressBook retrieved = storageManager.readAddressBook().get();
        assertEquals(original, new AddressBook(retrieved));
    }

    @Test
    public void getAddressBookFilePath() {
        assertNotNull(storageManager.getAddressBookFilePath());
    }

    @Test
    public void checkAddressBookSaveAllowed_afterMalformedRead_blocksLaterSave() throws Exception {
        Path file = testFolder.resolve("malformed.json");
        Files.writeString(file, "{");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(file);
        try {
            addressBookStorage.readAddressBook();
            throw new AssertionError("Expected malformed data to throw");
        } catch (DataLoadingException expected) {
            // Continue with the same storage instance used by StorageManager.
        }
        StorageManager manager = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));

        try {
            manager.saveAddressBook(new AddressBook());
            throw new AssertionError("Expected blocked saving to throw");
        } catch (java.io.IOException expected) {
            assertEquals("{", Files.readString(file));
        }
    }

    @Test
    public void checkAddressBookSaveAllowed_validStoragePath_allowsSave() throws Exception {
        Path file = testFolder.resolve("valid.json");
        StorageManager manager = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
        ModelManager model = new ModelManager();
        model.addStudent(new Student(new Name("Valid Student"), new Phone("91234567"), null));
        model.addLesson(new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0)));
        manager.saveAddressBook(model.getAddressBook());
        assertEquals(model.getAddressBook(), new AddressBook(manager.readAddressBook().orElseThrow()));
    }

}
