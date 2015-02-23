package games.treasureHunt.objects;

import games.treasureHunt.events.CollectEvent;

import java.awt.Color;
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
	
	public ScoreHUD(){
		timeString = new HUDString(timeLabel + time);
		scoreString = new HUDString(scoreLabel + score);
		timeString.setLocation(0.02, 0.95);
		scoreString.setLocation(0.02, 0.92);
		timeString.setColor(Color.WHITE);
		scoreString.setColor(Color.WHITE);
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
