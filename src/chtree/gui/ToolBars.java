package chtree.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import chtree.Constants;
import chtree.type.Sentence;
import chtree.type.Word;
import chtree.type.Word.POS;

public class ToolBars implements ActionListener {
	// Contained
	private JToolBar mainToolBar;
	private JToggleButton tgbtnNewArrow;
	private JButton btnDeleteArrow;
	private JButton btnUnselect;
	private JComboBox cbRelate;
	private JComboBox cbFromWord;
	private JComboBox cbToWord;

	private JPanel statusBar;
	private JLabel lblPrompt;
	private JLabel lblLibStatus;

	private ItemBufferManager itemBufferManager;

	// Associated with
	private EditArea editArea;

	public ToolBars(EditArea editArea) {
		this.editArea = editArea;
		itemBufferManager = new ItemBufferManager();

		buildToolBar();
		buildStatusBar();
	}

	private void buildToolBar() {
		tgbtnNewArrow = new JToggleButton("添加箭头");
		btnDeleteArrow = new JButton("删除箭头");
		btnUnselect = new JButton("取消选择");
		cbFromWord = new JComboBox();
		cbRelate = new JComboBox();
		cbToWord = new JComboBox();

		tgbtnNewArrow.setFont(Constants.UI_FONT);
		btnDeleteArrow.setFont(Constants.UI_FONT);
		btnUnselect.setFont(Constants.UI_FONT);
		cbFromWord.setFont(Constants.UI_FONT);
		cbRelate.setFont(Constants.UI_FONT);
		cbToWord.setFont(Constants.UI_FONT);

		// The list of Relate is constant
		cbRelate.setModel(Constants.LIST_RELATE);

		tgbtnNewArrow.setToolTipText("添加一个箭头：依次单击子Word和父Word");
		btnDeleteArrow.setToolTipText("刪除当前选中的箭头");
		btnUnselect.setToolTipText("取消当前的选择");

		tgbtnNewArrow.setEnabled(false);
		btnDeleteArrow.setEnabled(false);
		btnUnselect.setEnabled(false);
		cbFromWord.setEnabled(false);
		cbRelate.setEnabled(false);
		cbToWord.setEnabled(false);

		tgbtnNewArrow.addActionListener(this);
		btnDeleteArrow.addActionListener(this);
		btnUnselect.addActionListener(this);
		cbFromWord.addActionListener(this);
		cbRelate.addActionListener(this);
		cbToWord.addActionListener(this);

		mainToolBar = new JToolBar();
		mainToolBar.setFloatable(false);
		mainToolBar.add(tgbtnNewArrow);
		mainToolBar.add(btnDeleteArrow);
		mainToolBar.add(btnUnselect);
		mainToolBar.add(cbFromWord);
		mainToolBar.add(cbRelate);
		mainToolBar.add(cbToWord);
	}

	private void buildStatusBar() {
		statusBar = new JPanel(new BorderLayout());

		lblPrompt = new JLabel("就绪");
		lblLibStatus = new JLabel();

		lblPrompt.setFont(Constants.UI_FONT);
		lblLibStatus.setFont(Constants.UI_FONT);

		lblPrompt.setHorizontalAlignment(JLabel.LEFT);
		lblLibStatus.setHorizontalAlignment(JLabel.RIGHT);

		statusBar.add(lblPrompt, BorderLayout.WEST);
		statusBar.add(lblLibStatus, BorderLayout.EAST);
	}

	public JToolBar getMainToolBar() {
		return mainToolBar;
	}

	public JPanel getStatusBar() {
		return statusBar;
	}

	public EditArea getEditArea() {
		return editArea;
	}

	public void setStatusBarPrompt(String s) {
		lblPrompt.setText(s);
	}

	public void setStatusBarLibStatus(String s) {
		lblLibStatus.setText(s);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == tgbtnNewArrow) {
			if (tgbtnNewArrow.isSelected()) {
				new NewArrowTask().start();
			} else {
				// Notify the running thread NewArrowTask to terminate.
				itemBufferManager.setItemBuffer(null);
			}
			return;
		}

