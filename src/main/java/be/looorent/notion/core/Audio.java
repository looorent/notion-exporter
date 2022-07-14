package be.looorent.notion.core;

import notion.api.v1.model.blocks.AudioBlock;
import notion.api.v1.model.common.FileDetails;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Audio extends DocumentChunk {
    private final AudioBlock block;

    public Audio(AudioBlock block) {
        super(block.getId());
        this.block = block;
    }

    public String getType() {
        return block.getAudio().getType();
    }

    public Optional<FileDetails> getFile() {
        return ofNullable(block.getAudio().getFile());
    }

    public List<PageProperty.RichText> getCaption() {
        return block.getAudio().getCaption();
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
