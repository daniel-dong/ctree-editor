package chtree.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import chtree.Constants;
import chtree.database.TreeIO;

public class MainMenus implements ActionListener {
	// Containers
	private JMenuBar menuBar = new JMenuBar();
	private JMenu mnFile = new JMenu("文件");
	private JMenuItem mntmOpen = new JMenuItem("打开数据库…");
	private JMenuItem mntmUpdate = new JMenuItem("更新数据库");
	private JMenuItem mntmUpdateAs = new JMenuItem("更新数据库到副本…");
	private JMenuItem mntmClose = new JMenuItem("关闭数据库");
	private JMenuItem mntmQuit = new JMenuItem("退出");

	// Outside dependencies
	private TreeIO tIO;
	private ToolBars toolBars;
	private DocTree docTree;
	private EditArea editArea;

	public MainMenus(TreeIO tIO, ToolBars toolBars, DocTree docTree,
			EditArea editArea) {
		this.tIO = tIO;
		this.toolBars = toolBars;
		this.docTree = docTree;
		this.editArea = editArea;

		build();
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public void build() {
		// Enable/disable JMenuItems
		mntmClose.setEnabled(false);
		mntmUpdate.setEnabled(false);
		mntmUpdateAs.setEnabled(false);

		// Build menu tree
		menuBar.add(mnFile);
		mnFile.add(mntmOpen);
		mnFile.add(mntmUpdate);
		mnFile.add(mntmUpdateAs);
		mnFile.add(mntmClose);
		mnFile.add(mntmQuit);

		// Set styles for JMenus & JMenuItems.
		// Add ActionListeners to JMenuItems.
		for (int i = 0; i < menuBar.getMenuCount(); ++i) {
			JMenu mn = menuBar.getMenu(i);
			setStyle(mn);
			for (int j = 0; j < mn.getItemCount(); ++j) {
				JMenuItem mntm = mn.getItem(j);
				setStyle(mntm);
				mntm.addActionListener(this);

			}
		}
	}

	private void setStyle(JComponent cmp) {
		cmp.setFont(Constants.UI_FONT);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) e.getSource();

		if (source == mntmOpen) {
			JFileChooser chooser = new JFileChooser(".");
			int isApproved = chooser.showOpenDialog(null);
			if (isApproved == JFileChooser.APPROVE_OPTION) {
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				new OpenTask(filePath).start();
			}
			return;
		}

		if (source == mntmUpdate) {
			new UpdateTask().start();
			return;
		}

		if (source == mntmUpdateAs) {
			JFileChooser chooser = new JFileChooser(".");
			int isApproved = chooser.showSaveDialog(null);
			if (isApproved == JFileChooser.APPROVE_OPTION) {
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				new UpdateAsTask(filePath).start();
			}
			return;
		}

		if (source == mntmClose) {
			toolBars.setStatusBarPrompt("正在关闭依存树数据库…");
			mntmUpdate.setEnabled(false);
			mntmUpdateAs.setEnabled(false);
			mntmClose.setEnabled(false);

			editArea.reset();
			docTree.deactivate();

			try {
				tIO.closeLibrary();
			} catch (Exception ex) {
			} finally {
				toolBars.setStatusBarPrompt("就绪");
				toolBars.setStatusBarLibStatus(tIO.getLibName());
			}

			mntmOpen.setEnabled(true);
			return;
		}

		if (source == mntmQuit) {
			toolBars.setStatusBarPrompt("正在退出…");
			editArea.reset();
			docTree.deactivate();

			try {
				tIO.closeLibrary();
			} catch (Exception ex) {
			}

			System.exit(0);
		}
	}

	private class OpenTask extends Thread {
		private String filePath;

		public OpenTask(String filePath) {
			this.filePath = filePath;
		}

		public void run() {
			try {
				EventQueue.invokeLater(new SetStatusBar(toolBars,
						"正在打开依存树数据库…", null));
				tIO.openLibrary(filePath);

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						mntmOpen.setEnabled(false);
						docTree.activate(tIO.getParas(), editArea);
						mntmUpdate.setEnabled(true);
						mntmUpdateAs.setEnabled(true);
						mntmClose.setEnabled(true);
					}
				});
			} catch (Exception ex) {
				ex.printStackTrace();
				EventQueue.invokeLater(new ErrMsg("打开汉语依存树数据库失败！", ex
						.getMessage()));
			} finally {
				EventQueue.invokeLater(new SetStatusBar(toolBars, "就绪", tIO
						.getLibName()));
			}
		}
	}

	private class UpdateTask extends Thread {
		public void run() {
			try {
				EventQueue.invokeLater(new SetStatusBar(toolBars,
						"正在更新依存树数据库…", null));
				tIO.updateLibrary();

			} catch (Exception ex) {
				ex.printStackTrace();
				EventQueue.invokeLater(new ErrMsg("更新汉语依存树数据库 "
						+ tIO.getLibName() + " 失败！", ex.getMessage()));
			} finally {
				EventQueue.invokeLater(new SetStatusBar(toolBars, "就緒", tIO
						.getLibName()));
			}
		}
	}

	private class UpdateAsTask extends Thread {
		private String filePath;

		public UpdateAsTask(String filePath) {
			this.filePath = filePath;
		}

		public void run() {
			try {
				EventQueue.invokeLater(new SetStatusBar(toolBars,
						"正在更新依存树数据库到副本…", null));
				tIO.updateLibraryAs(filePath);

			} catch (Exception ex) {
				ex.printStackTrace();
				EventQueue.invokeLater(new ErrMsg("更新汉语依存树数据库到副本 " + filePath
						+ " 失败！", ex.getMessage()));
			} finally {
				EventQueue.invokeLater(new SetStatusBar(toolBars, "就緒", tIO
						.getLibName()));
			}
		}
	}
}