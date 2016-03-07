package me.meyerzinn.placeholderapi;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.spongepowered.api.entity.living.player.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by meyerzinn on 3/6/16.
 */
public class RegexPlaceholderService implements PlaceholderService {

    private final Map<Pattern, Placeholder> PLACEHOLDERS = new HashMap<>();

    private final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\}]*)\\}");

    // Yes, we need a lot of Pattern objects. The speed benefit makes up for it.
    private final LoadingCache<String, Pattern> PATTERN_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(
            new CacheLoader<String, Pattern>() {
                @Override public Pattern load(String key) throws Exception {
                    Matcher matcher = null;
                    for (Pattern p : PLACEHOLDERS.keySet()) {
                        if (matcher == null) {
                            matcher = p.matcher(key);
                        } else {
                            matcher.reset();
                            matcher.usePattern(p);
                        }
                        if (matcher.find()) {
                            return p;
                        }
                    }
                    throw new Exception();
                }
            }
    );

    @Override
    public String replace(Optional<User> user, String input) {
        String output = input;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        while (matcher.find()) {
            try {
                Placeholder placeholder = PLACEHOLDERS.get(PATTERN_CACHE.get(matcher.group(1)));
                output = output.replaceAll("\\{" + matcher.group(1) + "\\}", placeholder.replace(user, PATTERN_CACHE.get(matcher.group(1)), matcher
                        .group(1)));
            } catch (Exception e) {
                continue;
            }
        }
        return output;
    }

    @Override public void registerPlaceholder(Pattern regex, Placeholder replacer) {
        PLACEHOLDERS.put(regex, replacer);
    }

    @Override public void removePlaceholder(Pattern key) {
        PATTERN_CACHE.invalidate(key);
    }

    @Override public Optional<Placeholder> getPlaceholder(String regex) {
        try {
            return Optional.of(PLACEHOLDERS.get(PATTERN_CACHE.get(regex)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
