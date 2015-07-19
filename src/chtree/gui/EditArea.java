package chtree.gui;

import chtree.Constants;
import chtree.type.Sentence;
import chtree.type.Word;

import edu.umd.cs.piccolo.PCanvas;

public class EditArea {
	// Contained
	private PCanvas canvas;
	private Token tokens[];

	public final Token TOKEN_EOS;
	private Arrow arrows[];
	private int arrowNum;
	private Selectable selectedItem = null;

	// Outside dependencies
	private Sentence sent;
	private ToolBars toolBars;

	public EditArea() {
		canvas = new PCanvas();
		canvas.setZoomEventHandler(null);
		TOKEN_EOS = new Token(this, Constants.WORD_EOS);
	}

	public EditArea(ToolBars toolBar) {
		canvas = new PCanvas();
		canvas.setZoomEventHandler(null);
		this.toolBars = toolBar;
		TOKEN_EOS = new Token(this, Constants.WORD_EOS);
	}

	public void setToolBar(ToolBars toolBar) {
		this.toolBars = toolBar;
	}

	public PCanvas getCanvas() {
		return canvas;
	}

	public Token[] getTokens() {
		return tokens;
	}

	public Arrow[] getArrows() {
		return arrows;
	}

	public int getArrowNum() {
		return arrowNum;
	}

	public Selectable getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Selectable selectedItem) {
		this.selectedItem = selectedItem;
		toolBars.selectedItemChanged();
	}

	public Sentence getSent() {
		return sent;
	}

	// Redisplay current sentence, i.e. only redraw arrows.
	public void displaySentence() {
		removeArrows();
		layoutArrow(sent.getWords());
		setSelectedItem(null);
	}

	public void displaySentence(Sentence sent) {
		canvas.getLayer().removeAllChildren();
		this.sent = sent;
		layoutToken(sent.getWords());
		layoutArrow(sent.getWords());
		setSelectedItem(null);
	}

	private void removeArrows() {
		for (int i = 0; i < arrowNum; ++i) {
			canvas.getLayer().removeChild(arrows[i]);
		}
		// !! canvas.getLayer().getChildrenCount() seems to have bugs.
	}

	public void clear() {
		canvas.getLayer().removeAllChildren();
		sent = null;
		setSelectedItem(null);
	}

	public void resetView() {
		canvas.getCamera().setViewOffset(0, 0);
		canvas.getCamera().setViewScale(1);
	}

	public void reset() {
		resetView();
		clear();
	}

	private void layoutToken(Word words[]) {
		int maxWordLen = 0;
		for (int i = 0; i < words.length; ++i) {
			int len = words[i].getCont().length();
			maxWordLen = (len > maxWordLen) ? len : maxWordLen;
		}

		int cvWidth = canvas.getWidth();
		int cvHeight = canvas.getHeight();

		int x_Token = (cvWidth - (words.length + 1) * Constants.TOKEN_WIDTH - words.length
				* Constants.TOKEN_INTERVAL) / 2;
		x_Token = Math.max(x_Token, Constants.TOKEN_WIDTH);

		int y_Token = cvHeight - (maxWordLen + 1) * Constants.TOKEN_HEIGHT_UNIT;

		tokens = new Token[words.length + 1];

		for (int i = 0; i < words.length; ++i) {
			// Make sure that the index of tokens[] is identical to Word::id
			int wordId = words[i].getId();
			tokens[wordId] = new Token(this, words[i]);
			
			canvas.getLayer().addChild(tokens[wordId]);
			tokens[wordId].setOffset((double)x_Token, (double)y_Token);
			
			x_Token += Constants.TOKEN_WIDTH + Constants.TOKEN_INTERVAL;
		}

		tokens[words.length] = TOKEN_EOS;
		canvas.getLayer().addChild(tokens[words.length]);
		tokens[words.length].setOffset((double) x_Token, (double) y_Token);
	}

	private void layoutArrow(Word words[]) {
		// Max(arrowNum) = n(Token) - 1 = n(words);
		arrows = new Arrow[words.length];
		arrowNum = 0;

		for (int i = 0; i < words.length; ++i) {
			if (words[i].getParent() == Constants.PARENT_EOS) {
				arrows[arrowNum] = new Arrow(this, tokens[words[i].getId()],
						tokens[words.length]);
				arrows[arrowNum].draw();
				++arrowNum;
			} else if (words[i].getParent() != Constants.PARENT_NULL) {
				arrows[arrowNum] = new Arrow(this, tokens[words[i].getId()],
						tokens[words[i].getParent()]);
				arrows[arrowNum].draw();
				++arrowNum;
			}

		}

	}
}
