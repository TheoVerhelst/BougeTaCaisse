package ParkingEscape;

import java.io.*;

public class IOManager {
	public static Graph createGraph(String path) throws IOException {
		Graph ret = new Graph();
		String content;
		try {
			content = readFile(path);
			System.out.println(content);
		} catch(FileNotFoundException e) {
			System.out.println("parameter file does not exist");
		}
		return ret;
    }

	private static String readFile(String path) throws FileNotFoundException, IOException {
		String line = "", content = "";
		InputStream iStream = new FileInputStream(path);
		BufferedReader buff = new BufferedReader(new InputStreamReader(iStream));
		while((line = buff.readLine()) != null)
			content += line + "\n";
		buff.close();
		return content;
	}
}
