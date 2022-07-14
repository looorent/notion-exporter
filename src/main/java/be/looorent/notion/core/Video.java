package be.looorent.notion.core;

import notion.api.v1.model.blocks.VideoBlock;
import notion.api.v1.model.common.ExternalFileDetails;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Video extends DocumentChunk {
    private final VideoBlock block;

    public Video(VideoBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getType() {
        return ofNullable(block.getVideo()).map(VideoBlock.Element::getType);
    }

    public Optional<ExternalFileDetails> getExternal() {
        return ofNullable(block.getVideo()).map(VideoBlock.Element::getExternal);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getVideo()).map(VideoBlock.Element::getCaption);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
