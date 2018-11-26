package uet.oop.bomberman.entities.character.enemy.ai;

import java.util.Random;

public class AILow extends AI {
	private Random random = new Random();

	@Override
	public int calculateDirection() {
		return random.nextInt(4);
	}

}
