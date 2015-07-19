package chtree.type;

import chtree.Constants;


public class Word {
	private int id;
	private String cont;
	private POS pos;
	private int parent;
	private RELATE relate;

	public enum POS {
		A, B, C, D, E, G, H, I, J, K, M, N, ND, NH, NI, NL, NS, NT, NZ, O, P, Q, R, U, V, WP, WS, X, FAIL
	}

	public enum RELATE {
		ATT, QUN, COO, APP, LAD, RAD, VOB, POB, SBV, SIM, HED, VV, CNJ, MT, IS, ADV, CMP, DE, DI, DEI, BA, BEI, IC, DC, PUN, FAIL
	}

	public static POS stringToPos(String s) {
		for (POS p : POS.values()) {
			if (s.toUpperCase().equals(p.toString())) {
				return p;
			}
		}
		return POS.FAIL;
	}

	public static RELATE stringToRelate(String s) {
		for (RELATE r : RELATE.values()) {
			if (s.toUpperCase().equals(r.toString())) {
				return r;
			}
		}
		return RELATE.FAIL;
	}

	// Special constructor for creating "EOS" (End of Sentence)
	public Word() {
		this.id = -1;
		this.cont = Constants.END_OF_SENTENCE;
		this.pos = null;
		this.parent = -1;
		this.relate = null;
	}

	public Word(int id, String cont, String pos, int parent, String relate) {
		this.id = id;
		this.cont = cont;
		this.pos = stringToPos(pos);
		this.parent = parent;
		this.relate = stringToRelate(relate);
	}

	public int getId() {
		return id;
	}

	public String getCont() {
		return cont;
	}

	public POS getPos() {
		return pos;
	}

	public String getPosString() {
		if (pos != null)
			return pos.toString();
		else
			return "";
	}

	public int getParent() {
		return parent;
	}

	public RELATE getRelate() {
		return relate;
	}

	public String getRelateString() {
		if (relate != null)
		return relate.toString();
		else return "";
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public void setRelate(RELATE relate) {
		this.relate = relate;
	}

	public void setRelate(String relate) {
		this.relate = stringToRelate(relate);
	}

	@Override
	public String toString() {
		return cont + "[" + pos + "]";
	}

}
