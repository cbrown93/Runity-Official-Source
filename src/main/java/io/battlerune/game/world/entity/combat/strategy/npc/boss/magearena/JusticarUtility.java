package io.battlerune.game.world.entity.combat.strategy.npc.boss.magearena;

import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.Direction;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.Utility;

/**
 * Created by Adam_#6732
 */
public class JusticarUtility {

    public static Npc generatejusiticarSpawn() {
        SpawnData spawn = SpawnData.generate();
        Npc jusiticar = new Npc(7858, spawn.position, 10, Direction.NORTH);
        World.sendMessage("<col=8714E6> Justicar has just spawned! He is located at " + spawn.location + "!");
       World.sendBroadcast(1, "The Justicar boss has spawned!" + spawn.location + "!", true);
       jusiticar.register();
       jusiticar.definition.setRespawnTime(-1);
       jusiticar.definition.setAggressive(true);
       jusiticar.speak("Darkness is here to penetrate your souls!");
        return jusiticar;
    }



    public static void defeated(Npc jusiticar, Player player) {
    	
    	
        boolean hasClan = player.clanChannel != null;

        if (hasClan) {
            player.clanChannel.getDetails().points += 5;
            player.clanChannel.addExperience(10000);
            World.sendMessage("<col=8714E6> jusiticar has been defeated by " + player.getName() + ", a clan member of " + player.clanChannel.getName() + "!");
            player.clanChannel.message("Hell yeah boys! We just killed jusiticar!! We earned 10,000 EXP & 5 CP!");
        } else {
            World.sendMessage("<col=8714E6> jusiticar has been defeated by " + player.getName() + ", a solo individual with balls of steel!");            
            }

        jusiticar.unregister();
    }

    public enum SpawnData {
        LEVEL_19("lvl 15 wild near dark warrior's fortress", new Position(3009, 3637, 0)),
        LEVEL_28("lvl 54 wild near wilderness resource area", new Position(3184, 3948, 0)),
        LEVEL_41("lvl 52 wild near Rouges Castle", new Position(3270, 3933, 0)),
        LEVEL_52("lvl 1 wild North of edgeville", new Position(3100, 3528, 0)),
        LEVEL_53("lvl 19 wild near graveyard of shadows", new Position(3146, 3672, 0));

        public final String location;
        public final Position position;

        SpawnData(String location, Position position) {
            this.location = location;
            this.position = position;
        }

        public static SpawnData generate() {
            return Utility.randomElement(values());
        }
    }
}
