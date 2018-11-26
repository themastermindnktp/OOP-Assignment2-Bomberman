package uet.oop.bomberman.entities.character;

import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_PARAMS;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.audio.Sound;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.enemy.Balloon;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.entities.tile.item.SpeedItem;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.level.FileLevelLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    private ArrayList<Bomb> _onBomb = new ArrayList<Bomb>();

    private int[] gapX1 = {9, 10, 9, 1};
    private int[] gapY1 = {-15, -1, 0, -1};
    private int[] gapX2 = {2, 10, 2, 1};
    private int[] gapY2 = {-15, -14, 0, -14};
    private int[] gapX3 = {2, 9, 9, 2};
    private int[] gapY3 = {-14, -14, -3, -3};
    private int[] gapX4 = {-8, -8, 8, 8};
    private int[] gapY4 = {-8, 8, -8, 8};

    /**
     * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo,
     * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
     */
    protected int _timeBetweenPutBombs = 0;


    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs > 0) _timeBetweenPutBombs--;

        animate();

        calculateMove();

        checkCollide();

        checkOutOfBomb();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    /**
     * Kiểm tra xem có đặt được bom hay không? nếu có thì đặt bom tại vị trí hiện tại của Bomber
     */
    private void detectPlaceBomb() {
        if (_input.space && _timeBetweenPutBombs == 0 && Game.getBombRate() > 0) {
            placeBomb(Coordinates.pixelToTile(_x + 5), Coordinates.pixelToTile(_y - 8));
            Game.addBombRate(-1);
            _timeBetweenPutBombs = 15;
        }
    }

    protected void placeBomb(int x, int y) {
        Bomb bomb = new Bomb(x, y, _board);
        _board.addBomb(bomb);
        _onBomb.add(bomb);
        int xc = Coordinates.tileToPixel(x);
        int yc = Coordinates.tileToPixel(y);
        Iterator<Character> itr = _board._characters.iterator();
        Character cur;
        while(itr.hasNext()) {
            cur = itr.next();
            if (cur instanceof  Enemy)
                if(xc - 15 <= cur.getX() && cur.getX() <= xc + 15 && yc + 1 <= cur.getY() && cur.getY() <= yc + Game.TILES_SIZE + 15)
                    ((Enemy) cur).addBomb(bomb);
        }
        Sound.makeSound("SetBomb");
    }

    protected void checkOutOfBomb()
    {
        for(int i =_onBomb.size() - 1; i >= 0; --i) {
            Bomb bomb = _onBomb.get(i);
            boolean out = true;
            for (int corner = 0; corner < 4; ++corner) {
                int cornerX = (int) _x + gapX3[corner];
                int cornerY = (int) _y + gapY3[corner];
                if (_board.getBombAt(Coordinates.pixelToTile(cornerX), Coordinates.pixelToTile(cornerY)) == bomb)
                {
                    out = false;
                    break;
                }
            }
            if (out) _onBomb.remove(bomb);
        }
    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }
    }


    @Override
    public void kill() {
        if (!_alive) return;
        Sound.makeSound("BomberDie");
        _alive = false;
        _board.add_live(-1);
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            if (_board.get_live() < 1) _board.endGame();
            else _board.restartLevel();
        }
    }

    @Override
    protected void calculateMove() {
        int dx = 0, dy = 0;
        if(_input.up) dy--;
        if(_input.down) dy++;
        if(_input.left) dx--;
        if(_input.right) dx++;
        if(dx != 0 || dy != 0)  {
            _moving = true;
            for(int i = 1, movement = (int) Game.getBomberSpeed(); i <= movement; ++i)
                move(dx, dy);
        } else _moving = false;
    }

    @Override
    public boolean canMove(double dx, double dy) {
        if (!_alive) return false;
        int direction = _direction;
        if (dy < 0) direction = 0;
        if (dx > 0) direction = 1;
        if (dy > 0) direction = 2;
        if (dx < 0) direction = 3;

        int x1 = (int) _x + gapX1[direction], y1 = (int) _y + gapY1[direction];
        int x2 = (int) _x + gapX2[direction], y2 = (int) _y + gapY2[direction];

        Bomb bomb;
        bomb = _board.getBombAt(Coordinates.pixelToTile(x1), Coordinates.pixelToTile(y1));
        if (bomb != null && _onBomb.indexOf(bomb) == -1) return false;
        bomb = _board.getBombAt(Coordinates.pixelToTile(x2), Coordinates.pixelToTile(y2));
        if (bomb != null && _onBomb.indexOf(bomb) == -1) return false;

        return (FileLevelLoader.emptyCell(_x + gapX1[direction], _y + gapY1[direction], _board) &&
                FileLevelLoader.emptyCell(_x + gapX2[direction], _y + gapY2[direction], _board));
    }

    public void checkCollide() {
        for (int corner = 0 ; corner < 4; ++corner)
        {
            double cornerX = _x + gapX3[corner];
            double cornerY = _y + gapY3[corner];
            for(int i = 0; i < 4; ++i) {
                int cellX = Coordinates.pixelToTile(cornerX + gapX4[i]);
                int cellY = Coordinates.pixelToTile(cornerY + gapY4[i]);

                Entity entity = _board.getEntity(cellX, cellY, this);
                if (entity instanceof Enemy) {
                    double cx = entity.getX();
                    double cy = entity.getY();
                    if (_x - Game.TILES_SIZE + 2 <= cx && cx < _x + 10 && _y - 14 <= cy && cy <= _y + Game.TILES_SIZE - 2) {
                        kill();
                        return;
                    }
                }
            }
            int cellX = Coordinates.pixelToTile(cornerX);
            int cellY = Coordinates.pixelToTile(cornerY);
            Entity entity = _board.getEntity(cellX, cellY, this);
            if (entity instanceof FlameSegment) {
                kill();
                return;
            }
            if (entity instanceof LayeredEntity)
            {
                LayeredEntity layeredEntity = (LayeredEntity) entity;
                if (layeredEntity.getTopEntity() instanceof Item) {
                    Item item = (Item) layeredEntity.getTopEntity();
                    if (item instanceof BombItem) Game.addBombRate(1);
                    if (item instanceof FlameItem) Game.addBombRadius(1);
                    if (item instanceof SpeedItem && Game.getBomberSpeed() == 1) Game.addBomberSpeed(1);
                    Sound.makeSound("CollectItem");
                    item.remove();
                    layeredEntity.update();
                }
                if (layeredEntity.getTopEntity() instanceof Portal)
                {
                    Sound.makeSound("EnterPortal");
                    _board.nextLevel();
                }
            }
        }
    }

    @Override
    public void move(double xa, double ya) {
        if (xa > 0) _direction = 1;
        if (xa < 0) _direction = 3;
        if (ya < 0) _direction = 0;
        if (ya > 0) _direction = 2;

        if (xa != 0 && canMove(xa, 0)) {
            _x += xa;

        }
        if (ya != 0 && canMove(0, ya))
        {
            _y += ya;

        }

    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }
}
