package seedu.bob;

import java.util.ArrayList;

import seedu.bob.exception.InvalidContactIndexException;

/**
 * Stores contacts in insertion order and displays contact changes.
 */
public class ContactManager {

    private static final int INDEX_OFFSET = 1;

    private final ArrayList<Contact> contacts = new ArrayList<>();

    /**
     * Returns whether there are no contacts.
     *
     * @return {@code true} if the contact list is empty.
     */
    public boolean isEmpty() {
        return contacts.isEmpty();
    }

    /**
     * Adds a contact to the list and prints a confirmation.
     *
     * @param contact Contact to add.
     */
    public void addContact(Contact contact) {
        contacts.add(contact);
        System.out.printf("Successfully added: %s (%s) to contacts.%n",
                contact.getName(), contact.getTelephone());
    }

    /**
     * Removes a contact by its one-based index and prints a confirmation.
     *
     * @param userIndex One-based contact position, as shown by {@code list-c}.
     * @throws InvalidContactIndexException If the index is outside the contact list.
     */
    public void deleteContact(int userIndex) throws InvalidContactIndexException {
        Contact removedContact = getContact(userIndex);
        contacts.remove(userIndex - INDEX_OFFSET);
        System.out.println("Successfully removed: " + removedContact.getName() + " from contacts.");
    }

    /**
     * Returns the contact at the position shown by {@code list-c}.
     *
     * @param userIndex One-based contact position.
     * @return Contact at the requested position.
     * @throws InvalidContactIndexException If the index is outside the contact list.
     */
    public Contact getContact(int userIndex) throws InvalidContactIndexException {
        if (userIndex <= 0 || userIndex > contacts.size()) {
            throw new InvalidContactIndexException(userIndex, contacts.size());
        }
        return contacts.get(userIndex - INDEX_OFFSET);
    }

    /**
     * Prints all contacts with one-based positions.
     */
    public void listContacts() {
        System.out.println(Ui.DIVIDER);
        System.out.println("Contacts");
        System.out.println(Ui.DIVIDER);

        if (contacts.isEmpty()) {
            System.out.println("No contacts saved.");
        } else {
            for (int i = 0; i < contacts.size(); i++) {
                System.out.printf("%d. %s%n", i + INDEX_OFFSET, contacts.get(i));
            }
        }
        System.out.println(Ui.DIVIDER);
    }
}
