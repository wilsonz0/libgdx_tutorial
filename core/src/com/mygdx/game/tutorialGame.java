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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class tutorialGame extends Game {
	private OrthographicCamera camera;			// ??? 
	private Array<Rectangle> raindrops;
	private Array<Rectangle> logs;
	private Rectangle bucket;
	
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	
	Texture bucketImg;
	Texture dropletImg;
	Texture logImg;
	Sound dropSound;
	Music rainMusic;
	BitmapFont font;
	
	float SPEED = 240;
	float x, y;
	private long lastDropTime;
	private long lastLogTime;
	boolean activated = false;
	private long lastPowerTime;
	
	public int score = 0;
	
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		font = new BitmapFont();
		
		// the images		
		bucketImg = new Texture("bucket.png");
		dropletImg = new Texture("droplet.png");
		logImg = new Texture("log2.png");
		
		// initializing the images		
		bucket = new Rectangle();
	    bucket.x = 800 / 2 - 64 / 2;
	    bucket.y = 20;
	    bucket.width = 64;
	    bucket.height = 64;
	    
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		
		logs = new Array<Rectangle>();
		spawnLog();
		
		// the sound and music
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
	    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
	    
	    // start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.6f, 1);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		/*
		 * the movement of the game. Users can press:
		 * LEFT arrow key: to move left
		 * RIGHT arrow key: to move right
		 * D key: to accelerate
		 */
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= SPEED * deltaTime;
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += SPEED * deltaTime;
		
		if (Gdx.input.isKeyPressed(Keys.D) && !activated) powerActivated(); 
		if(activated && TimeUtils.nanoTime() - lastPowerTime > 2_100_000_000) powerDeactivated();
		
		
		// every interval spawn new raindrop or log		
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		if(TimeUtils.nanoTime() - lastLogTime > 2000000000) spawnLog();
		
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
		
		for (Iterator<Rectangle> iter = logs.iterator(); iter.hasNext(); ) {
		      Rectangle log = iter.next();
		      log.y -= 200 * Gdx.graphics.getDeltaTime();
		      if(log.y + 64 < 0) iter.remove();
		      
		      if(log.overlaps(bucket)) {
		    	  score--;
		          dropSound.play();
		          iter.remove();
		       }
		}
		
		// bound the bucket within the frame
		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 725) bucket.x = 725;
		
		
		// Batch Drawing		
		batch.begin();
		
		batch.draw(bucketImg, bucket.x, bucket.y);
		
		for(Rectangle raindrop: raindrops) {
		      batch.draw(dropletImg, raindrop.x, raindrop.y);
		}
		
		for(Rectangle log: logs) {
		      batch.draw(logImg, log.x, log.y);
		}
		
		font.draw(batch, Integer.toString(score), 10, 20);
		
		batch.end();
		
		// TESTING START
//		shapeRenderer.begin(ShapeType.Line);
//		shapeRenderer.setColor(1, 0, 0, 1); // Red line
//		for(Rectangle raindrop: raindrops) {
//			shapeRenderer.rect(raindrop.x, raindrop.y, raindrop.width, raindrop.height);
//		}
//		for(Rectangle log: logs) {
//			shapeRenderer.rect(log.x, log.y, log.width, log.height);
//		}
//		
//		shapeRenderer.end();
//		// TESTING END
	}
	
	@Override
    public void dispose() {
       dropletImg.dispose();
       bucketImg.dispose();
       logImg.dispose();
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
   
   private void spawnLog() {
	   Rectangle log = new Rectangle();
	   log.x = MathUtils.random(0, 800-64);
	   log.y = 480;
	   log.width = 180;
	   log.height = 80;
	   logs.add(log);
	   lastLogTime = TimeUtils.nanoTime();
   }
   
   private void powerActivated() {
	   SPEED *= 3;
	   lastPowerTime = TimeUtils.nanoTime();
	   activated = true;
   }
   
   private void powerDeactivated() {
	   SPEED = 240;
	   activated = false;
   }
}
