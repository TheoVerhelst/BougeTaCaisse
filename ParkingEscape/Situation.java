package ParkingEscape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalArgumentException;

public class Situation {
	public enum Movement {
		Up(new Point(0, -1)),
		Down(new Point(0, 1)),
		Left(new Point(-1, 0)),
		Right(new Point(1, 0));
		private final Point composition;

		Movement(Point composition) {
			this.composition = composition;
		}

		public Point getComposition() {
			return composition;
		}
	}

	public enum Orientation {
		Vertical, Horizontal
	}

	private final Point size;
	private int[][] parking;
	private List<Point> carsPositions;
	private List<Orientation> carsOrientations;
	private static final int emptyCell = -1;
	private static final int goalCell = 0;
	private static Point exit;

	public Situation(Point size) {
		this.carsPositions = new ArrayList<>();
		this.carsPositions.add(getGoalCar(), new Point(-1, -1));
		this.carsOrientations = new ArrayList<>();
		this.carsOrientations.add(getGoalCar(), Orientation.Vertical);
		this.size = size;
		parking = new int[size.y][];
		for(int i = 0; i < size.y; ++i) {
			parking[i] = new int[size.x];
			for(int j = 0; j < size.x; ++j)
				setCar(getEmptyCell(), j, i);
		}
	}

	public Situation(int width, int height) {
		this(new Point(width, height));
	}

	private void checkPositions(List<Point> positions) throws IllegalArgumentException {
		//Verify that there is not another car at specified positions
		//and that positions are adjacent.
		if(positions.size() < 2)
			throw new IllegalArgumentException("Cars must be at least 2 units long.");
		for(int i = 0; i < positions.size(); ++i) {
			Point pos = positions.get(i);
			if(!isCellEmpty(pos)) {
				throw new IllegalArgumentException("There is already a car at specified position.");
			}
			if(i > 0) {
				Point difference = positions.get(i - 1);
				if(Math.abs(difference.x-pos.x) + Math.abs(difference.y-pos.y) != 1)
					throw new IllegalArgumentException("Points specified are not adjacent.");
			}
		}
	}

	private void setCarPositions(int car, List<Point> positions) {
		for(Point pos : positions)
			setCar(car, pos);
	}

	public int addCar(List<Point> positions) throws IllegalArgumentException {
		checkPositions(positions);
		//All is fine, we can add the car
		int newCar = carsPositions.size();
		carsPositions.add(positions.get(0));
		carsOrientations.add(positions.get(0).x - positions.get(1).x == 0 ? Orientation.Vertical : Orientation.Horizontal);
		setCarPositions(newCar, positions);
		return newCar;
	}
	
