package ParkingEscape;

import java.io.*;
import java.util.List;
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
			int x = Integer.parseInt(dims[0]);
			int y = Integer.parseInt(dims[1]);
			ret = new Situation(x, y);
			for(int i = 2*y + 6; i < content.size(); ++i) {
				if(content.get(i).contains("Goal"))
					ret.setGoalPositions(parseListPoint(content.get(i)));
				else
					ret.addCar(parseListPoint(content.get(i)));
			}
			assert ret.getCarPositions(ret.getGoalCar()).size() != 0 : "No goal car found in Parking";
			if(ret.getCarOrientation(ret.getGoalCar()) == Situation.Orientation.Horizontal) {
				int GoalY = ret.getCarPositions(ret.getGoalCar()).get(0).y;
				String line = content.get(2*(1+GoalY));
				if(line.charAt(0) == ' ')
					ret.setExit(0, GoalY);
				else if(line.charAt(line.length()-1) == ' ')
					ret.setExit(x-1, GoalY);
				else
					throw new ParseException("No valid exit was found.", 0);
			} else {
				int GoalX = ret.getCarPositions(ret.getGoalCar()).get(0).x;
				if(content.get(1).substring(1 + 4*GoalX, 4*(GoalX+1)) == "   ")
					ret.setExit(GoalX, 0);
				else if(content.get(1+2*y).substring(1 + 4*GoalX, 4*(GoalX+1)) == "   ")
					ret.setExit(GoalX, y-1);
				else
					throw new ParseException("No valid exit was found.", 0);
			}
		} catch(IndexOutOfBoundsException e) {
			throw new ParseException("Unable to parse correctly: incorrect file format.", 0);
		}
		return ret;
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
}
