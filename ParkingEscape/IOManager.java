package ParkingEscape;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.text.ParseException;

public class IOManager {
	public static Graph createGraph(String path) throws ParseException, FileNotFoundException, IOException {
		List<String> content;
		Graph ret;
		Situation initialSituation;
		try {
			content = readFile(path);
			String dims[] = content.get(0).split(": ")[1].split(" fois ");
			int x = Integer.parseInt(dims[0]);
			int y = Integer.parseInt(dims[1]);
			initialSituation = new Situation(x, y);
			int nbGoals = Integer.parseInt(content.get(2*y + 3).split(": ")[1]);
			int nbCars = Integer.parseInt(content.get(2*y + 4).split(": ")[1]);
			for(int i = 2*y + 6; i < content.size(); ++i)
			{
				if(content.get(0).contains("Goal")) {
					int goalCar = initialSituation.getGoalCar();
				}
			}
			ret = new Graph(initialSituation);

		} catch(IndexOutOfBoundsException e) {
			throw new ParseException("Unable to parse correctly: incorrect file format.", 0);
		}
		return ret;
    }

	public static List<String> readFile(String path) throws FileNotFoundException, IOException {
		String line;
		List<String> content = new Vector<>();
		InputStream iStream = new FileInputStream(path);
		BufferedReader buff = new BufferedReader(new InputStreamReader(iStream));
		while((line = buff.readLine()) != null)
			content.add(line);
		buff.close();
		return content;
	}
}
