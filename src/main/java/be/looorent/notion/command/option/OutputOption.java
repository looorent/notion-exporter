package be.looorent.notion.command.option;

import be.looorent.notion.port.Format;
import be.looorent.notion.port.OutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static java.nio.file.Files.*;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

public class OutputOption {
    private static final Logger LOG = LoggerFactory.getLogger(OutputOption.class);

    @Option(names = {"-f", "--format"},
            description = "The output formats of the exported document. Multiple values can be provided, separated by a comma. Each format is outputed in a dedicated subfolder. Available values: json, md, markdown",
            split=",",
            defaultValue = "json")
    private List<String> formats;

    @Option(names = {"-o", "--output-folder"},
            description = "The path to the output folder. If the folder does not exist, it is created. " +
                    "Considering this CLI runs in a Docker container, this option does not really matter. " +
                    "The important value is the volume you define when running the CLI with Docker. " +
                    "By default, you should map the expected output folder on this default value. For example `-v /your-folder:/output`",
            defaultValue = "/output")
    private String folderPath;

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Collection<OutputFormat> toOutputFormats() {
        var folder = configureOutputFolder();
        var outputs = Format.find(formats)
                .stream()
                .map(format -> OutputFormat.from(folder, format))
                .collect(toList());
        if (outputs.isEmpty()) {
            throw new IllegalArgumentException("There is no valid output configured.");
        }
        return outputs;
    }

    private Path createMissingFolder(Path folder) {
        if (!exists(folder)) {
            try {
                LOG.info("Folder missing: {}. Creating it...", folder.toAbsolutePath());
                return createDirectories(folder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return folder;
        }
    }

    private Path configureOutputFolder() {
        var outputFolder = ofNullable(folderPath)
                .map(Path::of)
                .map(this::createMissingFolder)
                .orElseGet(() -> {
                    try {
                        return createTempDirectory(randomUUID().toString());
                    } catch (IOException e) {
                        LOG.error("Impossible to create an empty temporary folder", e);
                        return null;
                    }
                });

        if (outputFolder != null && !exists(outputFolder)) {
            throw new IllegalStateException("Folder has not been created: " + outputFolder.toAbsolutePath());
        }
        return outputFolder;
    }

}
