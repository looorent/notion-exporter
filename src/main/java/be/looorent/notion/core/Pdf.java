package be.looorent.notion.core;

import notion.api.v1.model.blocks.PDFBlock;
import notion.api.v1.model.common.ExternalFileDetails;
import notion.api.v1.model.common.FileDetails;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Pdf extends DocumentChunk {
    private final PDFBlock block;
    private final Asset asset;

    public Pdf(PDFBlock block, Asset asset) {
        super(block.getId());
        this.block = block;
        this.asset = asset;
    }

    public Optional<String> getType() {
        return ofNullable(block.getPdf()).map(PDFBlock.Element::getType);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getPdf()).map(PDFBlock.Element::getCaption);
    }

    public Optional<ExternalFileDetails> getExternal() {
        return ofNullable(block.getPdf()).map(PDFBlock.Element::getExternal);
    }

    public Optional<FileDetails> getFileDetails() {
        return ofNullable(block.getPdf()).map(PDFBlock.Element::getFile);
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
