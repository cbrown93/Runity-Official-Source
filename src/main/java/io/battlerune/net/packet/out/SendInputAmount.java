package io.battlerune.net.packet.out;

import java.util.Optional;
import java.util.function.Consumer;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

/**
 * Sends an input dialogue.
 * 
 * @author Daniel
 * @author Michael
 */
public class SendInputAmount extends OutgoingPacket {

	private final Consumer<String> action;
	private final String inputMessage;
	private final int inputLength;

	public SendInputAmount(Consumer<String> action) {
		this("Enter an amount:", 10, action);
	}

	public SendInputAmount(String message, int length, Consumer<String> action) {
		super(27, PacketType.VAR_SHORT);
		this.action = action;
		this.inputMessage = message;
		this.inputLength = length;
	}

	@Override
	public boolean encode(Player player) {
		player.enterInputListener = Optional.of(action);
		System.out.println(inputMessage + " : " + inputLength + " : " + player.enterInputListener.toString());
		builder.writeString(inputMessage).writeShort(inputLength, ByteModification.ADD);
		return true;
	}

}
