package de.maxikg.pluginmessaging.bukkit;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public enum Effect {

    ROCKET() {
        @Override
        public void play(Player player) {
            player.setVelocity(new Vector(Math.random() / 4, Math.random() * 4, Math.random() / 4));
        }
    },
    HURT() {
        @Override
        public void play(Player player) {
            player.playEffect(EntityEffect.HURT);
        }
    };

    public abstract void play(Player player);

    public static Effect match(String part) {
        String normalized = part.toLowerCase();
        for (Effect effect : Effect.values()) {
            if (effect.name().toLowerCase().startsWith(normalized))
                return effect;
        }

        return null;
    }
}
