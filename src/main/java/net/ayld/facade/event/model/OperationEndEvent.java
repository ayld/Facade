package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class OperationEndEvent extends ComponentEvent {

	public OperationEndEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
