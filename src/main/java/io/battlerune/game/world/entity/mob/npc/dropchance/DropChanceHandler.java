package io.battlerune.game.world.entity.mob.npc.dropchance;

import io.battlerune.game.world.entity.mob.player.Player;

public class DropChanceHandler {

	private Player player;

	public DropChanceHandler(Player player) {
		this.player = player;
	}

	public int droprate = 0;

	public int getRate() {
		for (int i = 0; i < player.equipment.getEquipment().length; i++) {
			if (player.equipment.getEquipment()[i] != null) {
				for (DropChanceData data : DropChanceData.values()) {
					if (data.getItemId() == player.equipment.getEquipment()[i].getId()) {
						if (droprate >= 100) {
							return 100;
						}
						droprate += data.getModifier();
					}
				}
			}
		}
		return droprate;
	}
}
