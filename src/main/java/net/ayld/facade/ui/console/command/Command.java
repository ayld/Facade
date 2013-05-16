package net.ayld.facade.ui.console.command;

import net.ayld.facade.ui.console.model.Argument;

public interface Command {

	public void execute(Argument arg);
}
