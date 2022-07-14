package be.looorent.notion.adapter.md;

import be.looorent.notion.core.Document;
import be.looorent.notion.core.DocumentChunk;
import be.looorent.notion.core.ReferenceContainer;
import be.looorent.notion.core.ReferenceVisitor;
import be.looorent.notion.port.DocumentWriteException;
import be.looorent.notion.port.DocumentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import static be.looorent.notion.adapter.md.MarkdownLine.removeAdjacentDuplicates;
import static java.nio.charset.StandardCharsets.UTF_8;

@ApplicationScoped
public class MarkdownWriter implements DocumentWriter {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownWriter.class);
    private static final String DOCUMENT_FILENAME = "document.md";

    @Override
    public Path write(Document document, Path folder) throws DocumentWriteException {
        var references = createReferences(document);
        var formatVisitor = new MarkdownVisitor(references);
        document.accept(formatVisitor);
        try (var writer = new FileWriter(folder.resolve(DOCUMENT_FILENAME).toFile(), UTF_8);
             var print = new PrintWriter(writer)) {
            var lines = formatVisitor.getLines();
            LOG.info("Markdown: {} lines.", lines.size());
            lines = removeAdjacentDuplicates(lines);
            LOG.info("Markdown: {} lines without adjacent empty lines.", lines.size());
            lines.forEach(print::println);
            LOG.info("Markdown: {} lines printed.", lines.size());
        } catch (IOException e) {
            throw new MarkdownWriteException("Error when writing the document to: " + folder.toAbsolutePath().toAbsolutePath(), e);
        }
        return folder;
    }

    @Override
    public String getName() {
        return "Markdown";
    }

    private ReferenceContainer createReferences(DocumentChunk composite) {
        var referenceVisitor = new ReferenceVisitor();
        composite.accept(referenceVisitor);
        return referenceVisitor.getReferences();
    }
}
