package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.character.enemy.ai.AIEasy;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

public class Kondoria extends Enemy {
	private AIEasy _ai;

	public Kondoria(int x, int y, Board board) {
		super(x, y, board, Sprite.kondoria_dead, 0.5, 100);
		_sprite = Sprite.kondoria_left1;
		
		_ai = new AIEasy();
		_direction = _ai.calculateDirection();
	}

	@Override
	public void calculateMove() {
		_ai = new AIEasy();
		int xi = (int) _x;
		int yi = (int) _y;
		if (xi == _x && yi == _y && xi % 16 == 0 && yi % 16 == 0) {
			_direction = _ai.calculateDirection();
		}

		int dx = 0, dy = 0;
		switch (_direction){
			case 0: dy--; break;
			case 1: dx++; break;
			case 2: dy++; break;
			case 3: dx--; break;
		}

		if (dx != 0 || dy != 0)
			move(dx * _speed, dy * _speed);


	}

	@Override
	public boolean canMove(double x, double y) {
		int xc = (int) (x + gapX1[_direction]);
		int yc = (int) (y + gapY1[_direction]);
		Bomb bomb = _board.getBombAt(Coordinates.pixelToTile(xc), Coordinates.pixelToTile(yc));
		if (bomb != null && _onBomb.indexOf(bomb) == -1) return false;
		Entity entity = _board.getEntityAt(xc, yc);
		return !(entity instanceof Wall);
	}

	@Override
	protected void chooseSprite() {
		switch(_direction) {
			case 0:
			case 1:
				_sprite = Sprite.movingSprite(Sprite.kondoria_right1, Sprite.kondoria_right2, Sprite.kondoria_right3, _animate, 60);
				break;
			case 2:
			case 3:
				_sprite = Sprite.movingSprite(Sprite.kondoria_left1, Sprite.kondoria_left2, Sprite.kondoria_left3, _animate, 60);
				break;
		}
	}
}
