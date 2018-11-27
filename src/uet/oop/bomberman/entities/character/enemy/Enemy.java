package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.audio.Sound;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

import java.awt.*;
import java.util.ArrayList;

public abstract class Enemy extends Character {

	protected int _points;

	protected double _speed;

	protected final double MAX_STEPS;
	protected final double rest;
	protected double _steps;
	
	protected int _finalAnimation = 30;
	protected Sprite _deadSprite;

	private static int _numberOfEnemy = 0;

	protected ArrayList<Bomb> _onBomb = new ArrayList<Bomb>();

	protected double[] gapX1 = {0, 16, 0, -0.5};
	protected double[] gapY1 = {-16.5, -1, 0, -1};
	protected double[] gapX2 = {2.5, 13.5, 13, 2};
	protected double[] gapY2 = {-13.5, -14, -3, -2.5};
	protected double[] gapX3 = {0.5, 15.5, 15, 0};
	protected double[] gapY3 = {-15.5, -16, -1, -0.5};

	public Enemy(int x, int y, Board board, Sprite dead, double speed, int points) {
		super(x, y, board);

		_numberOfEnemy++;

		_points = points;
		_speed = speed;
		
		MAX_STEPS = Game.TILES_SIZE / _speed;
		rest = (MAX_STEPS - (int) MAX_STEPS) / MAX_STEPS;
		_steps = MAX_STEPS;
		
		_timeAfter = 20;
		_deadSprite = dead;
	}

	public static void setNumberOfEnemy(int _numberOfEnemy) {
		Enemy._numberOfEnemy = _numberOfEnemy;
	}

	public static int getNumberOfEnemy() {
		return _numberOfEnemy;
	}

	@Override
	public void update() {
		animate();
		
		if(!_alive) {
			afterKill();
			return;
		}
		
		if(_alive)
		{
			calculateMove();
			checkCollide();
			checkOutOfBomb();
			_board.getBomber().checkCollide();
		}
	}

	public void addBomb(Bomb bomb)
	{
		_onBomb.add(bomb);
	}

	protected void checkOutOfBomb()
	{
		for(int i =_onBomb.size() - 1; i >= 0; --i) {
			Bomb bomb = _onBomb.get(i);
			boolean out = true;
			for (int corner = 0; corner < 4; ++corner) {
				int cornerX = (int) (_x + gapX3[corner]);
				int cornerY = (int) (_y + gapY3[corner]);
				if (_board.getBombAt(Coordinates.pixelToTile(cornerX), Coordinates.pixelToTile(cornerY)) == bomb)
				{
					out = false;
					break;
				}
			}
			if (out) _onBomb.remove(bomb);
		}
	}
	
	@Override
	public void render(Screen screen) {
		
		if(_alive)
			chooseSprite();
		else {
			if(_timeAfter > 0) {
				_sprite = _deadSprite;
				_animate = 0;
			} else {
				_sprite = Sprite.movingSprite(Sprite.mob_dead1, Sprite.mob_dead2, Sprite.mob_dead3, _animate, 60);
			}
				
		}
			
		screen.renderEntity((int)_x, (int)_y - _sprite.SIZE, this);
	}

	void checkCollide()
	{
		for(int corner = 0; corner < 4; ++corner)
		{
			int cellX = Coordinates.pixelToTile(_x + gapX2[corner]);
			int cellY = Coordinates.pixelToTile(_y + gapY2[corner]);
			Entity entity = _board.getEntity(cellX, cellY, this);
			if (entity instanceof FlameSegment)
			{
				kill();
				return;
			}
		}
	}

	@Override
	public void move(double xa, double ya) {
		if(!_alive) return;
		if (canMove(_x, _y)) {
			_y += ya;
			_x += xa;
		}
	}
	
	@Override
	public void kill() {
		if(!_alive) return;
		_alive = false;

		_numberOfEnemy--;

		_board.addPoints(_points);

		Message msg = new Message("+" + _points, getXMessage(), getYMessage(), 2, Color.white, 14);
		_board.addMessage(msg);
		Sound.makeSound("EnemyDies");
	}
	
	
	@Override
	protected void afterKill() {
		if(_timeAfter > 0) --_timeAfter;
		else {
			if(_finalAnimation > 0) --_finalAnimation;
			else
				remove();
		}
	}
	
	protected abstract void chooseSprite();

	public ArrayList<Bomb> getOnBomb() {
		return _onBomb;
	}
}
