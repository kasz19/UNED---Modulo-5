package org.uned.modulo5.Modulo5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Utils {
	
public static ArrayList<String> getWords(String fileName){
		
		BufferedReader br = null;
		FileReader fr = null;
		ArrayList<String> resul = new ArrayList<String>();
		try {

			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			
			String sCurrentLine;

			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.isEmpty()) resul.add(sCurrentLine.toLowerCase());
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		return resul;
	}

public static void writeStringListToFile(String fileName, String url, HashMap<String, BigDecimal> weights) throws FileNotFoundException, UnsupportedEncodingException{
	
	File fileOutput = new File(fileName);
    PrintWriter writerOutput = new PrintWriter(fileOutput, "UTF-8");
    Iterator<Entry<String, BigDecimal>> it = weights.entrySet().iterator();
    writerOutput.println("DOCUMENT -> " + url);
    writerOutput.println();
    while (it.hasNext())
    {
    	Entry<String, BigDecimal> pair = it.next();
    	writerOutput.println(pair.getKey() + " : " + pair.getValue().toPlainString());
    	it.remove();
    }
    writerOutput.close();
}
}