		if (source == btnDeleteArrow) {
			Word fromWord = ((Arrow) editArea.getSelectedItem()).getFromToken()
					.getWord();
			fromWord.setParent(Constants.PARENT_NULL);

			postTaskUIUpdate(true, null);
			return;
		}

		if (source == btnUnselect) {
			editArea.getSelectedItem().toggleSelected(false);
			postTaskUIUpdate(false, null);
		}

		if (source == cbFromWord) {
			Arrow arrow = (Arrow) editArea.getSelectedItem();
			if (arrow == null) {
				return;
			}

			String relate = arrow.getRelate();
			Word newFromWord = (Word) cbFromWord.getSelectedItem();
			if (newFromWord != arrow.getFromToken().getWord()) {
				arrow.getFromToken().getWord().setParent(Constants.PARENT_NULL);
				newFromWord.setParent(arrow.getToToken().getWord().getId());
				newFromWord.setRelate(relate);

				postTaskUIUpdate(true,
						editArea.getTokens()[newFromWord.getId()]);
			}
			return;
		}

		if (source == cbRelate) {
			Arrow arrow = (Arrow) editArea.getSelectedItem();
			if (arrow == null) {
				return;
			}

			String newRelate = (String) cbRelate.getSelectedItem();
			if (newRelate != arrow.getRelate()) {
				Token fromToken = arrow.getFromToken();
				fromToken.getWord().setRelate(newRelate);

				postTaskUIUpdate(true, fromToken);
			}
			return;
		}

