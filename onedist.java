import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class FrMinPair {
	public static void main(String[] args)throws Exception{
		
		FileWriter fw = new FileWriter("minPairs.txt");
		
		BufferedReader br = new BufferedReader(new FileReader("word-phoneme-mapping.txt"));
		String currLine;

		List<WordPron> words = new ArrayList<WordPron>(); 

		while((currLine=br.readLine())!=null){
			String[] parts = currLine.split(" ");
			WordPron newWord  = new WordPron();
			newWord.word = parts[0];
			if(parts.length<2){
				continue;
			}
			newWord.pron = parts[1];
			words.add(newWord);
		}
		br.close();
		
		for(int i=0;i<words.size();i++){
			String pron1 = words.get(i).pron;
			for(int j=i+1;j<words.size();j++){
				if(hasOneDistance(pron1, words.get(j).pron)){
					String added = words.get(i).word+"["+words.get(i).pron+"] / "+words.get(j).word+"["+words.get(j).pron+"]";
					fw.write(added+"\r\n");
//					System.out.println(added);
					
				}
			}
			if(i%1000==1){
				System.out.println(i+"/"+words.size());
				fw.flush();
			}
		}

		fw.close();
		
	}
	public static class WordPron{
		String word;
		String pron;
	}                                             
	private static int minimum(int a, int b, int c) {                            
		return Math.min(Math.min(a, b), c);                                      
	}                                                                            

	private static int[][] distance = new int[1000][1000];
	public static boolean hasOneDistance(String str1,String str2) {
		if(Math.abs(str1.length()-str2.length())>1){
			return false;
		}


		for (int i = 0; i <= str1.length(); i++)                                 
			distance[i][0] = i;                                                  
		for (int j = 1; j <= str2.length(); j++)                                 
			distance[0][j] = j;                                                  



		for (int i = 1; i <= str1.length(); i++){
			int localMin = Integer.MAX_VALUE;
			for (int j = 1; j <= str2.length(); j++)
			{
				distance[i][j] = minimum(                                        
						distance[i - 1][j] + 1,                                  
						distance[i][j - 1] + 1,                                  
						distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
				localMin = Math.min(localMin, distance[i][j]);
			}
			if(localMin>1){
				return false;
			}
		}

		return distance[str1.length()][str2.length()]==1;                           
	}                                                                            

}
