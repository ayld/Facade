package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class SourceDependencyResolutionEndEvent extends OperationEndEvent {

	public SourceDependencyResolutionEndEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
