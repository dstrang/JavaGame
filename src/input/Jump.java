package input;

import net.java.games.input.Event;
import objects.Avatar;
import sage.input.action.AbstractInputAction;


public class Jump extends AbstractInputAction {
	private Avatar	avatar;
	

	public Jump(Avatar avatar) {
		this.avatar = avatar	;
	}

	@Override
	public void performAction(	float time, Event e) {
		avatar.setJumping(!avatar.isJumping());
	}

}
