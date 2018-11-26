package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.ai.AI;
import uet.oop.bomberman.entities.character.enemy.ai.AILow;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.level.FileLevelLoader;

import java.awt.*;
import java.util.ArrayList;

import static uet.oop.bomberman.graphics.Sprite.bomb;


public abstract class Enemy extends Character {

	protected int _points;

	protected double _speed;
	protected AI _ai;

	protected final double MAX_STEPS;
	protected final double rest;
	protected double _steps;
	
	protected int _finalAnimation = 30;
	protected Sprite _deadSprite;

	private static int _numberOfEnemy = 0;

	private ArrayList<Bomb> _onBomb = new ArrayList<Bomb>();

	private double[] gapX1 = {0, 16, 0, -0.5};
	private double[] gapY1 = {-16.5, -1, 0, -1};
	private double[] gapX2 = {2.5, 13.5, 13, 2};
	private double[] gapY2 = {-13.5, -14, -3, -2.5};
	private double[] gapX3 = {0.5, 15.5, 15, 0};
	private double[] gapY3 = {-15.5, -16, -1, -0.5};

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

	@Override
	public void calculateMove() {
		// DONETODO: Tính toán hướng đi và di chuyển Enemy theo _ai và cập nhật giá trị cho _direction
		// DONETODO: sử dụng canMove() để kiểm tra xem có thể di chuyển tới điểm đã tính toán hay không
		// DONETODO: sử dụng move() để di chuyển
		// DONETODO: nhớ cập nhật lại giá trị cờ _moving khi thay đổi trạng thái di chuyển
		int dx = 0, dy = 0;
		AILow _ai = new AILow();
		int xi = (int) _x;
		int yi = (int) _y;
		if (xi == _x && yi == _y && xi % 16 == 0 && yi % 16 == 0) {
			_direction = _ai.calculateDirection();
		}

		switch (_direction){
			case 0: dy--; break;
			case 1: dx++; break;
			case 2: dy++; break;
			case 3: dx--; break;
		}

		if (dx != 0 || dy != 0) {
			_moving = true;
			move(dx * _speed, dy * _speed);
		}
		else _moving = false;


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
	public boolean canMove(double x, double y) {
		// DONETODO: kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không
		int xc = (int) (x + gapX1[_direction]);
		int yc = (int) (y + gapY1[_direction]);
		Bomb bomb = _board.getBombAt(Coordinates.pixelToTile(xc), Coordinates.pixelToTile(yc));
		if (bomb != null && _onBomb.indexOf(bomb) == -1) return false;
		return FileLevelLoader.emptyCell(xc, yc, _board);
	}

	@Override
	public boolean collide(Entity e) {
		// DONETODO: xử lý va chạm với Flame
		// DONETODO: xử lý va chạm với Bomber
		if (e instanceof  Flame) {
			this.kill();
			return true;
		}
		return false;
	}
	
	@Override
	public void kill() {
		if(!_alive) return;
		_alive = false;

		_numberOfEnemy--;

		_board.addPoints(_points);

		Message msg = new Message("+" + _points, getXMessage(), getYMessage(), 2, Color.white, 14);
		_board.addMessage(msg);
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

}