		if (source == cbToWord) {
			Arrow arrow = (Arrow) editArea.getSelectedItem();
			if (arrow == null) {
				return;
			}

			String relate = arrow.getRelate();
			Word newToWord = (Word) cbToWord.getSelectedItem();
			if (newToWord != arrow.getToToken().getWord()) {
				Token fromToken = arrow.getFromToken();
				fromToken.getWord().setParent(newToWord.getId());
				fromToken.getWord().setRelate(relate);

				postTaskUIUpdate(true, fromToken);
			}
			return;
		}
	}

	private void postTaskUIUpdate(boolean arrowsChanged, Token fromToken) {
		// Update editArea & StatusBar
		if (arrowsChanged) {
			editArea.displaySentence();
		}

		// Re-select the arrow that starts from fromToken
		if (fromToken != null) {
			Arrow arrows[] = editArea.getArrows();
			int arrowNum = editArea.getArrowNum();
			for (int i = 0; i < arrowNum; ++i) {
				if (arrows[i].getFromToken() == fromToken) {
					arrows[i].toggleSelected(false);
					break;
				}
			}
		}

		// Set status bar
		this.setStatusBarPrompt("就绪");
	}

	public void updateMainToolBar() {
		Selectable item = editArea.getSelectedItem();
		Sentence newSent = editArea.getSent();
		if (newSent == null) {
			tgbtnNewArrow.setEnabled(false);
			btnDeleteArrow.setEnabled(false);
			cbFromWord.setEnabled(false);
			cbRelate.setEnabled(false);
			cbToWord.setEnabled(false);
		} else {
			if (item == null) {
				tgbtnNewArrow.setEnabled(true);
				btnDeleteArrow.setEnabled(false);
				btnUnselect.setEnabled(false);
				cbFromWord.setEnabled(false);
				cbRelate.setEnabled(false);
				cbToWord.setEnabled(false);
			} else {
				btnUnselect.setEnabled(true);
				if (item.getClass() == Arrow.class) {
					Arrow arrow = (Arrow) item;

					tgbtnNewArrow.setEnabled(false);
					btnDeleteArrow.setEnabled(true);

					cbFromWord.setModel(new DefaultComboBoxModel(newSent
							.getWords()));
					cbToWord.setModel(new DefaultComboBoxModel(newSent
							.getWords()));
					cbToWord.addItem(Constants.WORD_EOS);

					removePunctuation(cbFromWord);
					removePunctuation(cbToWord);

					cbFromWord.setSelectedItem(arrow.getFromToken().getWord());
					cbToWord.setSelectedItem(arrow.getToToken().getWord());
					cbRelate.setSelectedItem(arrow.getRelate());

					cbFromWord.setEnabled(true);
					cbToWord.setEnabled(true);
					cbRelate.setEnabled(true);
				} else {
					tgbtnNewArrow.setEnabled(false);
					btnDeleteArrow.setEnabled(false);
					cbFromWord.setEnabled(false);
					cbRelate.setEnabled(false);
					cbToWord.setEnabled(false);
				}
			}
		}
	}

	private void removePunctuation(JComboBox cb) {
		ArrayList<Word> puncts = new ArrayList<Word>();
		for (int i = 0; i < cb.getItemCount(); ++i) {
			Word word = (Word) cb.getItemAt(i);
			if (word.getPos() == POS.WP) {
				puncts.add(word);
			}
		}

		for (ListIterator<Word> it = puncts.listIterator(); it.hasNext();) {
			cb.removeItem(it.next());
		}
	}

	public void selectedItemChanged() {
		Selectable item = editArea.getSelectedItem();
		itemBufferManager.setItemBuffer(item);
		updateMainToolBar();
	}

	private class ItemBufferManager {
		private Selectable itemBuffer;
		private boolean itemBufferReady;
		private Thread task;

		public ItemBufferManager() {
			super();
			this.itemBuffer = null;
			this.itemBufferReady = false;
		}

		synchronized public void makeItemBufferReady(Thread task) {
			itemBufferReady = true;
			this.task = task;
		}

		synchronized public Selectable getItemBuffer() {
			return itemBuffer;
		}

		synchronized public boolean setItemBuffer(Selectable item) {
			if (!itemBufferReady) {
				return false;
			}

			itemBuffer = null;
			// If the user switch to another sentence, or clicks an arrow, set
			// itemBffer null to cancel the task.
			if (item != null && item.getClass() == Token.class) {
				item.toggleSelected(true);
				itemBuffer = item;
			}
			itemBufferReady = false;

			synchronized (task) {
				// System.out.println(fromToken.getWord().getCont());
				task.notify();
			}
			return true;
		}
	}

	private class NewArrowTask extends Thread {
		private Token tokenPair[] = new Token[2];

		private void taskCancelledUIUpdate() {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					tgbtnNewArrow.setSelected(false);
					setStatusBarPrompt("操作已取消！");
				}
			});
		}

		private boolean checkItem(Selectable item) {
			// The user switches the sentence
			if (item == null)
				return false;
			// An arrow is selected
			if (item.getClass() != Token.class)
				return false;

			Token token = (Token) item;
			// A punctuation is selected
			if (token.getWord().getPos() == POS.WP)
				return false;
			return true;
		}

		public void run() {
			for (int i = 0; i < tokenPair.length; ++i) {
				EventQueue.invokeLater(new SetStatusBar(ToolBars.this, "Step "
						+ (i + 1)
						+ ": "
						+ ((i == 0) ? "请点击子Word（即箭头尾端的Word）"
								: "请点击父Word（即箭头指向的Word）"), null));
				itemBufferManager.makeItemBufferReady(this);
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Selectable item = itemBufferManager.getItemBuffer();
				if (checkItem(item)) {
					tokenPair[i] = (Token) item;
				} else {
					taskCancelledUIUpdate();
					return;
				}
			} // for (int i = 0 ...)

			//
			if (tokenPair[0].getWord() == Constants.WORD_EOS) {
				EventQueue.invokeLater(new ErrMsg("箭头不能起始于EOS（EOS不能有Parent）！",
						"操作已取消。"));
				taskCancelledUIUpdate();
				return;
			}

			// No need to treat EOS specially when it is a parent
			tokenPair[0].getWord().setParent(tokenPair[1].getWord().getId());

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					tgbtnNewArrow.setSelected(false);
					postTaskUIUpdate(true, tokenPair[0]);
				}
			});
		} // run()
	} // class NewArrowTask
} // class ToolBars
