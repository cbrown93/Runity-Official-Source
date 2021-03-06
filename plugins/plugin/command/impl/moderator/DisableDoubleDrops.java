package plugin.command.impl.moderator;

import io.battlerune.Config;
import io.battlerune.content.command.Command;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;

/**
 * 
 * @author Adam_#6723
 *
 */

public class DisableDoubleDrops implements Command {

	@Override
	public void execute(Player player, String command, String[] parts) {
		Config.DOUBLE_DROPS = false;
	}
	@Override
	public boolean canUse(Player player) {
		return PlayerRight.isPriviledged(player);
	}

}
