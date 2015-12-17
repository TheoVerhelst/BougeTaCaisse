package ParkingEscape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Situation implements Cloneable {
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
	private ArrayList<Point> carsPositions;
	private ArrayList<Orientation> carsOrientations;
	private static final int emptyCell = -1;
	private static final int goalCell = 0;
	private static Point exit;
	private static String goalString = "G";
	private static String otherCarStringPrefix = "c";

	public Situation(Point size) {
		this.size = size;
		this.carsPositions = new ArrayList<>();
		this.carsPositions.add(getGoalCar(), new Point(-1, -1));
		this.carsOrientations = new ArrayList<>();
		this.carsOrientations.add(getGoalCar(), Orientation.Vertical);
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

	/* Copy constructor.*/
	public Situation(Situation other) {
		this.size = new Point(other.size);
		this.parking = new int[this.size.y][];
		for(int i = 0; i < this.size.y; ++i)
			this.parking[i] = other.parking[i].clone();
		this.carsPositions = new ArrayList<>(other.carsPositions.size());
		for(Point pos : other.carsPositions)
			this.carsPositions.add(new Point(pos));
		this.carsOrientations = new ArrayList<>(other.carsOrientations.size());
		for(Orientation orientation : other.carsOrientations)
			this.carsOrientations.add(orientation);
	}

	public int addCar(List<Point> positions) throws IllegalArgumentException {
		checkPositions(positions);
		//All is fine, we can add the car
		final int newCar = carsPositions.size();
		carsPositions.add(positions.get(0));
		carsOrientations.add(positions.get(0).x - positions.get(1).x == 0 ? Orientation.Vertical : Orientation.Horizontal);
		setCarPositions(newCar, positions);
		return newCar;
	}
	
	public ArrayList<Point> getCarPositions(int car) throws IndexOutOfBoundsException {
		checkCarArgument(car);
		final Point pos = carsPositions.get(car);
		final Orientation orientation = carsOrientations.get(car);
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

	public int getBlockingCar(int car, Movement blockedMovement) throws IndexOutOfBoundsException, IllegalArgumentException {
		checkCarArgument(car);
		final Orientation carOrientation = getCarOrientation(car);
		if(((blockedMovement == Movement.Left || blockedMovement == Movement.Right) && carOrientation != Orientation.Horizontal)
				|| ((blockedMovement == Movement.Up || blockedMovement == Movement.Down) && carOrientation != Orientation.Vertical))
			throw new IllegalArgumentException("The movement argument is not consistent with the orientation of the car argument.");

		final List<Point> pos = getCarPositions(car);
		Point previousCell = new Point(pos.get(0));
		Point nextCell = new Point(pos.get(pos.size()-1));
		final int dx = blockedMovement.getComposition().x;
		final int dy = blockedMovement.getComposition().y;
		previousCell.translate(dx, dy);
		nextCell.translate(dx, dy);
		if(isInParking(previousCell) && getCar(previousCell) != car)
			return getCar(previousCell);
		else if(isInParking(nextCell) && getCar(nextCell) != car)
			return getCar(nextCell);
		else
			return getEmptyCell();
	}

	public ArrayList<Movement> getPossibleMovements(int car) throws IndexOutOfBoundsException {
		checkCarArgument(car);
		ArrayList<Movement> result = new ArrayList<>();
		final List<Point> pos = getCarPositions(car);
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
		checkCarArgument(car);
		if(!getPossibleMovements(car).contains(movement))
			throw new IllegalArgumentException("Movement not supported by specified car.");
		else {
			final int dx = movement.getComposition().x;
			final int dy = movement.getComposition().y;
			final List<Point> carPositions = getCarPositions(car);
			for(Point carPosition : carPositions)
				setCar(getEmptyCell(), carPosition);
			for(Point carPosition : carPositions)
				setCar(car, carPosition.x + dx, carPosition.y + dy);
			carsPositions.get(car).translate(dx, dy);
		}
	}

	public int getCar(Point position) {
		return getCar(position.x, position.y);
	}

	public int getCar(int x, int y) {
		return parking[y][x];
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
		carsPositions.set(getGoalCar(), positions.get(0));
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
			final Situation otherSituation = (Situation) other;
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

	@Override
	public String toString() {
		String res = new String("+");
		for(int i = 0; i < size.x; ++i)
			res += "---+";
		for(int i = 0; i < size.y; ++i) {
			res += System.lineSeparator() + "|";
			for(int j = 0; j < size.x; ++j) {
				final int car = getCar(j, i);
				String cell;
				if(car == getEmptyCell())
					cell = "   ";
				else if(car == getGoalCar())
					cell = String.format("%-3s", goalString);
				else
					cell = String.format("c%-2d", car);
				res += cell;
				if(Math.abs(j - exit.x) > 0 || Math.abs(i - exit.y) > 0)
					res += "|";
			}
			res += System.lineSeparator() + "+";
			for(int j = 0; j < size.x; ++j)
				res += "---+";
		}
		return res;
	}
	
	public Orientation getCarOrientation(int car) {
		checkCarArgument(car);
		return this.carsOrientations.get(car);
	}

	public static ArrayList<Situation.Movement> getMovementsFromOrientation(Orientation orientation) {
		ArrayList<Situation.Movement> ret = new ArrayList<>();
		if(orientation == Orientation.Horizontal) {
			ret.add(Movement.Left);
			ret.add(Movement.Right);
		} else {
			ret.add(Movement.Up);
			ret.add(Movement.Down);
		}
		return ret;
	}

	public Point getSize() {
		return new Point(size);
	}

	public int getWidth() {
		return size.x;
	}

	public int getHeight() {
		return size.y;
	}

	public int getFirstCar() {
		return 0;
	}

	public int getCarCount() {
		return carsPositions.size();
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

	private void checkCarArgument(int car) throws IndexOutOfBoundsException {
		if(car >= carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
	}

	private void checkPositions(List<Point> positions) throws IllegalArgumentException {
		//Verify that there is not another car at specified positions
		//and that positions are adjacent.
		if(positions.size() < 2)
			throw new IllegalArgumentException("Cars must be at least 2 units long.");
		for(int i = 0; i < positions.size(); ++i) {
			final Point pos = positions.get(i);
			if(!isCellEmpty(pos))
				throw new IllegalArgumentException("There is already a car at specified position.");
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

	private void setCar(int car, Point position) {
		setCar(car, position.x, position.y);
	}

	private void setCar(int car, int x, int y) {
		assert isInParking(x, y) : "Given position is not in parking: (" + x + ", " + y + ").";
		parking[y][x] = car;
	}
}

