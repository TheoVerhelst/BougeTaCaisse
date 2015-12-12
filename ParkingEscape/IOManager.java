package ParkingEscape;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Point;
import java.text.ParseException;

public class IOManager {
	public static Situation createSituation(String path) throws ParseException, FileNotFoundException, IOException {
		List<String> content;
		Situation ret;
		try {
			content = readFile(path);
			String dims[] = content.get(0).split(": ")[1].split(" fois ");
			final int x = Integer.parseInt(dims[0]);
			final int y = Integer.parseInt(dims[1]);
			ret = new Situation(x, y);
			for(int i = 2*y + 6; i < content.size(); ++i) {
				if(content.get(i).contains("Goal"))
					ret.setGoalPositions(parseListPoint(content.get(i)));
				else
					ret.addCar(parseListPoint(content.get(i)));
			}
			//TODO Je mettrais plutot une ParseException ici, comme pour l'exit
			assert ret.getCarPositions(Situation.getGoalCar()).size() != 0 : "No goal car found in Parking";
			if(ret.getCarOrientation(Situation.getGoalCar()) == Situation.Orientation.Horizontal) {
				final int GoalY = ret.getCarPositions(Situation.getGoalCar()).get(0).y;
				String line = content.get(2*(1+GoalY));
				if(line.charAt(0) == ' ')
					Situation.setExit(0, GoalY);
				else if(line.charAt(line.length()-1) == ' ')
					Situation.setExit(x-1, GoalY);
				else
					throw new ParseException("No valid exit was found.", 0);
			} else {
				int GoalX = ret.getCarPositions(Situation.getGoalCar()).get(0).x;
				if(content.get(1).substring(1 + 4*GoalX, 4*(GoalX+1)) == "   ")
					Situation.setExit(GoalX, 0);
				else if(content.get(1+2*y).substring(1 + 4*GoalX, 4*(GoalX+1)) == "   ")
					Situation.setExit(GoalX, y-1);
				else
					throw new ParseException("No valid exit was found.", 0);
			}
		} catch(IndexOutOfBoundsException e) {
			throw new ParseException("Unable to parse correctly: incorrect file format.", 0);
		}
		return ret;
    }

	public static void writeSolution(Graph.Solution solution, String outputFile1, String outputFile2) {
		System.out.println("Situation finale: \n" + solution.finalSituation);
		System.out.println("Une façon de sortir du parking en " + solution.moves.size() + " mouvements a été trouvée.\n");
		for(Map.Entry<Integer, List<List<Point>>> moves : solution.moves.entrySet()) {
			System.out.println("Déplacements car" + moves.getKey() + ":");
			for(int i = 0; i < moves.getValue().size(); ++i) {
				System.out.print(listAsString(moves.getValue().get(i)));
				if(i < moves.getValue().size() - 1)
					System.out.print(" -> ");
			}
		}
	}

	public static List<String> readFile(String path) throws FileNotFoundException, IOException {
		String line;
		List<String> content = new ArrayList<>();
		InputStream iStream = new FileInputStream(path);
		BufferedReader buff = new BufferedReader(new InputStreamReader(iStream));
		while((line = buff.readLine()) != null)
			content.add(line);
		buff.close();
		return content;
	}

	private static List<Point> parseListPoint(String listAsString) throws ParseException {
		List<Point> ret = new ArrayList<>();
		//Get only the bracket-list after the semicolon
		listAsString = listAsString.split(":")[1].trim();
		Pattern tuplesPattern = Pattern.compile("\\[\\((\\d+),\\s*(\\d+)\\)\\s*(?:,\\s*\\((\\d+),\\s*(\\d+)\\)\\s*)*\\]");
		Matcher matches = tuplesPattern.matcher(listAsString);
		if(!matches.matches() || matches.groupCount() % 2 != 0)
			throw new ParseException("List of positions is badly written.", 0);
		//The groupCount does not contains matches.group(0), which is the entire match, so i must start at 1
		for(int i = 1; i < matches.groupCount() + 1; i += 2)
			ret.add(new Point(Integer.parseInt(matches.group(i + 1)), Integer.parseInt(matches.group(i))));
		return ret;
	}

	private static String listAsString(List<Point> points) {
		String ret = "[";
		for(int i = 0; i < points.size(); ++i) {
			ret += "(" + points.get(i).x + ", " + points.get(i).y + ")";
			if(i < points.size() - 1)
				ret += ", ";
		}
		ret += "]";
		return ret;
	}
}
