package ParkingEscape;

import java.io.*;

public class IOManager {
	public static Graph createGraph(String path) {
		Graph ret = new Graph();
		String content = readFile(path);
		System.out.println(content);
		return ret;
    }

	private static String readFile(String path) {
		String line = "", content = "";
		try {
			InputStream iStream = new FileInputStream(path);
			BufferedReader buff = new BufferedReader(new InputStreamReader(iStream));
			while((line = buff.readLine()) != null)
				content += line + "\n";
			buff.close();
		} catch(Exception e) {
			System.out.println("error: " + e);
		}
		return content;
	}
}
