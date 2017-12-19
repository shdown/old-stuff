package ru.sanboll.rapidball;
/**
 * √лавный класс
 */
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import ru.sanboll.rapidball.ui.*;
import ru.sanboll.rapidball.game.GameScreen;
import ru.sanboll.gamelib.*;

public class Main extends MIDlet {
	
	/** »гровой экран */
	GameScreen game;
	
	/** √лавное меню */
	GameMenu menu;
	
	/** Ёкран паузы */
	GameMenu pause;
	
	/** Ёкран рекордов */
	MessageScreen highScore;
	
	/** Ёкран при окончании игры */
	MessageScreen gameOver;
	
	/** «вук */
	GameSound player = new GameSound();
	
	/** ¬лючен ли звук */
	boolean soundEnabled = false;

	public Main() {
		loadGameObjects();
		show(menu);
	}
	
	private void show(Displayable d) {
		Display.getDisplay(this).setCurrent(d);
	}

	private void createMainMenu() {
		String soundStr = Locale.getString(4) + ' ' +
			(soundEnabled ? Locale.getString(5) : Locale.getString(6));
		menu = new GameMenu(new String[] {
			Locale.getString(1), Locale.getString(2),
			soundStr, Locale.getString(7)
		}, this);
	}

	public void loadGameObjects() {
		createMainMenu();
		pause = new GameMenu(new String[] {
			Locale.getString(8),
			Locale.getString(9)
			}, this
		);
		GameScreen.loadStatic();
		game = new GameScreen(this);
	}

	public void menuItemSelected(GameMenu m) {
		if(m == menu) {
			switch(menu.getSelectedIndex()) {
				case 0:
					game.replayGame();
					show(game);
					if(soundEnabled) {
						player.closeSound();
						player.loadSound("/mus/music.mid");
						player.setCyclic(true);
						player.playSound();
					}	
				break;
				case 1:
					highScore = new MessageScreen(new String[] {
						Locale.getString(3) + ' ' + GameRMS.read("highscore")
					}, 2, this);
					show(highScore);
				break;
				case 2:
					soundEnabled = !soundEnabled;
					createMainMenu();
					show(menu);
				break;
				case 3:
					notifyDestroyed();
				break;
			}
		} else if(m == pause) {
			if(pause.getSelectedIndex() == 0) {
				show(game);
				game.startGame();
				if(soundEnabled) player.playSound();
			} else {
				show(menu);
			}
		}
	}
	
	public void startApp() {
	}
	
	public void pauseApp() {
		game.stopGame();
		pause();
	}
	
	public void destroyApp(boolean unc) {
		notifyDestroyed();
	}

	public void pause() {
		show(pause);
		pause.setSelectedIndex(0);
		if(soundEnabled) player.stopSound();
	}

	public void messageSkipped(MessageScreen msgScreen) {
		highScore = gameOver = null;
		show(menu);
	}

	public void gameover(int nPoints) {
		int lastScore = GameRMS.read("highscore");
		boolean newHighscore = lastScore < nPoints;
		if(newHighscore) GameRMS.save("highscore", nPoints);
		gameOver = new MessageScreen(new String[] {
			Locale.getString(10), 
			Locale.getString(11) + ' ' + nPoints,
			newHighscore ? Locale.getString(12) :
				Locale.getString(3) + ' ' + lastScore
		}, 2, this);
		show(gameOver);

		if(soundEnabled) {
			player.closeSound();
			player.loadSound("/mus/gameover.mid");
			player.playSound();
		}
	}
}