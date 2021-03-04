package mandelbrot.events.handlers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import mandelbrot.events.Event;
import mandelbrot.events.handlers.MouseEventHandler.MouseChange.Modifier;

public class MouseEventHandler extends EventHandler implements MouseMotionListener{
	
	private MouseChange curDragChange;
	
	public static class MouseChange {
		public final Point before;
		public final Point after;
		public final Modifier modifier;
		
		public static enum Modifier {
			NONE,DRAG;
		}
		
		private MouseChange(Point before, Point after,Modifier modifier) {
			this.after = after;
			this.before = before;
			this.modifier = modifier;
			
		}
		
		public static MouseChange getZero() {
			return new MouseChange(new Point(0,0), new Point(0,0),Modifier.NONE);
		}
		
		public MouseChange getUpdated(Point newAfter,Modifier modifier) {
			return new MouseChange(after, newAfter,modifier);
		}
		
		@Override
		public String toString() {
			return before.toString()+" -> "+after.toString();
		}
	}

	public MouseEventHandler(List<Event> eventQueue) {
		super(eventQueue);
		curDragChange = MouseChange.getZero();
	}	

	@Override
	public void mouseDragged(MouseEvent e) {
		var event = new Event(Event.EventType.MOUSE_MOVE, ()-> curDragChange.getUpdated(e.getPoint(),Modifier.DRAG));
		curDragChange = curDragChange.getUpdated(e.getPoint(),Modifier.DRAG);
		addEvent(event);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		var event = new Event(Event.EventType.MOUSE_MOVE, ()->curDragChange.getUpdated(e.getPoint(),Modifier.NONE));
		curDragChange = curDragChange.getUpdated(e.getPoint(),Modifier.NONE);
		addEvent(event);
		
	}
	
	

}
