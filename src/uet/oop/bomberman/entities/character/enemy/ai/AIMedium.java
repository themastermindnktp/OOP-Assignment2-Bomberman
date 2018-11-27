package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.level.FileLevelLoader;

import java.util.LinkedList;
import java.util.Random;

public class AIMedium {
	private Random _random = new Random();
	private final int _detectingRadius = 5;
	private Enemy _enemy;
	private Board _board;
	private double _speed;
	private LinkedList<Integer> _queueX;
	private LinkedList<Integer> _queueY;
	private int[][] trace = new int[20][40];
	private int[][] distance = new int[20][40];

	private int[] gapX = {-1, 0, 1, 0};
	private int[] gapY = {0, 1, 0, -1};


	public AIMedium(Enemy enemy, Board board)
	{
		_enemy = enemy;
		_board = board;
		_queueX = new LinkedList<Integer>();
		_queueY = new LinkedList<Integer>();
	}

	public int calculateDirection() {
		int sx = Coordinates.pixelToTile(_enemy.getY() - 1);
		int sy = Coordinates.pixelToTile(_enemy.getX());
		Bomber bomber = _board.getBomber();
		for(int i = 0; i < 20; ++i)
			for(int j = 0; j < 40; ++j) {
				trace[i][j] = -1;
				distance[i][j] = 0;
			}
		_queueX.clear();
		_queueY.clear();
		trace[sx][sy] = -2;
		_queueX.add(sx);
		_queueY.add(sy);
		while (!_queueX.isEmpty())
		{
			int x = _queueX.pollFirst();
			int y = _queueY.pollFirst();
			if (distance[x][y] > _detectingRadius) continue;
			for(int direction = 0; direction < 4; ++direction)
			{
				int u = x + gapX[direction];
				int v = y + gapY[direction];
				if (trace[u][v] != -1) continue;
				trace[u][v] = (direction + 2) & 3;
				Bomb bomb = _board.getBombAt(v, u);
				if (bomb != null && _enemy.getOnBomb().indexOf(bomb) == -1) continue;
				int xc = Coordinates.tileToPixel(v);
				int yc = Coordinates.tileToPixel(u);
				if (!FileLevelLoader.emptyCell(xc, yc, _board)) continue;
				if (xc - 10 < bomber.getX() && bomber.getX() < xc + Game.TILES_SIZE - 2 && yc + 2 <= bomber.getY() && bomber.getY() <= yc + Game.TILES_SIZE + 14)
				{
					while (u + gapX[trace[u][v]] != sx || v + gapY[trace[u][v]] != sy)
					{
						int d = trace[u][v];
						u += gapX[d];
						v += gapY[d];
					}
					_speed = 1;
					return (trace[u][v] + 2) & 3;
				}
				distance[u][v] = distance[x][y] + 1;
				_queueX.add(u);
				_queueY.add(v);
			}
		}
		if (_random.nextInt(5) == 0) _speed = 1;
		else _speed = 0.5;
		return _random.nextInt(4);
	}

	public double calculateSpeed() {
		return _speed;
	}

}
