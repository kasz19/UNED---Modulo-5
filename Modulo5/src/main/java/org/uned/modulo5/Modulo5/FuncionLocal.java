package org.uned.modulo5.Modulo5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class FuncionLocal {

	
	
	

	public static HashMap<String, BigDecimal> aplicarFuncionLocal(String fileName, String url, ArrayList<String> words) {
			BigDecimal totalWords = new BigDecimal(words.size());
			HashMap<String, BigDecimal> weights = new HashMap<String, BigDecimal>();
			HashMap<String, Integer> contWords = new HashMap<String, Integer>();
			for(String word : words){
				if(!contWords.containsKey(word)){
					contWords.put(word, 1);
				}
				else{
					BigDecimal cont = new BigDecimal(contWords.get(word).intValue());
					cont = cont.add(BigDecimal.ONE);
					cont = cont.divide(totalWords, 8, RoundingMode.HALF_EVEN);
					weights.put(word, cont);
					contWords.put(word, cont.intValue());
				}
			}
			try {
				if(fileName != null)
					Utils.writeStringListToFile(fileName, url, weights);
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return weights;
		}

}
