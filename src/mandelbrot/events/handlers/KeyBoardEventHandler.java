package mandelbrot.events.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import mandelbrot.events.Event;
import mandelbrot.events.Event.EventType;

public class KeyBoardEventHandler extends EventHandler implements KeyListener {
	
	public KeyBoardEventHandler(List<Event> eventQueue) {
		super(eventQueue);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		var event = new Event(EventType.KEY_PRESS, ()->e.getKeyCode());
		addEvent(event);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

}
