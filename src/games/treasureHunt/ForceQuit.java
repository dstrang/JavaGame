package games.treasureHunt;

import net.java.games.input.Event;
import sage.app.BaseGame;
import sage.input.action.AbstractInputAction;


public class ForceQuit extends AbstractInputAction{
	
	BaseGame game;
	
	public ForceQuit(BaseGame game){
		this.game = game;
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		
	}
}
