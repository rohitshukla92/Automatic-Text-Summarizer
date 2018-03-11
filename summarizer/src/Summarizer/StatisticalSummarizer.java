package Summarizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import containers.Sentence;
import containers.StatsWord;

public class StatisticalSummarizer implements Summarizer {
	private List<Sentence> ls = new ArrayList<Sentence>();
	private HashMap<String, StatsWord> hm = new HashMap<String, StatsWord>();
	private HashMap<String, Boolean> keywords = new HashMap<String, Boolean>();
	private int wordCount = 0;
	private int nWords = 0;
	private int nSentence = 0;

	/*
	 * writer.write("Sr. No.\t"); writer.write("Position\t");
	 * writer.write("Positive Keyword\t"); writer.write("Centrality\t\t");
	 * writer.write("Topic Match\t"); writer.write("Proper Noun\t");
	 * writer.write("Length\n\n");
	 */

	private final String POSITION = "Position";
	private final String POSITIVE_KEYWORD = "Position Keyword";
	private final String CENTRALITY = "Centrality";
	private final String TOPIC_MATCH = "Topic Match";
	private final String PROPER_NOUN = "Proper Noun";
	private final String LENGTH = "Length";

	public StatisticalSummarizer(String doc) throws InvalidFormatException,
			IOException {
		init(doc);
	}

	public StatisticalSummarizer(String doc, String keywordsSet)
			throws InvalidFormatException, IOException {
		init(doc, keywordsSet);
	}

	public void init(String doc, String keywordsSet)
			throws InvalidFormatException, IOException {
		String keys[] = keywordsSet.split(" ");
		for (String s : keys) {
			keywords.put(s, true);
		}
		init(doc);
	}

