package be.looorent.notion.core;

import notion.api.v1.model.blocks.ParagraphBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class Paragraph extends DocumentChunk {
    private final ParagraphBlock block;
    private final List<DocumentChunk> children;

    public Paragraph(ParagraphBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getParagraph().getRichText();
    }

    public List<DocumentChunk> getChildren() {
        // the block itself has a List<Block>, we ignore it
        return children;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }
}
