import me.meyerzinn.placeholderapi.Placeholder;
import me.meyerzinn.placeholderapi.RegexPlaceholderService;
import org.junit.Test;
import org.spongepowered.api.entity.living.player.User;

/**
 * Created by meyerzinn on 3/7/16.
 */
public class PlaceholderAPITest {

    @Test
    public void test() {
        RegexPlaceholderService service = new RegexPlaceholderService();
        service.registerPlaceholder("hallo", new Placeholder() {
            @Override public String replace(User user, String placeholder) {
                return "Hi!";
            }
        });
        System.out.println(service.replace(null, "{hallo}"));
    }


}
