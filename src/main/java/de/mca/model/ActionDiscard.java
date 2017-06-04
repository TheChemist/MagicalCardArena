package de.mca.model;

import java.util.EventObject;

import de.mca.model.interfaces.IsPlayer;

@SuppressWarnings("serial")
public class ActionDiscard extends EventObject {

	private IsPlayer player;

	public ActionDiscard(Object source, IsPlayer player) {
		super(source);
		this.player = player;
	}

	public IsPlayer getPlayer() {
		return player;
	}

}
