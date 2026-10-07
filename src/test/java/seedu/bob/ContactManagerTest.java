package seedu.bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.bob.exception.CommandException;
import seedu.bob.exception.CommandFormatException;
import seedu.bob.exception.InvalidContactIndexException;

/**
 * Verifies contact validation, formatting, and lookup by the positions displayed to users.
 */
class ContactManagerTest {

    @Test
    void toString_allFields_formatsEmailAndRole() throws CommandException {
        Contact contact = new Contact("Component Supplier", "91234567", "supplier@example.com",
                "Resistors, LEDs, wires");

        assertEquals("Component Supplier (91234567, supplier@example.com) - Resistors, LEDs, wires",
                contact.toString());
    }

    @Test
    void toString_requiredFieldsOnly_omitsEmailAndRole() throws CommandException {
        Contact contact = new Contact("NUS Infrastructure", "65161234", null, null);

        assertEquals("NUS Infrastructure (65161234)", contact.toString());
    }

    @Test
    void constructor_invalidValues_exceptionThrown() {
        assertThrows(CommandFormatException.class, () -> new Contact(" ", "91234567", null, null));
        assertThrows(CommandFormatException.class, () -> new Contact("Bob", " ", null, null));
        assertThrows(CommandFormatException.class, () -> new Contact("Bob", "9123abcd", null, null));
        assertThrows(CommandFormatException.class, () -> new Contact("Bob", "91234567", "", null));
        assertThrows(CommandFormatException.class, () -> new Contact("Bob", "91234567", "not-an-email", null));
        assertThrows(CommandFormatException.class, () -> new Contact("Bob", "91234567", null, " "));
    }

    @Test
    void getContact_invalidIndex_exceptionThrown() {
        ContactManager manager = new ContactManager();

        assertThrows(InvalidContactIndexException.class, () -> manager.getContact(0));
        assertThrows(InvalidContactIndexException.class, () -> manager.getContact(1));
        assertThrows(InvalidContactIndexException.class, () -> manager.getContact(-1));
    }

    @Test
    void getContact_afterDeletion_remainingContactReturned() throws CommandException {
        ContactManager manager = new ContactManager();
        Contact first = new Contact("First", "11111111", null, null);
        Contact second = new Contact("Second", "22222222", null, null);
        manager.addContact(first);
        manager.addContact(second);

        manager.deleteContact(1);

        assertSame(second, manager.getContact(1));
        assertThrows(InvalidContactIndexException.class, () -> manager.getContact(2));
    }
}
