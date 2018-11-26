package uet.oop.bomberman.entities.character.enemy.ai;

public abstract class AI {
	
	/**
	 * Thuật toán tìm đường đi
	 * @return hướng đi xuống/phải/trái/lên tương ứng với các giá trị 0/1/2/3
	 */
	public abstract int calculateDirection();
}
