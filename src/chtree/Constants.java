package chtree;

import java.awt.Color;
import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import chtree.type.Word;


public class Constants {
	// System wide propriety
	public static final String SYSTEM_NEW_LINE = System
			.getProperty("line.separator");

	// UI propriety
	public static final String WINDOW_TITLE = "汉语依存树编辑器";
	public static final String ERROR_MSGDIAG_TITLE = "错误";

	// Fonts
	public static final Font UI_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
	public static final int UI_FONT_SIZE = UI_FONT.getSize();

	public static final Font TOKEN_FONT = new Font(Font.SANS_SERIF, Font.PLAIN,
			24);
	public static final int TOKEN_FONT_SIZE = TOKEN_FONT.getSize();

	public static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.BOLD,
			16);
	public static final int LABEL_FONT_SIZE = LABEL_FONT.getSize();

	// Token & Arrow style codes
	public static final int STYLE_NORMAL = 0;
	public static final int STYLE_HIGHLIGHTED = 1;
	public static final int STYLE_SELECTED = 2;

	// // Token style
	public static final int TOKEN_WIDTH = TOKEN_FONT_SIZE;
	public static final int TOKEN_HEIGHT_UNIT = TOKEN_FONT_SIZE;
	public static final int TOKEN_INTERVAL = TOKEN_FONT_SIZE;
	public static final String END_OF_SENTENCE = "EOS";

	public static final Color NORMAL_TOKEN_COLOR = Color.DARK_GRAY;
	public static final Color HIGHLIGHTED_TOKEN_COLOR = Color.GRAY;
	public static final Color SELECTED_TOKEN_COLOR = Color.MAGENTA;

	// // Arrow style
	public static final int NORMAL_ARROW_WIDTH = 5;
	public static final int HEAD_LEN = 10;

	public static final Color NORMAL_ARROW_COLOR = Color.BLUE;
	public static final Color HIGHLIGHTED_ARROW_COLOR = Color.ORANGE;
	public static final Color SELECTED_ARROW_COLOR = Color.RED;

	// Models for Components
	public static final DefaultTreeModel INACTIVE_DOCTREE = new DefaultTreeModel(
			new DefaultMutableTreeNode("未打开依存树数据库"));
	public static final DefaultComboBoxModel LIST_RELATE = new DefaultComboBoxModel(
			new String[] { "ATT", "QUN", "COO", "APP", "LAD", "RAD", "VOB",
					"POB", "SBV", "SIM", "HED", "VV", "CNJ", "MT", "IS", "ADV",
					"CMP", "DE", "DI", "DEI", "BA", "BEI", "IC", "DC" });

	// Special Word::parentId
	public static final Word WORD_EOS = new Word();
	public static final int PARENT_EOS = -1;
	public static final int PARENT_NULL = -2;

}
