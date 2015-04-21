//
//var serverAddress = "localhost";
//var serverPort = 50001;


var JavaPackages = new JavaImporter(
		Packages.net.java.games.input.Component.Identifier,
		Packages.sage.input.IInputManager,
		Packages.input);
with (JavaPackages) {

	function initInput(game, inputManager, player) {

//		var serverAddress = "localhost";
//		var serverPort = 50001;

		var key = net.java.games.input.Component.Identifier.Key;
		var axis = net.java.games.input.Component.Identifier.Axis;
		var keypress = IInputManager.INPUT_ACTION_TYPE;

		var keyboard = inputManager.getKeyboardName();
		var gamepad = inputManager.getFirstGamepadName();
		var forceQuit = new ForceQuit(game);
		var moveX = new MoveX(player);
		var moveZ = new MoveZ(player);
		var moveXAxis = new MoveXAxis(player);
		var moveZAxis = new MoveZAxis(player);
		
		var inputMap = {
			"keyboard": [
				{ "Key": key.A, "Action": moveX, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.D, "Action": moveX, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.W, "Action": moveZ, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.S, "Action": moveZ, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": key.ESCAPE, "Action": forceQuit, "Keypress": keypress.ON_PRESS_AND_RELEASE }
			],
			"gamepad": [
				{ "Key": axis.X, "Action": moveXAxis, "Keypress": keypress.REPEAT_WHILE_DOWN },
				{ "Key": axis.Y, "Action": moveZAxis, "Keypress": keypress.REPEAT_WHILE_DOWN }
			]
		};
		
		for(var input in inputMap){
			var controller = (input === "keyboard") ? keyboard : gamepad;
			if(controller){
				for(var i = 0; i < inputMap[input].length; i++){
					var map = inputMap[input][i];
					inputManager.associateAction(controller, map.Key, map.Action, map.Keypress);
				}
			}
		}
	}

}