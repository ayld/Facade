package net.ayld.facade.ui.console.command.impl;

import net.ayld.facade.ui.console.command.Command;

public class StyleCommand extends AbstractCommand implements Command{

	public StyleCommand() {
		supportNames("style");
	}

	@Override
	protected void internalExecute(String... args) {
		System.out.println("baby baby");
	}

}
