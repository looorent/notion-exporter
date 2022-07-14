package be.looorent.notion.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

public class ReferenceContainer {
    private final Map<String, Reference> referencePerId;

    ReferenceContainer(Collection<Reference> references) {
        this.referencePerId = references.stream().collect(toMap(reference -> Reference.formatId(reference.getId()), identity()));
    }

    public Optional<Reference> findReference(String id) {
        return ofNullable(id)
                .filter(not(String::isBlank))
                .map(Reference::formatId)
                .map(referencePerId::get);
    }
}
