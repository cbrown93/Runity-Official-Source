package io.battlerune.game.world.entity.combat.attack.listener.npc;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;
import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = { 4882 })
public class Karamel extends SimplifiedListener<Npc> {

	private static MagicAttack MAGIC;
	private static CombatStrategy<Npc>[] STRATEGIES;

	static {
		try {
			MAGIC = new MagicAttack();
			STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), MAGIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Npc attacker, Mob defender) {
		if (!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(MAGIC);
		}
		return attacker.getStrategy().canAttack(attacker, defender);
	}

	@Override
	public void start(Npc attacker, Mob defender, Hit[] hits) {
		if (!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(MAGIC);
		} else {
			attacker.setStrategy(randomStrategy(STRATEGIES));
		}
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(getDefinition("Ice Barrage"));
		}

		@Override
		public void hit(Npc attacker, Mob defender, Hit hit) {
			super.hit(attacker, defender, hit);
			attacker.speak("Semolina-Go!");
		}

		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, combatProjectile);
			combatHit.setAccurate(true);
			return new CombatHit[] { combatHit };
		}
	}
}
