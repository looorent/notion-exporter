package be.looorent.notion.adapter.json;

import be.looorent.notion.core.Document;
import be.looorent.notion.port.DocumentWriteException;
import be.looorent.notion.port.DocumentWriter;
import com.google.gson.Gson;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

@ApplicationScoped
public class JsonWriter implements DocumentWriter {
    private static final String DOCUMENT_FILENAME = "document.json";

    @Override
    public Path write(Document document, Path folder) throws DocumentWriteException {
        try (var writer = new FileWriter(folder.resolve(DOCUMENT_FILENAME).toFile(), UTF_8)) {
            new Gson().toJson(document, writer);
        } catch (IOException e) {
            throw new JsonWriteException("Error when writing to JSON: "+ folder.toAbsolutePath(), e);
        }
        return folder;
    }

    @Override
    public String getId() {
        return "JSON";
    }

    @Override
    public String toString() {
        return getId();
    }
}
