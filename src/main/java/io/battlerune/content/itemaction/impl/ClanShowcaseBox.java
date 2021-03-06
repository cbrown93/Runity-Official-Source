package io.battlerune.content.itemaction.impl;

import java.util.ArrayList;
import java.util.List;

import io.battlerune.content.clanchannel.ClanUtility;
import io.battlerune.content.clanchannel.channel.ClanChannel;
import io.battlerune.content.clanchannel.content.ClanLevel;
import io.battlerune.content.itemaction.ItemAction;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

public class ClanShowcaseBox extends ItemAction {

	@Override
	public String name() {
		return "Clan Showcase Box";
	}

	@Override
	public boolean inventory(Player player, Item item, int opcode) {
		if (opcode != 1) {
			return false;
		}
		ClanChannel channel = player.clanChannel;
		if (channel == null) {
			player.send(new SendMessage("You need to be in a clan to do this!"));
			return true;
		}
		if (channel.getShowcase().showcaseItems.size() >= 28) {
			player.send(new SendMessage(
					"You have reached the maximum capacity of showcase items you can hold. Please delete an item to proceed."));
			return true;
		}
		ClanLevel level = channel.getDetails().level;
		List<Item> items = new ArrayList<>();

		for (int reward : ClanUtility.getRewardItems(level)) {
			Item rewardItem = new Item(reward, 1);
			for (Item showcase : channel.getShowcaseItems()) {
				if (rewardItem.getId() != showcase.getId())
					items.add(rewardItem);
			}
		}

		if (items.isEmpty()) {
			return true;
		}

		Item showcaseReward = Utility.randomElement(items);
		player.inventory.remove(item);
		channel.getShowcase().showcaseItems.add(showcaseReward.getId());
		channel.message("We just received " + showcaseReward.getName() + " from the showcase box!");
		return true;
	}
}
