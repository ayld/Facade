package net.ayld.facade.event.model;

import net.ayld.facade.component.ListenableComponent;

public class ClassDependencyResolutionEndEvent extends OperationEndEvent {

	public ClassDependencyResolutionEndEvent(String message, Class<? extends ListenableComponent> by) {
		super(message, by);
	}
}
