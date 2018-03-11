package containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Sentence {
	private int _length;
	private double _rating;
	public HashMap<String, Integer> hm = new HashMap<String, Integer>();
	private String original;
	int nWords = 0;
	public double ratingPosition = 0;
	public double ratingPositiveKeyword = 0;
	public double ratingSentenceToTopic = 0;
	public double ratingCentrality = 0;
	public double ratingNegativeKeyword = 0;
	public double ratingResemblanceToTitle = 0;
	public double ratingProperNoun = 0;
	public double ratingNumericalData = 0;
	public double ratingLength = 0;
	public double ratingbushyPath = 0;
	public double ratingSummationSimilarity = 0;
	public double ratingName = 0;
	public double ratingLocation = 0;
	public double ratingDateTime = 0;
	public boolean isNucleus;

	public Sentence(List<String> l, String st) {
		_rating = 0;
		_length = l.size();
		original = st;

		for (String s : l) {
			nWords++;
			if (hm.containsKey(s)) {
				int val = hm.get(s);
				hm.put(s, val + 1);
			} else {
				hm.put(s, 1);
			}
		}
	}

	public int getLength() {
		return _length;
	}

	public String getSentence() {
		return original;
	}

	public double getRating() {
		return _rating;
	}

	public void setRating(double sum) {
		_rating = sum;
	}

	public Boolean containsWord(String s) {
		return hm.containsKey(s);
	}

	public Iterator<Entry<String, Integer>> getWordList() {
		return hm.entrySet().iterator();
	}

	public Set<String> getList() {
		return hm.keySet();
	}

}
