package mandelbrot.events.handlers;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import mandelbrot.events.Event;

public class ComponentEventHandler extends EventHandler implements ComponentListener{

	public ComponentEventHandler(List<Event> eventQueue) {
		super(eventQueue);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
		var event = new Event(Event.EventType.SCREEN_RESIZE, ()->null);
		addEvent(event);
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

}
