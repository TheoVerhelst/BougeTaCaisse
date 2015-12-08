package ParkingEscape;

import java.awt.Point;
import java.util.Vector;
import java.util.List;
import java.lang.IllegalArgumentException;

public class Situation {
	public enum Movement {
		Up(0), Down(1), Left(2), Right(3);

		private final int value;

		Movement(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private enum Orientation {
		Vertical, Horizontal
	}

	private final Point size;
	private int[][] parking;
	private static final int emptyCell = 0;
	private List<Point> carsPositions;
	private List<Orientation> carsOrientations;
	private static final Point[] movementComposition = {new Point(0, -1), new Point(0, 1), new Point(-1, 0), new Point(1, 0)};

    public Situation(Point size) {
		this.carsPositions = new Vector<>();
		this.carsOrientations = new Vector<>();
		this.size = size;
		parking = new int[size.y][];
		for(int i = 0; i < size.y; ++i)
			parking[i] = new int[size.x];
    }

	public int addCar(List<Point> positions) throws IllegalArgumentException {
		//Verify that there is not another car at specified positions
		//and that positions are adjacent.
		if(positions.size() <= 1)
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
		//All is fine, we can add the car
		int newCar = carsPositions.size()+1;
		carsPositions.add(positions.get(0));
		carsOrientations.add(positions.get(0).x - positions.get(1).x == 0 ? Orientation.Vertical : Orientation.Horizontal);
		for(Point pos : positions)
			parking[pos.y][pos.x] = newCar;
		return newCar;
	}
	
	public List<Point> getCarPositions(int car) throws IndexOutOfBoundsException {
		if(car > carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist");
		Point pos = carsPositions.get(car-1);
		Orientation orientation = carsOrientations.get(car-1);
		Vector<Point> ret = new Vector<>();
		if(orientation == Orientation.Vertical) {
			int y = pos.y;
			while(isInParking(pos.x, y) && getCar(pos.x, y) == car) {
				ret.add(new Point(pos.x, y));
				++y;
			}
			y = pos.y - 1;
			while(isInParking(pos.x, y) && getCar(pos.x, y) == car) {
				ret.add(0, new Point(pos.x, y));
				--y;
			}
		} else {
			int x = pos.x;
			while(isInParking(x, pos.y) && getCar(x, pos.y) == car) {
				ret.add(new Point(x, pos.y));
				++x;
			}
			x = pos.x-1;
			while(isInParking(x, pos.y) && getCar(x, pos.y) == car) {
				ret.add(0, new Point(x, pos.y)) ;
				--x;
			}
		}
		return ret;
	}

	public List<Movement> getPossibleMovements(int car) throws IndexOutOfBoundsException {
		if(car > carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
		Vector<Movement> result = new Vector<>();
		List<Point> pos = getCarPositions(car);
		Point previousCell = new Point(pos.get(0)),
		      nextCell = new Point(pos.get(pos.size()-1));
		if(carsOrientations.get(car-1) == Orientation.Horizontal) {
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
		if(car > carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
		if(!getPossibleMovements(car).contains(movement))
			throw new IllegalArgumentException("Movement not supported by specified car.");
		else {
			final int dx = this.movementComposition[movement.getValue()].x;
			final int dy = this.movementComposition[movement.getValue()].y;
			final List<Point> carPositions = getCarPositions(car);
			for(Point carPosition : carPositions)
				parking[carPosition.y][carPosition.x] = emptyCell;
			for(Point carPosition : carPositions)
				parking[carPosition.y + dy][carPosition.x + dx] = car;
		}
	}

	public int getCar(Point position) {
		return getCar(position.x, position.y);
	}

	public int getCar(int x, int y) {
		return parking[y][x];
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
				result = prime * result + parking[i][j];
		return result;
	}

	private boolean isCellEmpty(Point cell) {
		return isCellEmpty(cell.x, cell.y);
	}

	private boolean isCellEmpty(int x, int y) {
		return getCar(x, y) == emptyCell;
	}

	private boolean isInParking(Point cell) {
		return isInParking(cell.x, cell.y);
	}

	private boolean isInParking(int x, int y) {
		return 0 <= x && x < this.size.x && 0 <= y && y < this.size.y;
	}

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
}

