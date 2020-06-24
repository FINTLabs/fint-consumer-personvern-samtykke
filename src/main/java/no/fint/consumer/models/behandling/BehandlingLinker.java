package no.fint.consumer.models.behandling;

import no.fint.model.resource.personvern.samtykke.BehandlingResource;
import no.fint.model.resource.personvern.samtykke.BehandlingResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class BehandlingLinker extends FintLinker<BehandlingResource> {

    public BehandlingLinker() {
        super(BehandlingResource.class);
    }

    public void mapLinks(BehandlingResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public BehandlingResources toResources(Collection<BehandlingResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public BehandlingResources toResources(Stream<BehandlingResource> stream, int offset, int size, int totalItems) {
        BehandlingResources resources = new BehandlingResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(BehandlingResource behandling) {
        return getAllSelfHrefs(behandling).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(BehandlingResource behandling) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(behandling.getSystemId()) && !isEmpty(behandling.getSystemId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(behandling.getSystemId().getIdentifikatorverdi(), "systemid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(BehandlingResource behandling) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(behandling.getSystemId()) && !isEmpty(behandling.getSystemId().getIdentifikatorverdi())) {
            builder.add(behandling.getSystemId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

