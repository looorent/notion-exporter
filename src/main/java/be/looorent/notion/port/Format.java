package be.looorent.notion.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

public enum Format {
    JSON("json", emptyList()),
    MARKDOWN("md", of("markdown"));

    private final String id;
    private final List<String> aliases;

    public static Collection<Format> find(Collection<String> names) {
        return names.stream()
                .map(Format::find)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
    }

    public static Optional<Format> find(String name) {
        return ofNullable(name)
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(not(String::isBlank))
                .flatMap(sanitizedName -> stream(values()).filter(value -> value.hasName(sanitizedName)).findAny());
    }

    Format(String folderName, List<String> aliases) {
        this.id = folderName;
        this.aliases = aliases;
    }

    public String getId() {
        return id;
    }

    public List<String> getAliases() {
        return aliases;
    }

    private boolean hasName(String name) {
        return id.equals(name) || aliases.contains(name);
    }
}
