package net.ayld.facade.ui.console.model;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Argument)) {
			return false;
		}
		
		final Argument arg = (Argument) obj;
		
		return Objects.equals(arg.toString(), name);
	}
}
