package me.meyerzinn.placeholderapi;

import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * Created by meyerzinn on 3/6/16.
 */
public interface PlaceholderService {

    String replace(@Nonnull Optional<User> player, String input);

    void registerPlaceholder(Pattern regex, Placeholder replacer);

    /**
     * Removes a placeholder from the registry based on a key.
     * NOTE: This does NOT clear the cached version. It merely prevents it from being loaded again if it is invalidated from the cache.
     * @param key the identifier
     */
    void removePlaceholder(Pattern key);

    /**
     * Allows you to retrieve a Placeholder by an input (eg. number:100).
     * @param input
     * @return The Placeholder, if found.
     */
    Optional<Placeholder> getPlaceholder(String input);
}
