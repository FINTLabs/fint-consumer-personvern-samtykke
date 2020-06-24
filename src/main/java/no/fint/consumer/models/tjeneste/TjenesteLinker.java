package no.fint.consumer.models.tjeneste;

import no.fint.model.resource.personvern.samtykke.TjenesteResource;
import no.fint.model.resource.personvern.samtykke.TjenesteResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class TjenesteLinker extends FintLinker<TjenesteResource> {

    public TjenesteLinker() {
        super(TjenesteResource.class);
    }

    public void mapLinks(TjenesteResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public TjenesteResources toResources(Collection<TjenesteResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public TjenesteResources toResources(Stream<TjenesteResource> stream, int offset, int size, int totalItems) {
        TjenesteResources resources = new TjenesteResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(TjenesteResource tjeneste) {
        return getAllSelfHrefs(tjeneste).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(TjenesteResource tjeneste) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(tjeneste.getSystemId()) && !isEmpty(tjeneste.getSystemId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(tjeneste.getSystemId().getIdentifikatorverdi(), "systemid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(TjenesteResource tjeneste) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(tjeneste.getSystemId()) && !isEmpty(tjeneste.getSystemId().getIdentifikatorverdi())) {
            builder.add(tjeneste.getSystemId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

