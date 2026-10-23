package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelpWindowTest {

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_displaysTutorReachCommandUsageAndUserGuide() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                HelpWindow helpWindow = new HelpWindow(stage);
                Label helpMessage = (Label) stage.getScene().lookup("#helpMessage");

                assertEquals("TutorReach Help", stage.getTitle());
                assertFalse(stage.isResizable());
                assertEquals(HelpWindow.HELP_MESSAGE, helpMessage.getText());
                assertTrue(helpMessage.getText().contains("addstudent"));
                assertTrue(helpMessage.getText().contains(
                        "editstudent INDEX [n/NAME] [p/PHONE] [gn/GUARDIAN_NAME gp/GUARDIAN_PHONE]"));
                assertTrue(helpMessage.getText().contains(
                        "  findstudent [n/NAME_KEYWORDS...] [gn/GUARDIAN_NAME_KEYWORDS...] [p/PHONE]\n"));
                assertEquals(1, helpMessage.getText().split("findstudent", -1).length - 1,
                        "each command should be listed once");
                assertTrue(helpMessage.getText().contains("addlesson"));
                assertTrue(helpMessage.getText().contains(
                        "editlesson INDEX [n/NAME] [d/DAY] [st/START_TIME] [et/END_TIME]"));
                assertTrue(helpMessage.getText().contains("listlessons"));
                assertTrue(helpMessage.getText().contains("unenrol"));
                assertTrue(helpMessage.getText().contains(HelpWindow.USERGUIDE_URL));
            } finally {
                stage.close();
            }
            return null;
        });
    }
}
