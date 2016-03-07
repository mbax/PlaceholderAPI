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

    private final Pattern placeholderPattern = Pattern.compile("(\\{)(.*)(\\})");

    private LoadingCache<String, Pattern> compiledPatterns = CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>() {
        @Override public Pattern load(String key) {
            return Pattern.compile(key);
        }
    });

    private LoadingCache<String, Optional<Placeholder>> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(
            new CacheLoader<String, Optional<Placeholder>>() {
                @Override public Optional<Placeholder> load(String key) {
                    Matcher matcher = null;
                    for (String s : placeholders.keySet()) {
                        Pattern p = compiledPatterns.getUnchecked(s);
                        if (matcher == null) {
                            matcher = p.matcher(key);
                        } else {
                            matcher.reset();
                            matcher.usePattern(p);
                        }
                        System.out.println("Looking for matches.");
                        if (matcher.find()) {
                            System.out.println("Found!");
                            return Optional.of(placeholders.get(key));
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
                System.out.println(matcher.group(2));
                Optional<Placeholder> placeholder = cache.get(matcher.group(2));
                if (placeholder.isPresent()) {
                    output = placeholder.get().replace(user, output);
                }
            } catch (ExecutionException e) {
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
