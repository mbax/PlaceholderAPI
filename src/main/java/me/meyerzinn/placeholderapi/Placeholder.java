package me.meyerzinn.placeholderapi;

import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by meyerzinn on 3/6/16.
 */
public interface Placeholder {

    /**
     * Called when a provided placeholder string (such as "number:100") needs to be contextually replaced.
     * @param user (if relevant)
     * @param pattern the pattern used.
     * @param actual the actual match.
     * @return the replacement string.
     */
    String replace(Optional<User> user, Pattern pattern, String actual);

}
