package net.ayld.facade.event.model;

public abstract class StatusUpdate {
	
	private final String message;

	protected StatusUpdate(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
