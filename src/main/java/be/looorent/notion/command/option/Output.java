package be.looorent.notion.command.option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

public class Output {
    private static final Logger LOG = LoggerFactory.getLogger(Output.class);

    @Option(names = {"-f", "--format"},
            description = "The output format of the exported document. Available values: json, md, markdown",
            defaultValue = "json")
    private Format format;

    @Option(names = {"-o", "--output-folder"},
            description = "The path to the output folder. If the folder does not exist, it is created. " +
                    "Considering this CLI runs in a Docker container, this option does not really matter. " +
                    "The important value is the volume you define when running the CLI with Docker. " +
                    "By default, you should map the expected output folder on this default value. For example `-v /your-folder:/output`",
            defaultValue = "/output")
    private String folderPath;

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Path configureOutputFolder() {
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
}
