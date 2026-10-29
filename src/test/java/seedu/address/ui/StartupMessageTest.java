package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class StartupMessageTest {
    @TempDir
    public Path folder;

    @BeforeAll
    public static void initializeJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void startupWarning_appearsInExistingMessageBox() throws Exception {
        String warning = "Warning: skipped 1 invalid record. Original data backed up.";
        String displayed = JavaFxTestUtil.runOnFxThread(() -> {
            Path file = folder.resolve("addressbook.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(file),
                    new JsonUserPrefsStorage(folder.resolve("prefs.json")));
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, new LogicManager(new ModelManager(), storage), file);
                window.fillInnerParts();
                window.showStartupMessage(warning);
                window.showStartupMessage(""); // An absent warning must not clear existing feedback.
                TextArea result = (TextArea) stage.getScene().lookup("#resultDisplay");
                return result.getText();
            } finally {
                stage.close();
            }
        });
        assertEquals(warning, displayed);
    }

    @Test
    public void multilineFeedback_expandsResultPanelWithinMaximumHeight() throws Exception {
        double[] heights = JavaFxTestUtil.runOnFxThread(() -> {
            Path file = folder.resolve("addressbook.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(file),
                    new JsonUserPrefsStorage(folder.resolve("prefs.json")));
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, new LogicManager(new ModelManager(), storage), file);
                window.fillInnerParts();
                stage.show();
                Parent root = stage.getScene().getRoot();
                StackPane resultPanel = (StackPane) root.lookup("#resultDisplayPlaceholder");

                window.showStartupMessage("Short message");
                root.applyCss();
                root.layout();
                double shortMessageHeight = resultPanel.getHeight();

                window.showStartupMessage("Line\n".repeat(20));
                root.layout();
                return new double[] {shortMessageHeight, resultPanel.getHeight(), resultPanel.getMaxHeight()};
            } finally {
                stage.close();
            }
        });
        assertTrue(heights[1] > heights[0]);
        assertTrue(heights[1] <= heights[2]);
        assertEquals(250, heights[2]);
    }
}
