package be.looorent.notion.core;

import notion.api.v1.model.blocks.LinkToPageBlock;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class LinkToPage extends DocumentChunk {
    private final LinkToPageBlock block;

    public LinkToPage(LinkToPageBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getType() {
        return ofNullable(block.getLinkToPage()).map(notion.api.v1.model.common.LinkToPage::getType);
    }

    public Optional<String> getPageId() {
        return ofNullable(block.getLinkToPage()).map(notion.api.v1.model.common.LinkToPage::getPageId);
    }

    public Optional<String> getDatabaseId() {
        return ofNullable(block.getLinkToPage()).map(notion.api.v1.model.common.LinkToPage::getDatabaseId);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
