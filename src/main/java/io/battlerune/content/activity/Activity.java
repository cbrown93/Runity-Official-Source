package io.battlerune.content.activity;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.battlerune.Config;
import io.battlerune.content.activity.panel.Activity_Panel;
import io.battlerune.content.consume.FoodData;
import io.battlerune.content.event.EventDispatcher;
import io.battlerune.content.event.InteractionEvent;
import io.battlerune.content.event.InteractionEventListener;
import io.battlerune.content.event.impl.ClickButtonInteractionEvent;
import io.battlerune.content.event.impl.FirstItemClickInteractionEvent;
import io.battlerune.content.event.impl.FirstNpcClick;
import io.battlerune.content.event.impl.FirstObjectClick;
import io.battlerune.content.event.impl.ItemContainerInteractionEvent;
import io.battlerune.content.event.impl.ItemInteractionEvent;
import io.battlerune.content.event.impl.ItemOnItemInteractionEvent;
import io.battlerune.content.event.impl.ItemOnObjectInteractionEvent;
import io.battlerune.content.event.impl.NpcInteractionEvent;
import io.battlerune.content.event.impl.ObjectInteractionEvent;
import io.battlerune.content.event.impl.PickupItemInteractionEvent;
import io.battlerune.content.event.impl.SecondItemClickInteractionEvent;
import io.battlerune.content.event.impl.SecondNpcClick;
import io.battlerune.content.event.impl.SecondObjectClick;
import io.battlerune.content.event.impl.ThirdItemClickInteractionEvent;
import io.battlerune.content.event.impl.ThirdObjectClick;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.Entity;
import io.battlerune.game.world.entity.combat.attack.listener.CombatListener;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.NpcDeath;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.equipment.EquipmentType;

/**
 * A {@code Activity} object constructs an in-game activity and sequences it
 * through the {@link #start()} and {@link #finish()} methods with a {@code
 * cooldown} set in game ticks.
 *
 * @author Michael | Chex
 */
public abstract class Activity implements InteractionEventListener {

	/** The 'start' cooldown id. */
	protected static final int START = 0;

	/** The 'finish' cooldown id. */
	protected static final int FINISH = -1;

	/** The 'pause' cooldown id. */
	protected static final int PAUSE = -2;

	/** The sequencing cooldown. */
	private final int cooldown;

	/** The activity instance level. */
	private int instance;

	/** The remaining game ticks. */
	private int ticks;

	/** The panel for this activity. */
	private Activity_Panel panel;

	protected void restart(int delay, Runnable runnable) {
		World.schedule(delay, runnable::run);
	}

	/** Constructs a new {@code SequencedMinigame} object. */
	public Activity(int cooldown, int instance) {
		this.cooldown = cooldown;
		this.instance = instance;
	}

	public static <T extends Activity> Optional<T> search(Player player, Class<T> clazz) {
		final Activity activity = player.activity;

		if (activity == null) {
			return Optional.empty();
		}

		if (clazz.isInstance(activity)) {
			return Optional.of(clazz.cast(activity));
		}

		return Optional.empty();
	}

	public static boolean evaluate(Mob mob, Predicate<Activity> predicate) {
		return mob != null && mob.activity != null && predicate.test(mob.activity);
	}

	public static void forActivity(Mob mob, Consumer<Activity> consumer) {
		if (mob == null) {
			return;
		}

		if (mob.activity == null) {
			return;
		}

		consumer.accept(mob.activity);
	}

	public boolean canEquipItem(Player player, Item item, EquipmentType type) {
		return true;
	}

	public boolean canEat(Player player, FoodData foodType) {
		return true;
	}

	public boolean canUseSpecial(Player player) {
		return true;
	}

	public boolean canUsePrayer(Player player) {
		return true;
	}

	public boolean canDrinkPotions(Player player) {
		return true;
	}

	public boolean canLogout(Player player) {
		return true;
	}

	public boolean canSpellCast(Player player) {
		return true;
	}

	/** Sequences the activity. */
	public void sequence() {
		update();

		if (isPaused()) {
			return;
		}

		if (ticks > 0) {
			ticks--;
		} else if (ticks == START) {
			start();
		} else if (ticks == FINISH) {
			finish();
		}
	}

	/** Starts the next activity stage. */
	protected abstract void start();

	/** Finishes the activity. */
	public abstract void finish();

	/** Cleans up the activity when finished. */
	public abstract void cleanup();

	/** The update method. */
	public void update() {

	}

	public abstract ActivityType getType();

	/** Called when the player logs out. */
	public void onLogout(Player player) {
		remove(player);
	}

	/** Called when the player die */
	public void onDeath(Mob mob) {
		if (mob.isNpc()) {
			World.schedule(new NpcDeath(mob.getNpc()));
			return;
		}
		remove(mob);
		mob.move(Config.DEFAULT_POSITION);
		finish();
	}

	/** Sets the activity panel. */
	public void setPanel(Activity_Panel panel) {
		this.panel = panel;
	}

	/** Gets an optional of the activity panel. */
	public Optional<Activity_Panel> getPanel() {
		return Optional.ofNullable(panel);
	}

