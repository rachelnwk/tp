package seedu.bob;

import seedu.bob.exception.CommandException;
import seedu.bob.exception.CommandFormatException;

/**
 * Represents a contact, such as a supplier or maintenance office, with a telephone number and an
 * optional email address and role.
 */
public class Contact {

    private final String name;
    private final String telephone;
    /** Email address, or {@code null} if none was supplied. */
    private final String email;
    /** Description of what the contact is for, or {@code null} if none was supplied. */
    private final String role;

    /**
     * Creates a validated contact.
     *
     * @param name Contact name.
     * @param telephone Telephone number, which must consist of digits only.
     * @param email Email address, or {@code null} if not supplied.
     * @param role Role of the contact, or {@code null} if not supplied.
     * @throws CommandException If any supplied value is blank or invalid.
     */
    public Contact(String name, String telephone, String email, String role) throws CommandException {
        if (name == null || name.isBlank()) {
            throw new CommandFormatException("Contact name cannot be blank.");
        }
        if (telephone == null || telephone.isBlank()) {
            throw new CommandFormatException("Telephone cannot be blank.");
        }
        if (!telephone.matches("\\d+")) {
            throw new CommandFormatException("Telephone must contain digits only.");
        }
        if (email != null && email.isBlank()) {
            throw new CommandFormatException("Email cannot be blank.");
        }
        if (email != null && !email.matches("\\S+@\\S+")) {
            throw new CommandFormatException("Email must be a valid address, e.g. name@example.com.");
        }
        if (role != null && role.isBlank()) {
            throw new CommandFormatException("Role cannot be blank.");
        }

        this.name = name;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    /**
     * Returns the contact in the form {@code NAME (TELEPHONE[, EMAIL])[ - ROLE]}.
     *
     * @return User-facing description of this contact.
     */
    @Override
    public String toString() {
        String details = email == null ? telephone : telephone + ", " + email;
        String description = name + " (" + details + ")";
        return role == null ? description : description + " - " + role;
    }
}
