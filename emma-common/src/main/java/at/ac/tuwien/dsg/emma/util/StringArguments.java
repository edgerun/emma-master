package at.ac.tuwien.dsg.emma.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * StringArguments.
 */
public class StringArguments {

    private final String[] args;

    public StringArguments(String... args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public int getLength() {
        return args.length;
    }

    public Optional<Integer> getInt(int index) {
        return getInt(index, Throwable::printStackTrace);
    }

    public Optional<Integer> getInt(int index, Consumer<NumberFormatException> exceptionHandler) {
        return get(index, str -> {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                exceptionHandler.accept(e);
                return null;
            }
        });
    }

    public Optional<String> get(int index) {
        if (index >= args.length || index < 0) {
            return Optional.empty();
        }
        return Optional.of(args[index]);
    }

    public <T> Optional<T> get(int index, Function<String, T> mapper) {
        return get(index).map(mapper);
    }

    /**
     * Parses long-opts arguments (i,e., "--option=value", or "--option") from this StringArguments returns them as a
     * map. Flag-like options will be added to the map with the string value 'true'.
     *
     * @return an option map ({option = value})
     */
    public Map<String, String> parseOptions() {
        Map<String, String> options = new HashMap<>(args.length);

        for (String arg : getArgs()) {
            if (!arg.startsWith("--")) {
                continue;
            }

            String[] parts = arg.split("=");

            String key = parts[0].substring(2);

            String val;
            val = parts.length > 1 ? parts[1] : "true";

            options.put(key, val);
        }

        return options;
    }
}
