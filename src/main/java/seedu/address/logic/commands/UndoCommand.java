package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Undo the previous {@code UndoableCommand}.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_USAGE_SUCCESS = "Undo success!";
    public static final String MESSAGE_USAGE_FAILURE = "Unable to perform undo!";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model, undoRedoStack);

        if (!undoRedoStack.canUndo()) {
            throw new CommandException(MESSAGE_USAGE_FAILURE);
        } else {
            undoRedoStack.popUndo().undo(model);
            return new CommandResult(MESSAGE_USAGE_SUCCESS);
        }
    }

    @Override
    public void setData(StackUndoRedo undoRedoStack) {
        this.undoRedoStack = undoRedoStack;
    }
}
