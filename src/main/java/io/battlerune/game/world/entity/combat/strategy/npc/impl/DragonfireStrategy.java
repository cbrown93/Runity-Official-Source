package io.battlerune.game.world.entity.combat.strategy.npc.impl;

import io.battlerune.game.world.entity.combat.CombatUtil;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;

public class DragonfireStrategy extends NpcMagicStrategy {

	public DragonfireStrategy(CombatProjectile projectileDefinition) {
		super(projectileDefinition);
	}

	@Override
	public int getAttackDistance(Npc attacker, FightType fightType) {
		return 1;
	}

	@Override
	public CombatHit[] getHits(Npc attacker, Mob defender) {
		return new CombatHit[] { CombatUtil.generateDragonfire(attacker, defender, 60, true) };
	}

}