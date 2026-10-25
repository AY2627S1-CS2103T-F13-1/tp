package seedu.address.pe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Runs commands as a PE tester types them, through {@code LogicManager} and a JSON data file, for the regression
 * tests in this package.
 */
class PeTestHarness {
    private final Path dataFile;
    private final JsonAddressBookStorage addressBookStorage;
    private final ModelManager model;
    private final LogicManager logic;

    private PeTestHarness(Path dataFile, JsonAddressBookStorage addressBookStorage, ReadOnlyAddressBook data) {
        this.dataFile = dataFile;
        this.addressBookStorage = addressBookStorage;
        model = new ModelManager(data, new UserPrefs());
        logic = new LogicManager(model, new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(dataFile.resolveSibling("preferences.json"))));
    }

    /**
     * Returns a harness with no data, whose data file in {@code folder} is not yet written.
     */
    static PeTestHarness withNoData(Path folder) {
        return withData(folder, new AddressBook());
    }

    /**
     * Returns a harness with the sample data, as on a first launch, whose data file in {@code folder} is not yet
     * written.
     */
    static PeTestHarness withSampleData(Path folder) {
        return withData(folder, SampleDataUtil.getSampleAddressBook());
    }

    /**
     * Returns a harness with the sample data and two lessons, Maths and Science, with the first sample student
     * enrolled in Maths. The lessons differ from the User Guide's examples, so those examples add no duplicates.
     */
    static PeTestHarness withSampleDataAndLessons(Path folder) {
        AddressBook data = new AddressBook(SampleDataUtil.getSampleAddressBook());
        Lesson maths = new Lesson("Maths", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        data.addLesson(maths);
        data.addLesson(new Lesson("Science", DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)));
        data.addEnrolment(new Enrolment(data.getStudentList().getFirst().getId(), maths.getId()));
        return withData(folder, data);
    }

    /**
     * Returns a harness that loads {@code dataFile} as {@code MainApp} does: sample data if the file is missing,
     * and no data if it cannot be read.
     */
    static PeTestHarness launch(Path dataFile) {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        ReadOnlyAddressBook data;
        try {
            data = storage.readAddressBook().orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            data = new AddressBook();
        }
        return new PeTestHarness(dataFile, storage, data);
    }

    private static PeTestHarness withData(Path folder, ReadOnlyAddressBook data) {
        Path dataFile = folder.resolve("data").resolve("addressbook.json");
        return new PeTestHarness(dataFile, new JsonAddressBookStorage(dataFile), data);
    }

    CommandResult execute(String command) throws CommandException, ParseException {
        return logic.execute(command);
    }

    /**
     * Asserts that {@code command} is rejected with a parse or command error, not another exception, and that
     * neither the data nor the data file changes.
     */
    void assertRejectedWithoutChange(String command) throws IOException {
        AddressBook before = new AddressBook(model.getAddressBook());
        byte[] fileBefore = readDataFile();

        Exception error = assertThrows(Exception.class, () -> logic.execute(command));

        assertTrue(error instanceof ParseException || error instanceof CommandException,
                "Expected a parse or command error but got " + error);
        assertEquals(before, new AddressBook(model.getAddressBook()));
        assertArrayEquals(fileBefore, readDataFile());
    }

    /**
     * Returns the data file's contents, or an empty array if it has not been written.
     */
    byte[] readDataFile() throws IOException {
        return Files.exists(dataFile) ? Files.readAllBytes(dataFile) : new byte[0];
    }

    /**
     * Saves the current data to the data file.
     */
    void save() throws IOException {
        addressBookStorage.saveAddressBook(model.getAddressBook());
    }

    ModelManager getModel() {
        return model;
    }

    Path getDataFile() {
        return dataFile;
    }

    JsonAddressBookStorage getAddressBookStorage() {
        return addressBookStorage;
    }
}
