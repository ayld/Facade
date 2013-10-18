package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class SourceDependencyResolutionStartEvent extends OperationStartEvent {

	public SourceDependencyResolutionStartEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
