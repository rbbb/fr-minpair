package frminpair;

import java.io.Reader;
import java.util.List;
import java.util.Vector;

public class CSVUtils {
	//readcsv
		//http://stackoverflow.com/questions/843997/csv-parsing-in-java-working-example
		public static List<String> parseLine(Reader r) throws Exception {
		    int ch = r.read();
		    while (ch == '\r') {
		        //ignore linefeed chars wherever, particularly just before end of file
		        ch = r.read();
		    }
		    if (ch<0) {
		        return null;
		    }
		    Vector<String> store = new Vector<String>();
		    StringBuffer curVal = new StringBuffer();
		    boolean inquotes = false;
		    boolean started = false;
		    while (ch>=0) {
		        if (inquotes) {
		            started=true;
		            if (ch == '\"') {
		                inquotes = false;
		            }
		            else {
		                curVal.append((char)ch);
		            }
		        }
		        else {
		            if (ch == '\"') {
		                inquotes = true;
		                if (started) {
		                    // if this is the second quote in a value, add a quote
		                    // this is for the double quote in the middle of a value
		                    curVal.append('\"');
		                }
		            }
		            else if (ch == ',') {
		                store.add(curVal.toString());
		                curVal = new StringBuffer();
		                started = false;
		            }
		            else if (ch == '\r') {
		                //ignore LF characters
		            }
		            else if (ch == '\n') {
		                //end of a line, break out
		                break;
		            }
		            else {
		                curVal.append((char)ch);
		            }
		        }
		        ch = r.read();
		    }
		    store.add(curVal.toString());
		    return store;
		}
		
		public static String csvEscape(String word){
			if(!word.contains("\"") && !word.contains(",")){
				return word;
			}else{
				String wordR = word.replaceAll("\"", "\"\"");
				return "\""+wordR+"\"";
			}
		}
		
		public static void main(String[] args){
			System.out.println(csvEscape("'\"plop"));
		}
}
