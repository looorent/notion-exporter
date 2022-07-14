package be.looorent.notion.core;

import notion.api.v1.model.blocks.CodeBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Code extends DocumentChunk {
    private final CodeBlock block;

    public Code(CodeBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getLanguage() {
        return ofNullable(block.getCode()).map(CodeBlock.Element::getLanguage);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getCode()).map(CodeBlock.Element::getCaption);
    }

    public Optional<List<PageProperty.RichText>> getRichText() {
        return ofNullable(block.getCode()).map(CodeBlock.Element::getRichText);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
