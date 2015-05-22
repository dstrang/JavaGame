var JavaPackages = new JavaImporter(
		Packages.sage.scene.SceneNode,
		Packages.sage.scene.SkyBox,
		Packages.sage.scene.SkyBox.Face,
		Packages.sage.texture.Texture,
		Packages.sage.texture.TextureManager,
		Packages.java.io.File
);
with (JavaPackages) {
	
	// networking
	var serverAddress = "localhost";
	var serverPort = "50001";
	
	var imagesDirectory = "." + File.separator + "src" + File.separator + "images" + File.separator;
	var size = 20;

	// initialize skybox
	var skybox = new SkyBox("Skybox", size, size, size);

	// load textures
	var northTexture = TextureManager.loadTexture2D(imagesDirectory + "ocean_front.png");
	var southTexture = TextureManager.loadTexture2D(imagesDirectory + "ocean_back.png");
	var eastTexture = TextureManager.loadTexture2D(imagesDirectory + "ocean_right.png");
	var westTexture = TextureManager.loadTexture2D(imagesDirectory + "ocean_left.png");
	var upTexture = TextureManager.loadTexture2D(imagesDirectory + "ocean_up.png");
	var downTexture = TextureManager.loadTexture2D(imagesDirectory + "ocean_down.png");

	// attach textures to skybox
	skybox.setTexture(SkyBox.Face.North, northTexture);
	skybox.setTexture(SkyBox.Face.South, southTexture);
	skybox.setTexture(SkyBox.Face.East, eastTexture);
	skybox.setTexture(SkyBox.Face.West, westTexture);
	skybox.setTexture(SkyBox.Face.Up, upTexture);
	skybox.setTexture(SkyBox.Face.Down, downTexture);
	
}