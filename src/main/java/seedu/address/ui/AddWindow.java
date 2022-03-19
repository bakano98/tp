package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;


/**
 * Controller for an Add page
 */
public class AddWindow extends UiPart<Stage> {

    public static final String HELP_MESSAGE = "You can add a person easily here";
    public static final String NAME_LABEL = "Name: ";
    public static final String PHONE_LABEL = "Number: ";
    public static final String ADDRESS_LABEL = "Address: ";
    public static final String EMAIL_LABEL = "Email: ";
    public static final String STAT_LABEL = "Status: ";
    public static final String MODULES_LABEL = "Modules: ";

    private static final Logger logger = LogsCenter.getLogger(AddWindow.class);
    private static final String FXML = "AddWindow.fxml";
    private Logic logic;

    @FXML
    private Button addButton;

    @FXML
    private Label addMessageLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label statLabel;

    @FXML
    private Label modulesLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField statField;

    @FXML
    private TextField modulesField;


    /**
     * Creates a new AddWindow.
     *
     * @param root Stage to use as the root of the AddWindow.
     */
    public AddWindow(Stage root) {
        super(FXML, root);
    }

    /**
     * Creates a new AddWindow.
     */
    public AddWindow(Logic logic) {
        this(new Stage());
        this.logic = logic;
        addMessageLabel.setText(HELP_MESSAGE);
        nameLabel.setText(NAME_LABEL);
        phoneLabel.setText(PHONE_LABEL);
        addressLabel.setText(ADDRESS_LABEL);
        emailLabel.setText(EMAIL_LABEL);
        statLabel.setText(STAT_LABEL);
        modulesLabel.setText(MODULES_LABEL);
        errorLabel.setText("");
    }

    /**
     * Shows the add window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Resets the fields when handleCancel() is triggered, or when submission is successful
     */
    public void resetFields() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        emailField.setText("");
        statField.setText("");
        modulesField.setText("");
        errorLabel.setText("");
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the add window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Handles submission of the fields inputted by the user through AddWindow's Ui.
     */
    @FXML
    private void handleAdd() {
        String name = "n/" + nameField.getText();
        String phone = "p/" + phoneField.getText();
        String address = "a/" + addressField.getText();
        String email = "e/" + emailField.getText();
        String status = "s/" + statField.getText();
        String[] modules = modulesField.getText().split(" ");
        StringBuilder userInput = new StringBuilder();
        String[] personFields = {"add", name, phone, address, email};
        if (isAnyCompulsoryFieldEmpty()) {
            errorLabel.setText("You must input all fields!");
            return;
        }

        // Craft the user input to be fed into executeCommand
        for (int i = 0; i < personFields.length; i++) {
            userInput.append(personFields[i]).append(" ");
        }

        try {
            executeCommand(userInput.toString());
        } catch (CommandException | ParseException e) {
            errorLabel.setText("Error encountered");
            return;
        }

        // Since user command execution is successful, then we do the other stuff next.
        // Notice that whenever a new Person is added into AddressBook, it'll list out all Persons.
        // So we simply need to retrieve the last Person added...
        ReadOnlyAddressBook ab = logic.getAddressBook();
        ObservableList<Person> personList = ab.getPersonList();
        int lastIndex = personList.size();

        // Do not need to handle the fact that the given status might not be valid.
        if (!status.equals("")) {
            String setStatus = "status " + lastIndex + " " + status;
            try {
                executeCommand(setStatus);
            } catch(CommandException | ParseException e) {
                errorLabel.setText("Error encountered\nEnsure status is only blacklist, whitelist, or empty.");
                return;
            }
        }

        if (modules.length != 0) {
            String modsToAdd = "";
            for (int i = 0; i < modules.length; i++) {
                modsToAdd += "m/" + modules[i];

                // Append whitespace if it's not the last module to add.
                if (i != modules.length-1) {
                    modsToAdd += " ";
                }
            }

            // Then, execute the addmodule command.
            String commandText = "addmodule " + lastIndex + " " + modsToAdd;
            try {
                executeCommand(commandText);
            } catch (CommandException | ParseException e) {
                errorLabel.setText("Error encountered");
            }
        }


        // reset all fields and then hide the panel
        this.resetFields();
        this.hide();
    }

    /**
     * Checks all the fields to make sure none of it is empty.
     * @return true if any of the field is empty
     */
    private boolean isAnyCompulsoryFieldEmpty() {
        if (nameField.getText().equals("") || phoneField.getText().equals("")
                || addressField.getText().equals("") || emailField.getText().equals("")) {
            return true;
        }

        return false;
    }

    /**
     * Handles the case where the "Cancel" button is pressed.
     */
    @FXML
    private void handleCancel() {
        this.resetFields();
        this.hide();
    }

    /**
     * Handles the case where ENTER key is pressed on the last TextField
     */
    @FXML
    private void handleEnter() {
        this.handleAdd();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            MainWindow.resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            MainWindow.resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
