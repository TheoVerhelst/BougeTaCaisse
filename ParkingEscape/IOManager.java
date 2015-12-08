package ParkingEscape;

import java.io.*;
import java.util.List;
import java.util.Vector;

public class IOManager {
	public static Graph createGraph(String path) throws IOException {
		Graph ret = new Graph();
		List<String> content;
		try {
			content = readFile(path);
			String dims[] = content.get(0).split(": ")[1].split(" fois ");
			int x = Integer.parseInt(dims[0]);
			int y = Integer.parseInt(dims[1]);
			int nbGoals = Integer.parseInt(content.get(2*y + 3).split(": ")[1]);
			int nbCars = Integer.parseInt(content.get(2*y + 4).split(": ")[1]);
		} catch(FileNotFoundException e) {
			System.out.println("parameter file does not exist");
		}
		return ret;
    }

	public static List<String> readFile(String path) throws FileNotFoundException, IOException {
		String line = "";
		List<String> content = new Vector<>();
		InputStream iStream = new FileInputStream(path);
		BufferedReader buff = new BufferedReader(new InputStreamReader(iStream));
		while((line = buff.readLine()) != null)
			content.add(line);
		buff.close();
		return content;
	}
}
