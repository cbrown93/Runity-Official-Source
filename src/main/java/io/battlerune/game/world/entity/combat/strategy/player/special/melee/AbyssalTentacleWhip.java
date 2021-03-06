package io.battlerune.game.world.entity.combat.strategy.player.special.melee;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.player.Player;

/**
 * @author Daniel
 */
public class AbyssalTentacleWhip extends PlayerMeleeStrategy {
	private static final Animation ANIMATION = new Animation(1658, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(181, 100);
	private static final AbyssalTentacleWhip INSTANCE = new AbyssalTentacleWhip();

	@Override
	public void attack(Player attacker, Mob defender, Hit hit) {
		super.attack(attacker, defender, hit);
		defender.graphic(GRAPHIC);
	}

	@Override
	public void start(Player attacker, Mob defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		// TODO EFFECT
	}

	@Override
	public CombatHit[] getHits(Player attacker, Mob defender) {
		return new CombatHit[] { nextMeleeHit(attacker, defender) };
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Mob defender) {
		return ANIMATION;
	}

	public static AbyssalTentacleWhip get() {
		return INSTANCE;
	}

}