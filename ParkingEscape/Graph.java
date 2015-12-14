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
		public Map<Integer, ArrayList<Situation.Movement>> moves;
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

		//Breadth-first algorithm
		Solution ret = new Solution();
		ArrayDeque<Situation> queue = new ArrayDeque<>();
		ArrayList<Integer> previous = new ArrayList<>();
		ArrayList<Situation.Movement> carsMovements = new ArrayList<>();
		ArrayList<Integer> carsMoved = new ArrayList<>();

		queue.addLast(initialSituation);
		ret.initialSituation = initialSituation;
		ret.moves = new TreeMap<>();
		while(queue.size() > 0) {
			final Situation currentSituation = queue.removeFirst();
			final int currentSituationIndex = situations.get(currentSituation);
			//For each useful movement in the current situation
			for(Map.Entry<Integer, List<Situation.Movement>> movementsList : getUsefulMovements(currentSituation).entrySet()) {
				final int car = movementsList.getKey();
				for(Situation.Movement movement : movementsList.getValue()) {
					//Copy the current situation and apply the movement
					Situation resultingSituation = new Situation(currentSituation);
					resultingSituation.moveCar(car, movement);
					if(!situations.containsKey(resultingSituation))
						queue.addLast(resultingSituation);
					final int resultingSituationIndex = addSituation(resultingSituation);
					while(previous.size() <= resultingSituationIndex) {
						previous.add(null);
						carsMovements.add(null);
						carsMoved.add(null);
					}
					previous.set(resultingSituationIndex, currentSituationIndex);
					carsMovements.set(resultingSituationIndex, movement);
					carsMoved.set(resultingSituationIndex, car);
					if(isTargetSituation(resultingSituation)) {
						ret.finalSituation = resultingSituation;
						fillSolution(ret, previous, carsMovements, carsMoved);
						return ret;
					}
				}
			}
		}
		return ret;
	}
	
	private Map<Integer, List<Situation.Movement>> getUsefulMovementsFor(int car, Situation.Movement movement, Situation situation) {
		Map<Integer, List<Situation.Movement>> ret = new TreeMap<>();
		ArrayDeque<Integer> cars = new ArrayDeque<>();
		ArrayDeque<Situation.Movement> movements = new ArrayDeque<>();
		// Stack<Integer> placeInRec = new Stack<>();
		int blocking;
		cars.addLast(car);
		movements.addLast(movement);
		while(cars.size() > 0) {
			car = cars.removeFirst();
			movement = movements.removeFirst();
			// !situation.getPossibleMovements(car).contains(movement)
			while((blocking = situation.getBlockingCar(car, movement)) != Situation.getEmptyCell()) {
				List<Situation.Movement> theoreticalMoves = Situation.getMovementsFromOrientation(situation.getCarOrientation(blocking));
				cars.addLast(blocking);
				movements.addLast(theoreticalMoves.get(0));
				car = blocking;
				movement = theoreticalMoves.get(1);
			}
			if(!addEntrance(ret, car, movement)) // stuck
				return new TreeMap<>();
		}
		return ret;
	}
	
	private boolean addEntrance(Map<Integer, List<Situation.Movement>> map, int idx, Situation.Movement value) {
		if(!map.containsKey(idx))
			map.put(idx, new ArrayList<Situation.Movement>());
		if(map.get(idx).contains(value))
			return false;
		map.get(idx).add(value);
		return true;
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

	private void fillSolution(Solution solution, ArrayList<Integer> previous, ArrayList<Situation.Movement> carsMovements, ArrayList<Integer> carsMoved) {
		int situationIndex = situations.get(solution.finalSituation);
		final int initialSituationIndex = situations.get(solution.initialSituation);
		while(situationIndex != initialSituationIndex) {
			final int carMoved = carsMoved.get(situationIndex);
			final Situation.Movement carMovement = carsMovements.get(situationIndex);
			if(!solution.moves.containsKey(carMoved))
				solution.moves.put(carMoved, new ArrayList<Situation.Movement>());
			solution.moves.get(carMoved).add(0, carMovement);
			++solution.length;
			situationIndex = previous.get(situationIndex);
		}
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
	}
}

