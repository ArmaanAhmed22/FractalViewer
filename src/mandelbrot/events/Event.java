package mandelbrot.events;

public class Event {
	
	private final EventType eventType;
	private final EventInformation associatedData;
	
	public enum EventType {
		MOUSE_MOVE,KEY_PRESS,SCREEN_RESIZE;
	}
	
	public Event(EventType eventType,EventInformation associatedData) {
		this.eventType = eventType;
		this.associatedData = associatedData;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	public EventInformation getAssociatedData() {
		return associatedData;
	}

}
