package be.looorent.notion.core;

import java.nio.file.Path;
import java.util.Objects;

public class Asset {
    private final String id;
    private final String name;
    private transient final Path path;
    private transient final Path parentFolder;

    public Asset(String id, String name, Path path, Path parentFolder) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.parentFolder = parentFolder;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public String getRelativePath() {
        return parentFolder.relativize(path).toString();
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
}
