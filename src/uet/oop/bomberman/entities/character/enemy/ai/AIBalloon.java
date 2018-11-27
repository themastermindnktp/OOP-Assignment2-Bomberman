package uet.oop.bomberman.entities.character.enemy.ai;

import java.util.Random;

public class AIBalloon{
	Random _random = new Random();

	public int calculateDirection() {
		return _random.nextInt(4);
	}

}
