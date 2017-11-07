import java.util.ArrayList;

public class Node {
	public Node(GameState state, Node parent, ArrayList<Node> children, int depth) {
		super();
		this._state = state;
		this._parent = parent;
		this._children = children;
		this._depth = depth;
	}

	Node() {
		_depth = 0;
		_g = 0;
		_h = 0;
	}
	
	double _g;
	
	double _h;
	
	GameState _state;
	
	Node _parent;
	
	ArrayList<Node> _children;
	
	int _depth;
	
	boolean visited;
	
	Move _move;
	
}
