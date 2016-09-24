package de.mca;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import de.mca.factories.FactoryAbility;
import de.mca.factories.FactoryEffect;
import de.mca.factories.FactoryMagicPermanent;
import de.mca.factories.FactoryMagicSpell;
import de.mca.factories.FactoryMatch;
import de.mca.factories.FactoryPhase;
import de.mca.factories.FactoryPlayer;
import de.mca.factories.FactoryStep;
import de.mca.factories.FactoryTurn;
import de.mca.factories.FactoryZone;
import de.mca.model.MagicPermanent;
import de.mca.model.Player;
import de.mca.model.ZoneDefault;
import de.mca.model.enums.PlayerType;
import de.mca.model.enums.ZoneType;
import de.mca.model.interfaces.IsPlayer;
import de.mca.model.interfaces.IsZone;

public class MainModule extends AbstractModule {

	private final EventBus eventBus = new EventBus("Default EventBus");

	@Provides
	public ZoneDefault<MagicPermanent> provideBattlefield() {
		return new ZoneDefault<>(PlayerType.NONE, ZoneType.BATTLEFIELD);
	}

	@Override
	protected void configure() {
		bind(MagicParser.class).asEagerSingleton();

		install(new FactoryModuleBuilder().build(FactoryMatch.class));
		install(new FactoryModuleBuilder().build(FactoryTurn.class));
		install(new FactoryModuleBuilder().build(FactoryPhase.class));
		install(new FactoryModuleBuilder().build(FactoryStep.class));
		install(new FactoryModuleBuilder().build(FactoryMagicPermanent.class));
		install(new FactoryModuleBuilder().build(FactoryMagicSpell.class));
		install(new FactoryModuleBuilder().build(FactoryAbility.class));
		install(new FactoryModuleBuilder().build(FactoryEffect.class));

		install(new FactoryModuleBuilder().implement(IsZone.class, ZoneDefault.class).build(FactoryZone.class));
		install(new FactoryModuleBuilder().implement(IsPlayer.class, Player.class).build(FactoryPlayer.class));

		bind(EventBus.class).toInstance(eventBus);
	}

}
