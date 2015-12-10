package ParkingEscape;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.awt.Point;

public class Graph {
	private HashMap<Situation, Integer> situations;
	private ArrayList<ArrayList<Boolean>> adjacencyMatrix;
	private ArrayDeque<Integer> blockingCars;

	public Graph(Situation initialSituation) {
		situations = new HashMap<Situation, Integer>();
		adjacencyMatrix = new ArrayList<ArrayList<Boolean>>();
		blockingCars = new ArrayDeque<Integer>();
		addSituation(initialSituation);
	}

	public void solve() {
		//Amazing stuffs go here
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
}
