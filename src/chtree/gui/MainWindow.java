package chtree.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import chtree.Constants;
import chtree.database.TreeIO;
import chtree.database.TreeXMLHandler;

public class MainWindow {
	// Containers
	private TreeIO tIO;
	private JFrame frame;
	private MainMenus mainMenus;
	private ToolBars toolBars;
	private DocTree docTree;
	private EditArea editArea;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Enable Anti-aliasing
					System.setProperty("awt.useSystemAAFontSettings", "on");
					System.setProperty("swing.aatext", "true");

					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindow() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame(Constants.WINDOW_TITLE);
		tIO = new TreeXMLHandler();
		docTree = new DocTree();
		editArea = new EditArea();
		toolBars = new ToolBars(editArea);
		editArea.setToolBar(toolBars);
		mainMenus = new MainMenus(tIO, toolBars, docTree, editArea);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.setJMenuBar(mainMenus.getMenuBar());
		frame.getContentPane().add(toolBars.getMainToolBar(),
				BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(new JScrollPane(docTree.getDocTree()));
		splitPane.setRightComponent(editArea.getCanvas());
		// splitPane.setDividerLocation(0.5);

		// frame.getContentPane().add(new JScrollPane(docTree.getDocTree()),
		// BorderLayout.WEST);
		// frame.getContentPane().add(editArea.getCanvas(),
		// BorderLayout.CENTER);

		frame.getContentPane().add(toolBars.getStatusBar(), BorderLayout.SOUTH);
	}

}
