package net.ayld.facade.component;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.eventbus.EventBus;

public abstract class ListenableComponent {

	protected EventBus eventBus;

	@Required
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
