package frminpair;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

public class Step3FilterValidPronunciation {
	
	public static void main(String[] args) throws Exception{
		FileWriter acceptedOutput = new FileWriter("accepted-phonemes.txt");
		FileWriter rejectedOutput = new FileWriter("rejected-phonemes.txt");
		
		//g phoneme is sometimes written with another char
		FrenchPronunciationRules.IPAToXSampaRule gCompatibility = new FrenchPronunciationRules.IPAToXSampaRule();
		gCompatibility.IPAString = "ɡ";
		gCompatibility.XSampaString = "g";
		FrenchPronunciationRules.IPAToXSampaRuleset.add(gCompatibility);
				
		
		FileReader fr = new FileReader("wiktionary-phonemes.txt");
		List<String> wordAndPronunciation;
		int wordCount = 0;
		while((wordAndPronunciation=CSVUtils.parseLine(fr))!=null){
			
			wordCount++;
			if(wordCount%10000==0){
				acceptedOutput.flush();
				rejectedOutput.flush();
				System.out.println(wordCount+" words");
			}
			
			
			
			String word = wordAndPronunciation.get(0);
			String pronunciationIPA = wordAndPronunciation.get(1);
			
			//System.out.println(word);
			
			String pronunciationXSampa = FrenchPronunciationRules.IPAToXSampaOrNull(pronunciationIPA);
			if(pronunciationXSampa==null){
				rejectedOutput.write("contains characters not in French IPA,"+word+","+pronunciationIPA+"\r\n");
				continue;
			}
			if(!FrenchPronunciationRules.canBeProducedByFrenchPronunciationRules(word.toLowerCase(Locale.FRENCH), pronunciationXSampa)){
				rejectedOutput.write("cannot be produced by given pronunciation rules,"+word+","+pronunciationXSampa+","+pronunciationIPA+"\r\n");
				continue;
			}
			
			acceptedOutput.write(CSVUtils.csvEscape(word)+","+CSVUtils.csvEscape(pronunciationXSampa)+","+CSVUtils.csvEscape(pronunciationIPA)+"\r\n");
		}
		
		acceptedOutput.flush();
		acceptedOutput.close();
		rejectedOutput.flush();
		rejectedOutput.close();
	}
	
}
