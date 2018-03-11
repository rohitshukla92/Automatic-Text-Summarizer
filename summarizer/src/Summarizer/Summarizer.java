package Summarizer;

import java.io.IOException;

import opennlp.tools.util.InvalidFormatException;

public interface Summarizer {
	public void summarize() throws InvalidFormatException, IOException;
	public void printSentences();
}
