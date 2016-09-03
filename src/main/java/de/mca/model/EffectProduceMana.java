package de.mca.model;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.mca.model.enums.EffectType;
import de.mca.model.interfaces.IsAbility;
import de.mca.model.interfaces.IsManaMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
final public class EffectProduceMana extends Effect {

	/**
	 * Speichert, wie viel Mana der Effekt produziert.
	 */
	private ObjectProperty<IsManaMap> produceMap;

	@Inject
	EffectProduceMana(@Assisted IsAbility source, @Assisted IsManaMap produceMap) {
		super(source, EffectType.PRODUCE_MANA);
		this.produceMap = new SimpleObjectProperty<>(produceMap);
	}

	public IsManaMap getProduceMap() {
		return produceMap.get();
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(super.toString()).append(" m=").append(produceMap).append("]").toString();
	}
}
