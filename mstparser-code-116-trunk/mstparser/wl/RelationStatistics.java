package wl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class RelationStatistics {
	
	public static HashMap<String, Integer> map = new HashMap<String, Integer>();

	/**
	 * @param args should be a list of .lab or .ulab files
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
//		boolean label = true;
		
		for(int i=0; i<args.length; i++){
			File file = new File(args[i].trim());
//			check if file name is valid
			if(file.isDirectory() == true 
					|| file.exists()==false
					|| args[i].endsWith(".lab")==false ){
				System.err.println("Invalid file path: "+args[i]);
				continue;
			}			
			
			FileWriter writer = new FileWriter(new File(args[i]+".sta"));
			Scanner cin = new Scanner(file);
			cin.useDelimiter("\\n");
			map.clear();
			while(cin.hasNextLine()){
				cin.nextLine(); cin.nextLine();
				String[] relations = cin.nextLine().trim().split("\\s++");
				cin.nextLine();
				cin.nextLine();
				for(int j=0; j<relations.length; j++){
					if(map.containsKey(relations[j])){
						int old = map.get(relations[j]);
						map.put(relations[j], old+1);
					}
					else{
						map.put(relations[j], 1);
					}
				}// end for loop
			}// end while loop
			
//			output results
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()){
				String current = it.next();
				writer.write(current+" "+map.get(current)+"\n");
			}
			
			writer.flush();
		}// end for loop
		
	}// end method main

}// end class RelationStatistics
