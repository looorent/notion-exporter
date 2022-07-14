package be.looorent.notion.core;

import notion.api.v1.model.blocks.ImageBlock;
import notion.api.v1.model.common.ExternalFileDetails;
import notion.api.v1.model.common.FileDetails;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Image extends DocumentChunk {
    private final ImageBlock block;
    private final Asset asset;

    public Image(ImageBlock block, Asset asset) {
        super(block.getId());
        this.block = block;
        this.asset = asset;
    }

    public Optional<String> getType() {
        return ofNullable(block.getImage()).map(ImageBlock.Element::getType);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getImage()).map(ImageBlock.Element::getCaption);
    }

    public Optional<ExternalFileDetails> getExternal() {
        return ofNullable(block.getImage()).map(ImageBlock.Element::getExternal);
    }

    public Optional<FileDetails> getFileDetails() {
        return ofNullable(block.getImage()).map(ImageBlock.Element::getFile);
    }

    public Optional<Asset> getAsset() {
        return ofNullable(asset);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
