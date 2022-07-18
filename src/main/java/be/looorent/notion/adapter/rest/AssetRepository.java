package be.looorent.notion.adapter.rest;

import be.looorent.notion.core.Asset;
import be.looorent.notion.port.Format;
import be.looorent.notion.port.OutputFormat;
import notion.api.v1.model.blocks.ImageBlock;
import notion.api.v1.model.blocks.PDFBlock;
import notion.api.v1.model.common.FileDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import static java.nio.channels.Channels.newChannel;
import static java.nio.file.Files.*;
import static java.util.Optional.*;
import static java.util.UUID.randomUUID;

@ApplicationScoped
class AssetRepository {
    private static final Logger LOG = LoggerFactory.getLogger(AssetRepository.class);
    private static final String IMAGE_EXTENSION = "png";
    private static final String PDF_EXTENSION = "pdf";

    public Optional<Asset> downloadAssetIn(ImageBlock block, Collection<OutputFormat> outputs) {
        return ofNullable(block.getImage())
                .map(ImageBlock.Element::getFile)
                .map(FileDetails::getUrl)
                .flatMap(url -> downloadUrl(url, block.getId(), IMAGE_EXTENSION, outputs));
    }

    public Optional<Asset> downloadAssetIn(PDFBlock block, Collection<OutputFormat> outputs) {
        return ofNullable(block.getPdf())
                .map(PDFBlock.Element::getFile)
                .map(FileDetails::getUrl)
                .flatMap(url -> downloadUrl(url, block.getId(), PDF_EXTENSION, outputs));
    }

    private Optional<Asset> downloadUrl(String url,
                                        String id,
                                        String extension,
                                        Collection<OutputFormat> outputs) {
        try {
            var pathPerFormat = new HashMap<Format, Path>();
            var temporaryPath = downloadFile(url);
            for (var output : outputs) {
                createDirectories(output.getAssetFolder());
                var expectedDestination = output.getAssetFolder().resolve(id + "." + extension);
                LOG.debug("Copying '{}' to '{}'...", temporaryPath.toAbsolutePath(), expectedDestination.toAbsolutePath());
                var destination = copy(temporaryPath, expectedDestination);
                LOG.debug("Copying '{}' to '{}'. Done.", temporaryPath.toAbsolutePath(), destination.toAbsolutePath());
                pathPerFormat.put(output.getFormat(), destination);
            }
            return of(new Asset(id, id, pathPerFormat));
        } catch (IOException e) {
            LOG.error("Error when downloading '{}' for block {}", url, id, e);
            return empty();
        }
    }

    private Path downloadFile(String url) throws IOException {
        var temporary = createTempDirectory(randomUUID().toString());
        if (!exists(temporary)) {
            createDirectories(temporary);
        }
        var path = temporary.resolve(randomUUID().toString());
        LOG.debug("Downloading '{}' to '{}'...", url, path.toAbsolutePath());
        try (var readableByteChannel = newChannel(new URL(url).openStream());
             var fileOutputStream = new FileOutputStream(path.toFile());
             var fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        LOG.debug("Downloading '{}' to '{}'. Done.", url, path);
        return path;
    }
}
