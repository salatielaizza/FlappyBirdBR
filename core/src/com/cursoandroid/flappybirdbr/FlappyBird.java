package com.cursoandroid.flappybirdbr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture passaro;

	@Override
	public void create () {

		batch = new SpriteBatch();
		passaro = new Texture("passaro1.png");

	}

	@Override
	public void render () {

		batch.begin();

		batch.draw(passaro, 300, 550);

		batch.end();

	}
	
}
