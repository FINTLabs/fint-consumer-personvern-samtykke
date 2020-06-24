package no.fint.consumer.models.samtykke;

import no.fint.model.resource.personvern.samtykke.SamtykkeResource;
import no.fint.model.resource.personvern.samtykke.SamtykkeResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class SamtykkeLinker extends FintLinker<SamtykkeResource> {

    public SamtykkeLinker() {
        super(SamtykkeResource.class);
    }

    public void mapLinks(SamtykkeResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public SamtykkeResources toResources(Collection<SamtykkeResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public SamtykkeResources toResources(Stream<SamtykkeResource> stream, int offset, int size, int totalItems) {
        SamtykkeResources resources = new SamtykkeResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(SamtykkeResource samtykke) {
        return getAllSelfHrefs(samtykke).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(SamtykkeResource samtykke) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(samtykke.getSystemId()) && !isEmpty(samtykke.getSystemId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(samtykke.getSystemId().getIdentifikatorverdi(), "systemid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(SamtykkeResource samtykke) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(samtykke.getSystemId()) && !isEmpty(samtykke.getSystemId().getIdentifikatorverdi())) {
            builder.add(samtykke.getSystemId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

