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

    private Map<String, Placeholder> placeholders = new HashMap<>();

    private final Pattern placeholderPattern = Pattern.compile("\\{([^\\}]*)\\}");

    private LoadingCache<String, Pattern> compiledPatterns = CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>() {
        @Override public Pattern load(String key) {
            return Pattern.compile(key);
        }
    });

    private LoadingCache<String, Placeholder> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(
            new CacheLoader<String, Placeholder>() {
                @Override public Placeholder load(String key) throws Exception {
                    Matcher matcher = null;
                    for (String s : placeholders.keySet()) {
                        Pattern p = compiledPatterns.getUnchecked(s);
                        if (matcher == null) {
                            matcher = p.matcher(key);
                        } else {
                            matcher.reset();
                            matcher.usePattern(p);
                        }
                        if (matcher.find()) {
                            return placeholders.get(p.pattern());
                        }
                    }
                    throw new Exception();
                }
            }
    );

    @Override
    public String replace(User user, String input) {
        String output = input;
        Matcher matcher = placeholderPattern.matcher(input);
        while (matcher.find()) {
            try {
                Placeholder placeholder = cache.get(matcher.group(1));
                output = output.replaceAll("\\{" + matcher.group(1) + "\\}", placeholder.replace(user, matcher.group(1)));
            } catch (Exception e) {
                continue;
            }
        }
        return output;
    }

    @Override public void registerPlaceholder(String regex, Placeholder replacer) {
        placeholders.put(regex, replacer);
    }

    @Override public Placeholder getPlaceholder(String regex) {
        return placeholders.get(regex);
    }
}
