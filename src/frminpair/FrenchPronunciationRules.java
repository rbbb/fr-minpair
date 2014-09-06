package frminpair;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FrenchPronunciationRules {
	
	public static class IPAToXSampaRule{
		String IPAString;
		String XSampaString;
	}
	public static List<IPAToXSampaRule> IPAToXSampaRuleset = new ArrayList<FrenchPronunciationRules.IPAToXSampaRule>();
	
	static{
		for(XSampa.Vowel vowel:FrenchPhonemes.vowels){
			IPAToXSampaRule newRule = new IPAToXSampaRule();
			newRule.IPAString = Normalizer.normalize(vowel.getIpaCode(), Normalizer.Form.NFD);
			newRule.XSampaString = vowel.getXSampaCode();
			IPAToXSampaRuleset.add(newRule);
		}
		for(XSampa.Consonant consonant:FrenchPhonemes.consonants){
			IPAToXSampaRule newRule = new IPAToXSampaRule();
			newRule.IPAString = Normalizer.normalize(consonant.getIpaCode(), Normalizer.Form.NFD);
			newRule.XSampaString = consonant.getXSampaCode();
			IPAToXSampaRuleset.add(newRule);
		}
		
		//sort by reverse IPA, so longest string are first
		Collections.sort(IPAToXSampaRuleset, Collections.reverseOrder(new Comparator<IPAToXSampaRule>(){

			@Override
			public int compare(IPAToXSampaRule o1, IPAToXSampaRule o2) {
				return o1.IPAString.compareTo(o2.IPAString);
			}
		}));
		
		
	}
	
	public static String IPAToXSampaOrNull(String ipaPronunciation){
		//decombine all combinalizing unicode chars
		String normalized = Normalizer.normalize(ipaPronunciation, Normalizer.Form.NFD);
		//remove liaison marker, accent marker, syllable separator, lengthening marker
		String raw = normalized.replaceAll("[‿'ː \\|\\.]", "");
		
		String xSampaString = "";
		int curr = 0;
		while(curr<raw.length()){
			String remainingString = raw.substring(curr);
			
			boolean found = false;
			for(IPAToXSampaRule rule:IPAToXSampaRuleset){
				if(remainingString.startsWith(rule.IPAString)){
					xSampaString += rule.XSampaString;
					curr += rule.IPAString.length();
					found = true;
					break;
				}
			}

			if(!found){
				return null;	
			}
			
		}
		return xSampaString;
	}
	
	public static boolean canBeProducedByFrenchPronunciationRules(String word, String xSampaPronunciation){
		//add guard
		String wordNormalized = word.trim().replaceAll("[- ']+", "-");
		while(wordNormalized.startsWith("-")){
			wordNormalized = wordNormalized.substring(1);
		}
		while(wordNormalized.endsWith("-")){
			wordNormalized = wordNormalized.substring(0, wordNormalized.length()-1);
		}
		wordNormalized = wordNormalized.replaceAll("-", "><");
		return recursiveCheck("<"+wordNormalized+">", xSampaPronunciation);
	}
	
	private static boolean recursiveCheck(String word, String remainingPronunciation){
		if(word.length()==0 && remainingPronunciation.length()==0){
			return true;
		}
		for(PronCase pronunciationCase:cases){
			if(!remainingPronunciation.startsWith(pronunciationCase.xsampa)){
				continue;
			}
			for(String guardedWrittenForm:pronunciationCase.guardedCases){
				if(word.startsWith(guardedWrittenForm)){
					if(recursiveCheck(word.substring(guardedWrittenForm.length()), remainingPronunciation.substring(pronunciationCase.xsampa.length()))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static class PronCase
	{
		String xsampa;
		List<String> guardedCases = new ArrayList<String>();

		public PronCase(String xsampa, String isolatedCases, String beginCases, String middleCases, String endCases)
		{
			this.xsampa = xsampa;
			for(String s:isolatedCases.split(","))
			{
				if(s.length()==0)
				{
					continue;
				}
				this.guardedCases.add("<"+s+">");
			}
			for(String s:beginCases.split(","))
			{
				if(s.length()==0)
				{
					continue;
				}
				this.guardedCases.add("<"+s);
			}
			for(String s:endCases.split(","))
			{
				if(s.length()==0)
				{
					continue;
				}
				this.guardedCases.add(s+">");
			}
			for(String s:middleCases.split(","))
			{
				if(s.length()==0)
				{
					continue;
				}
				this.guardedCases.add(s);
			}
		}

	}

	public static List<PronCase> cases = new ArrayList<PronCase>();

	static
	{
		//X-Sampa, isolated forms, beginning of word forms, middle of word forms, end of word forms
		cases.add(new PronCase("m","m’","m","m,mm","m"));
		cases.add(new PronCase("v","","v,w","bv,v,w","v"));
		cases.add(new PronCase("f","","f,ph","f,ff,ph","f"));
		cases.add(new PronCase("b","","b,bh","b,bb","b"));
		cases.add(new PronCase("p","","ph","p,pp,b","p"));
		cases.add(new PronCase("d","d’","d","d,dd,z","d"));
		cases.add(new PronCase("t","t’","t,th","th,tt,cht,pt,t","th,tt,cht,t"));
		cases.add(new PronCase("n","n’","n","n,mn,nn","n"));
		cases.add(new PronCase("J","","gn","ign,ñ,ny,gn",""));
		cases.add(new PronCase("N","","","ng","ng,ngs"));
		cases.add(new PronCase("g","","g,gh,gu","c,g,gg,gn,gu","g,gg"));
		cases.add(new PronCase("k","qu’","c,ch,k,kh,q,qu","c,cc,ch,ck,cqu,k,q,qu,kk,kh","c,ch,ck,k,q"));
		cases.add(new PronCase("Z","j’","g,ge,j","g,ge,j",""));
		cases.add(new PronCase("S","","ch,sch,sh","ch,sch,sc","ch,sh"));
		cases.add(new PronCase("z","","z","s,x,z","s,x,z"));
		cases.add(new PronCase("s","s’,ç’","c,ç,s,sc","c,ç,s,sc,sç,ss,sth,t","ce,s,ss,x"));
		cases.add(new PronCase("R","","r,rh,rr","r,rh,rr,rrh","r,rc,rcs,rd,rds,rf,rfs"));
		cases.add(new PronCase("l","l’","l","l,ll","l,ll,ls"));
		cases.add(new PronCase("j","il ya","hi,i,y","i,ï,ill,glie,gui,y,yi","i,ï,ïe,il,y"));
		cases.add(new PronCase("w","","o,ou,w","o,ou,u,w",""));
		cases.add(new PronCase("H","","","u",""));
		cases.add(new PronCase("i","y","hi,hy,i,î,y","ea,ee,i,î,ï,y,ÿ","i,ï,il,ils,is,ïs,it,ît,its,y,ys"));
		cases.add(new PronCase("e","et,hé","æ,e,é,œ","æ,e,é,œ","ae,ai,é,ed,eds,ée,éent,ées,ef,efs,er,ers,és,ez,ë"));
		cases.add(new PronCase("E","es,est","ai,aî,e,ê","ae,ai,aî,e,è,ê,ë,ea,ei","ai,aid,aids,aie,aient,aies,ais,ait,aît,aits,aix,ay,ect,ects,es,ès,et,êt,ets,êts,ey"));
		cases.add(new PronCase("E~","Ain,hein","ain,im,in","ain,ein,im,in,yn,ym,en","aim,aims,ain,ainc,aincs,ains,aint,ein,eins,eing,eings,eint,eints,en,in,inct,incts,ingt,ingts,inq,ins,int,înt,ints,ing,ings"));
		cases.add(new PronCase("9~","un","un","un","um,ums,un,uns,unt,unts"));
		cases.add(new PronCase("9","","œ,œu","e,eu,œu,ogl,u,ue",""));
		cases.add(new PronCase("@","","","e","e,ent,es"));
		cases.add(new PronCase("2","euh,eux,des œufs","eu,heu","eu,eû","eu,eue,eues,eus,eux,œu,œufs,œux"));
		cases.add(new PronCase("y","eu,eus,eut,eût","u,eu","u,û","u,û,ue,uë,uent,ues,ul,uls,us,ûs,ut,ût,uts"));
		cases.add(new PronCase("u","ou,où","aoû,ou","aou,oo,ou,oû,ow","aoul,aouls,ew,ou,oue,ouent,oues,oo,oup,oups,ous,out,oût,oûts,oux,u'"));
		cases.add(new PronCase("o","aulx,eau,eaux,ô,oh,os,au","au,oh,o","a,au,o,ô,oa,oo","au,aud,auds,aut,auts,aux,aulx,eau,eaux,o,ô,oc,op,os,ot,ots,ôt"));
		cases.add(new PronCase("O","","o,ho","o,oa,oo,u,ü",""));
		cases.add(new PronCase("O~","on,ils ont","om,on","om,on,ump","om,omb,ombs,ompt,ompts,oms,on,onc,oncs,ond,onds,ong,ongs,ons,ont,onts,omps"));
		cases.add(new PronCase("A~","an,ans,en","am,an,em,en","aën,am,an,em,en","aen,amp,amps,anc,ancs,and,ands,ang,angs,ans,ant,ants,an,aon,aons,emps,empt,empts,end,ends,eng,engs,ens,ent,ents"));
		cases.add(new PronCase("A","","â","â,am","ars,as,âs,ât,âts,az"));
		cases.add(new PronCase("a","a,à,as,ha","a","a,aë,ao,e,ê,y","a,à,ac,acs,ah,ap,as,at,ats"));

		//regles supplementaires pour matcher tous les mots
		
		//voyelles/consonnes muettes
		cases.add(new PronCase("", "", "h", "h,-,'", "t,e,s,es,d,ds,h,ent,c,x,gts,gt"));
		//e muet en milieu de mot
		cases.add(new PronCase("", "", "", "e", ""));
		//mots composes et racines
		cases.add(new PronCase("", "", "-", "-", "-"));
		//rayon, ayons
		cases.add(new PronCase("Ej","","ay","ay","ay"));
		//trier
		cases.add(new PronCase("ij","","i","i","i"));
		//cryo
		cases.add(new PronCase("ij","","y","y","y"));
		//fuyez
		cases.add(new PronCase("Hij","","uy","uy","uy"));
		//trainions ?????
		//cases.add(new PronCase("J","","ni","ni","ni"));
		//bille
		cases.add(new PronCase("ij","","","ill,illi","ille,illent,illes"));
		//o prononce wa, comme dans aboyer
		cases.add(new PronCase("wa","","o","o","o"));
		//oi oî oix
		cases.add(new PronCase("wa","","oi,oî","oi,oî,oig","oi,oix"));
		//affrication du x, z
		cases.add(new PronCase("ks","","x","x,xc,xs","x"));//fax
		cases.add(new PronCase("gs","","x","x,xc,xs","x"));//inexorable
		cases.add(new PronCase("kz","","x","x,xs","x"));//exercice
		cases.add(new PronCase("gz","","x","x,xs","x"));//exhausteur
		cases.add(new PronCase("dz","","z","z","z"));//zeta
		//ai initial dans aigue, ainee, medial dans saisira
		cases.add(new PronCase("e","","ai","ai",""));
		//ei dans veiner
		cases.add(new PronCase("e","","ei","ei",""));

		//mots rares
		//enhayer, enherber
		cases.add(new PronCase("A~n", "", "enh", "", ""));
		//ay prononce ai (noms propres ?)
		cases.add(new PronCase("E","","","ay",""));
		//sangsues
		cases.add(new PronCase("sA~", "", "sang", "", ""));
		//punch (la boisson)
		cases.add(new PronCase("pO~S", "", "punch", "", ""));

		//ne gere pas les liaisons dans les mots composes
		//en-avant, etats-unis

		//???
		cases.add(new PronCase("e","","","ê,é","é"));
		cases.add(new PronCase("E","","","é",""));


		//cas bizarres
		cases.add(new PronCase("E","","è","",""));//èvres
		//machisme
		cases.add(new PronCase("tS","","","ch",""));
		//prononciation du a accent grave comme une personne du commun
		cases.add(new PronCase("a","","â","â","â"));
		cases.add(new PronCase("j","","","illi",""));//tailliez prononce taje et pas taije
		cases.add(new PronCase("J","","","gni",""));//lorgniez prononce lorgner
		cases.add(new PronCase("j","","","ïi,ii",""));//daïions prononce tel un gueux, planchéiiez articule tel un manant

		//prononciation du a sans accent comme un intellectuel pedant
		cases.add(new PronCase("A","","a","a","a"));
		cases.add(new PronCase("O","","o,au,ô","o,au,ô","o,au,ô"));


		//epelation
		cases.add(new PronCase("be","b","b","b","b"));
		cases.add(new PronCase("se","c","c","c","c"));
		cases.add(new PronCase("de","d","d","d","d"));
		cases.add(new PronCase("Ef","f","f","f","f"));
		cases.add(new PronCase("Ze","g","g","g","g"));
		cases.add(new PronCase("aS","h","h","h","h"));
		cases.add(new PronCase("Zi","j","j","j","j"));
		cases.add(new PronCase("ka","k","k","k","k"));
		cases.add(new PronCase("El","l","l","l","l"));
		cases.add(new PronCase("Em","m","m","m","m"));
		cases.add(new PronCase("En","n","n","n","n"));
		cases.add(new PronCase("pe","p","p","p","p"));
		cases.add(new PronCase("ky","q","q","q","q"));
		cases.add(new PronCase("Er","r","r","r","r"));
		cases.add(new PronCase("Es","s","s","s","s"));
		cases.add(new PronCase("te","t","t","t","t"));
		cases.add(new PronCase("y","u","u","u","u"));
		cases.add(new PronCase("ve","v","v","v","v"));
		cases.add(new PronCase("dublve","w","w","w","w"));
		cases.add(new PronCase("dubl@ve","w","w","w","w"));
		cases.add(new PronCase("iks","x","x","x","x"));
		cases.add(new PronCase("igREk","y","y","y","y"));
		cases.add(new PronCase("zed","z","z","z","z"));
		cases.add(new PronCase("E","è","","",""));


		//prononciations anglaises
		cases.add(new PronCase("SEk","","shak","shak","shak"));
		cases.add(new PronCase("aj","","i,y","i,y,ig,eig,eigh","i,y"));
		cases.add(new PronCase("S","","sh","sh",""));//le final existe deja
		cases.add(new PronCase("e","","a","a","a"));
		cases.add(new PronCase("E","","a","a","a"));
		cases.add(new PronCase("aw","","ou","ou","ou"));
		cases.add(new PronCase("ju","","ew","ew","ew"));
		cases.add(new PronCase("i","","ea","ea","ea"));
		cases.add(new PronCase("tS","","ch","","ch"));//ou espagnol, chili
		cases.add(new PronCase("dZ","","","g",""));
		cases.add(new PronCase("dZ","","j","j",""));
		cases.add(new PronCase("9","","","u",""));
		cases.add(new PronCase("z","","","zz",""));
		cases.add(new PronCase("O","","","a","")); //wall
		cases.add(new PronCase("R","","wr","wr","")); //write
		cases.add(new PronCase("@","","","i","")); //write
		cases.add(new PronCase("o","","","o","")); //snow
		cases.add(new PronCase("O","","","oa","")); //board
		cases.add(new PronCase("di","","de","",""));//design
		//italien
		cases.add(new PronCase("dz","","","zz",""));//pizza
		//sanskrit
		cases.add(new PronCase("s","","ç","",""));
		//u prononce ou (plusieurs langues)
		cases.add(new PronCase("u","","u","u","u"));
		//devocalisation des stops finaux
		cases.add(new PronCase("k","","","","g"));
		cases.add(new PronCase("d","","","","t"));
		cases.add(new PronCase("b","","","","p"));
	}
	
	public static void main(String[] args){
		canBeProducedByFrenchPronunciationRules("au-delà","od@la");
	}
}
