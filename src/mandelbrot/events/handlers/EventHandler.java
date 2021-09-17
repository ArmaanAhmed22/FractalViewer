package mandelbrot.events.handlers;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mandelbrot.events.Event;

public abstract class EventHandler {
	
	public final static List<String> eventListenerTypes;
	
	static {
		eventListenerTypes = Collections.unmodifiableList(Arrays.asList(
				
				new String[] {
						"ComponentListener",
						"FocusListener",
						"HierarchyBoundsListener",
						"HierarchyListener",
						"InputMethodListener",
						"KeyListener",
						"MouseListener",
						"MouseMotionListener",
						"WindowListener"
						}
				
				
				
				));
		
	}
	
	
	
	private final List<Event> eventQueue;

	public EventHandler(List<Event> eventQueue) {
		this.eventQueue = eventQueue;
	}
	
	protected final void addEvent(Event e) {
		synchronized (eventQueue) {
			eventQueue.add(e);
		}
	}
	
	

}
