package be.looorent.notion.core;

import notion.api.v1.model.blocks.EmbedBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Embed extends DocumentChunk {
    private final EmbedBlock block;

    public Embed(EmbedBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getUrl() {
        return ofNullable(block.getEmbed()).map(EmbedBlock.Element::getUrl);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getEmbed()).map(EmbedBlock.Element::getCaption);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
