package me.meyerzinn.placeholderapi;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.spongepowered.api.entity.living.player.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by meyerzinn on 3/6/16.
 */
public class RegexPlaceholderService implements PlaceholderService {

    private Map<Pattern, Placeholder> placeholders = new HashMap<>();

    private final Pattern placeholderPattern = Pattern.compile("/(\\{)(.*)(\\})/g");

    private LoadingCache<String, Optional<Placeholder>> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(
            new CacheLoader<String, Optional<Placeholder>>() {
                @Override public Optional<Placeholder> load(String key) {
                    Matcher matcher = null;
                    for (Pattern p : placeholders.keySet()) {
                        if (matcher == null) {
                            matcher = p.matcher(key);
                        } else {
                            matcher.reset();
                            matcher.usePattern(p);
                        }
                        if (matcher.find()) {
                            return Optional.of(placeholders.get(p));
                        }
                    }
                    return Optional.empty();
                }
            });

    @Override
    public String replace(User user, String input) {
        String output = input;
        Matcher matcher = placeholderPattern.matcher(input);
        while (matcher.find()) {
            try {
                Optional<Placeholder> placeholder = cache.get(matcher.group(1));
                if (placeholder.isPresent()) {
                    output = placeholder.get().replace(user, output);
                }
            } catch (ExecutionException e) {
                continue;
            }
        }
        return output;
    }

    @Override public void registerPlaceholder(Pattern regex, Placeholder replacer) {
        placeholders.put(regex, replacer);
    }
}
