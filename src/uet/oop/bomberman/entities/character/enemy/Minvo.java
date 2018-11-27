package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.character.enemy.ai.AIHard;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.level.FileLevelLoader;

public class Minvo extends Enemy {
	private AIHard _ai;

	public Minvo(int x, int y, double speed, Board board) {
		super(x, y, board, Sprite.minvo_dead, speed, 200);
		
		_sprite = Sprite.minvo_left1;
		
		_ai = new AIHard(this, _board);
		_direction  = 2;
	}

	@Override
	public void calculateMove() {
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
		return FileLevelLoader.emptyCell(xc, yc, _board);
	}

	@Override
	protected void chooseSprite() {
		switch(_direction) {
			case 0:
			case 1:
				_sprite = Sprite.movingSprite(Sprite.minvo_right1, Sprite.minvo_right2, Sprite.minvo_right3, _animate, 60);
				break;
			case 2:
			case 3:
				_sprite = Sprite.movingSprite(Sprite.minvo_left1, Sprite.minvo_left2, Sprite.minvo_left3, _animate, 60);
				break;
		}
	}
}
