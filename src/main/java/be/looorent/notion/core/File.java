package be.looorent.notion.core;

import notion.api.v1.model.blocks.FileBlock;
import notion.api.v1.model.common.ExternalFileDetails;
import notion.api.v1.model.common.FileDetails;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class File extends DocumentChunk {
    private final FileBlock block;

    public File(FileBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getType() {
        return ofNullable(block.getFile()).map(FileBlock.Element::getType);
    }

    public Optional<ExternalFileDetails> getExternal() {
        return ofNullable(block.getFile()).map(FileBlock.Element::getExternal);
    }

    public Optional<FileDetails> getFileDetails() {
        return ofNullable(block.getFile()).map(FileBlock.Element::getFile);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getFile()).map(FileBlock.Element::getCaption);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}