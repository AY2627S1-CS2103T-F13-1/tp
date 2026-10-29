package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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
    public void constructor_fontChanges_recalculatesPreferredRows() throws Exception {
        int preferredRows = JavaFxTestUtil.runOnFxThread(() -> {
            TextArea textArea = getTextArea(new ResultDisplay());
            textArea.setFont(Font.font("Monospaced", 18));
            return textArea.getPrefRowCount();
        });
        assertEquals(3, preferredRows);
    }

    @Test
    public void setFeedbackToUser_multilineMessage_increasesPreferredHeight() throws Exception {
        boolean isHeightIncreased = JavaFxTestUtil.runOnFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            VBox container = new VBox(resultDisplay.getRoot(), spacer);
            new Scene(container, 600, 600);
            container.applyCss();
            container.layout();

            resultDisplay.setFeedbackToUser("First line");
            container.layout();
            double shortMessageHeight = resultDisplay.getRoot().getHeight();

            resultDisplay.setFeedbackToUser("First line\nSecond line\nThird line\nFourth line\nFifth line");
            container.layout();
            double multilineMessageHeight = resultDisplay.getRoot().getHeight();
            return multilineMessageHeight > shortMessageHeight;
        });
        assertTrue(isHeightIncreased);
    }

    @Test
    public void setFeedbackToUser_wrappedMessage_increasesPreferredHeight() throws Exception {
        boolean isHeightIncreased = JavaFxTestUtil.runOnFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            TextArea textArea = getTextArea(resultDisplay);
            textArea.resize(180, 100);
            resultDisplay.setFeedbackToUser("Short message");
            int shortMessageRows = textArea.getPrefRowCount();

            resultDisplay.setFeedbackToUser("This command feedback is deliberately long enough to wrap across "
                    + "several visible lines in a narrow result panel.");
            return textArea.getPrefRowCount() > shortMessageRows;
        });
        assertTrue(isHeightIncreased);
    }

    @Test
    public void setFeedbackToUser_veryLongMessage_limitsPreferredRows() throws Exception {
        int preferredRows = JavaFxTestUtil.runOnFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            resultDisplay.setFeedbackToUser("Line\n".repeat(20));
            return getTextArea(resultDisplay).getPrefRowCount();
        });
        assertEquals(9, preferredRows);
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