	public void init(String doc) throws InvalidFormatException, IOException {

		InputStream is = new FileInputStream("data/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(doc);
		for (String s : sentences) {
			s = s.trim();
			String[] words = s.split(" ");
			List<String> wList = new ArrayList<String>();
			for (String word : words) {
				word = word.trim();

				if (word.length() > 0) {
					wordCount++;
					wList.add(word);

					if (hm.containsKey(word)) {
						StatsWord sw = hm.get(word);
						sw.increamentWordCount();
						hm.put(word, sw);
					} else {
						StatsWord sw = new StatsWord();
						sw.increamentWordCount();
						hm.put(word, sw);
					}
				}
			}
			Sentence sen = new Sentence(wList, s);
			Iterator<Entry<String, Integer>> it = sen.getWordList();
			while (it.hasNext()) {
				Entry<String, Integer> pairs = it.next();
				StatsWord sw = hm.get(pairs.getKey());
				sw.increamentSentenceCount();
				hm.put(pairs.getKey(), sw);
			}
			nWords += wList.size();
			ls.add(sen);
		}
		nSentence += ls.size();
	}

	public void summarize() throws InvalidFormatException, IOException {
		int index = 0;
		for (Sentence s : ls) {
			double sum = 0;
			sum += s.ratingPosition = sentencePosition(index);
			sum += s.ratingPositiveKeyword = positiveKeyword(s);
			sum += s.ratingSentenceToTopic = sentenceToTopic(s);
			sum += s.ratingCentrality = centrality(s);
			sum += s.ratingName = containsName(s);
			sum += s.ratingDateTime = containsDateTime(s);
			sum += s.ratingLocation = containsLocation(s);
			sum += s.ratingLength = relativeLength(s);
			s.setRating(sum);
			index++;
		}
		// Collections.sort(ls, new SentenceCompare());
		/*
		 * for (Sentence s : ls) { //System.out.println(s.getSentence() + " " +
		 * s.getRating()); }
		 */
		// printSentences();
	}

	private double sentencePosition(int index) {
		if (index <= 5)
			return ((double) 5 - index) / 5;
		return 0;
	}

	private float positiveKeyword(Sentence s) {
		float out = 0;
		float probS = (float) (1.0 / nSentence);
		Iterator<Entry<String, Integer>> it = s.getWordList();
		int totalWords = 0;

		while (it.hasNext()) {
			Entry<String, Integer> pairs = it.next();
			float tf = pairs.getValue();
			float probkey = (float) hm.get(pairs.getKey()).getTotalCount()
					/ wordCount;
			totalWords += pairs.getValue();
			out += tf / probkey;
		}
		out *= probS;
		out /= totalWords;
		return out;
	}

	private double centrality(Sentence s) {
		Set<String> wordList = s.getList();
		int commonWords = 0;
		int totalWords = hm.keySet().size();
		for (String word : wordList) {
			if (hm.get(word).getSentenceCount() > 1)
				commonWords++;
		}
		return (double) commonWords / totalWords;
	}

	private double sentenceToTopic(Sentence s) {
		Set<String> wordList = s.getList();
		int commonWords = 0;
		for (String word : wordList) {
			if (keywords.containsKey(word)) {
				commonWords++;
			}
		}
		int totalWords = wordList.size() + keywords.keySet().size()
				- commonWords;

		return (float) commonWords / totalWords;
	}

	private float containsName(Sentence s) throws InvalidFormatException,
			IOException {
		int sum = 0;
		InputStream is = new FileInputStream("data/en-ner-person.bin");

		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		NameFinderME nameFinder = new NameFinderME(model);

		String[] sentence = s.getSentence().split(" ");

		Span[] nameSpans = nameFinder.find(sentence);

		sum += nameSpans.length;
		// is = new FileInputStream("data/en-ner-organization.bin");
		// model = new TokenNameFinderModel(is);
		// is.close();

		// nameFinder = new NameFinderME(model);
		// nameSpans = nameFinder.find(sentence);
		// sum += nameSpans.length;

		// is = new FileInputStream("data/en-ner-percentage.bin");
		// model = new TokenNameFinderModel(is);
		// is.close();

		// nameFinder = new NameFinderME(model);
		// nameSpans = nameFinder.find(sentence);
		// sum += nameSpans.length;

		// is = new FileInputStream("data/en-ner-money.bin");
		// model = new TokenNameFinderModel(is);
		// is.close();

		// nameFinder = new NameFinderME(model);
		// nameSpans = nameFinder.find(sentence);
		// sum += nameSpans.length;

		return sum;
	}

	private double containsDateTime(Sentence s) throws InvalidFormatException,
			IOException {
		double sum = 0;
		InputStream is = new FileInputStream("data/en-ner-date.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		String[] sentence = s.getSentence().split(" ");
		NameFinderME nameFinder = new NameFinderME(model);
		Span[] nameSpans = nameFinder.find(sentence);
		sum += nameSpans.length;

		is = new FileInputStream("data/en-ner-time.bin");
		model = new TokenNameFinderModel(is);
		is.close();

		nameFinder = new NameFinderME(model);
		nameSpans = nameFinder.find(sentence);
		sum += nameSpans.length;

		return sum;
	}

	private double containsLocation(Sentence s) throws InvalidFormatException,
			IOException {
		double sum = 0;
		InputStream is = new FileInputStream("data/en-ner-location.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		String[] sentence = s.getSentence().split(" ");
		NameFinderME nameFinder = new NameFinderME(model);
		Span[] nameSpans = nameFinder.find(sentence);
		sum += nameSpans.length;

		return sum;
	}

	private double relativeLength(Sentence s) {
		return s.getLength();
	}

	private void writeToFile(String fileName) throws IOException {
		FileWriter fileWriter;
		BufferedWriter writer = null;

		try {
			fileWriter = new FileWriter(fileName + ".txt");
			writer = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		writer.write("Sr. No.\t");

	}

	public void printSentences() {
		BufferedWriter writer = null;
		try {
			FileWriter out = new FileWriter("Sentences.txt");
			writer = new BufferedWriter(out);
			int count = 1;
			for (Sentence s : ls) {
				writer.write(count + "\t" + s.getSentence() + "\n\n");
				count++;
			}

			writer.flush();
			writer.close();

			FileWriter fileWriter[] = new FileWriter[8];
			fileWriter[0] = new FileWriter(POSITION);
			fileWriter[1] = new FileWriter(POSITIVE_KEYWORD);
			fileWriter[2] = new FileWriter(CENTRALITY);
			fileWriter[3] = new FileWriter(TOPIC_MATCH);
			fileWriter[4] = new FileWriter(PROPER_NOUN);
			fileWriter[5] = new FileWriter(LENGTH);
			fileWriter[6] = new FileWriter("Location");
			fileWriter[7] = new FileWriter("Date & Time");

			BufferedWriter bufferedWriter[] = new BufferedWriter[8];

			for (int i = 0; i < 8; i++)
				bufferedWriter[i] = new BufferedWriter(fileWriter[i]);

			/*
			 * writer.write("Sr. No.\t"); writer.write("Position\t");
			 * writer.write("Positive Keyword\t");
			 * writer.write("Centrality\t\t"); writer.write("Topic Match\t");
			 * writer.write("Proper Noun\t"); writer.write("Length\n\n");
			 */

			int index = 1;
			for (Sentence s : ls) {
				// writer.write(String.valueOf(index) + "\t" + );
				// writer.write("\t");

				bufferedWriter[0].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingPosition) + "\n\n");
				// writer.write(String.valueOf(s.ratingPosition));
				// writer.write("\t\t");
				bufferedWriter[1].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingPositiveKeyword) + "\n\n");
				// writer.write("\t");
				bufferedWriter[2].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingCentrality) + "\n\n");
				// writer.write("\t");
				bufferedWriter[3].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingSentenceToTopic) + "\n\n");
				// writer.write("\t\t");
				bufferedWriter[4].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t" + String.valueOf(s.ratingName)
						+ "\n\n");
				// writer.write("\t\t");
				bufferedWriter[5].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingLength) + "\n\n");

				bufferedWriter[6].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingLocation) + "\n\n");

				bufferedWriter[7].write(String.valueOf(index) + "\t"
						+ s.getSentence() + "\t"
						+ String.valueOf(s.ratingDateTime) + "\n\n");
				// writer.write("\n\n");
				index++;
			}

			for (int i = 0; i < 8; i++) {
				bufferedWriter[i].flush();
				bufferedWriter[i].close();
			}

			// writer.flush();
			// writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}