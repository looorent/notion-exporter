package be.looorent.notion.core;

import be.looorent.notion.port.Format;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Asset {
    private final String id;
    private final String name;
    private transient final Map<Format, Path> pathPerFormat;

    public Asset(String id, String name, Map<Format, Path> pathPerFormat) {
        this.id = id;
        this.name = name;
        this.pathPerFormat = pathPerFormat;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Asset image = (Asset) other;
        return id.equals(image.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<Path> getPath(Format format) {
        return ofNullable(format).map(pathPerFormat::get);
    }
}
