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
	Texture back; ///background image
	Texture birds[]; // bird images
	Texture bottom; //Bottom tube
	int flappyState=0; // state of bird
	int score=0; // initial score
	float birdY=0; // y coordinate of bird
	float velocity=0; // velocity of bird
	int gameState=0; //initial game state is 0 means game didnt start
	float gravity=2; 
	float gap=475; //gap between top and bottom tube
	Texture gameOver; //game over image
	BitmapFont font;
	Texture top; //Top tube 
	int touch=0; //used when game is over
	float maxTubeOffset; 
Circle birdCircle;  
	int noOfTubes=4;// no of tubes
	Sound jump,sc; //sound effects
	float distanceBetweenTubes; // distance between consecutive tubes
	float tubeVelocity=4;  //speed of tubes
	float []tubeX=new float[noOfTubes]; // since Y coordinate remains same so we take array of X coordinates
	float []tubeOffset=new float[noOfTubes]; // max height of tube
	Rectangle[]topTubeRectangles;
	Rectangle[]bottomTubeRectangles;
	int scoreTube=0; // keeping track of score being earned from respective tube
	Texture scorecard; // To be displayed after game is over
	boolean play=false; //related to playing music when game is over
Music over; // game over music
	int highScore=0; //highScore scored till now , obtained by using Preferences class

	Random random; // random no generator so that tubes are of different sizes
	Music music; // background music
	private boolean flag=false; // related to game over state

	@Override
	public void create () {
	///initializing fields
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
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");

		maxTubeOffset=Gdx.graphics.getHeight()/2-gap/2-100;
		random=new Random();
		distanceBetweenTubes=Gdx.graphics.getWidth()*3/4;

startGame();

	}
	private  void startGame()
	{
		birdY=Gdx.graphics.getHeight()/2-birds[flappyState].getHeight()/2;
		for(int i=0;i<noOfTubes;i++)
		{ // initialising tubes offset and X coordinate of both bottom and top tubes
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
		//drawing background image
		batch.draw(back,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

//gameState =1 means game is in progress
//gameState= 2 means game over
//gameState =3 means game didnt start ,waiting for user to touch the screen  
		if(gameState==1)
		{                  
			if(tubeX[scoreTube]<Gdx.graphics.getWidth()/2) //if user successfully passes through the tube
			{
				sc.play();
				score++;
				Gdx.app.log("score",String.valueOf(score));
				if(scoreTube<noOfTubes-1)
					scoreTube++;
				else
					scoreTube=0;
			}


			if(Gdx.input.justTouched()) // if user touches screen 
		{velocity=-30; // setting velocity so that it will look  like bird is jumping
jump.play();
		}
			for(int i=0;i<noOfTubes;i++) {
			
				if(tubeX[i]<-top.getWidth()) // if x coordinate is less than negative of tube width means the tube passed the left most end of the phone screen
				{ // so reset the tube x coordinate so that it shifts back to rightmost end of the phone screen
					tubeX[i]+=noOfTubes*distanceBetweenTubes;
					tubeOffset[i]=(random.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
				}else{ // or else decrement x coordinate ,it looks like tube is moving from right to left
				tubeX[i] -= tubeVelocity;


				}
				//draw the tubes
				batch.draw(top, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 +tubeOffset[i]);
				batch.draw(bottom, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottom.getHeight() + tubeOffset[i]);
                     topTubeRectangles[i]=new Rectangle( tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 +tubeOffset[i],top.getWidth(),top.getHeight());
				bottomTubeRectangles[i]=new Rectangle( tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottom.getHeight() + tubeOffset[i],bottom.getWidth(),bottom.getHeight());
//Rectanges objects are used so that we can detect collisions
			}if(birdY>0 ) // if y coordinate of bird is greater than zero ,then decrement the y coordinate
			{velocity+=gravity;
		birdY-=velocity;}else
		{ // else bird is at bottom of screen so game is over ,set the gameState to 2
			gameState=2;
			if(!play)
			over.play();

		}


		}else if(gameState==0) {
//gameState is 0,waiting for users touch to start the game

			if(Gdx.input.justTouched())
			{ //start the game
				gameState=1;
				Gdx.app.log("touched","touch");
			}

		}else if(gameState==2)
		{
//game is over so displaying gameOver image and scorecard

			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			batch.draw(scorecard,Gdx.graphics.getWidth()/2-scorecard.getWidth()/2,Gdx.graphics.getHeight()/2+gameOver.getHeight()/2+100);
           font.getData().setScale(2);
			font.draw(batch,String.valueOf(score),Gdx.graphics.getWidth()/2-60,Gdx.graphics.getHeight()/2+270);
			Preferences preferences=Gdx.app.getPreferences("highscore"); //getting the value of highscore
			highScore=preferences.getInteger("highscore",0);

			if(score>highScore)// comparing current score with highscore
			{
				preferences.putInteger("highscore",score);
				highScore=score; // setting highscore as score
				preferences.flush();
			}
			font.draw(batch,String.valueOf(highScore),Gdx.graphics.getWidth()/2-60,Gdx.graphics.getHeight()/2+200);
//Gdx.app.log("birdy",String.valueOf(birdY));
//bird falls down when game is over
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
							//user touched the screen again , so restart the game 
				startGame();
			score=0;
play=false;
	gravity=2;							flag=false;
			velocity=0;
			scoreTube=0;
					touch=0;		}
		}}

//to display animation of flipping wings of bird ,flappyState is used to set bird image
		  if (flappyState == 0) {
			  flappyState = 1;
		  } else
			  flappyState = 0;

		  batch.draw(birds[flappyState], Gdx.graphics.getWidth() / 2 - birds[flappyState].getWidth() / 2, birdY);
			font.getData().setScale(5);
		  font.draw(batch, String.valueOf(score), 100, 200);

		  batch.end();
		  //Circle shape size of bird is created
		  birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flappyState].getHeight() / 2, birds[flappyState].getWidth() / 2);

		  for (int i = 0; i < noOfTubes; i++) {

//If tube(rectangle) and bird(circle) interesects then game is over
			  if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

				  Gdx.app.log("collision", "yes");
				  gameState = 2;
				  if(!play)
				  over.play();
			  }
		  }
	  }



	//clearing up memory
	@Override
	public void dispose () {
		batch.dispose();
		back.dispose();
		music.dispose();

	}
}
