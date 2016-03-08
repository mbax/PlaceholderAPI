# PlaceholderAPI [![Latest Release](https://img.shields.io/badge/download-1.0--SNAPSHOT-green.svg)](https://github.com/AniOrg/PlaceholderAPI/releases/latest) [![Build Status](https://travis-ci.org/AniOrg/PlaceholderAPI.svg?branch=master)](https://travis-ci.org/AniOrg/PlaceholderAPI)

This project was created to make it easier for plugins to offer "placeholder" text to players. Get the latest download [here](https://github.com/AniOrg/PlaceholderAPI/releases/latest) (any plugins that want to use PlaceholderAPI functionality needs to have this installed).

### Features:
1. Uses Regular Expressions as keys for Placeholders. This allows you to not only offer placeholders for static text, such as ```{cat} --> Meow!```,
but also ```{number:(\d)} --> #$1```, etc. Regular Expressions can have as many capture groups as needed, but it is encouraged to keep it simple
and low for the sake of speed.
2. Caches Regular Expressions. This greatly speeds up the time it takes to find the correct placeholder. For example, using `{number:100}` will be
cached for 10 minutes, so any subsequent `number:100` calls will be served from the cache.
3. Supports using multiple placeholders in the same string. `{cat} {number:100}` will result in `Meow! #100`.
4. Contextual placeholders--your `Placeholder` object is passed a User object and the actual String that was flagged for replacement.

### Usage

First, add PlaceholderAPI as a dependency:
```
repositories {
    maven {
        url "https://jitpack.io"
    }
}
dependencies {
    compile 'com.github.AniOrg:PlaceholderAPI:-SNAPSHOT'
}
```

Then, add PlaceholderAPI as a dependency.
```java
@Plugin(... dependencies = "required-after:PlaceholderAPI")
```

To get the `PlaceholderService`:
```java
Optional<PlaceholderService> potentialService = Sponge.getService.provide(PlaceholderService.class);
if (service.isPresent()) {
    PlaceholderService service = potentialService.get();
} else {
    // PlaceholderAPI not loaded! Something really screwed up.
}
```

To register a Placeholder:
```java
// Let's register {demo} --> Welcome!
service.registerPlaceholder(Pattern.compile("demo"), new Placeholder() {
    public String replace(Optional<User> user, Pattern pattern, String actual) {
        return "Welcome!";
    }
})
```

Let's convert `{number:100}` to `#100`:
```java
service.registerPlaceholder(Pattern.compile("(number:)(\\d)"), new Placeholder() {
    @Override public String replace(Optional<User> user, Pattern pattern, String actual) {
        Matcher m = pattern.matcher(actual);
        m.find(); // There has to be a match.
        return "#" + m.group(1);
    }
});
```

Time to get fancy! Let's send a player their ping:
```java
service.registerPlaceholder(Pattern.compile("ping"), new Placeholder() {
    @Override public String replace(Optional<User> user, Pattern pattern, String actual) {
        if (user.isPresent() && user.get().isOnline() && user.get().getPlayer().isPresent()) {
            return "" + user.get().getPlayer().get().getConnection().getPing();
        } else {
            return "-1";
        }
    }
});
```

To use the PlaceholderAPI on a string, just call:
```java
service.replace(Optional<User> user, String input);
```
It returns the fully replaced string.

These are just a few examples of the PlaceholderAPI. If you have any questions or issues, please post them on the issues page.

### The Do's and Don'ts of Placeholders
* __DO__ cache all database queries. Placeholders can be called at any time and should not take more than ~10ms maximum to get results. Even 10ms
is pushing it.
* __DON'T__ return a null value as a placeholder. Return an empty string instead.
* __DO__ make placeholders' names specific to your plugin (eg. {wizardry_score}) to prevent overlapping placeholders.
* __DON'T__ include any formatting in your placeholder unless documented. Let players format it.
* __DO__ feel free to use RegEx flags.
* __DON'T__ include excessive RegEx fields, as it takes longer to execute.
* __DO__ try to generalize the placeholders as much as possible to limit how many possible inputs can produce the output. This helps keep the cache
 low on overhead (although it's not too high anyways).
