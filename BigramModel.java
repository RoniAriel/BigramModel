import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	public static final String EMMA_FILENAME = "resources/hw5/emma.txt";
	String[] mVocabulary;
	int[][] mBigramCounts;


	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException {
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);

	}


	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException { // Q 1
		BufferedReader BF = new BufferedReader(new FileReader(new File(fileName)));
		String[] tempArray = new String[MAX_VOCABULARY_SIZE];
		String Line;
		int index = 0;
		while ((Line = BF.readLine()) != null && index < MAX_VOCABULARY_SIZE) {
			if (Line.length() > 0) {
				String[] legalWords = checkforlegalwords(Line);
				for (String word : legalWords) {
					if (isPresent(word, tempArray, index)) {
						tempArray[index++] = word;
					}
				}
			}
		}
		BF.close();
		return Arrays.copyOfRange(tempArray, 0, index);
	}

	private String[] checkforlegalwords(String sentence) {
		String[] fragments = sentence.split("\\s+");
		String[] LegalWords = new String[fragments.length];
		int index = 0;
		search:
		for (String fragment : fragments) {
			if (fragment.matches(".*[a-zA-Z]+.*")) {
				LegalWords[index++] = fragment.toLowerCase();
			} else {
				char[] charsArray = fragment.toCharArray();
				for (char oneChar : charsArray) {
					boolean checkNum = Character.isDigit(oneChar);
					if (checkNum == false) {
						continue search;
					}
				}
				LegalWords[index++] = SOME_NUM;
			}
		}
		String[] legalWordsandSize = Arrays.copyOfRange(LegalWords, 0, index);
		return legalWordsandSize;
	}

	public boolean isPresent(String str, String[] array, int currIndex) {
		for (int j = 0; j < currIndex; j++) {
			if (array[j].equals(str)) {
				return false;
			}
		}
		return true;
	}

	public int getIndex(String str,String []vocabulary){
		int len=vocabulary.length;
		for(int i=0;i<len;i++){
			if(vocabulary[i].equals(str)){
				return i;
			}
		}
		return -1;
	}
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Q - 2
		int[][] countPairs = new int[vocabulary.length][vocabulary.length];
		BufferedReader BF = new BufferedReader(new FileReader(new File(fileName)));
		String Line;
		while ((Line = BF.readLine()) != null) {
			String[] fragments = Line.toLowerCase().split("\\s+"); // check for ,.!!!!!!!!!
			if ((fragments.length == 1) | (Line.length() == 0)) {
				continue;
			}
			fragments = Line.split("\\s+");
			int len = fragments.length;
			for (int idx = 0; idx < len - 1; idx++) {
				int index1 = getIndex(fragments[idx].toLowerCase(), vocabulary);
				int index2 = getIndex(fragments[idx + 1].toLowerCase(), vocabulary);
				if (index1 == -1 || index2 == -1) {
					continue;
				}
				countPairs[index1][index2]++;
			}
		}
		BF.close();
		return countPairs;

	}


	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException { // Q-3
		BufferedWriter BW = new BufferedWriter(new FileWriter(new File(fileName + this.VOC_FILE_SUFFIX)));
		BW.write(mVocabulary.length + " words" + System.lineSeparator());
		for (int i = 0; i < this.mVocabulary.length; i++) {
			BW.write(i + "," + this.mVocabulary[i] + System.lineSeparator());
		}
		BW.close();
		BufferedWriter BW2 = new BufferedWriter(new FileWriter(new File(fileName + this.COUNTS_FILE_SUFFIX)));
		for (int i = 0; i < this.mBigramCounts.length; i++) {
			for (int j = 0; j < this.mBigramCounts.length; j++) {
				if (mBigramCounts[i][j] != 0) {
					BW2.write(i + "," + j + ":" + mBigramCounts[i][j] + System.lineSeparator());
				}
			}
		}
		BW2.close();

	}


	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException { // Q - 4
		BufferedReader BR3 = new BufferedReader(new FileReader(new File(fileName + this.VOC_FILE_SUFFIX)));
		String Line = BR3.readLine();
		String[] fragment = Line.split(" ");
		int totalWords = Integer.parseInt(fragment[0]);
		this.mVocabulary = new String[totalWords];
		this.mBigramCounts = new int[totalWords][totalWords];
		while ((Line = BR3.readLine()) != null) {
			String[] fragments = Line.split(",");
			int i = Integer.parseInt(fragments[0]);
			mVocabulary[i] = fragments[1];
		}
		BufferedReader BR4 = new BufferedReader(new FileReader(new File(fileName + this.COUNTS_FILE_SUFFIX)));
		String oneLine;
		while ((oneLine = BR4.readLine()) != null) {
			String[] index = oneLine.split("[:,]");
			int i = Integer.parseInt(index[0]);
			int j = Integer.parseInt(index[1]);
			int count = Integer.parseInt(index[2]);
			mBigramCounts[i][j] = count;
		}
	}

	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word) {  // Q - 4
		for (int i = 0; i < mVocabulary.length; i++) {
			if (mVocabulary[i].equals(word)) {
				return i;
			}
		}
		return ELEMENT_NOT_FOUND;
	}


	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2) { //  Q - 6
		int index1 = getWordIndex(word1);
		int index2 = getWordIndex(word2);
		if ((index1 != -1) | (index2 != -1)) {
			return mBigramCounts[index1][index2];
		}
		return 0;
	}


	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word) { //  Q - 7
		int i = getWordIndex(word);
		int[] iBigramcount = mBigramCounts[i];
		int MaxCount = 0;
		int indexForMaxCount = 0;
		for (int j = 0; j < iBigramcount.length; j++) {
			if (iBigramcount[j] > MaxCount) {
				MaxCount = iBigramcount[j];
				indexForMaxCount = j;
			}
		}
		if (mBigramCounts [i][indexForMaxCount] == 0 ){
			return null;
		}
		return mVocabulary[indexForMaxCount];
	}


	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence) {  //  Q - 8
		String[] SplittedSentence = sentence.split("\\s+");
		if (SplittedSentence.length < 2) {
			return false;
		} else {
			for (int i = 0, j = 1; i < SplittedSentence.length - 1; i++, j++) {
				String word1 = SplittedSentence[i];
				String word2 = SplittedSentence[j];
				int IndexI = getWordIndex(word1);
				int IndexJ = getWordIndex(word2);
				if ((IndexI == -1) | (IndexJ == -1)) {
					return false;
				}
				int count = getBigramCount(word1, word2);
				if (count == 0) {
					return false;
				}
			}
			return true;
		}

	}


	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2) { //  Q - 9
		double totalAB = 0;
		double ExpoA = 0;
		double ExpoB = 0;
		for (int i = 0; i < arr1.length; i++) {
			totalAB += arr1[i] * arr2[i];
			ExpoA += Math.pow(arr1[i], 2);
			ExpoB += Math.pow(arr2[i], 2);
		}
		double ExpoASqrt = Math.sqrt(ExpoA);
		double ExpoBSqrt = Math.sqrt(ExpoB);
		double Sum = totalAB / (ExpoASqrt * ExpoBSqrt);
		return Sum;
	}


	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized),
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word) {
		int indexOfWord = getWordIndex(word);
		int[] WordBigramCounts = mBigramCounts[indexOfWord];
		double MaxSimilarity = 0;
		int MaxIndex= 0;
		for (int i = 0; i< mVocabulary.length; i++){
			if (i == indexOfWord){
				continue;
			}
			int [] iBigramCount = mBigramCounts[i];
			double cosineSim = calcCosineSim(WordBigramCounts,iBigramCount);
			if (cosineSim > MaxSimilarity){
				MaxSimilarity = cosineSim;
				MaxIndex = i;
			}
		}
		return mVocabulary[MaxIndex];
	}

	}
