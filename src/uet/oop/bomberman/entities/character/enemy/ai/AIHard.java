package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.character.enemy.Oneal;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.level.FileLevelLoader;

import java.util.LinkedList;
import java.util.Random;

public class AIHard {
	private Random _random = new Random();
	private final int _detectingRadius = 5;
	private Enemy _enemy;
	private Board _board;
	private int _direction;
	private LinkedList<Integer> _queueX;
	private LinkedList<Integer> _queueY;
	private int[][] trace = new int[20][40];
	private int[][] distance = new int[20][40];

	private int[] gapX1 = {-1, 0, 1, 0};
	private int[] gapY1 = {0, 1, 0, -1};
	private double[] gapX2 = {0, 16, 0, -0.5};
	private double[] gapY2 = {-16.5, -1, 0, -1};
	private int[] permutation = {0, 1, 2, 3};


	public AIHard(Enemy enemy, Board board)
	{
		_enemy = enemy;
		_board = board;
		_queueX = new LinkedList<Integer>();
		_queueY = new LinkedList<Integer>();
	}

	public boolean isSafe(int x, int y)
	{
		int radius = Game.getBombRadius();
		for(int direction = 0; direction < 4; ++direction)
		{
			int u = x, v = y;
			for(int i = 1; i <= radius; ++i)
			{
				u += gapX1[direction];
				v += gapY1[direction];
				Bomb bomb = _board.getBombAt(v, u);
				if (bomb != null) return false;
				Entity entity = _board.getEntityAt(Coordinates.tileToPixel(v), Coordinates.tileToPixel(u));
				if (entity instanceof LayeredEntity)
					if (((LayeredEntity) entity).getTopEntity() instanceof Brick || ((LayeredEntity) entity).getTopEntity() instanceof Portal) break;
				if (entity instanceof Wall) break;
			}
		}
		return true;
	}

	public int safeMove()
	{
		for(int i = 0; i < 4; ++i)
		{
			int j = _random.nextInt(4);
			int temp = permutation[i];
			permutation[i] = permutation[j];
			permutation[j] = temp;
		}
		for(int i = 0; i < 4; ++i)
		{
			double xc = _enemy.getX() + gapX2[permutation[i]];
			double yc = _enemy.getY() + gapY2[permutation[i]];
			Bomb bomb = _board.getBombAt(Coordinates.pixelToTile(xc), Coordinates.pixelToTile(yc));
			if (bomb != null && _enemy.getOnBomb().indexOf(bomb) != -1) continue;
			if (FileLevelLoader.emptyCell(xc, yc, _board) && isSafe(Coordinates.pixelToTile(yc), Coordinates.pixelToTile(xc)))
			{
				System.out.println(permutation[i]);
				return permutation[i];
			}
		}
		System.out.println(5);
		return 5;
	}

	public int calculateDirection() {
		int sx = Coordinates.pixelToTile(_enemy.getY() - 1);
		int sy = Coordinates.pixelToTile(_enemy.getX());
		if (isSafe(sx, sy)) return safeMove();
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
				int u = x + gapX1[direction];
				int v = y + gapY1[direction];
				if (trace[u][v] != -1) continue;
				trace[u][v] = (direction + 2) & 3;
				Bomb bomb = _board.getBombAt(v, u);
				if (bomb != null && _enemy.getOnBomb().indexOf(bomb) == -1) continue;
				int xc = Coordinates.tileToPixel(v);
				int yc = Coordinates.tileToPixel(u);
				if (!FileLevelLoader.emptyCell(xc, yc, _board)) continue;
				if (isSafe(u, v))
				{
					while (u + gapX1[trace[u][v]] != sx || v + gapY1[trace[u][v]] != sy)
					{
						int d = trace[u][v];
						u += gapX1[d];
						v += gapY1[d];
					}
					return (trace[u][v] + 2) & 3;
				}
				distance[u][v] = distance[x][y] + 1;
				_queueX.add(u);
				_queueY.add(v);
			}
		}
		return _random.nextInt(4);
	}

}
