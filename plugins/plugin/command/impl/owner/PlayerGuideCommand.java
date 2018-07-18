package plugin.command.impl.owner;

import io.battlerune.content.command.Command;
import io.battlerune.content.playerguide.PlayerGuideHandler;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.net.packet.out.SendString;

/**
 * @author Adam_#6723
 */

public class PlayerGuideCommand implements Command {

	@Override
	public void execute(Player player, String[] command) {

		PlayerGuideHandler guide = new PlayerGuideHandler();
		guide.open(player);
		player.sendTeleportButton();
		player.sendTeleportButtonNpc(8059);


	}

	@Override
	public boolean canUse(Player player) {
		if (PlayerRight.isDeveloper(player)) {
			return true;
		}
		player.speak("Hey everyone, i just tried doing something silly.");
		return false;
	}

}
