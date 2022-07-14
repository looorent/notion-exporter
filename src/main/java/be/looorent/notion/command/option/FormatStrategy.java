package be.looorent.notion.command.option;

import be.looorent.notion.adapter.json.JsonWriter;
import be.looorent.notion.adapter.md.MarkdownWriter;
import be.looorent.notion.port.DocumentWriter;

import javax.enterprise.context.ApplicationScoped;

import static java.util.Optional.ofNullable;

@ApplicationScoped
public class FormatStrategy {
    private final JsonWriter jsonWriter;
    private final MarkdownWriter markdownWriter;

    public FormatStrategy(JsonWriter jsonWriter,
                          MarkdownWriter markdownWriter) {
        this.jsonWriter = jsonWriter;
        this.markdownWriter = markdownWriter;
    }

    public DocumentWriter findDatabaseWriter(Format format) {
        return ofNullable(format)
                .map(value -> switch (value) {
                    case json -> jsonWriter;
                    case markdown, md -> markdownWriter;
                    default -> null;
                })
                .orElseThrow(() -> new IllegalArgumentException("Format not supported: '" + format + "'"));
    }
}
