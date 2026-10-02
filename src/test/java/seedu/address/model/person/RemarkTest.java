package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RemarkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void value_unconstrained_preserved() {
        for (String value : new String[] {"", "  ", "Likes baseball! 中文\nSecond line"}) {
            Remark remark = new Remark(value);
            assertEquals(value, remark.toString());
            assertEquals(remark, new Remark(value));
            assertEquals(remark.hashCode(), new Remark(value).hashCode());
        }
        assertNotEquals(new Remark("one"), new Remark("two"));
        assertNotEquals(new Remark("one"), null);
    }

    @Test
    public void person_differentRemark_sameIdentityButNotEqual() {
        Person original = new PersonBuilder().build();
        Person edited = new PersonBuilder(original).withRemark("note").build();
        assertTrue(original.isSamePerson(edited));
        assertNotEquals(original, edited);
        assertEquals(edited, new PersonBuilder(edited).build());
    }
}
