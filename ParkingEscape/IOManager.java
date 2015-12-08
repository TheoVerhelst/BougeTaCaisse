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
			System.out.println(content);
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
			content.add(line + "\n");
		buff.close();
		return content;
	}
}
