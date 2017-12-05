package cs380assn4;

import java.util.ArrayList;
import java.util.List;

public class GodlikeOthelloPlayer extends OthelloPlayer {

	public GodlikeOthelloPlayer(int searchDepth) {
		_searchDepth = searchDepth;
		
		int [][] csquares = {{0,1},{1,0},{1,1},
						{6,1},{6,0},{7,1},
						{0,6},{1,6},{1,7},
						{6,6},{7,6},{6,7}};
		
		for(int i = 0; i < csquares.length; i++) {
				cSquareLocations.add(csquares[i]);
		}
	}
	
	@Override
	public OthelloMove getMove(OthelloState state) {
		return MinimaxDecision(state);
	}
	
	public OthelloMove MinimaxDecision(OthelloState state) {
		_currentSearchDepth = 0;
		
		List<OthelloMove> actions = state.generateMoves(state.nextPlayerToMove);
		
		return MAX(state.nextPlayerToMove, actions, state);
		
	}
	
	OthelloMove MAX(int player, List<OthelloMove> actions, OthelloState state) {
		
		OthelloMove maxMove = null;
		int maxMoveForPlayer = player == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		for(OthelloMove move : actions) { 
			OthelloState newState = state.applyMoveCloning(move);
			int score = MinValue(newState);
			
			if(player == 0 && score > maxMoveForPlayer) {
				maxMoveForPlayer = score;
				maxMove = move;
			}
			if(player == 1 && score < maxMoveForPlayer) {
				maxMoveForPlayer = score;
				maxMove = move;
			}
		}
		
		return maxMove;
	}
	
	OthelloMove MIN(int player, List<OthelloMove> actions, OthelloState state) {
		OthelloMove minMove = null;
		int minMoveForPlayer = player == 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		
		for(OthelloMove move : actions) { 
			OthelloState newState = state.applyMoveCloning(move);
			int score = newState.score();
			
			if(player == 0 && score < minMoveForPlayer) {
				minMoveForPlayer = score;
				minMove = move;
			}
			if(player == 1 && score > minMoveForPlayer) {
				minMoveForPlayer = score;
				minMove = move;
			}
		}
		
		return minMove;
	}
	
	public int MaxValue(OthelloState state) {
		_currentSearchDepth++;
		int player = state.nextPlayerToMove;
		
		List<OthelloMove> actions = state.generateMoves(state.nextPlayerToMove);
		
		if(TerminalTest(state)) {
			_currentSearchDepth--;
			return Utility(state, actions.size());
		}
		
		int maxMoveForPlayer = player == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		for(int i = 0; i < actions.size(); i++) {
			int score = MinValue(state.applyMoveCloning(actions.get(i)));
			
			if(player == 0 && score > maxMoveForPlayer) {
				maxMoveForPlayer = score;
			}
			if(player == 1 && score < maxMoveForPlayer) {
				maxMoveForPlayer = score;
			}
		}
		
		_currentSearchDepth--;
		return maxMoveForPlayer;
	}

	public int MinValue(OthelloState state) {
		
		_currentSearchDepth++;
		int player = state.nextPlayerToMove;
		
		List<OthelloMove> actions = state.generateMoves(player);
		
		if(TerminalTest(state)) {
			_currentSearchDepth--;
			return Utility(state, actions.size());
		}
		
		int minMoveForPlayer = state.nextPlayerToMove == 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		
		for(int i = 0; i < actions.size(); i++) {
			int score = MaxValue(state.applyMoveCloning(actions.get(i)));
			
			if(player == 0 && score < minMoveForPlayer) {
				minMoveForPlayer = score;
			}
			if(player == 1 && score > minMoveForPlayer) {
				minMoveForPlayer = score;
			}
		}
		_currentSearchDepth--;
		return minMoveForPlayer;
	}
	
	public boolean TerminalTest(OthelloState state) {
		if(state.gameOver()) {
			return true;
		} else if(_currentSearchDepth > _searchDepth) {
			return true;
		} else {
			return false;
		}
	}
	
	public int Utility(OthelloState state) { 
		return Utility(state, -1);
	}
	
	public int Utility(OthelloState state, int numMoves) {
		// Positive if player "o" is winning.
		// Todo : We may need to know which player we are.
		
		int currentPlayerToMove = state.nextPlayerToMove;
		int opposingPlayer = currentPlayerToMove == 0? 1 : 0;
		
		/* 
		 * Ok, here I am putting weights on my strategy elements
		 */
		int MAX_POSSIBLE_MOVES = 24;
		int MAX_CORNERS = 4;
		int MAX_CSQUARES = 12;
		
		double weight_corner = 1.0;
		double weight_csquare = 0.75;
		double weight_numMoves = 0.5;
		
		int numMovesForPlayer = state.generateMoves(0).size();
		int numMovesForOpposingPlayer = -1 * state.generateMoves(1).size();
		
		int corners = getNumCornersForPlayer(state, 0);
		int opposingCorners = -1 * getNumCornersForPlayer(state, 1);
		
		int csquares = getNumCSquaresForPlayer(state, 0);
		int opposingCSquares = -1 * getNumCSquaresForPlayer(state, 1);
		
		// 0 - 24 // Likely around 0.05
		double moves_squore = (numMovesForPlayer + numMovesForOpposingPlayer) / MAX_POSSIBLE_MOVES * weight_numMoves;
				
		// 0 - 4 // Likely around 0.5 
		double corner_squore = (corners + opposingCorners) / MAX_CORNERS * weight_corner;
		
		// 0 - 12 // Likely around 375
		double csquare_squore = (csquares + opposingCSquares) / MAX_CSQUARES * weight_csquare;
		
		int score = (int) Math.floor(moves_squore + corner_squore + csquare_squore);
		
		
		return score;
	}
	
	static int getNumCornersForPlayer(OthelloState state, int player) { 
		int [][] board = state.board;
		
		int numCorners = 0;
		
		int i = 0;
		while(i < board.length) {
			int j = 0;
			while(j < board.length) {
				
				if(board[i][j] == player) {
					numCorners++;
				}
				
				j += board.length -1;
			}
			i += board.length - 1;
		}
		return numCorners;
	}
	
	static int getNumCSquaresForPlayer(OthelloState state, int player) { 
		int [][] board = state.board;
		
		int numCsquares = 0;
		
		for(int[] csquare : cSquareLocations) {
			if(board[csquare[0]][csquare[1]] == player) {
				numCsquares++;
			}
		}
		
		return numCsquares;
	}
	
	private int _currentSearchDepth;
	private int _searchDepth;
	
	final int WORST_MOVE = Integer.MIN_VALUE;
	final int BEST_MOVE_FOR_OPPONENT = Integer.MAX_VALUE;
	
	static List<int[]> cSquareLocations = new ArrayList<int[]>();
	
}
