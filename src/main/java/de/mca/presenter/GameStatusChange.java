package de.mca.presenter;

import java.util.EventObject;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class GameStatusChange extends EventObject {

	private boolean disableProgressButton;
	private String progressButtonText;

	public GameStatusChange(Object source, String name, boolean disableButton) {
		super(source);
		this.progressButtonText = name;
		this.disableProgressButton = disableButton;
	}

	public boolean getDisableProgressButton() {
		return disableProgressButton;
	}

	public String getProgressButtonText() {
		return progressButtonText;
	}

}
