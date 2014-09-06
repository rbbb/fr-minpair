package frminpair;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Step2ExtractWiktionary {

	static FileWriter output;
	static Set<String> knownPronunciations;

	static String asciiCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqrstuvwxyz";
	static String nonasciiFrenchCharacters = "ÆŒÀÂÄÇÉÈÊËÎÏÔÖÙÛÜæœàâäçéèêëîïôöùûü";
	static String nonasciiFrenchNameCharacters = "ÿŸ";
	static String ponctuationFrenchCharacters = "’\\- \\.";
	static String frenchCharacters = asciiCharacters+nonasciiFrenchCharacters+nonasciiFrenchNameCharacters+ponctuationFrenchCharacters;
	static Pattern pronunciationPattern = Pattern.compile("'''(["+frenchCharacters+"]+)'''.*\\{\\{pron\\|([^}]+)\\}\\}");
	static Pattern pronunciationWithAudioPattern = Pattern.compile("'''(["+frenchCharacters+"]+)'''.*\\{\\{pron\\|([^}]+)\\}\\}");

	public static void main(String[] args) throws Exception
	{
		knownPronunciations = new HashSet<String>();
		output = new FileWriter("wiktionary-phonemes.txt");
		importWikipedia();
		output.flush();
		output.close();
	}

	public static void importWikipedia() throws Exception
	{
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			SectionHandler handler = new SectionHandler();
			saxParser.parse("/home/xnote/zevoice/frwiktionary-20131222-pages-articles.xml", handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private static final class SectionHandler extends DefaultHandler {

		boolean inText = false;
		String lastString;
		StringBuilder textBuilder = null;
		String title;

		public void startElement(String uri, String localName,String qName, 
				Attributes attributes) throws SAXException {
			if(qName.equalsIgnoreCase("text"))
			{
				inText = true;
				textBuilder = new StringBuilder();
			}
		}

		int numText = 0;

		public void endElement(String uri, String localName,
				String qName) throws SAXException {

			try{
				//keep article title
				if(qName.equalsIgnoreCase("title"))
				{
					title = lastString;
				}

				//do something with text in article
				if(qName.equalsIgnoreCase("text"))
				{
					
					numText++;

					inText = false;
					String text = textBuilder.toString();
					
					if(text.contains("{{pron")){
						int a;
						a = 19;
					}

					//http://fr.wiktionary.org/wiki/Mod%C3%A8le:pron
					Matcher pronunciationMatcher = pronunciationPattern.matcher(text);
					while(pronunciationMatcher.find()){

						String word = pronunciationMatcher.group(1);

						String pronunciationBlob = pronunciationMatcher.group(2);
						String[] pronunciationArguments = pronunciationBlob.replaceAll("[\\r\\n]+", "").split("\\|");
						int argNumber = 0;
						String pronunciationString = null;
						String languageString = null;
						for(int i=0;i<pronunciationArguments.length;i++){
							if(pronunciationArguments[i].startsWith("1=")){
								pronunciationString = pronunciationArguments[i].substring(2);
								continue;
							}else if(pronunciationArguments[i].startsWith("2=")){
								languageString = pronunciationArguments[i].substring(2);
								continue;
							}else if(pronunciationArguments[i].contains("=")){
								//skip named arguments
								continue;
							}
							argNumber++;
							if(argNumber==1){
								pronunciationString = pronunciationArguments[i];
							}else if(argNumber==2){
								languageString = pronunciationArguments[i];
							}
						}
						
						if(pronunciationString==null){
							continue;
						}

						if((languageString==null || languageString.equals("fr")) && pronunciationString.trim().length()>0){
							String addedLine = CSVUtils.csvEscape(word.trim())+","+CSVUtils.csvEscape(pronunciationString.trim())+"\r\n";
							if(!knownPronunciations.contains(addedLine)){
								output.write(addedLine);
								knownPronunciations.add(addedLine);
							}
						}
					}


					//http://fr.wiktionary.org/wiki/Mod%C3%A8le:pron-r%C3%A9g
					Matcher pronunciationWithAudioMatcher = pronunciationWithAudioPattern.matcher(text);
					while(pronunciationWithAudioMatcher.find()){
						String word = pronunciationWithAudioMatcher.group(1);

						String pronunciationBlob = pronunciationWithAudioMatcher.group(2);
						String[] pronunciationArguments = pronunciationBlob.replaceAll("[\\r\\n]+", "").split("\\|");
						int argNumber = 0;
						String pronunciationString = null;
						String languageString = null;
						String regionNameString = null;
						for(int i=0;i<pronunciationArguments.length;i++){
							if(pronunciationArguments[i].startsWith("1=")){
								regionNameString = pronunciationArguments[i].substring("1=".length());
								continue;
							}else if(pronunciationArguments[i].startsWith("2=")){
								pronunciationString = pronunciationArguments[i].substring("2=".length());
								continue;
							}else if(pronunciationArguments[i].startsWith("lang=")){
								languageString = pronunciationArguments[i].substring("lang=".length());
								continue;
							}else if(pronunciationArguments[i].contains("=")){
								//skip named arguments
								continue;
							}
							argNumber++;
							if(argNumber==1){
								regionNameString = pronunciationArguments[i];
							}else if(argNumber==2){
								pronunciationString = pronunciationArguments[i];
							}
						}
						
						if(pronunciationString==null || regionNameString == null){
							continue;
						}

						if((languageString==null || languageString.equals("fr")) && regionNameString.toUpperCase().contains("FRANCE") && pronunciationString.trim().length()>0){
							String addedLine = CSVUtils.csvEscape(word.trim())+","+CSVUtils.csvEscape(pronunciationString.trim())+"\r\n";
							if(!knownPronunciations.contains(addedLine)){
								output.write(addedLine);
								knownPronunciations.add(addedLine);
							}
						}
					}

					if(numText%100==0)
					{
						System.out.println("artNum "+numText);
					}

					textBuilder = null;
				}
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}	
		public void characters(char ch[], int start, int length) throws SAXException {
			lastString = new String(ch, start, length);
			if(inText)
			{
				textBuilder.append(lastString);
			}
		}
	}

	



}