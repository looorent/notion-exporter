package be.looorent.notion.adapter.md;

import java.util.ArrayList;
import java.util.List;

class MarkdownLine {
    private static final int NO_INDENTATION = 0;
    private final String text;
    private final int indentation;

    public static List<MarkdownLine> removeAdjacentDuplicates(List<MarkdownLine> lines) {
        var linesWithoutAdjacent = new ArrayList<MarkdownLine>();
        var previousIsEmpty = false;
        for (var line : lines) {
            if (!previousIsEmpty || !line.isEmpty()) {
                linesWithoutAdjacent.add(line);
            }
            previousIsEmpty = line.isEmpty();
        }
        return linesWithoutAdjacent;
    }

    public static MarkdownLine emptyLine() {
        return emptyLine(NO_INDENTATION);
    }

    public static MarkdownLine emptyLine(int indentation) {
        return mdLine("", indentation);
    }

    public static MarkdownLine mdLine(String text) {
        return mdLine(text, NO_INDENTATION);
    }

    public static MarkdownLine mdLine(String text, int indentation) {
        return new MarkdownLine(text, indentation);
    }

    private MarkdownLine(String text, int indentation) {
        this.text = text;
        this.indentation = indentation;
    }

    public String getText() {
        return text;
    }

    public int getIndentation() {
        return indentation;
    }

    @Override
    public String toString() {
        return "\t".repeat(indentation)+text;
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }
}
