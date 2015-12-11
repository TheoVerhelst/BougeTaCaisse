package ParkingEscape;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.awt.Point;

public class Graph {
	private HashMap<Situation, Integer> situations;
	private ArrayList<ArrayList<Boolean>> adjacencyMatrix;
	private ArrayDeque<Integer> blockingCars;
	private Situation currSit;

	public Graph(Situation initialSituation) {
		situations = new HashMap<Situation, Integer>();
		adjacencyMatrix = new ArrayList<ArrayList<Boolean>>();
		blockingCars = new ArrayDeque<Integer>();
		addSituation(initialSituation);
		currSit = initialSituation;
	}

	public void solve() throws SolutionNotFoundException {
		final int goal = Situation.getGoalCar();
		final Situation.Orientation goalOrientation = currSit.getCarOrientation(goal);
		final Point goalPos = currSit.getCarPositions(goal).get(0);
		if((goalOrientation == Situation.Orientation.Vertical && goalPos.x != Situation.getExit().x)
				|| (goalOrientation == Situation.Orientation.Horizontal && goalPos.y != Situation.getExit().y))
			throw new SolutionNotFoundException("The goal car is not aligned with the exit.");
	}

	private int addSituation(Situation situation) {
		final int index = situations.size();
		situations.put(situation, index);
		final int matrixIndex = addMatrixEntry();
		assert matrixIndex == index : "adjacencyMatrix and situations have different sizes.";
		return index;
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
