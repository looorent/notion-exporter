package be.looorent.notion.adapter.rest;

import notion.api.v1.model.blocks.Block;

import java.util.List;

record BlockComposite(Block block, List<BlockComposite> children) {
    int getNumberOfBlocks() {
        return 1 + children.stream().mapToInt(BlockComposite::getNumberOfBlocks).sum();
    }
}
