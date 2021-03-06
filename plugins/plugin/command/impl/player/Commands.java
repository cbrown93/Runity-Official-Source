package plugin.command.impl.player;

import io.battlerune.content.command.Command;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendURL;

//TODOCOMMAND
public class Commands implements Command {

	@Override
	public void execute(Player player, String command, String[] parts) {
		player.send(new SendURL("https://runity.io/forums/topic/61-runity-commands/"));
		player.message("Opening command list!");
	}

	@Override
	public boolean canUse(Player player) {
		return true;
	}

}
