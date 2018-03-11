import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import opennlp.tools.util.InvalidFormatException;
import Summarizer.StatisticalSummarizer;

public class TextSummarization {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static void main(String[] args) throws InvalidFormatException,
			IOException {

		FileReader fileReader = new FileReader("doc");
		BufferedReader br = new BufferedReader(fileReader);
		
		FileReader is = new FileReader("keywords");
		BufferedReader b = new BufferedReader(is);
		
		StatisticalSummarizer s = new StatisticalSummarizer(br.readLine(), b.readLine());
		
		//StatisticalSummarizer s = new StatisticalSummarizer(
			//	"The United States of America (USA or U.S.A.), commonly referred to as the United States (US or U.S.), America, and sometimes the States, is a federal republic[17][18] consisting of 50 states and a federal district. The 48 contiguous states and Washington, D.C., are in central North America between Canada and Mexico. The state of Alaska is the northwestern part of North America and the state of Hawaii is an archipelago in the mid-Pacific. The country also has five populated and nine unpopulated territories in the Pacific and the Caribbean. At 3.80 million square miles (9.85 million km2)[4] and with around 318 million people, the United States is the world's third- or fourth-largest country by total area and third-largest by population. It is one of the world's most ethnically diverse and multicultural nations, the product of large-scale immigration from many countries.[19] The geography and climate of the United States is also extremely diverse, and it is home to a wide variety of wildlife. Paleo-Indians migrated from Eurasia to what is now the U.S. mainland around 15,000 years ago,[20] with European colonization beginning in the 16th century. The United States emerged from 13 British colonies located along the Atlantic seaboard. Disputes between Great Britain and these colonies led to the American Revolution. On July 4, 1776, as the colonies were fighting Great Britain in the American Revolutionary War, delegates from the 13 colonies unanimously issued the Declaration of Independence. The war ended in 1783 with the recognition of independence of the United States from the Kingdom of Great Britain, and was the first successful war of independence against a European colonial empire.[21][22] The current Constitution was adopted on September 17, 1787. The first ten amendments, collectively named the Bill of Rights, were ratified in 1791 and designed to guarantee many fundamental civil rights and freedoms. Driven by the doctrine of manifest destiny, the United States embarked on a vigorous expansion across North America throughout the 19th century.[23] This involved displacing native tribes, acquiring new territories, and gradually admitting new states.[23] The American Civil War ended legal slavery in the country.[24] By the end of the 19th century, the United States extended into the Pacific Ocean,[25] and its economy began to soar.[26] The Spanish–American War and World War I confirmed the country's status as a global military power. The United States emerged from World War II as a global superpower, the first country to develop nuclear weapons, the only country to use them in warfare, and as a permanent member of the United Nations Security Council. The end of the Cold War and the dissolution of the Soviet Union left the United States as the sole superpower.", "States people");
		s.summarize();
		s.printSentences();
		
		br.close();
		b.close();
		// s.printWord();
	}
}
