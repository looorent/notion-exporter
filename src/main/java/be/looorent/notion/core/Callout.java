package be.looorent.notion.core;

import notion.api.v1.model.blocks.CalloutBlock;
import notion.api.v1.model.common.Icon;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Callout extends DocumentChunk {
    private final CalloutBlock block;
    private final List<DocumentChunk> children;

    public Callout(CalloutBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public List<DocumentChunk> getChildren() {
        return children;
    }

    public Optional<List<PageProperty.RichText>> getRichText() {
        return ofNullable(block.getCallout()).map(CalloutBlock.Element::getRichText);
    }

    public Optional<Icon> getIcon() {
        return ofNullable(block.getCallout()).map(CalloutBlock.Element::getIcon);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }
}
