package io.battlerune.content.activity.impl.flowerpoker;

import java.util.Random;


import io.battlerune.game.Animation;
import io.battlerune.game.task.impl.ObjectPlacementEvent;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.object.ObjectDirection;
import io.battlerune.game.world.object.ObjectType;
import io.battlerune.game.world.region.Region;
import io.battlerune.game.world.region.RegionManager;
/**
 * Handles the flower clicking
 * @author Harryl / Nerik#8690
 *
 */
public class FlowerHandler {

	private static Random random = new Random();
	private static FlowerData[] flower = FlowerData.values();
	private static FlowerData tempFlower;

	public static FlowerData getTempFlower() {
		return tempFlower;
	}

	public static void setTempFlower(FlowerData tempFlower) {
		FlowerHandler.tempFlower = tempFlower;
	}

	public static void plantFlower(Player player) {
		setTempFlower(getFlower());

		if (onFlower(player)) {
			player.message("You can't plant a flower on another flower!");
			return;
		}

		player.animate(new Animation(827));

		CustomGameObject gameObject = new CustomGameObject(getTempFlower().getObjectId(), player.getPosition().copy(),
				ObjectDirection.valueOf(0).orElse(ObjectDirection.WEST), ObjectType.INTERACTABLE);
		World.schedule(new ObjectPlacementEvent(gameObject, 50));

		player.message("You have planted " + ItemDefinition.get(getTempFlower().getItemId()).getName());
	}

	public static boolean onFlower(Mob mob) {
		for (Region region : RegionManager.getSurroundingRegions(mob.getPosition())) {
			for (GameObject object : region.getGameObjects(mob.getPosition())) {
				if (mob.getPosition().equals(object.getPosition())) {
					return true;
				}
			}
		}
		return false;
	}

	private static FlowerData getFlower() {
		return flower[random.nextInt(flower.length)];
	}
}