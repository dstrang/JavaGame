var JavaPackages = new JavaImporter(
		Packages.net.java.games.input.Component.Identifier,
		Packages.sage.input.IInputManager, 
		Packages.gameEngine.input.action);
with (JavaPackages) {

	function initInput(game, inputManager, player) {

		var key = net.java.games.input.Component.Identifier.Key;
		var keypress = IInputManager.INPUT_ACTION_TYPE;

		var keyboard = inputManager.getKeyboardName();
		var forceQuit = new ForceQuit(game);
		var moveX = new MoveX(player);
		var moveZ = new MoveZ(player);

		var inputMap = {
			"keyboard": [
				{ "Key": key.A, "Action": moveX, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.D, "Action": moveX, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.W, "Action": moveZ, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.S, "Action": moveZ, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.ESCAPE, "Action": forceQuit, "Keypress": keypress.ON_PRESS_AND_RELEASE }
			]
		};
		
		for(var input in inputMap){
			var controller = (input === "keyboard") ? keyboard : null;
			for(var i = 0; i < inputMap[input].length; i++){
				var map = inputMap[input][i];
				inputManager.associateAction(controller, map.Key, map.Action, map.Keypress);
			}
		}
	}

}