package containers;

public class StatsWord {
	private int nCount;
	private int senCount;

	public StatsWord() {
		nCount = 0;
		senCount = 0;
	}

	public void increamentSentenceCount() {
		senCount++;
	}

	public void increamentWordCount() {
		nCount++;
	}

	public int getTotalCount() {
		return nCount;
	}

	public int getSentenceCount() {
		return senCount;
	}
}
