package at.ac.tuwien.dsg.emma.util;

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
}
