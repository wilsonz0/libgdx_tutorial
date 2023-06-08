package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class tutorialGame extends Game {
	private OrthographicCamera camera;			// ??? 
	private Array<Rectangle> raindrops;
	private Rectangle bucket;
	SpriteBatch batch;
	Texture bucketImg;
	Texture dropletImg;
	Sound dropSound;
	Music rainMusic;
	BitmapFont font;
	
	float SPEED = 240;
	float x, y;
	private long lastDropTime;
	boolean activated = false;
	private long lastPowerTime;
	
	public int score = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		font = new BitmapFont();
		
		// the images		
		bucketImg = new Texture("bucket.png");
		dropletImg = new Texture("droplet.png");
		
		// the sound and music
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
	    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
	    
	    // start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();
		
		
		bucket = new Rectangle();
	    bucket.x = 800 / 2 - 64 / 2;
	    bucket.y = 20;
	    bucket.width = 64;
	    bucket.height = 64;
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.6f, 1);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= SPEED * deltaTime;
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += SPEED * deltaTime;
		
		if (Gdx.input.isKeyPressed(Keys.D) && !activated) {System.out.println("cool"); powerActivated();} 
		if(activated && TimeUtils.nanoTime() - lastPowerTime > 2_100_000_000) {System.out.println("Deactived??"); powerDeactivated();}
		
		
		
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
		      Rectangle raindrop = iter.next();
		      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
		      if(raindrop.y + 64 < 0) iter.remove();
		      
		      if(raindrop.overlaps(bucket)) {
		    	  score++;
		          dropSound.play();
		          iter.remove();
		       }
		}
		
		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 725) bucket.x = 725;
		
		batch.begin();
		batch.draw(bucketImg, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
		      batch.draw(dropletImg, raindrop.x, raindrop.y);
		}
		font.draw(batch, Integer.toString(score), 20, 20);
		batch.end();
	}
	
	@Override
    public void dispose() {
       dropletImg.dispose();
       bucketImg.dispose();
       dropSound.dispose();
       rainMusic.dispose();
       batch.dispose();
    }
	
	
   private void spawnRaindrop() {
	      Rectangle raindrop = new Rectangle();
	      raindrop.x = MathUtils.random(0, 800-64);
	      raindrop.y = 480;
	      raindrop.width = 64;
	      raindrop.height = 64;
	      raindrops.add(raindrop);
	      lastDropTime = TimeUtils.nanoTime();
   }
   
   private void powerActivated() {
	   SPEED *= 2;
	   lastPowerTime = TimeUtils.nanoTime();
	   activated = true;
   }
   
   private void powerDeactivated() {
	   SPEED = 240;
	   activated = false;
   }
}
