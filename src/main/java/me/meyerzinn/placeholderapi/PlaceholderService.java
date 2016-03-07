package me.meyerzinn.placeholderapi;

import org.spongepowered.api.entity.living.player.User;

/**
 * Created by meyerzinn on 3/6/16.
 */
public interface PlaceholderService {

    String replace(User player, String input);

    void registerPlaceholder(String regex, Placeholder replacer);

}
