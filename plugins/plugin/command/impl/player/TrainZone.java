package plugin.command.impl.player;

import io.battlerune.Config;
import io.battlerune.content.command.Command;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendMessage;

/**
 * 
 * @author Jordan
 *
 */
public class TrainZone implements Command {

	@Override
	public void execute(Player player, String command, String[] parts) {
		Teleportation.teleport(player, Config.STARTER_ZONE);
		player.send(new SendMessage("You have teleported to the training zone!"));
	}

	@Override
	public boolean canUse(Player player) {
		return true;
	}

}
