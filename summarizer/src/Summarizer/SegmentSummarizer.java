package Summarizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import containers.Sentence;
import containers.StatsWord;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class SegmentSummarizer implements Summarizer {

	private List<Sentence> ls = new ArrayList<Sentence>();
	private HashMap<String, StatsWord> hm = new HashMap<String, StatsWord>();
	private HashMap<String, Boolean> keywords = new HashMap<String, Boolean>();
	private int wordCount = 0;
	private int nWords = 0;
	private int nSentence = 0;
	private String[] breakWords = { "because", "but", "if", "however" };

	@Override
	public void summarize() throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void printSentences() {
		// TODO Auto-generated method stub

	}

	public void init(String doc) throws InvalidFormatException, IOException {

		InputStream is = new FileInputStream("data/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(doc);
		for (String st : sentences) {
			st = st.trim();
			List<String> ss = new ArrayList<String>();
			ss.add(st);
			for (String bw : breakWords) {
				List<String> ss1 = new ArrayList<String>();
				for (String ss2 : ss) {
					String ss4[] = ss2.split(bw);
					for (String ss5 : ss4) {
						ss5 = ss5.trim();
						ss1.add(ss5);
					}

				}
				ss = ss1;
			}

			for (String s : ss) {
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
	}

}
