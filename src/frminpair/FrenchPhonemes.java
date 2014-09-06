package frminpair;

import java.util.ArrayList;

public class FrenchPhonemes {
	public static ArrayList<XSampa.Consonant> consonants = new ArrayList<XSampa.Consonant>();
	public static ArrayList<XSampa.Vowel> vowels = new ArrayList<XSampa.Vowel>();
	static 
	{
		consonants.add(XSampa.findConsonantByIPA("m"));
		consonants.add(XSampa.findConsonantByIPA("n"));
		consonants.add(XSampa.findConsonantByIPA("p"));
		consonants.add(XSampa.findConsonantByIPA("b"));
		consonants.add(XSampa.findConsonantByIPA("f"));
		consonants.add(XSampa.findConsonantByIPA("v"));
		consonants.add(XSampa.findConsonantByIPA("t"));
		consonants.add(XSampa.findConsonantByIPA("d"));
		consonants.add(XSampa.findConsonantByIPA("s"));
		consonants.add(XSampa.findConsonantByIPA("z"));
		consonants.add(XSampa.findConsonantByIPA("l"));
		consonants.add(XSampa.findConsonantByIPA("ʃ"));
		consonants.add(XSampa.findConsonantByIPA("ʒ"));
		consonants.add(XSampa.findConsonantByIPA("ɲ"));
		consonants.add(XSampa.findConsonantByIPA("j"));
		consonants.add(XSampa.findConsonantByIPA("ɥ"));
		consonants.add(XSampa.findConsonantByIPA("k"));
		consonants.add(XSampa.findConsonantByIPA("g"));
		consonants.add(XSampa.findConsonantByIPA("w"));
		consonants.add(XSampa.findConsonantByIPA("ʁ"));
		
		//r guttural, real (h)orrorshow
		consonants.add(XSampa.findConsonantByIPA("x"));
		//campi(ng)
		consonants.add(XSampa.findConsonantByIPA("ŋ"));
		//le r allonge de courrait ???
		
		vowels.add(XSampa.findVowelByIPA("i"));
		vowels.add(XSampa.findVowelByIPA("y"));
		vowels.add(XSampa.findVowelByIPA("e"));
		vowels.add(XSampa.findVowelByIPA("ø"));
		vowels.add(XSampa.findVowelByIPA("ɛ"));
		vowels.add(XSampa.findVowelByIPA("œ"));
		vowels.add(XSampa.findVowelByIPA("ə"));
		vowels.add(XSampa.findVowelByIPA("u"));
		vowels.add(XSampa.findVowelByIPA("o"));
		vowels.add(XSampa.findVowelByIPA("ɔ"));
		vowels.add(XSampa.findVowelByIPA("a"));
		vowels.add(XSampa.findVowelByIPA("ɑ"));
		vowels.add(XSampa.findVowelByIPA("e"));
		
		
		XSampa.Vowel eNasalized = new XSampa.Vowel(XSampa.findVowelByIPA("ɛ"));
		eNasalized.setNasalized(true);
		eNasalized.setIpaCode("ɛ̃");
		eNasalized.setxSampaCode(eNasalized.getXSampaCode()+"~");
		eNasalized.preventFurtherModification();
		vowels.add(eNasalized);
		
		XSampa.Vowel euNasalized = new XSampa.Vowel(XSampa.findVowelByIPA("œ"));
		euNasalized.setNasalized(true);
		euNasalized.setIpaCode("œ̃");
		euNasalized.setxSampaCode(euNasalized.getXSampaCode()+"~");
		euNasalized.preventFurtherModification();
		vowels.add(euNasalized);
		
		XSampa.Vowel oNasalized = new XSampa.Vowel(XSampa.findVowelByIPA("ɔ"));
		oNasalized.setNasalized(true);
		oNasalized.setIpaCode("ɔ̃");
		oNasalized.setxSampaCode(oNasalized.getXSampaCode()+"~");
		oNasalized.preventFurtherModification();
		vowels.add(oNasalized);
		
		XSampa.Vowel aNasalized = new XSampa.Vowel(XSampa.findVowelByIPA("ɑ"));
		aNasalized.setNasalized(true);
		aNasalized.setIpaCode("ɑ̃");
		aNasalized.setxSampaCode(aNasalized.getXSampaCode()+"~");
		aNasalized.preventFurtherModification();
		vowels.add(aNasalized);
		
		
	}
	
	public static void main(String[] args) {
		for(XSampa.Consonant consonant:consonants){
			System.out.println(consonant);
		}
		for(XSampa.Vowel vowel:vowels){
			System.out.println(vowel);
		}
	}

}
