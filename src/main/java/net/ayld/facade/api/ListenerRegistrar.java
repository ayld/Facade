package net.ayld.facade.api;

import net.ayld.facade.util.Components;

import com.google.common.eventbus.EventBus;

public final class ListenerRegistrar {

	private final Object[] listeners;
	
	private ListenerRegistrar(Object[] listeners) {
		this.listeners = listeners;
	}

	public static ListenerRegistrar listeners(Object... listeners) {
		return new ListenerRegistrar(listeners);
	}
	
	public void register() {
		for (Object l : listeners) {
			Components.EVENT_BUS.<EventBus>getInstance().register(l);
		}
	}
}
