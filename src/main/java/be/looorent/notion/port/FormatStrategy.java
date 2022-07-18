package be.looorent.notion.port;

import be.looorent.notion.adapter.json.JsonWriter;
import be.looorent.notion.adapter.md.MarkdownWriter;

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
                    case JSON -> jsonWriter;
                    case MARKDOWN -> markdownWriter;
                    default -> null;
                })
                .orElseThrow(() -> new IllegalArgumentException("Format not supported: '" + format + "'"));
    }
}
