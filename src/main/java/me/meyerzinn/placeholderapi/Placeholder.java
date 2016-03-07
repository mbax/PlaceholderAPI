package me.meyerzinn.placeholderapi;

import org.spongepowered.api.entity.living.player.User;

import java.util.regex.Pattern;

/**
 * Created by meyerzinn on 3/6/16.
 */
public interface Placeholder {

    String replace(User user, Pattern pattern, String placeholder);

}
