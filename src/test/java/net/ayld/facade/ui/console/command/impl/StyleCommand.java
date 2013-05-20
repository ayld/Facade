package net.ayld.facade.ui.console.command.impl;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.model.Argument;

public class StyleCommand extends AbstractCommand implements Command{

	public StyleCommand() {
		supportNames("style");
	}

	@Override
	protected void internalExecute(Argument... args) {
		System.out.println("baby baby");
	}

}
