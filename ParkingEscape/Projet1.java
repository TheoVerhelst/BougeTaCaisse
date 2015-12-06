package ParkingEscape;

import java.awt.Point;
import java.util.Vector;
import java.util.List;

public class Projet1
{
	public static void main(String[] args)
	{
		if(args.length != 1) {
			System.out.println("usage : java Projet1 fichier");
		}
		else {
			test();
		}
	}

	private static void test() {
		Situation s = new Situation(new Point(5, 5));
		Vector<Point> car = new Vector<>();
		car.add(new Point(1, 3));
		car.add(new Point(2, 3));
		car.add(new Point(3, 3));
		s.addCar(car);
		System.out.println("Car added");
		int carId = s.getCar(car.get(0));
		List<Situation.Movement> mvs = s.getPossibleMovements(carId);
		for(Situation.Movement mv : mvs)
			System.out.println(mv.name());
		for(Situation.Movement m : Situation.Movement.values()) {
			System.out.println("Moving " + m.name());
			try {
				s.moveCar(carId, m);
			} catch (Exception e) {
				System.out.println("Failed");
			}
		}
	}
}
