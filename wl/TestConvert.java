package wl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TestConvert {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner reader = new Scanner(new File("featuresReduced.log"));
		FileWriter writer = new FileWriter(new File("features"));
		while (reader.hasNextLine()) {
			String cur = reader.nextLine().trim();
			int pos = cur.indexOf("times:");
			if (pos < 0) {
				System.err.println("Error in: " + cur);
				continue;
			}
			writer.write(cur.substring(0, pos).trim()+"\n");			
		}
		writer.flush();
		System.out.println("Program exits normally.");
	}

}
