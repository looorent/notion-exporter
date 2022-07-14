package be.looorent.notion.core;

import notion.api.v1.model.blocks.EquationBlock;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Equation extends DocumentChunk {
    private final EquationBlock block;

    public Equation(EquationBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getExpression() {
        return ofNullable(block.getEquation()).map(notion.api.v1.model.common.Equation::getExpression);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
