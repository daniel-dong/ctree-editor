package chtree.gui;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import chtree.Constants;
import chtree.type.Paragraph;
import chtree.type.Sentence;

public class DocTree implements TreeSelectionListener {
	// Container
	private JTree docTree;

	// Outside dependencies
	private Paragraph[] paras;
	private EditArea editArea;

	public DocTree() {
		docTree = new JTree();
		initializeDocTree();
		deactivate();
	}

	public JTree getDocTree() {
		return docTree;
	}

	public Paragraph[] getParas() {
		return paras;
	}

	public EditArea getEditArea() {
		return editArea;
	}

	private void initializeDocTree() {
		docTree.setFont(Constants.UI_FONT);
		docTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		docTree.setLargeModel(true);
	}

	public void activate(Paragraph[] paras, EditArea editArea) {
		this.paras = paras;
		this.editArea = editArea;

		docTree.setModel(createTreeModel());
		docTree.addTreeSelectionListener(this);
		docTree.setEnabled(true);
	}

	public void deactivate() {
		// Disable the JTree first to prevent TreeSelectionEvents from occurring
		// while removing the TreeSelectionListeners.
		docTree.setEnabled(false);

		// Remove all TreeSelectionListeners
		TreeSelectionListener tsl[] = docTree.getTreeSelectionListeners();
		for (int i = 0; i < tsl.length; ++i) {
			docTree.removeTreeSelectionListener(tsl[i]);
		}

		docTree.setModel(Constants.INACTIVE_DOCTREE);

		this.paras = null;
		this.editArea = null;
	}

	private DefaultTreeModel createTreeModel() {
		DefaultMutableTreeNode ndDoc, ndPara, ndSent;
		Sentence sents[];

		ndDoc = new DefaultMutableTreeNode("依存树");
		for (int i = 0; i < paras.length; ++i) {
			ndPara = new DefaultMutableTreeNode("段" + i);

			sents = paras[i].getSentences();
			for (int j = 0; j < sents.length; ++j) {
				ndSent = new DefaultMutableTreeNode(sents[j]);
				ndPara.add(ndSent);
			}
			ndDoc.add(ndPara);
		}

		return new DefaultTreeModel(ndDoc);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) docTree
				.getLastSelectedPathComponent();

		// Check if a node is selected.
		if (node == null) {
			return;
		}

		// check if it is a Sentence node
		Object userObj = node.getUserObject();
		if (userObj.getClass() == Sentence.class) {
			editArea.displaySentence((Sentence) userObj);
		} else {
			editArea.clear();
		}
	}
}
