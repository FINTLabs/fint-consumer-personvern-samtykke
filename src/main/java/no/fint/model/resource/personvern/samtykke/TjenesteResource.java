// Built from tag personvern

package no.fint.model.resource.personvern.samtykke;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.*;

import no.fint.model.FintMainObject;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.Link;
import org.hibernate.validator.constraints.NotBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TjenesteResource implements FintMainObject, FintLinks {
    // Attributes
    @NotBlank
    private String navn;
    @NotNull
    private @Valid
    Identifikator systemId;

    // Relations
    @Getter
    private final Map<String, List<Link>> links = createLinks();
        
    @JsonIgnore
    public List<Link> getBehandling() {
        return getLinks().getOrDefault("behandling", Collections.emptyList()); 
    }
    public void addBehandling(Link link) {
        addLink("behandling", link);
    }
}
