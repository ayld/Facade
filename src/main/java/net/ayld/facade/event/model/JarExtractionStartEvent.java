package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class JarExtractionStartEvent extends OperationStartEvent {

	public JarExtractionStartEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
