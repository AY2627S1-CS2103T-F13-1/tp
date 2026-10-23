package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;

public class ResultDisplayTest {

    @BeforeAll
    public static void setUpJavaFx() {
        JavaFxTestUtil.initializeToolkit();
    }

    @Test
    public void constructor_wrapsLongMessages() throws Exception {
        // A clash warning or a list of unenrolled students can be longer than the window is wide.
        boolean isWrapped = JavaFxTestUtil.runOnFxThread(() -> getTextArea(new ResultDisplay()).isWrapText());
        assertTrue(isWrapped);
    }

    @Test
    public void setFeedbackToUser_showsWholeMessage() throws Exception {
        String feedback = "New lesson added: IP Chemistry Intensive (Mon 1400-2000)\n"
                + "Warning: clashes with Sec 3 A-Math (Mon 1600-1800); Sec 4 E-Math (Mon 1630-1830)";
        String displayed = JavaFxTestUtil.runOnFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            resultDisplay.setFeedbackToUser(feedback);
            return getTextArea(resultDisplay).getText();
        });
        assertEquals(feedback, displayed);
    }

    @Test
    public void setFeedbackToUser_null_throwsNullPointerException() throws Exception {
        ResultDisplay resultDisplay = JavaFxTestUtil.runOnFxThread(ResultDisplay::new);
        assertThrows(NullPointerException.class, () -> resultDisplay.setFeedbackToUser(null));
    }

    private static TextArea getTextArea(ResultDisplay resultDisplay) {
        return (TextArea) resultDisplay.getRoot().lookup("#resultDisplay");
    }
}
