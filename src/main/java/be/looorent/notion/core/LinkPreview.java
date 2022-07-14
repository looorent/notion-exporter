package be.looorent.notion.core;

import notion.api.v1.model.blocks.LinkPreviewBlock;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class LinkPreview extends DocumentChunk {
    private final LinkPreviewBlock block;

    public LinkPreview(LinkPreviewBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getUrl() {
        return ofNullable(block.getLinkPreview()).map(LinkPreviewBlock.Element::getUrl);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }

}
