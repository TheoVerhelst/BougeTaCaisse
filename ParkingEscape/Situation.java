package ParkingEscape;

import java.awt.Point;
import java.util.Vector;
import java.util.List;
import java.lang.IllegalArgumentException;

public class Situation {
	public enum Movement {
		Up, Down, Left, Right
	}

	private enum Orientation {
		Vertical, Horizontal
	}

	private final Point size;
	private int[][] parking;
	List<Point> carsPositions;
	List<Orientation> carsOrientations;

    public Situation(Point size) {
		this.carsPositions = new Vector<>();
		this.carsOrientations = new Vector<>();
		this.size = size;
		parking = new int[size.y][];
		for(int i = 0; i < size.y; ++i)
			parking[i] = new int[size.x];
    }

	public void addCar(List<Point> positions) throws IllegalArgumentException {
		if(positions.size() == 0)
			return;
		//Verify that there is not another car at specified positions
		//and that positions are adjacent.
		if(positions.size() == 1)
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
		int newCar = carsPositions.size();
		carsPositions.add(positions.get(0));
		carsOrientations.add(positions.get(0).x - positions.get(1).x == 0 ? Orientation.Horizontal : Orientation.Vertical);
		for(Point pos : positions)
			parking[pos.y][pos.x] = newCar;
	}
	
	public List<Point> getCarPosition(int car) throws IndexOutOfBoundsException {
		if(car >= carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist");
		Point pos = carsPositions.get(car);
		Orientation orientation = carsOrientations.get(car);
		Vector<Point> ret = new Vector<>();
		if(orientation == Orientation.Horizontal) {
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

	public List<Movement> getPossibleMovements(int car) throws IndexOutOfBoundsException{
		if(car >= carsPositions.size())
			throw new IndexOutOfBoundsException("The specified car does not exist.");
		Vector<Movement> result = new Vector<>();
		List<Point> pos = getCarPosition(car);
		Orientation orientation = carsOrientations.get(car);
		Point previousCell = new Point(pos.get(0));
		Point nextCell = new Point(pos.get(pos.size()-1));
		if(orientation == Orientation.Horizontal) {
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
			//Do movement
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
		return getCar(cell) == 0;
	}

	private boolean isInParking(Point cell) {
		return isInParking(cell.x, cell.y);
	}

	private boolean isInParking(int x, int y) {
		return 0 <= x && x < this.size.x && 0 <= y && y < this.size.y;
	}
}

