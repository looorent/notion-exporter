package be.looorent.notion.core;

import notion.api.v1.model.blocks.QuoteBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Quote extends DocumentChunk {
    private final QuoteBlock block;
    private final List<DocumentChunk> children;

    public Quote(QuoteBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public Optional<List<PageProperty.RichText>> getRichText() {
        return ofNullable(block.getQuote()).map(QuoteBlock.Element::getRichText);
    }

    public List<DocumentChunk> getChildren() {
        return children;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }
}
