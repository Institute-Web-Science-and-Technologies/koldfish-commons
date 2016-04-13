package de.unikoblenz.west.koldfish.fluid.matching;

/**
 * Created by Martin Leinberger on 13.04.2016.
 * Yeah, I know, it doesn't really belong here, but it simplifies working with the connection manager
 */
public class PatternMatching {
    public interface  Pattern {
        boolean matches(Object value);
        Object apply(Object value);
    }

    private final Pattern[] patterns;

    public PatternMatching(Pattern... patterns) {
        this.patterns = patterns;
    }

    public Object matchFor(Object value) {
        for (Pattern p : patterns)
            if (p.matches(value))
                return p.apply(value);

        throw new IllegalArgumentException("Cannot match " + value);
    }
}
