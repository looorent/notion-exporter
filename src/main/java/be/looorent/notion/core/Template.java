package be.looorent.notion.core;

import notion.api.v1.model.blocks.TemplateBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Template extends DocumentChunk {
    private final TemplateBlock block;
    private final List<DocumentChunk> children;

    public Template(TemplateBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public Optional<List<PageProperty.RichText>> getRichText() {
        return ofNullable(block.getTemplate()).map(TemplateBlock.Element::getRichText);
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
