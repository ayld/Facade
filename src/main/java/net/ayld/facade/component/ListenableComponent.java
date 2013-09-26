package net.ayld.facade.component;

import net.ayld.facade.event.model.ClassResolverUpdate;
import net.ayld.facade.event.model.JarExtractionStartUpdate;
import net.ayld.facade.event.model.SourceResolverUpdate;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.eventbus.EventBus;

/**
 * Intended for extension.
 * 
 * Extenders should use the given event bus to notify listeners for progress updates.
 * The definition of what a 'progress update' is depends on the context of the extender.
 * 
 * @see ClassResolverUpdate
 * @see JarExtractionStartUpdate
 * @see SourceResolverUpdate
 * @see StatusUpdate
 * */
public abstract class ListenableComponent {

	protected EventBus eventBus;

	@Required
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
