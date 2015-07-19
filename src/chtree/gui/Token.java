package chtree.gui;

import chtree.Constants;
import chtree.type.Word;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

public class Token extends PText implements Selectable {
	private static final long serialVersionUID = 1L;

	// Contained
	private boolean selected = false;

	// Associated with
	private EditArea editArea;
	private Word word;

//	// Special constructor for creating "EOS" Token
//	public Token(EditArea editArea) {
//		super();
//		this.editArea = editArea;
//		this.word = new Word();
//		initStyle();
//		showText(this.word.getCont());
//	}

	public Token(EditArea editArea, Word word) {
		super();
		this.editArea = editArea;
		this.word = word;
		initStyle();
		showText(this.word.getCont());
	}

	@Override
	public String toString() {
		return "Token[" + word + "]";
	}

	public Word getWord() {
		return word;
	}

	private void initStyle() {
		setFont(Constants.TOKEN_FONT);
		setTextPaint(Constants.NORMAL_TOKEN_COLOR);
	}

	private void showText(String wordCont) {
		StringBuilder strBdr = new StringBuilder();
		for (int i = 0; i < wordCont.length() - 1; ++i) {
			strBdr.append(wordCont.charAt(i) + Constants.SYSTEM_NEW_LINE);
		}
		strBdr.append(wordCont.charAt(wordCont.length() - 1));

		setText(new String(strBdr));
		addInputEventListener(new TokenHandler());
	}

	// !! A clone of Arrow::ArrowHandler
	private class TokenHandler extends PBasicInputEventHandler {
		@Override
		public void mouseClicked(PInputEvent event) {
			super.mouseClicked(event);
			toggleSelected(true);
		}

		@Override
		public void mouseEntered(PInputEvent event) {
			super.mouseEntered(event);
			if (!isSelected())
				changeStyle(Constants.STYLE_HIGHLIGHTED);
		}

		@Override
		public void mouseExited(PInputEvent event) {
			super.mouseExited(event);
			if (!isSelected())
				changeStyle(Constants.STYLE_NORMAL);
		}
	}

	private void changeStyle(int styleCode) {
		switch (styleCode) {
		case Constants.STYLE_NORMAL:
			setTextPaint(Constants.NORMAL_TOKEN_COLOR);
			break;
		case Constants.STYLE_HIGHLIGHTED:
			setTextPaint(Constants.HIGHLIGHTED_TOKEN_COLOR);
			break;
		case Constants.STYLE_SELECTED:
			setTextPaint(Constants.SELECTED_TOKEN_COLOR);
			break;
		}
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	// !! A clone of Arrow::toggleSelected()
	@Override
	public void toggleSelected(boolean underMouse) {
		selected = !selected;
		if (selected) {
			Selectable selectedItem = editArea.getSelectedItem();
			if (selectedItem != null) {
				if (selectedItem.getClass() == Arrow.class) {
					((Arrow) selectedItem).toggleSelected(false);
				} else {
					((Token) selectedItem).toggleSelected(false);
				}
			}
			editArea.setSelectedItem(this);
			changeStyle(Constants.STYLE_SELECTED);
		} else {
			editArea.setSelectedItem(null);
			if (underMouse) {
				changeStyle(Constants.STYLE_HIGHLIGHTED);
			} else {
				changeStyle(Constants.STYLE_NORMAL);
			}
		}
	}
}
