public class Cell {
	
	/** 
	 * Constructors
	 */
	
	public Cell(int cellT) {
		_cellType = cellT;
	}
	
	public Cell(Cell c) {
		this._cellType = c.getCellType();
	}
	
	/** 
	 * Getters and Setters
	 */
	
	public int getCellType() {
		return _cellType;
	}
	public void setCellType(int cellType) {
		this._cellType = cellType;
	}
	
	/** 
	 * Class Variables
	 */
	private int		 		_cellType;
	
}
