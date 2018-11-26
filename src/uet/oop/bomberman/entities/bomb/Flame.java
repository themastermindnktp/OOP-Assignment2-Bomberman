package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.level.Coordinates;

public class Flame extends Entity {

	protected Board _board;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected FlameSegment[] _flameSegments = new FlameSegment[0];

	int[] gapX = {0, 1, 0, -1};
	int[] gapY = {-1, 0, 1, 0};

	/**
	 *
	 * @param x hoành độ bắt đầu của Flame
	 * @param y tung độ bắt đầu của Flame
	 * @param direction là hướng của Flame
	 * @param radius độ dài cực đại của Flame
	 */
	public Flame(int x, int y, int direction, int radius, Board board) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		_board = board;
		createFlameSegments();
	}

	/**
	 * Tạo các FlameSegment, mỗi segment ứng một đơn vị độ dài
	 */
	private void createFlameSegments() {
		/**
		 * tính toán độ dài Flame, tương ứng với số lượng segment
		 */
		int radius = calculatePermitedDistance();
		_flameSegments = new FlameSegment[radius];
		/**
		 * biến last dùng để đánh dấu cho segment cuối cùng
		 */
		// DONETODO: tạo các segment dưới đây
		for (int i = 1; i <= radius; i++) {
			Entity entity = _board.getEntityAt(Coordinates.tileToPixel(_x + i*gapX[_direction]), Coordinates.tileToPixel(_y + i*gapY[_direction]));
			//System.out.println((int) _x + i * gapX[_direction] + " " + ((int) _y + i * gapY[_direction]) + " " + entity);
			if (entity instanceof LayeredEntity) {
				LayeredEntity layeredEntity = (LayeredEntity) entity;
				entity = layeredEntity.getTopEntity();
				if (entity instanceof Brick || entity instanceof Item)
				{
					entity.remove();
					layeredEntity.update();
				}
			}
			_flameSegments[i - 1] = new FlameSegment((int) _x + i * gapX[_direction], (int) _y + i * gapY[_direction], _direction, (i == radius));
		}
	}

	/**
	 * Tính toán độ dài của Flame, nếu gặp vật cản là Brick/Wall, độ dài sẽ bị cắt ngắn
	 * @return
	 */
	private int calculatePermitedDistance() {
		int radius = Game.getBombRadius();
		for (int i = 1; i <= radius; i++) {

			Entity entity = _board.getEntityAt((_x + i*gapX[_direction])*Game.TILES_SIZE, (_y + i*gapY[_direction])*Game.TILES_SIZE );
			if (entity instanceof Portal) return (i - 1);
			if (entity instanceof LayeredEntity && ((LayeredEntity) entity).getTopEntity() instanceof Brick) return i;
			if (entity instanceof Wall) return (i - 1);
		}
		return radius;
	}
	
	public FlameSegment flameSegmentAt(int x, int y) {
		for (int i = 0; i < _flameSegments.length; i++) {
			if(_flameSegments[i].getX() == x && _flameSegments[i].getY() == y)
				return _flameSegments[i];
		}
		return null;
	}

	@Override
	public void update() {}
	
	@Override
	public void render(Screen screen) {
		for (int i = 0; i < _flameSegments.length; i++) {
			_flameSegments[i].render(screen);
		}
	}

}
