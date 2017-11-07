import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Game {

	/**
	 * Constructors
	 */
	
	public Game() {
	}
	
	/**
	 * Class Methods
	 */
	
	/**
	 * A*
	 */
	
	public ArrayList<Move> A(GameState gs) {

		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		
		GameState g0 = cloneGameState(gs);
		Node start = new Node();
		start._state = g0;
		start._g = 0;
		start._h = h(start);
		
		open.add(start);
		
		while(!open.isEmpty()) {
			Node n = removeLowestF(open);
			
			if(nodesExplored % 500 == 0) {
				 System.out.println(nodesExplored + " Nodes Explored. Depth " + n._depth);
			 }
			
			if(isPuzzleComplete(n._state)) {
				return getPathToRoot(n);
			}

			nodesExplored += 1;
			closed.add(n);
			ArrayList<Node> children = getChildrenNotInList(n, closed);
			
			open.addAll(children);
			
			if(nodesExplored%10000 == 0) {
				System.out.println("Searched " + nodesExplored + " nodes");
			} 
		} 
		
		System.out.println("No solution found");
		return null;
	}
	
	/*
	 * Finds all children (possible succeeding states) of Node n that are not in list closed
	 */
	ArrayList<Node> getChildrenNotInList(Node n, ArrayList<Node> closed) {
		ArrayList<Move> moves = getListOfPossibleMoves(n._state);
		ArrayList<Move> movesForNewState = new ArrayList<Move>(); 
		ArrayList<Node> children = new ArrayList<Node>();

		for(int i = 0; i < moves.size(); i++) {
			GameState gs = applyMoveCloning(n._state, moves.get(i));
			
			boolean inClosedList = false;
			
			for(int j = 0; j < closed.size(); j++) {
				//normalizeState(closed.get(j)._state);
				if(isStateSame(closed.get(j)._state, gs)) {
					inClosedList = true;
					break;
				}
			}
			if(!inClosedList) {
				movesForNewState.add(moves.get(i));
			}
		}
		for(int i = 0; i < movesForNewState.size(); i++) {
			Node child = new Node();
			child._parent = n;
			child._depth = n._depth +1;
			child._move = movesForNewState.get(i);
			child._state = applyMoveCloning(n._state, movesForNewState.get(i));
			child._g = n._g + 1;
			// CHANGE
			child._h = h(child);
			children.add(child);
		}
		
		return children;
	}
	
	/*
	 * Choose moves that move 2 block until none left
	 * h(x) is low
	 * g(x) is 
	 */
	
	ArrayList<Move> getPathToRoot(Node n) {
		ArrayList<Move> path = new ArrayList<Move>();
		while (n._parent != null) {
			path.add(0,n._move);
			n = n._parent;
		}
		return path;
	}
	
	/*
	 * Remove the node with the lowest 'F' function - f(n) = h(n) + g(n);
	 */
	public Node removeLowestF(ArrayList<Node> nodes) {
		
		double lowestF = -1;
		int indexToRemove = 0;
		
		for(int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			double f = g(n) + h(n);
			if(f < lowestF || lowestF == -1) {
				lowestF = f;
				indexToRemove = i;
			}
		}
		
		return nodes.remove(indexToRemove);
	}
	
	public double g(Node n) {
		return n._g;
	}
	
	public double h2(Node n) {
		
		GameState gs = n._state;
		
		int nrows = gs.getNumRows();
		int ncols = gs.getNumCols();
		
		ArrayList<int[]> goalLocations = new ArrayList<int[]>();
		ArrayList<int[]> masterBrickLocations = new ArrayList<int[]>();
		
		for(int i = 0; i < nrows; i++) {
			for(int j = 0; j < ncols; j++) {
				if(gs.getCellAt(i, j).getCellType() == -1) {
					int[] loc = {i,j};
					goalLocations.add(loc);
				}
				if(gs.getCellAt(i, j).getCellType() == 2) {
					int[] loc = {i,j};
					masterBrickLocations.add(loc);
				}
			}
		}
		
		int shortestDistance = -1;
		int [] closestMaster = new int[2];
		int [] closestGoal = new int[2];
		for(int i = 0; i < goalLocations.size(); i++) {
			for(int j = 0; j < masterBrickLocations.size(); j++) {
				
				int x0 = goalLocations.get(i)[0];
				int y0 = goalLocations.get(i)[1];
				int x1 = masterBrickLocations.get(j)[0];
				int y1 = masterBrickLocations.get(j)[1];
				
				int distance = Math.abs(x0-x1)+Math.abs(y0-y1);
				if(distance < shortestDistance || shortestDistance == -1) {
					shortestDistance = distance;
					closestGoal[0] = x0;
					closestMaster[0] = x1;
					closestGoal[1] = y0;
					closestGoal[1] = x1;
				}
			}
		}
		
		// YOU REVERSED MASTER AND GOAL DON'T FORGET IF THIS BREAKS
		int x0 = closestMaster[0];
		int x1 = closestGoal[0];
		int y0 = closestMaster[1];
		int y1 = closestGoal[1];
		
		int clogged = 1;
		if(y0 > y1) {
			int swp = y1;
			y1 = y0;
			y0 = swp;
		}
		if(x0 > x1) {
			int swp = x1;
			x1 = x0;
			x0 = swp;
		}
		
		int tot = 1;
		
		for(int i = x0; i <= x1; i++) {
			for(int j = y0; j <= y1; j++) {
				if(n._state.getCellAt(i, j).getCellType() != 0) {
					clogged += 1;
				}
				tot += 1;
			}
		}
		
		double h = shortestDistance * (clogged / tot);
		
		return h;
		
		// How much do I expect this algorithm to help?
		// Assume board mxn
		// Assume outside is all walls, so (m-1)(n-1) is number of spaces
		// Assume f free (zero) cells
		// Clogged Cells c = (m-1)(n-1) - (f)
		// Avg Manhattan distance between goal and board = d = (m/2) + (n/2)
		// How much closer are we to the goal if we have a free space between us and the goal?
		
		// We are trying to measure efficiency - How can I measure efficiency of this heuristic
		// What makes a "better" heuristic?
		// Lower return value from heuristic = closer to goal
		// closer is hard to define in a puzzle
		
		// In THIS puzzle, if we are n nodes away horizontally and there is n free space horizontally, 
		// we can say we are a distance of n away from the goal
		// However, if we are n nodes away horizontally, and there is n-1 free space horizontally, we can say we are at MOST
		// n+1 moves away from the goal 
		// If we are n nodes away vertically, and there is n-1 free space vertically, we can say we are at MOST n+1 
		/// moves away from the goal
		// How about n-1 free space vertically and n-1 free space horizontally?
		// n+2 moves away
		
		// What about worst-case?
		// Let's say we have a list of clogged horizontal cells, each at (x,yi)
		// If a cell at xi can be unclogged by moving (x+1, yi) 
		// we must move the cell at (x+1, yi), the cell at (x, yi), and the original cell.
		// So each "clogged" cell in our path requires us to move n+1
		// 
		// This will get into probability - I'd like to talk to Santiago about this.
		
		
		
		/**\
		 * want our heuristic to return 
		 * distance to goal * amount of filled space between node and goal
		 * Less Free Space = Higher Distance
		 * Higher Distance = Higher Distance
		 * 
		 * How to calculate distance : abs(x-x0) + abs(y-y0) 
		 * 
		 * How to calculate amount of free space
		 * 
		 * (x0,y0) = coordinates of brick closest to goal
		 * (x1,y1) = coordinates of goal closest to brick
		 * 
		 * Vertical Space between brick and goal = [y0,y1]
		 * 	if(y0-y1) < 0, brick is above goal. Use interval y0, y1
		 *
		 * Horizontal Space between brick and goal = [x0,x1]
		 * 
		 * Loop through cells at that interval and count # nonzeros +1
		 * 
		 * 
		 */
		
		
		
		
	}
	
	public double h(Node n) {
		
		GameState gs = n._state;
		int nrows = gs.getNumRows();
		int ncols = gs.getNumCols();
		
		double shortestDistance = -1;
		
		ArrayList<int[]> goalLocations = new ArrayList<int[]>();
		ArrayList<int[]> masterBrickLocations = new ArrayList<int[]>();
		
		for(int i = 0; i < nrows; i++) {
			for(int j = 0; j < ncols; j++) {
				if(gs.getCellAt(i, j).getCellType() == -1) {
					int[] loc = {i,j};
					goalLocations.add(loc);
				}
				if(gs.getCellAt(i, j).getCellType() == 2) {
					int[] loc = {i,j};
					masterBrickLocations.add(loc);
				}
			}
		}
		
		for(int i = 0; i < goalLocations.size(); i++) {
			for(int j = 0; j < masterBrickLocations.size(); j++) {
				
				// Distance is sqrt((x1-x0)^2 + (y-y0)^2)
				int x1 = goalLocations.get(i)[0];
				int y1 = goalLocations.get(i)[1];
				int x0 = masterBrickLocations.get(j)[0];
				int y0 = masterBrickLocations.get(j)[1];
				
				double distance = Math.sqrt(Math.pow((x1-x0),2) + Math.pow((y1-y0),2));
				if(distance < shortestDistance || shortestDistance == -1) {
					shortestDistance = distance;
				}
			}
		}
		if(goalLocations.isEmpty()) {
			return 0;
		}
		return shortestDistance;
	}
	
	/** 
	 * @param initialState
	 * @return
	 */
	public ArrayList<Move> bfsSearch(GameState initialState) {
		Queue<Node> q = new LinkedList<Node>();
		ArrayList<GameState> visited = new ArrayList<GameState>();
		Node n = new Node();
		n._depth = 0;
		n._parent = null;
		n.visited = false;
		n._state = initialState;
		
		return bfs(n, q, visited);
	}
	
	public ArrayList<Move> dfsSearch(GameState initialState, int depth) {
		Stack<Node> q = new Stack<Node>();
		ArrayList<GameState> visited = new ArrayList<GameState>();
		Node n = new Node();
		n._depth = 0;
		n._parent = null;
		n.visited = false;
		n._state = initialState;
		
		return dfs(n, q, visited, depth);
	}
	
	public ArrayList<Move> dfs(Node n, Stack<Node> q, ArrayList<GameState> visited, int depth) {
		 
		q.add(n);
		 
		 while(!q.isEmpty()) {
			 Node element = q.pop();
			 nodesExplored++;
			 if(nodesExplored % 100 == 0) {
				 System.out.println(nodesExplored + " Nodes Explored");
			 }
			
			 // System.out.println("-----NODE STATE------");
			 //displayGameState(element._state);
			 
			 if(isPuzzleComplete(element._state)) {
				 return getPathToRoot(element);
			 }else {
			 
			 element._g += 1;

			 }
			 visited.add(element._state);
			 
			 /*

			 if(element._depth > depth) {
				 // System.out.println("Exceeded Depth"); // Change to return
			 }
			 			  * 
			  */
			 
			 ArrayList<Node> children = new ArrayList<Node>();
			 element._children = children;
			 List<Move> moves = getListOfPossibleMoves(element._state);
			 
			 /* Find Children */ 
			 for( int i = 0; i < moves.size(); i++) {
				 boolean contains = false;
				 Node child = new Node();
				 child._parent = element;
				 child._depth = element._depth+1;
				 child._state = applyMoveCloning(element._state, moves.get(i));
				 child._move = moves.get(i);
				 
				 // REMOVE
				 GameState orig = cloneGameState(child._state);
				 for(int j = 0; j < visited.size(); j++) {
					 
					 if(isStateSame(child._state, visited.get(j))) {
						 contains = true;
					 }
				 }
				 // REMOVE 
				 if(!isStateSame(orig, child._state)) {
					 System.out.println("NOOOOO : Nodes Explored : " + nodesExplored);
				 }
				 
				 if(!contains) {
					 element._children.add(child);
					 contains = false;
					 //System.out.println("CHILD " + i + " STATE");
					 //displayGameState(child._state);
				 }
			 }
			
			 for(int i = 0; i < element._children.size(); i++) {
				 try {
					Node n1 = element._children.get(i);
					if(n1!=null) {
						q.push(n1);
				 	}
				 } catch(Exception e) {
					 
				 }
			 }
		 }
		 return null;
	}
	
	public ArrayList<Move> getPathFromRootToNode(Node n) {
		ArrayList<Move> path = new ArrayList<Move>();
				
		while(n._parent != null) {
			path.add(n._move);
			n = n._parent;
		}
		
		Collections.reverse(path);
		return path;
	}
	// Awful memory efficiency. 
	public ArrayList<Move> bfs(Node n, Queue<Node> q, ArrayList<GameState> visited) { 
		 q.add(n);
		 
		 while(!q.isEmpty()) {
			 Node element = q.remove();
			 nodesExplored++;
			 
			 if(nodesExplored % 500 == 0) {
				 System.out.println(nodesExplored + " Nodes Explored. Depth " + element._depth);
			 }
			
			 //System.out.println("-----NODE STATE------");
			 //displayGameState(element._state);
			 
			 if(isPuzzleComplete(element._state)) {
				 //System.out.println("Solution Found at depth " + element._depth);
				 return getPathFromRootToNode(element);
				 // We have a solution. Follow the path back up the tree
			 }
			 visited.add(element._state);
			 ArrayList<Node> children = new ArrayList<Node>();
			 element._children = children;
			 List<Move> moves = getListOfPossibleMoves(element._state);
			 
			 /* Find Children */ 
			 for( int i = 0; i < moves.size(); i++) {
				 boolean contains = false;
				 Node child = new Node();
				 child._parent = element;
				 child._depth = n._depth+1; 
				 child._state = applyMoveCloning(element._state, moves.get(i));
				 child._move = moves.get(i);
				 
				 for(int j = 0; j < visited.size(); j++) {
					 if(isStateSame(child._state, visited.get(j))) {
						 contains = true;
					 }
				 }
				 
				 if(!contains) {
					 element._children.add(child);
					 contains = false;
					 //System.out.println("CHILD " + i + " STATE");
					 //displayGameState(child._state);
				 }
			 }
			 //System.out.println("---------------------");
			 for(int i = 0; i < element._children.size(); i++) {
				 try {
					Node n1 = element._children.get(i);
					if(n1!=null) {
						q.add(n1);
				 	}
				 } catch(Exception e) {
					 
				 }
			 }
		 }
		 return null;
		 
	}
	
	public void RandomWalk(GameState gameState, int n) {
		Random rand = new Random();
		int moveCount = 0;
		
		while(moveCount < n && !isPuzzleComplete(gameState)) {
			moveCount++;
			List<Move> possibleMoves = getListOfPossibleMoves(gameState);
		
			Move randomMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
			applyMove(gameState, randomMove);
			normalizeState(gameState);
			
			System.out.println("After making move : " + randomMove.toString());
			gameState.display(System.out);
		}
	}
	
	public boolean isPuzzleComplete(GameState gameState) {
		
		for(int r = 0; r < gameState.getNumRows(); r++) {
			List<Cell> row = gameState.getRowAt(r);
			for(int c = 0; c < row.size(); c++) {
				if(row.get(c).getCellType() == -1) {
					return false;
				}
			}
		}
		return true;
	}
	
	static public List<Move> getListOfPossibleMovesForPiece(GameState gameState, int piece) {
		
		// Make a list of indexes of the pieces
		List<Move> possibleMoves = new ArrayList<Move>();
		Move.Direction [] directions = {Move.Direction.UP, Move.Direction.DOWN, Move.Direction.RIGHT, Move.Direction.LEFT};
		
		if(piece == -1 || piece == 1 || piece == 0) {
			return possibleMoves;
		}
		
		// This triple-nested-for loop is needed to check each piece in the game board with every possible direction
		int nRows = gameState.getNumRows();
		int nCols = gameState.getNumCols();
		
		for(int i = 0; i < directions.length; i++) {
			boolean isValidMove = true;
			for(int j = 0; j < nRows; j++) {
				for(int k = 0; k < nCols; k++) {
					Cell initialCell = gameState.getCellAt(j, k);
					if(initialCell.getCellType() != piece) {
						continue; // Skip over any cell that isn't our piece
					}
					
					int[] directionVector = Move.getDirectionVector(directions[i]);
					if( j + directionVector[1] < 0 || k + directionVector[0] < 0 ||
							j + directionVector[1] >= nRows || k + directionVector[0] >= nCols ) {
						isValidMove = false;
						break;
					}
					// second index specifies row direction
					// if (the cell we are moving this block into is not zero) and
					// 	  (the cell we are moving this block into is not the same as our original cell)
					// 	  This move is invalid.
					
					Cell transposed = gameState.getCellAt(j + directionVector[1], k + directionVector[0]);
					// true if (cell we are moving into is a goal) AND
					//		   (cell we are moving is a master brick)
					boolean goalCondition = (transposed.getCellType() == -1) && (piece == 2);
					
					if(transposed.getCellType() !=0 && transposed.getCellType() != initialCell.getCellType() && !goalCondition) {
						isValidMove = false;
						break;
					}
				}
				if(isValidMove == false) {
					break;
				}
			}
			if(isValidMove == true) {
				possibleMoves.add(new Move(piece, directions[i]));
			}
		}
		
		return possibleMoves; 
	}
	
	ArrayList<Move> getListOfPossibleMoves(GameState gameState) {
		ArrayList<Move> allMoves = new ArrayList<Move>();
		Set<Integer> pieces = new HashSet<Integer>();
		
		// Find all piece #s
		for(int i = 0; i < gameState.getNumRows(); i++) { 
			ArrayList<Cell> row = gameState.getCells().get(i); 
			for(int j = 0; j < row.size(); j++) {
				Cell cell = row.get(j);
				pieces.add(cell.getCellType());
			}
		}
		
		for(Integer piece : pieces) { // Just found out about this Syntax!
			List<Move> moves = getListOfPossibleMovesForPiece(gameState, piece);
			allMoves.addAll(moves);
		}
		
		return allMoves;
	}

	public void applyMove(GameState gameState, Move move) {
		
		ArrayList<Cell> from = new ArrayList<Cell>();
		ArrayList<Cell> to = new ArrayList<Cell>();
		
		for(int i = 0; i < gameState.getNumRows(); i++) { 
			ArrayList<Cell> row = gameState.getCells().get(i); 
			for(int j = 0; j < row.size(); j++) {
				if(row.get(j).getCellType() == move.getPiece()) { // Check if this is Cell is part of the piece we're looking for
					int [] directionVector = Move.getDirectionVector(move.getDirection());
					Cell initialCell = row.get(j);
					//System.out.println(i+directionVector[1]);
					//System.out.println(i+directionVector[0]);
					
					// i is row
					// j is column
					// -1, 0 specifies "LEFT"
					// so adding LEFT to a row causes it to go up. This is wrong
					// 
					Cell transposedCell = gameState.getCellAt(i+directionVector[1], j+directionVector[0]);
					from.add(initialCell);
					to.add(transposedCell);
				}
			}
		}
		
		for(int i = 0; i < from.size(); i++) {
			from.get(i).setCellType(0);
		}
		for(int i = 0; i < to.size(); i++) {
			to.get(i).setCellType(move.getPiece());
		}
	}
	
	public GameState applyMoveCloning(GameState gameState, Move move) {
		GameState clonedState = cloneGameState(gameState);
		applyMove(clonedState, move);
		
		return clonedState;
	}

	public boolean isStateSame(GameState g0, GameState g1) {
		
		/*
		GameState g0copy = cloneGameState(g0);
		GameState g1copy = cloneGameState(g1);
		
		normalizeState(g0copy);
		normalizeState(g1copy); */
		GameState g0copy = g0;
		GameState g1copy = g1;
		
		if(g0copy.getNumRows() != g1copy.getNumRows()) {
			return false;
		}
		
		for(int i = 0; i < g0copy.getNumRows(); i++) {
			List<Cell> row0 = g0copy.getRowAt(i);
			List<Cell> row1 = g1copy.getRowAt(i);
			if(row0.size() != row1.size()) {
				return false;
			}
			
			for(int j = 0; j < row1.size(); j++) {
				Cell c0 = row0.get(j);
				Cell c1 = row1.get(j);
				
				if(c0.getCellType() != c1.getCellType()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void normalizeState(GameState gameState) {
		int nextIdx = 3;
		for(int i = 0; i < gameState.getNumRows(); i++) {
			List<Cell> row = gameState.getRowAt(i);
			for(int j = 0; j < row.size(); j++) {
				Cell c = row.get(j);
				if(c.getCellType() == nextIdx) {
					nextIdx++;
				} else if (c.getCellType() > nextIdx) {
					swapIdx(gameState, nextIdx, c.getCellType());
					nextIdx++;
				}
			}
		}
	}
	void swapIdx(GameState gameState, int idx1, int idx2) {
		for(int i = 0; i < gameState.getNumRows(); i++) {
			List<Cell> row = gameState.getRowAt(i);
			for	(int j = 0; j < row.size(); j++) {
				Cell c = row.get(j);  
				if(c.getCellType() == idx1) {
					c.setCellType(idx2);
				} else if (c.getCellType() == idx2) {
					c.setCellType(idx1);
				}
			}
		}
	}
	
	public boolean loadGameState(String fileName, GameState gs) {
		List<String> fileLines = new ArrayList<String>();

		try {
			String line;
			
			File file = new File(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			while((line = reader.readLine()) != null) {
				fileLines.add(line);
			}
			setGameStateCells((ArrayList<String>) fileLines, gs);
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void setGameStateCells(ArrayList<String> file, GameState gs) {
		
		// Read First Line with Dimensions - Is this needed?
		String firstLine = file.get(0);
		String[] dimensions = firstLine.split(",");
		
		if (dimensions.length < 2) {
			System.err.println("Improper format - Dimensions not specified");
		}
		
		int w = Integer.parseInt(dimensions[0]);
		int h = Integer.parseInt(dimensions[1]);
		
		// Starts at 1 because we already read the first line
		for(int i = 1; i < h+1; i++) {
			
			gs.getCells().add(new ArrayList<Cell>(w));
			String [] row = file.get(i).split(",");
			if(row.length < w) {
				System.err.println("Improper format - Row width too short");
				return;
			}
			for(int j = 0; j < w; j++) {
				int blockInt = Integer.parseInt(row[j]);
				ArrayList<Cell> rowList = gs.getCells().get(i-1);
				rowList.add(new Cell(blockInt));
			}
		}
	}
	
	public GameState cloneGameState(GameState gs0) {
		GameState gs1 = new GameState();
		
		// Copy Inner arrays
		
		for(int i = 0; i < gs0.getNumRows(); i++) {
			// Row 0 refers to the original 
			List<Cell> gs0Row = gs0.getRowAt(i);
			List<Cell> gs1Row = new ArrayList<Cell>();
			for(int j = 0; j < gs0Row.size(); j++) {
				// Clone new Cell from old cell
				Cell newCell = new Cell(gs0Row.get(j));
				gs1Row.add(newCell);
			}
			gs1.getCells().add((ArrayList<Cell>) gs1Row);
		}
		
		return gs1;
	}
	
	public void displayGameState(GameState gs)  {
		gs.display(System.out);
	}
	
	/**
	 * Getters and Setters
	 */
	
	/**
	 * Class Variables
	 */
	
	int nodesExplored = 0;
}
