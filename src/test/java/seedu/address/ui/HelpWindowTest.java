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

    @Test
    public void handleHelp_afterMinimizing_reusesMinimizedWindow() throws Exception {
        JavaFxTestUtil.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                HelpWindow helpWindow = new HelpWindow(stage);
                helpWindow.show();
                assertTrue(stage.isShowing());

                stage.setIconified(true);
                assertTrue(stage.isShowing());
                assertTrue(stage.isIconified());

                helpWindow.show();
                assertTrue(stage.isShowing());
                assertTrue(stage.isIconified(), "Calling show again must not restore a minimized window");

                helpWindow.focus();
                assertTrue(stage.isShowing());
                assertTrue(stage.isIconified(), "Requesting focus must not restore a minimized window");
            } finally {
                stage.close();
            }
            return null;
        });
    }
}
