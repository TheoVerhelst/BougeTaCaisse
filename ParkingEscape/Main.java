package ParkingEscape;

import java.awt.Point;
import java.util.Vector;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Main {
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("usage : java Main file");
		} else {
			// testIO(args[0]);
			try {
				Graph g = IOManager.createGraph(args[0]);
			} catch(IOException e) {
				System.out.println("exception : " + e);
			}
		}
	}

	private static void testIO(String path) {
		List<String> fileContent;
		try {
			fileContent = IOManager.readFile(path);
			System.out.println(fileContent);
		} catch(FileNotFoundException e) {
			System.out.println("given file does not exist");
		} catch(IOException e) {
			System.out.println("Error while using file");
		}
	}

	private static void testSituation() {
		Situation s = new Situation(new Point(5, 5));
		Vector<Point> car = new Vector<>();
		car.add(new Point(1, 3));
		car.add(new Point(2, 3));
		car.add(new Point(3, 3));
		int carId = s.addCar(car);
		System.out.println("Car added, Parking = \n" + s.toString());

		List<Situation.Movement> mvs = s.getPossibleMovements(carId);
		System.out.println("[Left, Right] = " + mvs.toString());
		Vector<Point> otherCar = new Vector<>();
		otherCar.add(new Point(4, 3));
		otherCar.add(new Point(4, 2));
		int otherCarId = s.addCar(otherCar);
		System.out.println("Other car added, Parking =\n" + s.toString());
		List<Situation.Movement> otherMvs = s.getPossibleMovements(otherCarId);
		System.out.println("[Up, Down] = " + otherMvs.toString());
		mvs = s.getPossibleMovements(carId);
		System.out.println("[Left] = " + mvs.toString());
		for(Situation.Movement m : Situation.Movement.values()) {
			System.out.println("Moving " + m.name());
			try {
				s.moveCar(carId, m);
				System.out.println("\t\tSuccessful, Parking =\n" + s.toString());
			} catch (Exception e) {
				System.out.println("\t\tFailed");
			}
		}
	}
}
