package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.AnimatedEntitiy;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.*;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.entities.tile.item.SpeedItem;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

import java.io.File;
import java.util.Scanner;

public class FileLevelLoader extends LevelLoader {

	/**
	 * Ma trận chứa thông tin bản đồ, mỗi phần tử lưu giá trị kí tự đọc được
	 * từ ma trận bản đồ trong tệp cấu hình
	 */
	private static char[][] _map;

	public FileLevelLoader(Board board, int level) throws LoadLevelException {
		super(board, level);
	}

	public static char getMap(int x, int y) {
		return _map[x][y];
	}

	@Override
	public void loadLevel(int level) {
		try {
			File file = new File("res/levels/Level" + new Integer(level).toString() + ".txt");
			Scanner scanner = new Scanner(file);
			_level = scanner.nextInt();
			_height = scanner.nextInt();
			_width = scanner.nextInt();
			scanner.nextLine();
			String[] row = new String[_height];
			for (int i = 0; i < _height; i++) {
				row[i] = scanner.nextLine();
			}
			scanner.close();
			_map = new char[_height][_width];
			for (int i = 0; i < _height; i++) {
				for (int j = 0; j < _width; j++) {
					_map[i][j] = row[i].charAt(j);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Boolean emptyCell(double x, double y, Board board) {
		Entity entity = board.getEntityAt(x, y);

		if (entity instanceof LayeredEntity)
			entity = ((LayeredEntity) entity).getTopEntity();

		return (entity instanceof Grass || (entity instanceof Item) || (entity instanceof Portal && Enemy.getNumberOfEnemy() == 0));
	}

	@Override
	public void createEntities() {

		int pos;
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				pos = x + y * _width;
				switch (_map[y][x]) {
					case '#':
						_board.addEntity(pos,
								new Wall(x, y, Sprite.wall)
						);
						break;
					case '*':
						_board.addEntity(pos,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 'x':
						_board.addEntity(pos,
								new LayeredEntity(x, y,
										new Portal(x, y, Sprite.portal),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 'p':
						_board.addCharacter(new Bomber(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						Screen.setOffset(0, 0);
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
					case '1':
						_board.addCharacter(new Balloon(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
					case '2':
						_board.addCharacter(new Oneal(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
					case '3':
						_board.addCharacter(new Kondoria(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
					case '4':
						_board.addCharacter(new Minvo(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, 0.5, _board));
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
					case '5':
						_board.addCharacter(new Minvo(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, 1.0, _board));
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
					case 'b':
						_board.addEntity(x + y * _width,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new BombItem(x, y, Sprite.powerup_bombs),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 'f':
						_board.addEntity(x + y * _width,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new FlameItem(x, y, Sprite.powerup_flames),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case 's':
						_board.addEntity(x + y * _width,
								new LayeredEntity(x, y,
										new Grass(x, y, Sprite.grass),
										new SpeedItem(x, y, Sprite.powerup_speed),
										new Brick(x, y, Sprite.brick)
								)
						);
						break;
					case ' ':
						_board.addEntity(pos,
								new Grass(x, y, Sprite.grass)
						);
						break;
				}
			}
		}
	}

}
