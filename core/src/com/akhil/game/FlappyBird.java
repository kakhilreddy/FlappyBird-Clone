package com.akhil.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;




public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture back;
	Texture birds[];
	Texture bottom;
	int flappyState=0;
	int score=0;
	float birdY=0;
	float velocity=0;
	int gameState=0;
	float gravity=2;
	float gap=475;
	Texture gameOver;
	BitmapFont font;
	Texture top;
	int touch=0;
	float maxTubeOffset;
Circle birdCircle;
	int noOfTubes=4;
	Sound jump,sc;
	float distanceBetweenTubes;
	float tubeVelocity=4;
	float []tubeX=new float[noOfTubes];
	float []tubeOffset=new float[noOfTubes];
	Rectangle[]topTubeRectangles;
	Rectangle[]bottomTubeRectangles;
	int scoreTube=0;
	Texture scorecard;
	boolean play=false;
Music over;
	int highScore=0;
ShapeRenderer shapeRenderer;
	Random random;
	Music music;
	private boolean flag=false;

	@Override
	public void create () {
		shapeRenderer=new ShapeRenderer();
		birdCircle=new Circle();
		font=new BitmapFont();
		scorecard=new Texture("scorecard1.png");
	music=Gdx.audio.newMusic(Gdx.files.internal("sounds/music.wav"));
		sc=Gdx.audio.newSound(Gdx.files.internal("sounds/score.m4a"));
		jump=Gdx.audio.newSound(Gdx.files.internal("sounds/jump.m4a"));
		over=Gdx.audio.newMusic(Gdx.files.internal("sounds/gameOver.m4a"));
		font.setColor(Color.WHITE);
		gameOver=new Texture("gameOver.png");
		font.getData().setScale(10);
		topTubeRectangles=new Rectangle[noOfTubes];
		bottomTubeRectangles=new Rectangle[noOfTubes];
		batch = new SpriteBatch();
		bottom=new Texture("bottomtube.png");
		back = new Texture("bg.png");
		top=new Texture("toptube.png");
		birds=new Texture[2];
		distanceBetweenTubes=Gdx.graphics.getWidth()/2;
		birds[0]=new Texture("bird3.png");
		birds[1]=new Texture("bird3.png");

		maxTubeOffset=Gdx.graphics.getHeight()/2-gap/2-100;
		random=new Random();
		distanceBetweenTubes=Gdx.graphics.getWidth()*3/4;

startGame();

	}
	private  void startGame()
	{
		birdY=Gdx.graphics.getHeight()/2-birds[flappyState].getHeight()/2;
		for(int i=0;i<noOfTubes;i++)
		{
			tubeOffset[i]=(random.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);

			tubeX[i]=Gdx.graphics.getWidth()/2-top.getWidth()/2+Gdx.graphics.getWidth()+ i*distanceBetweenTubes;
			topTubeRectangles[i]=new Rectangle();
			bottomTubeRectangles[i]=new Rectangle();

		}
		music.play();
		music.setVolume(0.5f);
		music.setLooping(true);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(back,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


		if(gameState==1)
		{
			if(tubeX[scoreTube]<Gdx.graphics.getWidth()/2)
			{
				sc.play();
				score++;
				Gdx.app.log("score",String.valueOf(score));
				if(scoreTube<noOfTubes-1)
					scoreTube++;
				else
					scoreTube=0;
			}


			if(Gdx.input.justTouched())
		{velocity=-30;
jump.play();
		}
			for(int i=0;i<noOfTubes;i++) {
				if(tubeX[i]<-top.getWidth())
				{
					tubeX[i]+=noOfTubes*distanceBetweenTubes;
					tubeOffset[i]=(random.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
				}else{
				tubeX[i] -= tubeVelocity;


				}
				batch.draw(top, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 +tubeOffset[i]);
				batch.draw(bottom, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottom.getHeight() + tubeOffset[i]);
                     topTubeRectangles[i]=new Rectangle( tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 +tubeOffset[i],top.getWidth(),top.getHeight());
				bottomTubeRectangles[i]=new Rectangle( tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottom.getHeight() + tubeOffset[i],bottom.getWidth(),bottom.getHeight());

			}if(birdY>0 )
			{velocity+=gravity;
		birdY-=velocity;}else
		{
			gameState=2;
			if(!play)
			over.play();

		}


		}else if(gameState==0) {


			if(Gdx.input.justTouched())
			{
				gameState=1;
				Gdx.app.log("touched","touch");
			}

		}else if(gameState==2)
		{


			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			batch.draw(scorecard,Gdx.graphics.getWidth()/2-scorecard.getWidth()/2,Gdx.graphics.getHeight()/2+gameOver.getHeight()/2+100);
           font.getData().setScale(2);
			font.draw(batch,String.valueOf(score),Gdx.graphics.getWidth()/2-60,Gdx.graphics.getHeight()/2+270);
			Preferences preferences=Gdx.app.getPreferences("highscore");
			highScore=preferences.getInteger("highscore",0);

			if(score>highScore)
			{
				preferences.putInteger("highscore",score);
				highScore=score;
				preferences.flush();
			}
			font.draw(batch,String.valueOf(highScore),Gdx.graphics.getWidth()/2-60,Gdx.graphics.getHeight()/2+200);
//Gdx.app.log("birdy",String.valueOf(birdY));
			if(birdY>0)
			{gravity=5;
			velocity+=gravity;
			birdY-=velocity;}
			else
			{flag=true;

			}



music.stop();
play=true;



			if(Gdx.input.justTouched() && flag)
						{ touch++;
							if(touch>1){gameState=1;
				startGame();
			score=0;
play=false;
	gravity=2;							flag=false;
			velocity=0;
			scoreTube=0;
					touch=0;		}
		}}


		  if (flappyState == 0) {
			  flappyState = 1;
		  } else
			  flappyState = 0;

		  batch.draw(birds[flappyState], Gdx.graphics.getWidth() / 2 - birds[flappyState].getWidth() / 2, birdY);
			font.getData().setScale(5);
		  font.draw(batch, String.valueOf(score), 100, 200);

		  batch.end();
		  birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flappyState].getHeight() / 2, birds[flappyState].getWidth() / 2);

		  for (int i = 0; i < noOfTubes; i++) {


			  if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

				  Gdx.app.log("collision", "yes");
				  gameState = 2;
				  if(!play)
				  over.play();
			  }
		  }
	  }



	
	@Override
	public void dispose () {
		batch.dispose();
		back.dispose();
		music.dispose();

	}
}
