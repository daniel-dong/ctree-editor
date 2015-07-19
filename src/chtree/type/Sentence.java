package chtree.type;

public class Sentence {
	private int id;
	private String cont;
	private Word words[];

	public Sentence() {
	}

	public Sentence(int id, String cont, Word words[]) {
		this.id = id;
		this.cont = cont;
		this.words = words;
	}

	@Override
	public String toString() {
		return "句" + id + ": " + cont;
	}
	
	public int getId() {
		return id;
	}

	public String getCont() {
		return cont;
	}

	public Word[] getWords() {
		return words;
	}

	public Word getOneWord(int wordId) {
		return words[wordId];
	}

	public void edit(int wordId, int parentId, Word.RELATE relate) {
		words[wordId].setParent(parentId);
		words[wordId].setRelate(relate);
	}
	
	public void verifyTree() throws Exception {
		
	}
}
