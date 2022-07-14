package be.looorent.notion.adapter.rest;

import be.looorent.notion.core.Asset;
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
import java.util.Optional;

import static java.nio.channels.Channels.newChannel;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.util.Optional.*;
import static java.util.UUID.randomUUID;

@ApplicationScoped
class AssetRepository {
    private static final Logger LOG = LoggerFactory.getLogger(AssetRepository.class);

    public Optional<Asset> downloadAssetIn(ImageBlock block, Path folder, Path rootFolder) {
        return ofNullable(block.getImage())
                .map(ImageBlock.Element::getFile)
                .map(FileDetails::getUrl)
                .flatMap(url -> downloadAssetIn(url, block.getId(),"png", folder, rootFolder));
    }

    public Optional<Asset> downloadAssetIn(PDFBlock block, Path folder, Path rootFolder) {
        return ofNullable(block.getPdf())
                .map(PDFBlock.Element::getFile)
                .map(FileDetails::getUrl)
                .flatMap(url -> downloadAssetIn(url, block.getId(), "pdf", folder, rootFolder));
    }

    private Optional<Asset> downloadAssetIn(String assetUrl, String blockId, String extension, Path folder, Path rootFolder) {
        var asset = ofNullable(assetUrl)
                .flatMap(url -> {
                    LOG.debug("Download asset for block {} at: {}", blockId, url);
                    try {
                        return downloadUrl(url, extension, folder);
                    } catch (IOException e) {
                        LOG.error("Error when downloading '{}' for block {}", url, blockId, e);
                        return empty();
                    }
                })
                .map(path -> new Asset(blockId, blockId, path, rootFolder));
        if (asset.isEmpty()) {
            LOG.warn("No asset to download from block {}", blockId);
        }
        return asset;
    }

    private Optional<Path> downloadUrl(String url, String extension, Path folder) throws IOException {
        if (!exists(folder)) {
            createDirectories(folder);
        }
        var filename = randomUUID() + "." + extension;
        var path = folder.resolve(filename);
        try (var readableByteChannel = newChannel(new URL(url).openStream());
             var fileOutputStream = new FileOutputStream(path.toFile());
             var fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return of(path);
    }
}
