package mandelbrot.program;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

import mandelbrot.events.handlers.EventHandler;
import mandelbrot.events.handlers.EventHandlerManager;

public abstract class Program {
	
	private final int fps;
	private final int ups;
	private long time;
	private boolean isRunning;
	
	private final JFrame frame;
	private Dimension size;
	private ScreenImage screen;
	
	protected EventHandlerManager ehm;
	
	private int realFPS = 0;
	
	public Program(Dimension size, int fps) {
		this.fps = fps;
		this.ups = fps;
		
		ehm = new EventHandlerManager();
		
		frame = new JFrame();
		this.size = size;
		setupScreen(size);
	}
	
	public Program(Dimension size, int fps,int ups) {
		this.fps = fps;
		this.ups = ups;
		
		ehm = new EventHandlerManager();
		
		frame = new JFrame();
		this.size = size;
		setupScreen(size);
	}
	
	private void setupScreen(Dimension size) {
		frame.setSize(size);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		screen = new ScreenImage(size.width, size.height);
		
	}
	
	public void start() {
		if (isRunning)
			return;
		isRunning = true;
		loopEngine();
	}
	
	public void stop() {
		if (!isRunning)
			return;
		isRunning = false;
	}
	
	public abstract void onUpdate();
	public abstract void onRender(Graphics g);
	
	private final void onRenderFull() {
		var bs = frame.getBufferStrategy();
		if (bs == null) {
			frame.createBufferStrategy(2);
			bs = frame.getBufferStrategy();
		}
		var g = bs.getDrawGraphics();
		
		getScreenImage().fill(0, 0, 0);
		
		onRender(g);
		bs.show();
	}
	
	private final void loopEngine() {
		final var upsInv1000 = (1f/ups)*1000;
		final var fpsInv1000 = (1f/fps)*1000;
		var curTime = System.currentTimeMillis();
		var deltaTimeRender = 0l;
		var deltaTimeUpdate = 0l;
		
		var curFPS = 0;
		long fpsTime = 0;
		
		while (isRunning) {
			var delta = System.currentTimeMillis() - curTime;
			deltaTimeRender+=delta;
			deltaTimeUpdate+=delta;
			fpsTime+=delta;
			
			while (deltaTimeUpdate > upsInv1000) {
				onUpdate();
				time+=1;
				deltaTimeUpdate-=upsInv1000;
			}
			
			if (deltaTimeRender > fpsInv1000) {
				onRenderFull();
				curFPS+=1;
				deltaTimeRender = 0;
			}
			
			if (curFPS >= fps) {
				realFPS = (int) (curFPS / (fpsTime / 1000d));
				curFPS=0;
			}
			
			try {
				Thread.sleep((long) (upsInv1000 - deltaTimeRender));
			} catch (InterruptedException e) {
			}
			
		}
		
	}
	
	public final <T extends EventHandler & EventListener> void addEventHandler(Class<T> ehClass) {
		ehm.addEventHandler(ehClass, frame);
	}
	
	public final ScreenImage getScreenImage() {
		return screen;
	}
	
	public final Dimension getSize() {
		return size;
	}
	
	public final void adjustSize() {
		screen = new ScreenImage(frame.getWidth(), frame.getHeight());
		size.setSize(frame.getWidth(), frame.getHeight());
	}
	
	public final int getArea() {
		return size.width*size.height;
	}
	
	public final long getTime() {
		return time;
	}

}
