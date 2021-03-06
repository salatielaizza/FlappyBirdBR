package com.cursoandroid.flappybirdbr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canaBaixo;
	private Texture canoAlto;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	//private ShapeRenderer shape;


	//Atributos de COnfiguração
	private float largutaDispositivo;
	private float alturaDispositivo;
	private int espacoEntreCanos;
	private int estadoJogo = 0;  //estado 0: jogo nãp iniciado, estado 1: jogo iniciado, estado 2: Tela Game Over
	private int pontuacao = 0;

	private int variacaoInt = 0;
	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto = false;

	//Camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create() {

		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
		/*
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoTopo= new Rectangle();
		shape = new ShapeRenderer();
		*/
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(10);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(4);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		canaBaixo = new Texture("cano_baixo_maior.png");
		canoAlto = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		fundo = new Texture("fundo.png");

		/**
		 * Configuração dacamera
		 */
		camera = new OrthographicCamera();
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);


		largutaDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = largutaDispositivo - 100;
		espacoEntreCanos = 300;

	}

	@Override
	public void render() {

		camera.update();

		//limpar frames anteriores - Para o jogo utilizar menos memória
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		deltaTime = Gdx.graphics.getDeltaTime() * 10;
		variacao += deltaTime * 10;
		//if para fazer aparecer a imagem passaro3.png, que antes não aparecia
		if (variacao >= 0 && variacao < 0.66) {
			variacaoInt = 0;
		} else if (variacao >= 0.66 && variacao < 1.33) {
			variacaoInt = 1;
		} else if (variacao >= 1.33 && variacao < 2) {
			variacaoInt = 2;
		} else if (variacao >= 2) {
			variacaoInt = 0;
		}

		if (estadoJogo == 0) { //Jogo não iniciado

			if (Gdx.input.justTouched()) {
				estadoJogo = 1;
			}


		} else {   //Jogo Iniciado

			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
				posicaoInicialVertical -= velocidadeQueda;

			if (estadoJogo == 1) { //jogo iniciado

				posicaoMovimentoCanoHorizontal -= deltaTime * 70;

				//método Gdx.input.isTouched() utilizado para realizar algo após tocar na tela. velocidadeQueda adquire o valor de -15
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -15;
				}


				//Verificação se o cano saiu da tela e atribuição de nova altura
				if (posicaoMovimentoCanoHorizontal < -canaBaixo.getWidth()) {
					posicaoMovimentoCanoHorizontal = largutaDispositivo + canaBaixo.getWidth();
					alturaEntreCanosRandomica = numeroRandomico.nextInt(300) - 150;
					marcouPonto = false;
				}

				//Verifica pontuação
				if (posicaoMovimentoCanoHorizontal < 120) {

					if (!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}
			} else { //tela de game over

				if ( Gdx.input.justTouched()){
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoMovimentoCanoHorizontal = largutaDispositivo;
					posicaoInicialVertical = alturaDispositivo/2;
				}
			}
		}

		//Configurar DADOS de projeção dacamera

		//o combined recupera os dados de projeção
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(fundo, 0, 0, largutaDispositivo, alturaDispositivo);
		batch.draw(canaBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canaBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(canoAlto, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(passaros[(int) variacaoInt], 120, posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), largutaDispositivo / 2, alturaDispositivo - 50);

		if( estadoJogo == 2){
			batch.draw(gameOver, largutaDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo - gameOver.getHeight()*4);
			mensagem.draw(batch, " Toque para reiniciar ", largutaDispositivo/2 - 275, alturaDispositivo/2 - gameOver.getHeight()/2);
		}

		batch.end();

		//criar o passaroCirculo e os retangulos
		passaroCirculo.set(120 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getWidth() / 2, passaros[0].getWidth() / 2);
		retanguloCanoBaixo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canaBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica, canaBaixo.getWidth(), canaBaixo.getHeight());
		retanguloCanoTopo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoAlto.getWidth(), canoAlto.getHeight());

		/*
		//TESTAR desenho das formas
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
		shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height );
		shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height );
		shape.setColor(Color.RED);
		shape.end();
		*/

		//Teste de colisão
<<<<<<< dde24b230bd0eb3854b4ffbb2ae7922b8ba9e4c7
		if (Intersector.overlaps( passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps( passaroCirculo, retanguloCanoTopo)) {
			//Gdx.app.log("Colisão", "Houve Colisão");
			estadoJogo = 2;

		};




=======
		//Gdx.app.log("Colisão", "Houve Colisão");
		if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo ) || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ) {
			estadoJogo = 2; //GameOver
		}
	}
>>>>>>> Meu primeiro Jogo FlappyBirdBR_V1.0

	@Override
	public void resize(int width, int height) {

		viewport.update(width, height);

	}
}