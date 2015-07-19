package chtree.type;

public class Paragraph {
	private int id;
	private Sentence sentences[];

	public Paragraph() {
	}

	public Paragraph(int id, Sentence sentences[]) {
		this.id = id;
		this.sentences = sentences;
	}

	public int getId() {
		return id;
	}
	
	public Sentence[] getSentences() {
		return sentences;
	}

	public Sentence getOneSentence(int sentId) {
		return sentences[sentId];
	}
}
