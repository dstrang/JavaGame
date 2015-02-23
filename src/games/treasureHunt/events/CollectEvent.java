package games.treasureHunt.events;

import sage.event.*;

public class CollectEvent extends AbstractGameEvent{

	int treasureCount;
	
	public CollectEvent(int treasureCount){
		this.treasureCount = treasureCount;
	}
	
	public int getTreasureCount(){
		return treasureCount;
	}
	
}
