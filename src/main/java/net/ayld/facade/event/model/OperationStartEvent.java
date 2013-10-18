package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class OperationStartEvent extends ComponentEvent {

	public OperationStartEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