	/** Called when the player changes region. */
	public void onRegionChange(Player player) {
		remove(player);
	}

	/** Called when the player attempts to teleport. */
	public boolean canTeleport(Player player) {
		return false;
	}

	/** Adds a mob to the activity. */
	public void add(Mob mob) {
		if (mob.isNpc() && !mob.isRegistered()) {
			mob.register();
		}
		mob.setActivity(this);
		mob.instance = instance;
		getListener().ifPresent(mob.getCombat()::addListener);
		getCombatListener().ifPresent(mob.getCombat()::addListener);
	}

	/** Removes all mobs from the activity. */
	public void removeAll(Mob... mobs) {
		if (mobs.length != 0)
			for (Mob mob : mobs) {
				if (mob.isRegistered())
					remove(mob);
			}
	}

	/** Removes a mob from the activity. */
	public void remove(Mob mob) {
		getListener().ifPresent(mob.getCombat()::removeListener);
		getCombatListener().ifPresent(mob.getCombat()::removeListener);
		if (mob.isNpc()) {
			mob.getNpc().unregister();
		} else {
			mob.instance = Entity.DEFAULT_INSTANCE_HEIGHT;
			mob.getPlayer().setActivity(null);
		}
	}

	/** Sets the pause state of the activity. */
	public void setPause(boolean pause) {
		ticks = pause ? PAUSE : START;
	}

	/** Resets the remaining ticks to the cached cooldown ticks. */
	protected final void resetCooldown() {
		cooldown(cooldown);
	}

	/** Applies a cooldown. */
	public void cooldown(int cooldown) {
		this.ticks = cooldown;
	}

	/** Sets the cooldown flag to {@link #FINISH}. */
	protected final void finishCooldown() {
		ticks = FINISH;
	}

	/** Sets the cooldown flag to {@link #PAUSE}. */
	protected final void pause() {
		ticks = PAUSE;
	}

	/** Checks if the cooldown is paused. */
	public final boolean isPaused() {
		return cooldown == PAUSE;
	}

	public boolean safe() {
		return true;
	}

	/** Gets this activity's instance level. */
	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

	/** Gets the current ticks. */
	public int getTicks() {
		return ticks;
	}

	/**
	 * Gets an {@link Optional} of the {@link ActivityListener} for this activity.
	 */
	protected Optional<? extends ActivityListener<? extends Activity>> getListener() {
		return Optional.empty();
	}

	protected Optional<CombatListener> getCombatListener() {
		return Optional.empty();
	}

	protected boolean clickItem(Player player, ItemInteractionEvent event) {
		return false;
	}

	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		return false;
	}

	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		return false;
	}

	protected boolean clickButton(Player player, ClickButtonInteractionEvent event) {
		return false;
	}

	protected boolean useItem(Player player, ItemOnItemInteractionEvent event) {
		return false;
	}

	protected boolean pickupItem(Player player, PickupItemInteractionEvent event) {
		return false;
	}

	protected boolean useItem(Player player, ItemOnObjectInteractionEvent event) {
		return false;
	}

	protected boolean itemContainerAction(Player player, ItemContainerInteractionEvent event) {
		return false;
	}

	@Override
	public boolean onEvent(Player player, InteractionEvent interactionEvent) {
		final EventDispatcher dispatcher = new EventDispatcher(interactionEvent);
		dispatcher.dispatch(InteractionEvent.InteractionType.CLICK_BUTTON,
				e -> clickButton(player, (ClickButtonInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.ITEM_ON_ITEM,
				e -> useItem(player, (ItemOnItemInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.ITEM_ON_OBJECT,
				e -> useItem(player, (ItemOnObjectInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.FIRST_ITEM_CLICK,
				e -> clickItem(player, (FirstItemClickInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.SECOND_ITEM_CLICK,
				e -> clickItem(player, (SecondItemClickInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.THIRD_ITEM_CLICK,
				e -> clickItem(player, (ThirdItemClickInteractionEvent) e));
//		dispatcher.dispatch(InteractionType.FOURTH_ITEM_CLICK, e -> clickItem(player, (FourthItemClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.FIRST_CLICK_NPC, e -> clickNpc(player, (FirstNpcClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.SECOND_CLICK_NPC,
				e -> clickNpc(player, (SecondNpcClick) e));
//		dispatcher.dispatch(InteractionType.CLICK_NPC, e -> clickNpc(player, (ThirdNpcClick) e));
//		dispatcher.dispatch(InteractionType.CLICK_NPC, e -> clickNpc(player, (FourthNpcClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.FIRST_CLICK_OBJECT,
				e -> clickObject(player, (FirstObjectClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.SECOND_CLICK_OBJECT,
				e -> clickObject(player, (SecondObjectClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.THIRD_CLICK_OBJECT,
				e -> clickObject(player, (ThirdObjectClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.PICKUP_ITEM,
				e -> pickupItem(player, (PickupItemInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.ITEM_CONTAINER_INTERACTION_EVENT,
				e -> itemContainerAction(player, (ItemContainerInteractionEvent) e));
		return interactionEvent.isHandled();
	}

	public ActivityDeathType deathType() {
		// TODO Auto-generated method stub
		return null;
	}
}
