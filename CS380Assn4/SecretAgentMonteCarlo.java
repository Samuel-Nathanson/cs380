package cs380assn4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecretAgentMonteCarlo extends OthelloPlayer {

	public int _iterations;
	private OthelloRandomPlayer scoobyDoo1;
	private OthelloRandomPlayer scoobyDoo2;
	
	public SecretAgentMonteCarlo(int iterations) {
		_iterations = iterations;
		scoobyDoo1 = new OthelloRandomPlayer();
		scoobyDoo2 = new OthelloRandomPlayer();
	}
	
	@Override
	public OthelloMove getMove(OthelloState state) {
		return monteCarloTreeSearch(state, _iterations);
	}

	
	public OthelloMove monteCarloTreeSearch(OthelloState state, int iterations) {
		
		/* 
MonteCarloTreeSearch(board,iterations):
  root = createNode(board);
  for i = 0...iterations:
    node = treePolicy(root);
    if (node!=null)
      node2 = defaultPolicy(node);
      Node2Score = score(node2);
      backup(node,Node2Score);
  return action(bestChild(root))
		 */
		
		// CreateNode
		Node root = createNode(state);
		
		for(int i = 0 ; i < iterations ; i++) {
			
			Node node = treePolicy(root);
			
			if(node != null) {
				Node node2 = defaultPolicy(node);
				int node2Score = node2._state.score();
				backup(node, node2Score);
			}
		}
		
		// If root has no children, return null for the move. 
		// This may happen at the end of the game.
		Node bNode = bestChild(root);
		if(bNode == null) {
			return null;
		}
		return bNode._move;
	}
	
	public boolean isSameState(OthelloState s1, OthelloState s2) {
		if(s1.nextPlayerToMove != s2.nextPlayerToMove) {
			return false;
		}
		for(int i = 0; i < s1.boardSize ; i++) {
			for(int j = 0 ; j < s2.boardSize; j++) {
				if(s1.board[i][j] != s2.board[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	Node treePolicy(Node n) {
		
		List<Node> childrenInTree = n._children;
		List<Node> children = new ArrayList<Node>();
		List<OthelloMove> moves = n._state.generateMoves();
		
		/* Find out if "node" has any child not in the tree */
		/* Appears to be working */ 
		for(int i = 0; i < moves.size(); i++) {
			Node child = new Node(n._state.applyMoveCloning(moves.get(i)));
			child._move = moves.get(i);
			child._parent = n;
			child._visited = 0;
			child._averageScore = 0;
			children.add(child);
		}
		for(Node child : children) {
			OthelloState testChildState = child._state;
			boolean found = false;
			
			for(int j = 0; j < childrenInTree.size(); j++) {
				OthelloState treeChildState = childrenInTree.get(j)._state;
				if(isSameState(treeChildState, testChildState)) {
					found = true;
					break;
				}
			}
			if(found == false) {
				n._children.add(child);
				return child;
			}
		}
		
		/* If we don't have any moves, return n */
		if(moves.isEmpty()) {
			return n;
		}
		
		/* 90% chance to return the best move */
		Random rand = new Random();
		Node nodeTmp = null;
		if(rand.nextDouble() < 0.9) {
			nodeTmp = bestChild(n);
		} else {
			nodeTmp = n._children.get(rand.nextInt(n._children.size()));
			nodeTmp._parent = n;
		}
		return treePolicy(nodeTmp);
	}
	
	// Is bestChild returning the correct node?
	private Node bestChild(Node n) {
		/* I have not examined this function closely */
		Node bestChild = null;
		
		if(n._state.nextPlayerToMove == 0) {
			double maxScore = -100;
			for(Node child : n._children) {
				if(child._averageScore > maxScore) {
					maxScore = child._averageScore;
					bestChild = child;
				}
			}
		}
		
		if(n._state.nextPlayerToMove == 1) {
			double minScore = 100;
			for(Node child : n._children) {
				if(child._averageScore < minScore) {
					minScore = child._averageScore;
					bestChild = child;
				}
			}
		}
		
		return bestChild;
	}
	
	private void backup(Node n, int score) { 
		/* I have not examined this function closely */ 
		n._visited++;
		n._averageScore = ( n._averageScore + score ) / n._visited;

		if(n._parent != null) { 
			backup(n._parent, score);
		}
	}
	
	private Node defaultPolicy(Node node) {
		
		OthelloState state = node._state.clone();
		while(!state.gameOver()) {
			OthelloMove move= scoobyDoo1.getMove(state);
			
			state.applyMove(move);
		}
		
		return new Node(state);
	}

	public Node createNode(OthelloState state) {
		return new Node(state);
	}
	
	
	private class Node {
		
		public Node(OthelloState state) { 
			_state = state;
			_children = new ArrayList<Node>(); // Should this be initialized to 1?
			_visited = 0;
			_averageScore = 0;
			_parent = null;
		}
		
		public OthelloState _state;
		public Node _parent;
		public List<Node> _children;
		public OthelloMove _move;
		public int _visited;
		public double _averageScore;
		public double _test;
		
	}
}
