import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.naming.TimeLimitExceededException;

public class HW2 {

	public static void main(String [] args) {
		
		String fileName = args[0];
		String algorithm = args[1];
		
		Game game = new Game();
		GameState gs = new GameState();
		game.loadGameState(fileName, gs);
		game.normalizeState(gs);
		
		ArrayList<Move> path = new ArrayList<Move>();
		
		Instant start = Instant.now();
		
		if(algorithm.equals("bfs")) {
			path = game.bfsSearch(gs);
			for(int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i).toString());
				game.applyMove(gs, path.get(i));
			}
			game.displayGameState(gs);
		} else if(algorithm.equals("dfs")) {
			int depth = 6;
			path = game.dfsSearch(gs, depth);
			if(path == null) {
				System.out.println("DFS: no path found with depth " + depth);
			}
			for(int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i).toString());
				game.applyMove(gs, path.get(i));
			}
			game.displayGameState(gs);
		} else if(algorithm.equals("astar")){
			path = game.A(gs);
			if(path == null) {
				System.out.println("Astar: no path found");
			}
			for(int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i).toString());
				game.applyMove(gs, path.get(i));
			}
			game.displayGameState(gs);
		}
		
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		
		
		System.out.println("Nodes explored for algorithm " + algorithm + "= " + game.nodesExplored);
		System.out.println("Time taken for algorithm " + algorithm + "= "+ timeElapsed.toMillis() +" milliseconds");
		System.out.println("Solution Length =" + path.size());
	}
}
