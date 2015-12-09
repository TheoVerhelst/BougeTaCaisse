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
			int nbGoals = Integer.parseInt(content.get(2*y + 3).split(": ")[1]);
			int nbCars = Integer.parseInt(content.get(2*y + 4).split(": ")[1]);
			for(int i = 2*y + 6; i < content.size(); ++i)
				if(content.get(i).contains("Goal"))
					ret.setGoalPositions(parseListPoint(content.get(i)));
				else
					ret.addCar(parseListPoint(content.get(i)));

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
