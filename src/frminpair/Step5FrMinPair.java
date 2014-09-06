package frminpair;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Step5FrMinPair {
	public static void main(String[] args)throws Exception{
		
		FileWriter fw = new FileWriter("minPairs.txt");
		
		FileReader fr = new FileReader("accepted-phonemes.txt");

		//number of pronunciation chars -> pronunciation -> words with that pronunciation
		Map<Integer,Map<String,Set<String>>> words = new HashMap<Integer, Map<String, Set<String>>>(); 

		List<String> csvInput;
		while((csvInput=CSVUtils.parseLine(fr))!=null){
			String word = csvInput.get(0);
			String pron = csvInput.get(1);
			
			Map<String, Set<String>> mapByPronunciationSize = words.get(pron.length());
			if(mapByPronunciationSize==null){
				mapByPronunciationSize=new HashMap<String, Set<String>>();
				words.put(pron.length(), mapByPronunciationSize);
			}
			
			Set<String> wordsWithSamePronunciation = mapByPronunciationSize.get(pron);
			if(wordsWithSamePronunciation==null){
				wordsWithSamePronunciation = new HashSet<String>(1);
				mapByPronunciationSize.put(pron, wordsWithSamePronunciation);
			}
			
			wordsWithSamePronunciation.add(word);
		}
		fr.close();
		
		for(Integer pronunciationSize:words.keySet()){
			for(Entry<String, Set<String>> pronunciationAndWords:words.get(pronunciationSize).entrySet()){
				addWordsIfDistanceOne(pronunciationAndWords, words.get(pronunciationSize), fw);
				
				//remove for hamming distance
				addWordsIfDistanceOne(pronunciationAndWords, words.get(pronunciationSize+1), fw);
				addWordsIfDistanceOne(pronunciationAndWords, words.get(pronunciationSize-1), fw);
			}
			System.out.println("Done for pronunciationSize "+pronunciationSize);
		}
		
		fw.close();
		
	}

	
	
	private static void addWordsIfDistanceOne(
			Entry<String, Set<String>> pronunciationAndWords,
			Map<String, Set<String>> map, FileWriter fw) throws Exception {
		if(map==null){
			return;
		}
		String pronunciation = pronunciationAndWords.getKey();
		for(Entry<String, Set<String>> otherPronunciationAndWords:map.entrySet()){
			String otherPronunciation = otherPronunciationAndWords.getKey();
			if(pronunciation.compareTo(otherPronunciation)>=0){
				//avoid comparing a pair twice
				continue;
			}
			if(hasOneDistance(pronunciation, otherPronunciation)){
				String added = pronunciation+"/"+otherPronunciation+":"+pronunciationAndWords.getValue()+"/"+otherPronunciationAndWords.getValue();
				fw.write(added+"\r\n");
			}
		}
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
	
	//use for hamming distance
//		private static String nasalisedInOneCharacter(String pronunciation){
//			return pronunciation.replaceAll("A~", "亜").replaceAll("O~", "男").replaceAll("9~", "九").replaceAll("E~", "塀");
//		}
//		private static String restoreNasalized(String pronunciation){
//			return pronunciation.replaceAll("亜","A~").replaceAll("男","O~").replaceAll("九","9~").replaceAll("塀","E~");
//		}
	
//	private static boolean hasOneHammingDistance(String pronunciation,
//			String otherPronunciation) {
//		if(pronunciation.length()!=otherPronunciation.length()){
//			return false;
//		}
//		int dist = 0;
//		for(int i=0;i<pronunciation.length();i++){
//			if(pronunciation.charAt(i)!=otherPronunciation.charAt(i)){
//				dist++;
//			}
//			if(dist>1){
//				return false;
//			}
//		}
//		return dist==1;
//	}

}
