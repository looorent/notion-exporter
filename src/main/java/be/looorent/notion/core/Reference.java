package be.looorent.notion.core;

import java.util.Objects;

public class Reference {
    private final String id;
    private final String title;

    public static String formatId(String id) {
        return id.replace("-", "").replace("/", "");
    }

    public Reference(PageComposite page) {
        this.id = formatId(page.getId());
        this.title = page.getTitle();
    }

    public Reference(ChildPage page) {
        this.id = formatId(page.getId());
        this.title = page.getTitle();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Reference reference = (Reference) other;
        return id.equals(reference.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
