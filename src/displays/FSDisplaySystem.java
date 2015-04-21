package displays;

import java.awt.Canvas;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.renderer.RendererFactory;

public class FSDisplaySystem implements IDisplaySystem{
	
	private JFrame frame;
	private GraphicsDevice device;
	private IRenderer renderer;
	private int width, height, depth, rate;
	private Canvas canvas;
	private boolean isCreated;
	private boolean isFullScreen;
	

	public FSDisplaySystem(int width, int height, int depth, int rate, boolean isFS, String rName){
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.rate = rate;
		this.isFullScreen = isFS;
		
		renderer = RendererFactory.createRenderer(rName);
		if(renderer == null){
			throw new RuntimeException("Unable to find renderer");
		}
		
		canvas = renderer.getCanvas();
		frame = new JFrame("Treasure Hunter 2015");
		frame.add(canvas);
		
		DisplayMode displayMode = new DisplayMode(width, height, depth, rate);
		initScreen(displayMode, isFullScreen);
				
		DisplaySystem.setCurrentDisplaySystem(this);
		frame.setVisible(true);
		isCreated = true;
	}
	
	private void initScreen(DisplayMode displayMode, boolean FSRequested) {
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		
		if(device.isFullScreenSupported() && FSRequested){
			frame.setUndecorated(true);
			frame.setResizable(false);
			frame.setIgnoreRepaint(true);
			
			device.setFullScreenWindow(frame);
			
			if(displayMode != null && device.isDisplayChangeSupported()){
				try{
					device.setDisplayMode(displayMode);
					frame.setSize(displayMode.getWidth(), displayMode.getHeight());
				}catch (Exception ex){
					System.err.println("Exception setting DisplayMode: " + ex);
				}
			}else{
				System.err.println("Cannot set display mode");
			}
		}else{
			frame.setSize(displayMode.getWidth(), displayMode.getHeight());
			frame.setLocationRelativeTo(null);
		}
	}

	@Override
	public void addKeyListener(KeyListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseListener(MouseListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		if(device != null){
			Window window = device.getFullScreenWindow();
			if(window != null){
				window.dispose();
			}
			device.setFullScreenWindow(null);
		}
	}

	@Override
	public void convertPointToScreen(Point arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBitDepth() {
		return this.depth;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getRefreshRate() {
		return this.rate;
	}

	@Override
	public IRenderer getRenderer() {
		return this.renderer;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public boolean isCreated() {
		return this.isCreated;
	}

	@Override
	public boolean isFullScreen() {
		return this.isFullScreen;
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBitDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public void setCustomCursor(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void setPredefinedCursor(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRefreshRate(int rate) {
		this.rate = rate;
	}

	@Override
	public void setTitle(String title) {
		this.frame.setTitle(title);
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

}
