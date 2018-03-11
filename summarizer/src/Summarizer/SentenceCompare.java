package Summarizer;

import java.util.Comparator;

import containers.Sentence;

public class SentenceCompare implements Comparator<Sentence> {
	@Override
	public int compare(Sentence s1, Sentence s2) {
		int c1 = (int) (s1.getRating() * 1000);
		int c2 = (int) (s2.getRating() * 1000);
		return c2 - c1;
	}
}
