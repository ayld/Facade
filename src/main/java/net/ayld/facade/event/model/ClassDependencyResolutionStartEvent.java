package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class ClassDependencyResolutionStartEvent extends OperationStartEvent {

	public ClassDependencyResolutionStartEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
