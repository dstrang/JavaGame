package objects;

import events.CollectEvent;

import java.awt.Color;
import java.awt.Point;
import java.text.DecimalFormat;

import sage.app.AbstractGame;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.renderer.IRenderer;
import sage.scene.HUDObject;
import sage.scene.HUDString;

public class ScoreHUD extends HUDObject implements IEventListener{

	private String timeLabel = "Time: ";
	private String scoreLabel = "Score: ";
	private HUDString timeString;
	private HUDString scoreString;
	private float time = 0;
	private int score = 0;
	private AbstractGame game;
	private double locX;
	private double locY;
	
	public ScoreHUD(double x, double y){
		locX = x;
		locY = y;
		timeString = new HUDString(timeLabel + time);
		timeString.setLocation(locX, locY);
		timeString.setColor(Color.WHITE);
		timeString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		timeString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		scoreString = new HUDString(scoreLabel + score);
		scoreString.setLocation(locX, locY + 0.05);
		scoreString.setColor(Color.WHITE);
		scoreString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		scoreString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
	}

	public void updateTime(float elapsedTime){
		time += elapsedTime;
		DecimalFormat decimal = new DecimalFormat("0.0");
		timeString.setText(timeLabel + decimal.format(time/1000));
	}

	public void updateScore(int score){
		this.score = score;
		scoreString.setText(scoreLabel + score);
	}

	@Override
	public void draw(IRenderer r) {
		timeString.draw(r);
		scoreString.draw(r);
	}

	@Override
	public boolean handleEvent(IGameEvent event) {
		CollectEvent collect = (CollectEvent) event;
		this.updateScore(collect.getTreasureCount());
		return true;
	}
}
