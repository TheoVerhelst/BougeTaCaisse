package ParkingEscape;

public class Situation {
	private final Vector2<Integer> size;
	private int[][] parking;

    public Situation(Vector2<Integer> size) {
		this.size = size;
		parking = new int[size.x][];
		for(int i = 0; i < size.x; ++i)
			parking[i] = new int[size.y];
    }

	public void setCar(Vector2<Integer> position, int car) {
		setCar(position.x, position.y, car);
	}

	public void setCar(int x, int y, int car) {
		parking[x][y] = car;
	}

	public int getCar(Vector2<Integer> position) {
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
}
