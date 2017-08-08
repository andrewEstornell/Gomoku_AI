package tree;

// Wrapper class that pairs moves with their evaluated value
public class MoveWrapper {
	private int[] move;
	private int value;
	
	public MoveWrapper(int[] move, int value)
	{
		this.move = move;
		this.value = value;
	}
	
	public MoveWrapper(int[] move)
	{
		this.move = move;
	}
	
	public int[] getMove() { return move; }
	public int getValue() { return value; }
	public void setMove(int[] move) { this.move = move; }
	public void setValue(int value) { this.value = value; }
	@Override
	public String toString() {return "[" + this.move[0] + "," + this.move[1] + "] " + ": " + this.value;}
}
