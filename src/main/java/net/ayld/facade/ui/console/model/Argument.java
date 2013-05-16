package net.ayld.facade.ui.console.model;

public class Argument {
	
	private String arg;

	public Argument(String arg) {
		this.arg = arg;
	}

	public static Argument fromString(String arg) {
		return new Argument(arg);
	}

	@Override
	public String toString() {
		return arg;
	}
}
