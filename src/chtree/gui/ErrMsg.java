package chtree.gui;

import javax.swing.JOptionPane;

import chtree.Constants;

public class ErrMsg extends Thread {
	//private Component frame;
	private String desc;	//Description
	private String exMsg;	//Exception.getMessage()

	public ErrMsg(String desc, String exMsg) {
		super();
		this.desc = desc;
		this.exMsg = exMsg;
	}

	public void run() {
		JOptionPane.showMessageDialog(null, desc
				+ Constants.SYSTEM_NEW_LINE + exMsg,
				Constants.ERROR_MSGDIAG_TITLE, JOptionPane.ERROR_MESSAGE);
	}

}
