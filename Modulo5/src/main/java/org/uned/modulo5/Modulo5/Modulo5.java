package org.uned.modulo5.Modulo5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Modulo5 {

	private static final String FILENAME = "./StopWords.txt";
	
	
	public void procesar() throws IOException{
		
		List<String> stopWords = Utils.getWords(FILENAME);
		
		List<String> urls = getUrls();
		//HashMap<String, ArrayList<String>> wordsMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> stemmedWords = new HashMap<String, ArrayList<String>>();
		int k = 0;
		for(String url: urls){
			StringBuffer sb = getStringFromUrl(url);
			String [] totalWords = sb.toString().split(" ");
			ArrayList<String> validWords = new ArrayList<String> ();
			for(int i = 0; i < totalWords.length; i++)
			{
				String word = totalWords[i];
				word = word.replaceAll("[^A-Za-z]", "").toLowerCase(); // Eliminamos todo lo que no sea alfabetico
				if(word.isEmpty() || stopWords.contains(word)){ // Si es una "stopWord" no la contemplamos
					continue;
				}
				validWords.add(word);
			}

			stemmedWords.put(url, Stemmer.stemWordMap("document_" + k, validWords));
			FuncionLocal.aplicarFuncionLocal("./output/document__" + k + "_Output.txt", url, stemmedWords.get(url));
			k++;
		}
		
		File fileOutput = new File("./output/datosGlobales.txt");
	    PrintWriter writerOutput = new PrintWriter(fileOutput, "UTF-8");
	    ArrayList<Tupla> totalWords = getAllWords(stemmedWords);
	    int totalDocuments = urls.size();
	    writerOutput.println("------------- FUNCION GLOBAL - Term frequency–Inverse document frequency -------");
	    writerOutput.println("");
	    writerOutput.println("");
		// Por cada palabra y cada documento
		for(Tupla tupla : totalWords){
			int occurrences = getOcurrences(tupla, totalWords);
			double log = Math.log10(totalDocuments / occurrences );
			BigDecimal resul = new BigDecimal(log * tupla.getWeight().doubleValue()).setScale(15, BigDecimal.ROUND_HALF_EVEN);
			writerOutput.println("-------------");
			writerOutput.println("DOCUMENT : " + tupla.getDocument());
			writerOutput.println("WORD : " + tupla.getWord());
			writerOutput.println("WEIGHT: " + resul.toString());
			writerOutput.println("-------------");
		}
		writerOutput.close();
		
		// Escribimos el vocabulario
		File fileVoc = new File("./output/vocabulario.txt");
	    PrintWriter writerOutputVoc = new PrintWriter(fileVoc, "UTF-8");
	    ArrayList<String> wordsWritten = new ArrayList<>();
	    writerOutputVoc.println("------------- VOCABULARIO COLECCIONADO -------");
	    writerOutputVoc.println("");
	    writerOutputVoc.println("");
		// Por cada palabra y cada documento
		for(Tupla tupla : totalWords){
			if(wordsWritten.contains(tupla.getWord()))
				continue;
			writerOutputVoc.println(tupla.getWord());
			writerOutputVoc.println();
			wordsWritten.add(tupla.getWord());
		}
		writerOutputVoc.close();
	}



private int getOcurrences(Tupla tuplaParam, ArrayList<Tupla> totalWords) {
		int res = 0;
		for(Tupla tupla : totalWords){
			if(tupla.getWord().equals(tuplaParam.getWord())){
				res++;
			}
		}
		return res;
	}



private ArrayList<Tupla> getAllWords(HashMap<String, ArrayList<String>> stemmedWords) 
{
		
		Iterator<Entry<String, ArrayList<String>>> it = stemmedWords.entrySet().iterator();
		ArrayList<Tupla> resul = new ArrayList<>();
		while (it.hasNext())
		{
			 Entry<String, ArrayList<String>> pair = it.next();
			 HashMap<String, BigDecimal> localWeights = FuncionLocal.aplicarFuncionLocal(null, null, stemmedWords.get(pair.getKey()));
			 Iterator<Entry<String, BigDecimal>> itWeights = localWeights.entrySet().iterator();
			 while(itWeights.hasNext()){
				 Entry<String, BigDecimal> pairWeighted = itWeights.next();
				 Tupla tupla = new Tupla();
				 tupla.setDocument(pair.getKey());
				 tupla.setWord(pairWeighted.getKey());
				 tupla.setWeight(pairWeighted.getValue());
				 resul.add(tupla);
				 itWeights.remove();
			 }
		  	 it.remove();
		 }
		return resul;
}




private StringBuffer getStringFromUrl(String urlStr) throws IOException {
	Document doc = Jsoup.connect(urlStr).get();
	return new StringBuffer(Jsoup.parse(doc.text()).text());
}
	

private List<String> getUrls(){
	 Properties props = getProperties();
	 Enumeration<?> e = props.propertyNames();
	 ArrayList<String> resul = new ArrayList<>();
	 while (e.hasMoreElements())
	 {
	      resul.add(props.getProperty((String) e.nextElement()));
	 }
	 return resul;
}
	

private Properties getProperties(){
	
	Properties prop = new Properties();
	InputStream input = null;
	try {

		input = new FileInputStream("urls.properties");
		prop.load(input);
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	return prop;
  }










/*
//FuncionGlobal
 Iterator<Entry<String, ArrayList<String>>> it = stemmedWords.entrySet().iterator();
 HashMap<String, ArrayList<String>> stemmedWordsAux = new HashMap<>();
 stemmedWordsAux.putAll(stemmedWords);
 
 HashMap<String, BigDecimal> globalWeightMaps = new HashMap<>();
 while (it.hasNext())
 {
  	Entry<String, ArrayList<String>> pair = it.next();
  	HashMap<String, BigDecimal> weightMaps = FuncionLocal.aplicarFuncionLocal(null, stemmedWords.get(pair.getKey()));
  	String url = pair.getKey();
  	for(String word : pair.getValue()){
		if(!weightMaps.containsKey(word)){
  			continue;
  		}
	  	BigDecimal localWeight = weightMaps.get(word);
  	BigDecimal globalWeight = BigDecimal.ZERO;
	  	Iterator<Entry<String, ArrayList<String>>> itAux = stemmedWordsAux.entrySet().iterator();
		while (itAux.hasNext())
		{
		Entry<String, ArrayList<String>> pairAux = itAux.next();
	  	HashMap<String, BigDecimal> weightMapsAux = FuncionLocal.aplicarFuncionLocal(null, stemmedWords.get(pairAux.getKey()));
		  	if(weightMapsAux.containsKey(word)){
		  		globalWeight = globalWeight.add(weightMapsAux.get(word));
		  	}
			//itAux.remove();
		}
	  	
		if(globalWeight.compareTo(BigDecimal.ZERO) != 0){
			globalWeightMaps.put("Document HTML from: " + url + " WORD: " + word + " WEIGHT ---> ", localWeight.divide(globalWeight, 8, RoundingMode.HALF_EVEN));
		}
		else{
			globalWeightMaps.put("Document HTML from: " + url + " WORD: " + word + " WEIGHT ---> ", localWeight.divide(globalWeight, 8, RoundingMode.HALF_EVEN));
		}
  	}
//	it.remove();
 }
*/

}