	public List<Point> getCarPositions(int car) throws IndexOutOfBoundsException {
		if(car >= carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
		Point pos = carsPositions.get(car);
		Orientation orientation = carsOrientations.get(car);
		ArrayList<Point> ret = new ArrayList<>();
		if(orientation == Orientation.Vertical) {
			for(int y = pos.y; isInParking(pos.x, y) && getCar(pos.x, y) == car; ++y)
				ret.add(new Point(pos.x, y));
			for(int y = pos.y - 1; isInParking(pos.x, y) && getCar(pos.x, y) == car; --y)
				ret.add(0, new Point(pos.x, y));
		} else {
			for(int x = pos.x; isInParking(x, pos.y) && getCar(x, pos.y) == car; ++x)
				ret.add(new Point(x, pos.y));
			for(int x = pos.x - 1; isInParking(x, pos.y) && getCar(x, pos.y) == car; --x)
				ret.add(0, new Point(x, pos.y));
		}
		return ret;
	}

	public List<Movement> getPossibleMovements(int car) throws IndexOutOfBoundsException {
		if(car >= carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
		ArrayList<Movement> result = new ArrayList<>();
		List<Point> pos = getCarPositions(car);
		Point previousCell = new Point(pos.get(0)),
		      nextCell = new Point(pos.get(pos.size()-1));
		if(carsOrientations.get(car) == Orientation.Horizontal) {
			previousCell.translate(-1, 0);
			if(previousCell.x >= 0 && isCellEmpty(previousCell))
				result.add(Movement.Left);
			nextCell.translate(+1, 0);
			if(nextCell.x < this.size.x && isCellEmpty(nextCell))
				result.add(Movement.Right);
		} else {
			previousCell.translate(0, -1);
			if(previousCell.y >= 0 && isCellEmpty(previousCell))
				result.add(Movement.Up);
			nextCell.translate(0, +1);
			if(nextCell.y < this.size.y && isCellEmpty(nextCell))
				result.add(Movement.Down);
		}
		return result;
	}

	public void moveCar(int car, Movement movement) throws IndexOutOfBoundsException, IllegalArgumentException {
		if(car >= carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
		if(!getPossibleMovements(car).contains(movement))
			throw new IllegalArgumentException("Movement not supported by specified car.");
		else {
			final int dx = movement.getComposition().x;
			final int dy = movement.getComposition().y;
			final List<Point> carPositions = getCarPositions(car);
			for(Point carPosition : carPositions)
				setCar(getEmptyCell(), carPosition);
			for(Point carPosition : carPositions)
				setCar(car, carPosition.x+ dx, carPosition.y + dy);
		}
	}

	public int getCar(Point position) {
		return getCar(position.x, position.y);
	}

	public int getCar(int x, int y) {
		return parking[y][x];
	}

	private void setCar(int car, Point position) {
		setCar(car, position.x, position.y);
	}

	private void setCar(int car, int x, int y) {
		assert isInParking(x, y) : "Given position is not in parking: (" + x + ", " + y + ").";
		parking[y][x] = car;
	}

	public static int getGoalCar() {
		return goalCell;
	}

	public static int getEmptyCell() {
		return emptyCell;
	}

	public int setGoalPositions(List<Point> positions) throws IllegalArgumentException {
		checkPositions(positions);
		setCarPositions(getGoalCar(), positions);
		carsPositions.set(0, positions.get(0));
		carsOrientations.set(getGoalCar(), positions.get(0).x - positions.get(1).x == 0 ? Orientation.Vertical : Orientation.Horizontal);
		return getGoalCar();
	}

	public static void setExit(int x, int y) {
		Situation.exit = new Point(x, y);
	}
	
	public static Point getExit() {
		return new Point(exit);
	}

	@Override
	public boolean equals(Object other) {
		if(other == this)
			return true;
		else if(other != null && other instanceof Situation) {
			Situation otherSituation = (Situation) other;
			for(int i = 0; i < size.x; ++i)
				for(int j = 0; j < size.y; ++j)
					if(getCar(i, j) != otherSituation.getCar(i, j))
						return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + super.hashCode();
		for(int i = 0; i < size.y; ++i)
			for(int j = 0; j < size.x; ++j)
				result = prime * result + getCar(j, i);
		return result;
	}

	private boolean isCellEmpty(Point cell) {
		return isCellEmpty(cell.x, cell.y);
	}

	private boolean isCellEmpty(int x, int y) {
		return getCar(x, y) == getEmptyCell();
	}

	private boolean isInParking(Point cell) {
		return isInParking(cell.x, cell.y);
	}

	private boolean isInParking(int x, int y) {
		return 0 <= x && x < this.size.x && 0 <= y && y < this.size.y;
	}

	@Override
	public String toString() {
		String res = new String("+");
		for(int i = 0; i < size.x; ++i)
			res += "--+";
		for(int i = 0; i < size.y; ++i) {
			res += "\n|";
			for(int j = 0; j < size.x; ++j) {
				if(!isCellEmpty(j, i))
					res += String.format("%2d", getCar(j, i));
				else
					res += "  ";
				res += "|";
			}
			res += "\n+";
			for(int j = 0; j < size.x; ++j)
				res += "--+";
		}
		return res += "\n";
	}
	
	public Orientation getCarOrientation(int car) {
		if(car >= this.carsOrientations.size())
			throw new IndexOutOfBoundsException("Specified car does not exist");
		return this.carsOrientations.get(car);
	}

	public Point getSize() {
		return size;
	}
}

