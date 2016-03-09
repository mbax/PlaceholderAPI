package me.meyerzinn.placeholderapi;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.Validate;
import org.spongepowered.api.entity.living.player.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

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
    public String replace(@Nonnull Optional<User> user, String input) {
        Validate.notNull(user);
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        int start = 0;
        StringBuilder builder = new StringBuilder(input.length());
        while (matcher.find()) {
            String group1 = matcher.group(1);
            Pattern pattern;
            try {
                pattern = PATTERN_CACHE.get(group1);
            } catch (Exception ignored) {
                continue;
            }
            builder.append(input.substring(start, matcher.start(1) - 1));
            start = matcher.end(1) + 1;
            builder.append(PLACEHOLDERS.get(pattern).replace(user, pattern, group1));
        }
        if (start < input.length()) {
            builder.append(input.substring(start));
        }
        return builder.toString();
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
