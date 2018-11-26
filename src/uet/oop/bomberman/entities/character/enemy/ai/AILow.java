package uet.oop.bomberman.entities.character.enemy.ai;

import java.util.Random;

public class AILow extends AI {
	Random random = new Random();
	@Override
	public int calculateDirection() {
		// TODO: cài đặt thuật toán tìm đường đi

		return random.nextInt(4);
	}

}
