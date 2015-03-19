package mstparser;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;


/**
 * 
 * @author Liang Wang
 * @since 2013/9/23
 * @version 2013/9/23
 * 
 * <p> this class is used to store features for every input file,
 * features are stored in a 2D array, so we can access them quickly.</p>
 *
 */
public class iFeature {

	public Vector<String>[][] vector;
	public Vector<String>[] singleVector;
	
	/**
	 * an empty constructor
	 */
	public iFeature(){
		vector = null;
		singleVector = null;
	}// end empty constructor
	
	/**
	 * 
	 * @param file contains all features with suffix ".feat"
	 * @throws IOException 
	 */
	public iFeature(File file) throws IOException{
		if(file.exists()==false || file.isDirectory()){
			System.err.println("Invalid file name error: "+file.toString());			
		}
		
		Scanner reader = new Scanner(file);
		int N = Integer.parseInt(reader.nextLine().trim())+1;
		vector = new Vector[N][N];
		singleVector = new Vector[N];
		for(int i=0; i<N; i++){
			singleVector[i] = new Vector<String>();
			singleVector[i].clear();
			for(int j=0; j<N; j++){
				vector[i][j] = new Vector<String>();
				vector[i][j].clear();
			}
		}
		
		while(reader.hasNextLine()){
			String[] index = reader.nextLine().trim().split("\\s++");
			
//			check if there is any error
//			if(index.length != 2){
//				System.err.println("Invalid number of parameter in "+file.toString());
//				break;
//			}// end if
			int x = 0;
			int y = 0;
			if(index.length == 2){
				x = Integer.parseInt(index[0]);
				y = Integer.parseInt(index[1]);
				x++; y++;
			
//				add features into vector
				String[] features = reader.nextLine().trim().split("\\s++");
				vector[x][y].clear();
				for(String s : features)
					vector[x][y].add(s);
			}
//			add single features
			else if(index.length == 1){
				x = Integer.parseInt(index[0]);
				x++;
				String[] features = reader.nextLine().trim().split("\\s++");
				singleVector[x].clear();
				for(String s:features)
					singleVector[x].add(s);
			}
			else{
				System.err.println("Invlid number of parameter in "+file.toString());
				break;
			}
		}// end while loop
		
	}// end constructor
	
}// end class iFeature
