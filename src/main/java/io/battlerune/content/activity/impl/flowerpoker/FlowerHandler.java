package io.battlerune.content.activity.impl.flowerpoker;

import java.util.Random;

import io.battlerune.content.clanchannel.content.ClanViewer;
import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.task.TaskManager;
import io.battlerune.game.task.impl.ObjectPlacementEvent;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.object.ObjectDirection;
import io.battlerune.game.world.object.ObjectType;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.game.world.region.RegionManager;

/**
 * Handles the flower clicking
 * 
 * @author Harryl / Nerik#8690
 *
 */
public class FlowerHandler {

	private Player player;
	private Random random = new Random();
	private FlowerData[] flower = FlowerData.values();
	private FlowerData tempFlower;

	public FlowerHandler(Player player) {
		this.player = player;
	}

	public FlowerHandler(Player player, FlowerData data) {
		this.player = player;
		this.tempFlower = data;
	}

	public FlowerData getTempFlower() {
		return tempFlower;
	}

	public void setTempFlower(FlowerData tempFlower) {
		this.tempFlower = tempFlower;
	}

	public void plantFlower(boolean skip) {
		if (!skip) {
			setTempFlower(getFlower());
		}
		if (onFlower(player)) {
			player.message("You can't plant a flower on another flower!");
			return;
		}

		TaskManager.schedule(new Task(true, 1) {
			int tick = 0;

			@Override
			protected void execute() {
				switch (tick) {
				case 0:
					player.animate(new Animation(827));
					CustomGameObject gameObject = new CustomGameObject(getTempFlower().getObjectId(),
							player.getPosition().copy(), ObjectDirection.valueOf(0).orElse(ObjectDirection.WEST),
							ObjectType.INTERACTABLE);
					World.schedule(new ObjectPlacementEvent(gameObject, 200));
					player.message(
							"You have planted @red@" + ItemDefinition.get(getTempFlower().getItemId()).getName());
					break;
				case 1:
					player.walkExactlyTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY(),
							player.getPosition().getHeight()));
					break;
				}
				tick++;
			}
		});
	}


	private boolean onFlower(Mob mob) {
		for (Region region : RegionManager.getSurroundingRegions(mob.getPosition())) {
			for (GameObject object : region.getGameObjects(mob.getPosition())) {
				if (mob.getPosition().equals(object.getPosition())) {
					return true;
				}
			}
		}
		return false;
	}

	private FlowerData getFlower() {
		if (random.nextInt(1001) < 2) {
			return random.nextInt(2) == 1 ? FlowerData.BLACK_FLOWERS : FlowerData.WHITE_FLOWERS;
		} else {
			return flower[random.nextInt(7)];
		}
	}
}
