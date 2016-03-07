package me.meyerzinn.placeholderapi;

import org.spongepowered.api.entity.living.player.User;

/**
 * Created by meyerzinn on 3/6/16.
 */
public interface Placeholder {

    String replace(User user, String placeholder);

}
