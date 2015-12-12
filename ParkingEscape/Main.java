package ParkingEscape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.ParseException;

public class Main {
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage : java ParkingEscape.Main FILE");
		} else {
			//testIO();
			//testSituation();
			try {
				new Graph(IOManager.createSituation(args[0])).solve();
			} catch(Exception e) {
				System.out.println("Exception when solving:");
				e.printStackTrace();
			}
		}
	}

	private static void testIO() {
		List<String> fileContent;
		try {
			String filePath = "Parking.txt";
			fileContent = IOManager.readFile(filePath);
			System.out.println("Raw file:");
			for(String line : fileContent)
				System.out.println(line);
			System.out.println("Resulting situation:\n" + IOManager.createSituation(filePath));
		} catch(FileNotFoundException e) {
			System.out.println("Given file does not exist: " + e);
		} catch(IOException e) {
			System.out.println("Error while using file: " + e);
		} catch(ParseException e) {
			System.out.println("Error while parsing file: " + e);
		}
	}

	private static void testSituation() {
		Situation s = new Situation(new Point(5, 5));
		ArrayList<Point> car = new ArrayList<>();
		car.add(new Point(1, 3));
		car.add(new Point(2, 3));
		car.add(new Point(3, 3));
		int carId = s.addCar(car);
		System.out.println("addCar: " + car + ", Parking = \n" + s);
		List<Situation.Movement> mvs = s.getPossibleMovements(carId);
		System.out.println("getPossibleMovements: [Left, Right] = " + mvs.toString());
		ArrayList<Point> otherCar = new ArrayList<>();
		otherCar.add(new Point(4, 3));
		otherCar.add(new Point(4, 2));
		int otherCarId = s.addCar(otherCar);
		System.out.println("addCar: " + otherCar + ", Parking =\n" + s);
		List<Situation.Movement> otherMvs = s.getPossibleMovements(otherCarId);
		System.out.println("getPossibleMovements: [Up, Down] = " + otherMvs.toString());
		mvs = s.getPossibleMovements(carId);
		System.out.println("getPossibleMovements: [Left] = " + mvs.toString());
		for(Situation.Movement m : Situation.Movement.values()) {
			System.out.println("moveCar: " + m.name());
			try {
				s.moveCar(carId, m);
				System.out.println("\tSuccessful.");
			} catch(Exception e) {
				System.out.println("\tFailed.");
			}
		}
		System.out.println("It should have shown:");
		System.out.println("\tFailed for Up and Down, and Successful for Left and Right");
		try {
			System.out.println("moveCar: Right");
			s.moveCar(carId, Situation.Movement.Right);
			System.out.println("It should not succeed.");
		} catch(Exception e) {
			System.out.println("\tFailed, as it should be.");
		}
		System.out.println("Parking =\n" + s);
	}
}
