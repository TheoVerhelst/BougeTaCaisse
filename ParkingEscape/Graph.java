package ParkingEscape;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.awt.Point;

public class Graph {
	public class Solution {
		public Situation initialSituation, finalSituation;
		Map<Integer, List<List<Point>>> moves;
	}
	private HashMap<Situation, Integer> situations;
	private ArrayList<ArrayList<Boolean>> adjacencyMatrix;
	private ArrayDeque<Integer> carsToMove;
	private Situation currSit;

	public Graph(Situation initialSituation) {
		situations = new HashMap<Situation, Integer>();
		adjacencyMatrix = new ArrayList<ArrayList<Boolean>>();
		carsToMove = new ArrayDeque<Integer>();
		addSituation(initialSituation);
		currSit = initialSituation;
	}

	public Solution solve() throws SolutionNotFoundException {
		final int goal = Situation.getGoalCar();
		final Situation.Orientation goalOrientation = currSit.getCarOrientation(goal);
		final Point goalPos = currSit.getCarPositions(goal).get(0);
		final Point exitPos = Situation.getExit();
		Solution ret = new Solution();

		//Breadth-first algorithm
		HashSet<Situation> marked = new HashSet<>();
		ArrayDeque<Situation> queue = new ArrayDeque<>();
		marked.add(currSit);
		queue.addLast(currSit);
		ret.initialSituation = currSit;
		ret.moves = new HashMap<>();
		while(queue.size() > 0) {
			currSit = queue.removeFirst();
			final int edgeOrigin = situations.get(currSit);
			//For each useful movement in the current situation
			for(Map.Entry<Integer, List<Situation.Movement>> movementsList : getUsefulMovements().entrySet()) {
				for(Situation.Movement movement : movementsList.getValue()) {
					//Copy the current situation and apply the movement
					Situation resultingSituation = new Situation(currSit);
					resultingSituation.moveCar(movementsList.getKey(), movement);
					if(isTargetSituation(resultingSituation)) {
						ret.finalSituation = resultingSituation;
						return ret;
					}
					final int edgeDest = addSituation(resultingSituation);
					linkSituations(edgeOrigin, edgeDest);
					if(!marked.contains(resultingSituation)) {
						marked.add(resultingSituation);
						queue.addLast(resultingSituation);
					}
				}
			}
		}
		return ret;
	}

	private Map<Integer, List<Situation.Movement>> getUsefulMovements() {
		//Génération de tous les mouvements possibles <=> arbre entier des situations possibles
		//C'est ici qu'un élagage intelligent est à écrire pour eviter un memory overhead
		Map<Integer, List<Situation.Movement>> ret = new TreeMap<Integer, List<Situation.Movement>>();
		for(int car = currSit.getFirstCar(); car != currSit.getPastTheLastCar(); ++car)
			ret.put(car, currSit.getPossibleMovements(car));
		return ret;
	}

	private int addSituation(Situation situation) {
		if(situations.containsKey(situation))
			return situations.get(situation);
		final int index = situations.size();
		situations.put(situation, index);
		final int matrixIndex = addMatrixEntry();
		assert matrixIndex == index : "adjacencyMatrix and situations have different sizes.";
		return index;
	}

	private boolean isTargetSituation(Situation situation) {
		final int goal = Situation.getGoalCar();
		final Point exitPos = Situation.getExit();
		for(Point goalPos : situation.getCarPositions(goal)) {
			if(situation.getCarOrientation(goal) == Situation.Orientation.Horizontal) {
				if(goalPos.y != exitPos.y)
					return false;
				if(goalPos.x == exitPos.x)
					return true;
			} else {
				if(goalPos.x != exitPos.x)
					return false;
				if(goalPos.y == exitPos.y)
					return true;
			}
		}
		return false;
	}

	private int addMatrixEntry() {
		final int index = adjacencyMatrix.size();
		for(int i = 0; i < index; ++i)
			adjacencyMatrix.get(i).add(false);
		ArrayList<Boolean> newRow = new ArrayList<Boolean>();
		newRow.ensureCapacity(index + 1);
		for(int i = 0; i < index; ++i)
			newRow.add(false);
		newRow.add(true);
		adjacencyMatrix.add(newRow);
		return index;
	}

	private void linkSituations(int i, int j) {
		assert i < adjacencyMatrix.size() && j < adjacencyMatrix.size();
		adjacencyMatrix.get(i).set(j, true);
		adjacencyMatrix.get(j).set(i, true);
	}
}
