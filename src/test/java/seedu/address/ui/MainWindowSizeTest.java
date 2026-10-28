package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowSizeTest {
    @TempDir
    public Path folder;

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_savedWindowBelowMinimum_isRaisedToUsableSize() throws Exception {
        Integer[] size = JavaFxTestUtil.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                var logic = new LogicManager(new ModelManager(),
                        new StorageManager(new JsonAddressBookStorage(folder.resolve("data.json")),
                                new JsonUserPrefsStorage(folder.resolve("prefs.json"))));
                logic.setGuiSettings(new GuiSettings(10, 10, 20, 30));

                MainWindow window = new MainWindow(stage, logic, folder.resolve("data.json"));

                assertEquals(500.0, stage.getWidth());
                assertEquals(600.0, stage.getHeight());
                return new Integer[] {(int) stage.getWidth(), (int) stage.getHeight()};
            } finally {
                stage.close();
            }
        });
        assertEquals(500, size[0]);
        assertEquals(600, size[1]);
    }
}
