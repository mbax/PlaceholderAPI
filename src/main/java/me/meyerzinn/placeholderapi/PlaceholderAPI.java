package me.meyerzinn.placeholderapi;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

/**
 * Created by meyerzinn on 3/6/16.
 */
@Plugin(id = "placeholderapi", name = "PlaceholderAPI", version = "v1.1-SNAPSHOT")
public class PlaceholderAPI {

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        Sponge.getServiceManager().setProvider(this, PlaceholderService.class, new RegexPlaceholderService());
    }

}
