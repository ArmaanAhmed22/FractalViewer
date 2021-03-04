package mandelbrot.events.handlers;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import mandelbrot.events.Event;
import mandelbrot.events.Event.EventType;

public class EventHandlerManager {
	
	private final EnumMap<Event.EventType, Consumer<Event>> eventBindings;
	private final List<EventHandler> eventHandlers;
	private final List<Event> eventQueue;
	
	
	
	public EventHandlerManager() {
		eventBindings = new EnumMap<Event.EventType,Consumer<Event>>(Event.EventType.class);
		eventHandlers = new ArrayList<EventHandler>();
		eventQueue = new ArrayList<Event>();
	}
	
	public <T extends EventHandler & EventListener> void addEventHandler(Class<T> ehClass, Component c) {
		try {
			var ehConstructor = ehClass.getConstructor(List.class);
			EventHandler eh = ehConstructor.newInstance(eventQueue);
			eventHandlers.add(eh);
			System.out.println("here: "+ehClass);
			getComponentListenerMethod(ehClass).invoke(c, eh);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private <T extends EventHandler & EventListener> Method getComponentListenerMethod(Class<T> ehClass) throws ClassNotFoundException {
		var methodNames = new ArrayList<String>(EventHandler.eventListenerTypes);
		methodNames.replaceAll((s) -> "add"+s);
		
		for (Method method : Component.class.getMethods()) {
			if (!methodNames.contains(method.getName()))
				continue;
			if (!(method.getParameterCount() == 1))
				continue;
			if (method.getParameters()[0].getType().isAssignableFrom(ehClass)) {
				return method;
			}
		}
		throw new ClassNotFoundException("Could not find class");
	}
	
	public EventHandlerManager bind(Event.EventType e, Consumer<Event> c) {
		eventBindings.put(e, c);
		return this;
	}
	
	public void performAction(Event e) {
		eventBindings.get(e.getEventType()).accept(e);
	}
	
	public void checkAndPerformAction() {
		synchronized (eventQueue) {
			for (Event event : eventQueue) {
				performAction(event);
			}
			eventQueue.clear();
		}
	}
	

}
