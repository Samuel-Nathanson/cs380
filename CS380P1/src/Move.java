public class Move {
	
	public enum Direction {UP, DOWN, LEFT, RIGHT, NONE};
	
	/**
	 * Constructors
	 */
	
	public Move(int piece, Direction direction) {
		_piece = piece;
		_direction = direction;
	}
	
	/**
	 * Class Methods
	 */
	
	static int[] getDirectionVector(Direction d) {
		
		int [] arr = new int[2];
		if(d==Direction.UP) {
			arr[0] = 0;
			arr[1] = -1;
		}
		if(d==Direction.DOWN) {
			arr[0] = 0;
			arr[1] = 1;
		}
		if(d==Direction.LEFT) {
			arr[0] = -1;
			arr[1] = 0;
		}
		if(d==Direction.RIGHT) {
			arr[0] = 1;
			arr[1] = 0;
		}
		return arr;
	}
	
	public String toString() {
		String dirString = null;
		switch(_direction) {
			case UP:
				dirString = "UP";
				break;
			case DOWN:
				dirString = "DOWN";
				break;
			case LEFT:
				dirString = "LEFT";
				break;
			case RIGHT:
				dirString = "RIGHT";
				break;
		}
		return "("+ _piece + ", " +dirString + ")";
	}
	
	/**
	 * Getters and Setters
	 */
	
	public int getPiece() {
		return _piece;
	}

	public void setPiece(int _piece) {
		this._piece = _piece;
	}

	public Direction getDirection() {
		return _direction;
	}

	public void setDirection(Direction _direction) {
		this._direction = _direction;
	}

	
	
	/**
	 * Class Variables
	 */
	
	int _piece; // Piece number to move 
	
	Direction _direction; // Direction to move the piece
}
