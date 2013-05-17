package net.ayld.facade.ui.console.model;

public class Argument {
	
	private String name;

	private Argument(String arg) {
		this.name = arg;
	}

	public static Argument fromString(String arg) {
		return new Argument(arg);
	}

	@Override
	public String toString() {
		return name;
	}
}
