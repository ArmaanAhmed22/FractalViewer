package mandelbrot.program;

import java.awt.Component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

import mandelbrot.events.handlers.EventHandler;
import mandelbrot.events.handlers.EventHandlerManager;

public abstract class Program {
	
	private final static RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
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
		((Graphics2D)g).setRenderingHints(RENDERING_HINTS);
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
		
		var FPSAndUPSTimer = System.currentTimeMillis();
		
		while (isRunning) {
			var delta = System.currentTimeMillis() - curTime;
			deltaTimeRender+=delta;
			deltaTimeUpdate+=delta;
			
			while (deltaTimeUpdate > upsInv1000) {
				onUpdate();
				deltaTimeUpdate-=upsInv1000;
			}
			
			if (deltaTimeRender > fpsInv1000) {
				onRenderFull();
				curFPS+=1;
				deltaTimeRender = 0;
			}
			curTime = System.currentTimeMillis();
			if (curTime-FPSAndUPSTimer >= 1000) {
				realFPS = curFPS;
				curFPS=0;
				FPSAndUPSTimer=curTime;
			}
			time+=1;
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
	
	public final int getFPS() {
		return realFPS;
	}

}
