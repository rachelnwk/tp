package seedu.bob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises contact commands through the same parser used by the console application.
 */
class ParserContactTest {

    private static final String DIVIDER = Ui.DIVIDER;
    private static final String DELETE_PROMPT =
            "\nPlease enter the index of the contact to delete\n(type \"done\" to exit):\n";

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Inventory inventory = new Inventory();
    private final SessionManager sessions = new SessionManager();
    private final ContactManager contacts = new ContactManager();
    private PrintStream originalOutput;
    private PrintStream capturedOutput;

    @BeforeEach
    void setUp() {
        originalOutput = System.out;
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    @Test
    void addContact_validCommands_confirmationShown() {
        assertEquals("Successfully added: NUS Infrastructure (65161234) to contacts.\n",
                execute("add-c n/NUS Infrastructure t/65161234 r/Aircon and lighting repairs"));
        assertEquals("Successfully added: Component Supplier (91234567) to contacts.\n",
                execute("add-c n/Component Supplier t/91234567 e/supplier@example.com r/Resistors, LEDs, wires"));
        assertEquals("Successfully added: Email Only (12345678) to contacts.\n",
                execute("add-c n/Email Only t/12345678 e/a@b.com"));
        assertEquals("Successfully added: Bare (87654321) to contacts.\n",
                execute("add-c n/Bare t/87654321"));
    }

    @Test
    void listContacts_emptyAndPopulated_displaysInInsertionOrder() {
        assertEquals(DIVIDER + "\nContacts\n" + DIVIDER + "\nNo contacts saved.\n" + DIVIDER + "\n",
                execute("list-c"));

        execute("add-c n/NUS Infrastructure t/65161234 r/Aircon and lighting repairs");
        execute("add-c n/Component Supplier t/91234567 e/supplier@example.com r/Resistors, LEDs, wires");

        assertEquals(DIVIDER + "\nContacts\n" + DIVIDER + "\n"
                + "1. NUS Infrastructure (65161234) - Aircon and lighting repairs\n"
                + "2. Component Supplier (91234567, supplier@example.com) - Resistors, LEDs, wires\n"
                + DIVIDER + "\n", execute("list-c"));
    }

    @Test
    void addContact_malformedArguments_noStateChange() {
        String[] commands = {
            "add-c",
            "add-c n/Bob",
            "add-c t/91234567 n/Bob",
            "add-c n/Bob t/91234567 r/Role e/a@b.com",
            "add-c n/Bob t/91234567 x/1",
            "add-c n/Bob t/91234567 t/91234567",
            "add-c extra n/Bob t/91234567"
        };
        for (String command : commands) {
            assertEquals("Invalid format. Use: add-c n/NAME t/TELEPHONE [e/EMAIL] [r/ROLE]\n",
                    execute(command), command);
        }
        assertEquals(DIVIDER + "\nContacts\n" + DIVIDER + "\nNo contacts saved.\n" + DIVIDER + "\n",
                execute("list-c"));
    }

    @Test
    void addContact_invalidValues_errorShown() {
        assertEquals("Contact name cannot be blank.\n", execute("add-c n/ t/91234567"));
        assertEquals("Telephone cannot be blank.\n", execute("add-c n/Bob t/"));
        assertEquals("Telephone must contain digits only.\n", execute("add-c n/Bob t/abc"));
        assertEquals("Email cannot be blank.\n", execute("add-c n/Bob t/91234567 e/"));
        assertEquals("Email must be a valid address, e.g. name@example.com.\n",
                execute("add-c n/Bob t/91234567 e/nope"));
        assertEquals("Role cannot be blank.\n", execute("add-c n/Bob t/91234567 r/"));
    }

    @Test
    void deleteContact_validIndexThenDone_removesAndRedisplays() {
        execute("add-c n/First t/11111111");
        execute("add-c n/Second t/22222222");

        String expected = contactList("1. First (11111111)\n2. Second (22222222)\n")
                + DELETE_PROMPT
                + "Successfully removed: First from contacts.\n\n"
                + contactList("1. Second (22222222)\n");
        assertEquals(expected, execute("delete-c", "1", "done"));
        assertEquals(contactList("1. Second (22222222)\n"), execute("list-c"));
    }

    @Test
    void deleteContact_invalidIndex_errorShownAndPromptContinues() {
        execute("add-c n/First t/11111111");

        String expected = contactList("1. First (11111111)\n")
                + DELETE_PROMPT
                + "Contact index must be a positive integer.\n"
                + "Contact index must be a positive integer.\n"
                + "Contact index 2 is invalid. There are 1 contact(s).\n"
                + "Successfully removed: First from contacts.\n\n"
                + contactList("No contacts saved.\n");
        assertEquals(expected, execute("delete-c", "abc", "0", "2", "1", "DONE"));
    }

    @Test
    void deleteContact_inputEndsWithoutDone_stopsPrompting() {
        execute("add-c n/First t/11111111");

        assertEquals(contactList("1. First (11111111)\n") + DELETE_PROMPT, execute("delete-c"));
        assertEquals(contactList("1. First (11111111)\n"), execute("list-c"));
    }

    @Test
    void deleteContact_emptyList_noPrompt() {
        assertEquals(contactList("No contacts saved.\n"), execute("delete-c", "done"));
    }

    @Test
    void deleteContact_withArguments_usageShown() {
        execute("add-c n/First t/11111111");

        assertEquals("Invalid format. Use: delete-c\n", execute("delete-c 1"));
        assertEquals(contactList("1. First (11111111)\n"), execute("list-c"));
    }

    @Test
    void listContacts_withArguments_usageShown() {
        assertEquals("Invalid format. Use: list-c\n", execute("list-c extra"));
    }

    /**
     * Builds the expected output of the contact list display around the given rows.
     */
    private String contactList(String rows) {
        return DIVIDER + "\nContacts\n" + DIVIDER + "\n" + rows + DIVIDER + "\n";
    }

    /**
     * Executes one command and returns only that command's normalized console output.
     *
     * @param command Command line entered by the user.
     * @param followUpLines Lines the user then types in response to an interactive prompt.
     */
    private String execute(String command, String... followUpLines) {
        output.reset();
        Parser parser = new Parser(new Scanner(String.join("\n", followUpLines)));
        parser.handleCommand(command, inventory, sessions, contacts);
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
