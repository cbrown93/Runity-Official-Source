package io.battlerune.game.world.items.containers.inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.ItemContainer;
import io.battlerune.game.world.items.containers.ItemContainerAdapter;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.net.packet.out.SendItemOnInterface;
import io.battlerune.net.packet.out.SendItemOnInterfaceSlot;
import io.battlerune.net.packet.out.SendMessage;

/**
 * An {@link ItemContainer} implementation that manages the inventory for a
 * {@link Player}.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Inventory extends ItemContainer {

	/** The size of all equipment instances. */
	public static final int SIZE = 28;

	/** The inventory item display widget identifier. */
	public static final int INVENTORY_DISPLAY_ID = 3214;

	/** The player instance for which this inventory applies to. */
	private final Player player;

	/** Creates a new {@link Inventory}. */
	public Inventory(Player player) {
		super(SIZE, StackPolicy.STANDARD, true);
		addListener(new InventoryListener(player));
//		addListener(new ItemWeightListener(player));TODO
		this.player = player;
	}

	/** Refreshes the players inventory. */
	public void refresh() {
		refresh(player, INVENTORY_DISPLAY_ID);
	}

	/**
	 * Attempts to deposit the {@code items} to the inventory, if inventory is full
	 * it'll execute the {@code action} for the remaining items that were not added.
	 */
	public void addOrExecute(Consumer<Item> action, Optional<String> message, List<Item> items) {
		boolean val = false;
		for (Item item : items) {
			if (item == null)
				continue;
			if (hasCapacityFor(item)) {
				player.inventory.add(item);
			} else {
				action.accept(item);
				val = true;
			}
		}
		if (val) {
			message.ifPresent(m -> player.send(new SendMessage(m)));
		}
	}

	/**
	 * Attempts to deposit the {@code items} to the inventory, if inventory is full
	 * it'll execute the {@code action} for the remaining items that were not added.
	 */
	public void addOrExecute(Consumer<Item> action, Optional<String> message, Item... items) {
		addOrExecute(action, message, Arrays.asList(items));
	}

	/**
	 * Attempts to deposit the {@code items} to the inventory, if inventory is full
	 * it'll execute the {@code action} for the remaining items that were not added.
	 */
	public void addOrExecute(Consumer<Item> action, String message, Item... items) {
		addOrExecute(action, Optional.of(message), Arrays.asList(items));
	}

	/**
	 * Attempts to deposit the {@code items} to the inventory, if inventory is full
	 * it'll execute the {@code action} for the remaining items that were not added.
	 */
	public void addOrExecute(Consumer<Item> action, String message, List<Item> items) {
		addOrExecute(action, Optional.of(message), items);
	}

	/**
	 * Attempts to deposit the {@code items} to the inventory, if inventory is full
	 * it'll execute the {@code action} for the remaining items that were not added.
	 */
	public void addOrExecute(Consumer<Item> action, List<Item> items) {
		addOrExecute(action, Optional.empty(), items);
	}

	/**
	 * Attempts to deposit the {@code items} to the inventory, if inventory is full
	 * it'll execute the {@code action} for the remaining items that were not added.
	 */
	public void addOrExecute(Consumer<Item> action, Item... items) {
		addOrExecute(action, Arrays.asList(items));
	}

	/**
	 * Attempts to deposit an item to the players inventory, if there is no space
	 * it'll bank the item instead.
	 */
	public void addOrDrop(List<Item> items) {
		addOrExecute(t -> GroundItem.create(player, t), "Some of the items were dropped beneath you instead...", items);
	}

	/**
	 * Attempts to deposit an item to the players inventory, if there is no space
	 * it'll bank the item instead.
	 */
	public void addOrDrop(Item... items) {
		addOrDrop(Arrays.asList(items));
	}

	/**
	 * Attempts to deposit an item to the players inventory, if there is no space
	 * it'll bank the item instead.
	 */
	public void addOrBank(List<Item> items) {
		addOrExecute(t -> player.bank.depositFromNothing(t, 0), "Some of the items were banked instead...", items);
	}

	/**
	 * Attempts to deposit an item to the players inventory, if there is no space
	 * it'll bank the item instead.
	 */
	public void addOrBank(Item... items) {
		addOrBank(Arrays.asList(items));
	}

	/** Refreshes the inventory container. */
	@Override
	public void refresh(Player player, int widget) {
		player.send(new SendItemOnInterface(widget, toArray()), true);
	}

	public int getTotalItemCount() {
		int totalItemCount = 0;

		for (Item i : getItems()) {
			if (i != null) {
				totalItemCount++;
			}
		}

		return totalItemCount;
	}

	/**
	 * An {@link ItemContainerAdapter} implementation that listens for changes to
	 * the inventory.
	 */
	private final class InventoryListener extends ItemContainerAdapter {

		/** Creates a new {@link InventoryListener}. */
		InventoryListener(Player player) {
			super(player);
		}

		@Override
		public void itemUpdated(ItemContainer container, Optional<Item> oldItem, Optional<Item> newItem, int index,
				boolean refresh) {
			player.send(new SendItemOnInterfaceSlot(getWidgetId(), newItem.orElse(null), index), true);
		}

		@Override
		public int getWidgetId() {
			return INVENTORY_DISPLAY_ID;
		}

		@Override
		public String getCapacityExceededMsg() {
			return "You do not have enough space in your inventory.";
		}
	}
}