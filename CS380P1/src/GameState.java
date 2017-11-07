import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GameState {
	
	public GameState() {
		this._cells = new ArrayList<ArrayList<Cell>>();
	}
	
	private ArrayList<ArrayList<Cell>> _cells;
	
	int getNumRows() {
		return _cells.size();
	}
	int getNumCols() {
		try {
			int cols = _cells.get(0).size();
			return cols;
		} catch(Exception e) {
			return 0;
		}
		
	}

	public ArrayList<ArrayList<Cell>> getCells() {
		return _cells;
	}

	public void setCells(ArrayList<ArrayList<Cell>> _cells) {
		this._cells = _cells;
	}

	List<Cell> getRowAt(int r) {
		return _cells.get(r);
	}
	
	Cell getCellAt(int r, int c) {
		return _cells.get(r).get(c);
	}
	
	public void display(PrintStream ps) {
		for(int i = 0 ; i < _cells.size(); i++) {
			ArrayList<Cell> row = _cells.get(i);
			for(int j = 0; j < row.size(); j++) {
				ps.print(this._cells.get(i).get(j).getCellType() + ",");
			}
			ps.print("\n");
		}
	}
	
	public boolean areWallsInTact(GameState original) {
		for(int i = 0; i < getNumRows(); i++) {
			for(int j = 0; j < getNumCols(); j++) {
				if(original.getCellAt(i, j).getCellType() == 1) {
					if(this.getCellAt(i, j).getCellType() != 1) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
