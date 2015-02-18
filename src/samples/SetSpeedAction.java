package samples;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

public class SetSpeedAction extends AbstractInputAction
{
	private boolean running = false;
	public boolean isRunning() { return running; }
	
	public void performAction(float time, Event event)
	{ 
		System.out.println("changed"); running = !running;
	}

}
