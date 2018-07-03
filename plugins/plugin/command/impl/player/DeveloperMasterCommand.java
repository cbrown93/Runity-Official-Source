package plugin.command.impl.player;

import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.command.Command;
import io.battlerune.content.emote.EmoteHandler;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;

public class DeveloperMasterCommand implements Command {

	@Override
	public void execute(Player player, String[] command) {
        player.skills.master();
        AchievementHandler.completeAll(player);
        EmoteHandler.unlockAll(player);
        player.send(new SendMessage("Your account is now maxed out.", MessageColor.BLUE));

	}

	@Override
	public boolean canUse(Player player) {
		if(PlayerRight.isDeveloper(player)) {
			return true;
		}
		player.speak("Hey everyone, I just tried to do something silly!");
		return false;
	}

}