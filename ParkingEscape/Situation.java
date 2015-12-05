package ParkingEscape;

import java.awt.Point;

public class Situation {
	public enum Movement {
		Up, Down, Left, Right
	}

	private final Point size;
	private int[][] parking;
	List<Point> carsPositions;

    public Situation(Point size) {
		this.size = size;
		parking = new int[size.x][];
		for(int i = 0; i < size.x; ++i)
			parking[i] = new int[size.y];
    }

	public void addCar(List<Point> positions) {
		if(positions.size() == 0)
			return;
		//Verify that there is not another car at specified positions
		//and that positions are adjacents.
		for(int i = 0; i < positions.size(); ++i) {
			Point pos = positions.get(i);
			if(getCar(pos) >= 0)
				throw IllegalArgumentException("There arleady is a car at specified position.");
			if(i > 0) {
				Point difference = positions.get(i - 1);
				difference.translate(pos);
				if(length(difference) > 1.)
					throw IllegalArgumentException("Points specified for adding a car are not adjacents.");
			}
		}
		//All is fine, we can add the car
		int newCar = carsPositions.size();
		carsPositions.add(positions[0]);
		for(Point pos : positions) {
			parking[pos.x][pos.y] = newCar;
		}
	}


	public List<Movement> getPossibleMovements(int car) {
		return {Up};
	}

	public void moveCar(int car, Movement movement) {
		if(car >= numberOfCars)
			throw IndexOutOfBoundsException("The specified car does not exists.");
		if(!getPossibleMovements(car).contains(movement))
			throw IllegalArgumentException("Movement not supported by specified car.");
		else {
			//Do movement
		}
	}

	public int getCar(Point position) {
		return getCar(position.x, position.y);
	}

	public int getCar(int x, int y) {
		return parking[x][y];
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if(other == this)
			result = true;
		else if(other != null && other instanceof Situation) {
			result = true;
			Situation otherSituation = (Situation) other;
			for(int i = 0; i < size.x; ++i)
				for(int j = 0; j < size.y; ++j)
					result = result && (getCar(i, j) == otherSituation.getCar(i, j));
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + super.hashCode();
		for(int i = 0; i < size.x; ++i)
			for(int j = 0; j < size.y; ++j)
				result = prime * result + parking[i][j];
		return result;
	}

	private static float getLength(Point p) {
		return hypot((double) p.x, (double) p.y);
	}
}

