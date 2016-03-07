package me.meyerzinn.placeholderapi;

import org.spongepowered.api.entity.living.player.User;

import java.util.regex.Pattern;

/**
 * Created by meyerzinn on 3/6/16.
 */
public interface PlaceholderService {

    String replace(User player, Pattern pattern, String input);

    void registerPlaceholder(Pattern regex, Placeholder replacer);

}
