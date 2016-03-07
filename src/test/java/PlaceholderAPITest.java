import me.meyerzinn.placeholderapi.Placeholder;
import me.meyerzinn.placeholderapi.PlaceholderService;
import me.meyerzinn.placeholderapi.RegexPlaceholderService;
import org.junit.Before;
import org.junit.Test;
import org.spongepowered.api.entity.living.player.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by meyerzinn on 3/7/16.
 */
public class PlaceholderAPITest {

    private PlaceholderService service;
    private final Pattern p = Pattern.compile("(number:)(\\d*)");


    @Before public void setupService() {
        service = new RegexPlaceholderService();
        service.registerPlaceholder("(number:)(\\d*)", new Placeholder() {
            @Override public String replace(User user, String input) {
                Matcher m = p.matcher(input);
                if (m.find()) {
                    return "Number: " + m.group(2);
                }
                return "";
            }
        });
        service.registerPlaceholder("hallo", new Placeholder() {
            @Override public String replace(User user, String placeholder) {
                return "Hi!";
            }
        });
    }

    @Test
    public void testSingleWord() {
        long start = System.currentTimeMillis();
        System.out.println("Begin Single Word Test: " + start + "ns");
        System.out.println(service.replace(null, "{hallo}"));
        System.out.println("Phase 1 (init) complete: " + (System.currentTimeMillis() - start) + "ms");
        long cacheStart = System.currentTimeMillis();
        System.out.println("Begin Cache Test for Single Word: " + cacheStart);
        for (int i = 0; i < 10; i++) {
            System.out.println(service.replace(null, "{hallo}"));
        }
        System.out.println("Time for 10 iterations: " + (System.currentTimeMillis() - cacheStart) + "ms");
    }

    @Test
    public void testMultiple() {
        System.out.println(service.replace(null, "Sooooo {number:100} {hallo}"));
    }

    @Test
    public void testNotPlaceholder() {
        System.out.println(service.replace(null, "{notAPlaceholder}"));
    }

    @Test
    public void testNumberReplacement() {

        long start = System.currentTimeMillis();
        System.out.println("Begin Number Replacement Test: " + start + "ns");
        System.out.println(service.replace(null, "{number:100}"));
        System.out.println("Phase 1 (init) complete: " + (System.currentTimeMillis() - start) + "ms");
        long cacheStart = System.currentTimeMillis();
        System.out.println("Begin Cache Test for Number Replacement: " + cacheStart);
        for (int i = 0; i < 10; i++) {
            System.out.println(service.replace(null, "{number:100}"));
        }
        System.out.println("Time for 10 iterations: " + (System.currentTimeMillis() - cacheStart) + "ms");
    }

}
