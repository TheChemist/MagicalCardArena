package de.mca.presenter;

import java.util.EventObject;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class ProgressNameChange extends EventObject {

	private String name;

	public ProgressNameChange(Object source, String name) {
		super(source);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
