package chtree.gui;

public class SetStatusBar extends Thread {
	private ToolBars toolBars;
	private String prompt;
	private String libStatus;

	public SetStatusBar(ToolBars toolBars, String prompt, String libStatus) {
		super();
		this.toolBars = toolBars;
		this.prompt = prompt;
		this.libStatus = libStatus;
	}

	public void run() {
		if (prompt != null)
			toolBars.setStatusBarPrompt(prompt);
		if (libStatus != null)
			toolBars.setStatusBarLibStatus(libStatus);
	}
}
