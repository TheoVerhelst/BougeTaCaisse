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
		public boolean isValid;
		public String invalidityExplanation;
	}

	private HashMap<Situation, Integer> situations;
	private Situation initialSituation;
	private final static int goal = Situation.getGoalCar();;
	private final Situation.Orientation goalOrientation;

	public Graph(Situation initialSituation) {
		this.situations = new HashMap<Situation, Integer>();
		this.addSituation(initialSituation);
		this.initialSituation = initialSituation;
		this.goalOrientation = initialSituation.getCarOrientation(Situation.getGoalCar());
	}

	public Solution solve() {
		final Point goalPos = initialSituation.getCarPositions(goal).get(0);
		final Point exitPos = Situation.getExit();

		//Breadth-first algorithm
		Solution ret = new Solution();
		ArrayDeque<Situation> queue = new ArrayDeque<>();
		ArrayList<Integer> previous = new ArrayList<>();
		ArrayList<Situation.Movement> carsMovements = new ArrayList<>();
		ArrayList<Integer> carsMoved = new ArrayList<>();

		queue.addLast(initialSituation);
		ret.isValid = true;
		ret.initialSituation = initialSituation;
		ret.moves = new TreeMap<>();

		try {
			checkInitialSituation();
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
		} catch (SolutionNotFoundException e) {
			ret.isValid = false;
			ret.invalidityExplanation = e.toString().substring(e.toString().indexOf(":") + 2);
		}
		return ret;
	}

	private void checkInitialSituation() throws SolutionNotFoundException {
		final Point goalPos = initialSituation.getCarPositions(goal).get(0);
		for(int i = initialSituation.getFirstCar(); i < initialSituation.getCarCount(); ++i) {
			if(i != goal) {
				final Situation.Orientation carOrientation = initialSituation.getCarOrientation(i);
				final Point carPosition = initialSituation.getCarPositions(i).get(0);
				if((goalOrientation == Situation.Orientation.Horizontal
						&& carOrientation == Situation.Orientation.Horizontal
						&& goalPos.y == carPosition.y)
						|| (goalOrientation == Situation.Orientation.Vertical
						&& carOrientation == Situation.Orientation.Vertical
						&& goalPos.x == carPosition.x))
					throw new SolutionNotFoundException("la voiture " + i + " bloque la sortie.");
			}
		}
	}
	
	private Map<Integer, List<Situation.Movement>> getUsefulMovementsFor(int car, Situation.Movement movement, Situation situation) throws SolutionNotFoundException {
		Map<Integer, List<Situation.Movement>> ret = new TreeMap<>();
		ArrayDeque<Integer> cars = new ArrayDeque<>();
		ArrayDeque<Situation.Movement> movements = new ArrayDeque<>();
		int blocking;
		cars.addLast(car);
		movements.addLast(movement);
		while(cars.size() > 0) {
			car = cars.removeFirst();
			movement = movements.removeFirst();
			while((blocking = situation.getBlockingCar(car, movement)) != Situation.getEmptyCell()) {
				List<Situation.Movement> theoreticalMoves = Situation.getMovementsFromOrientation(situation.getCarOrientation(blocking));
				//Je suis pas sûr du tout que ca soit le bon endroit pour l'exception, tu peux confirmer ?
				if(cars.contains(blocking))
					throw new SolutionNotFoundException("la voiture " + car + " est bloquée par des voitures qui la bloquent elle-même.");
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
	
	private static boolean addEntrance(Map<Integer, List<Situation.Movement>> map, int idx, Situation.Movement value) {
		if(!map.containsKey(idx))
			map.put(idx, new ArrayList<Situation.Movement>());
		if(map.get(idx).contains(value))
			return false;
		map.get(idx).add(value);
		return true;
	}

	private Map<Integer, List<Situation.Movement>> getUsefulMovements(Situation situation) {
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
		return index;
	}

	private static boolean isTargetSituation(Situation situation) {
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
}

