package ParkingEscape;

import java.util.HashMap;
import java.util.ArrayList;

public class Graph {
	private HashMap<Situation, Integer> situations;
	private ArrayList<ArrayList<Boolean>> adjacencyMatrix;

	public Graph(Situation initialSituation) {
		addSituation(initialSituation);
	}

	private int addSituation(Situation situation) {
		final int index = situations.size();
		situations.put(situation, index);
		final int matrixIndex = addMatrixEntry();
		assert matrixIndex == index : "adjacencyMatrix and situations has different sizes.";
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
