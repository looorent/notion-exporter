package be.looorent.notion.port;

import java.nio.file.Path;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class OutputFormat {
    private final Path folder;
    private final Path assetFolder;
    private final Format format;

    public static Collection<OutputFormat> from(Path parentFolder, Collection<Format> formats) {
        return formats.stream()
                .map(format -> from(parentFolder, format))
                .collect(toList());
    }

    public static OutputFormat from(Path parentFolder, Format format) {
        var folder = parentFolder.resolve(format.getId());
        var assetFolder = folder.resolve("assets");
        return new OutputFormat(folder, assetFolder, format);
    }

    public OutputFormat(Path folder,
                        Path assetFolder,
                        Format format) {
        this.folder = folder;
        this.assetFolder = assetFolder;
        this.format = format;
    }

    public Path getFolder() {
        return folder;
    }

    public Path getAssetFolder() {
        return assetFolder;
    }

    public Format getFormat() {
        return format;
    }

    public String getRelativePath() {
        return folder.relativize(assetFolder).toString();
    }
}
