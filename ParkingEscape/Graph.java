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
		public Map<Integer, ArrayList<ArrayList<Point>>> moves;
		public int length;
	}
	private HashMap<Situation, Integer> situations;
	private ArrayList<ArrayList<Boolean>> adjacencyMatrix;
	private Situation initialSituation;
	private final Situation.Orientation goalOrientation;

	public Graph(Situation initialSituation) {
		this.situations = new HashMap<Situation, Integer>();
		this.adjacencyMatrix = new ArrayList<ArrayList<Boolean>>();
		this.addSituation(initialSituation);
		this.initialSituation = initialSituation;
		this.goalOrientation = initialSituation.getCarOrientation(Situation.getGoalCar());
	}

	public Solution solve() throws SolutionNotFoundException {
		final int goal = Situation.getGoalCar();
		final Point goalPos = initialSituation.getCarPositions(goal).get(0);
		final Point exitPos = Situation.getExit();
		Solution ret = new Solution();

		//Breadth-first algorithm
		HashSet<Situation> marked = new HashSet<>();
		ArrayDeque<Situation> queue = new ArrayDeque<>();
		marked.add(initialSituation);
		queue.addLast(initialSituation);
		ret.initialSituation = initialSituation;
		ret.moves = new HashMap<>();
		while(queue.size() > 0) {
			Situation currentSituation = queue.removeFirst();
			final int edgeOrigin = situations.get(currentSituation);
			//For each useful movement in the current situation
			for(Map.Entry<Integer, List<Situation.Movement>> movementsList : getUsefulMovements(currentSituation).entrySet()) {
				final int car = movementsList.getKey();
				if(!ret.moves.containsKey(car)) {
					ret.moves.put(car, new ArrayList<ArrayList<Point>>());
					ret.moves.get(car).add(currentSituation.getCarPositions(car));
				}
				for(Situation.Movement movement : movementsList.getValue()) {
					//Copy the current situation and apply the movement
					Situation resultingSituation = new Situation(currentSituation);
					resultingSituation.moveCar(car, movement);
					ret.moves.get(car).add(resultingSituation.getCarPositions(car));
					++ret.length;
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
	
	private Map<Integer, List<Situation.Movement>> getUsefulMovementsFor(int car, Situation.Movement movement, Situation situation) {
		Map<Integer, List<Situation.Movement>> ret = new TreeMap<Integer, List<Situation.Movement>>();
		final int blocking = situation.getBlockingCar(car, movement);
		if(blocking == Situation.getEmptyCell()) {
			ArrayList<Situation.Movement> moves = new ArrayList<>(1);
			moves.add(movement);
			ret.put(car, moves);
		} else {
			for(Situation.Movement move : Situation.getMovementsFromOrientation(situation.getCarOrientation(blocking))) {
				for(Map.Entry<Integer, List<Situation.Movement>> moveList : getUsefulMovementsFor(blocking, move, situation).entrySet()) {
					final int idx = moveList.getKey();
					if(ret.containsKey(idx))
						ret.get(idx).addAll(moveList.getValue());
					else
						ret.put(idx, moveList.getValue());
				}
			}
		}
		return ret;
	}

	private Map<Integer, List<Situation.Movement>> getUsefulMovements(Situation situation) {
		final int goal = Situation.getGoalCar();
		Situation.Movement goalMovement;
		if(goalOrientation == Situation.Orientation.Horizontal)
			goalMovement = Situation.getExit().x < situation.getCarPositions(goal).get(0).x ? Situation.Movement.Left : Situation.Movement.Right;
		else
			goalMovement = Situation.getExit().y < situation.getCarPositions(goal).get(0).y ? Situation.Movement.Up : Situation.Movement.Down;
		return getUsefulMovementsFor(Situation.getGoalCar(), goalMovement, situation);
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
