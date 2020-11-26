package no.fint.consumer.test

import no.fint.audit.FintAuditService
import no.fint.consumer.config.ConsumerProps
import no.fint.consumer.models.samtykke.SamtykkeCacheService
import no.fint.consumer.models.samtykke.SamtykkeController
import no.fint.consumer.models.samtykke.SamtykkeLinker
import no.fint.consumer.utils.PropertyFilter
import no.fint.consumer.utils.PropertyFilterOperator
import no.fint.consumer.utils.RestEndpoints
import no.fint.event.model.HeaderConstants
import no.fint.model.felles.Person
import no.fint.model.felles.kompleksedatatyper.Identifikator
import no.fint.model.felles.kompleksedatatyper.Periode
import no.fint.model.resource.Link
import no.fint.model.resource.personvern.samtykke.BehandlingResource
import no.fint.model.resource.personvern.samtykke.SamtykkeResource
import no.fint.model.resource.personvern.samtykke.SamtykkeResources
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Stream

class FilterSpec extends MockMvcSpecification {
    SamtykkeController controller = Mock()
    SamtykkeCacheService service = Mock()
    SamtykkeLinker linker = Mock()
    ConsumerProps props = Mock()
    FintAuditService auditService = Mock()
    MockMvc mockMvc

    void setup() {
        controller = new SamtykkeController(fintAuditService: auditService, cacheService: service, linker: linker, props: props)
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build()
    }

    def "Controller"() {
        given:
        def resource = new SamtykkeResource(systemId: new Identifikator(identifikatorverdi: 'system-id'))
        def resources = new SamtykkeResources()
        resources.addResource(resource)

        when:
        def response = mockMvc.perform(get(RestEndpoints.SAMTYKKE)
                .header(HeaderConstants.ORG_ID, 'mock')
                .header(HeaderConstants.CLIENT, 'mock'))

        then:
        1 * props.isOverrideOrgId() >> false
        1 * service.streamAll('mock') >> Stream.of(resource)
        1 * service.getCacheSize('mock') >> 1
        1 * linker.toResources(_, 0, 0, 1) >> resources

        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.total_items').value(1))

    }

    def "Level one"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'opprettet', PropertyFilterOperator.EQUALS, '2020-11-25T10:30:30Z')

        then:
        test
    }

    def "Level two"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'systemId.identifikatorverdi', PropertyFilterOperator.EQUALS, 'system-id')

        then:
        test
    }

    def "Level three"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'systemId.gyldighetsperiode.start', PropertyFilterOperator.EQUALS, '2020-11-25T10:30:30Z')

        then:
        test
    }

    def "String equals"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'systemId.identifikatorverdi', PropertyFilterOperator.EQUALS, 'system-id')

        then:
        test
    }

    def "String contains"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'systemId.identifikatorverdi', PropertyFilterOperator.CONTAINS, 'id')

        then:
        test
    }

    def "Boolean equals"() {
        given:
        def resource = newBehandlingResource()

        when:
        def test = PropertyFilter.filter(resource, 'aktiv', PropertyFilterOperator.EQUALS, 'true')

        then:
        test
    }

    def "Date equals"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'opprettet', PropertyFilterOperator.EQUALS, '2020-11-25T10:30:30Z')

        then:
        test
    }

    def "List equals"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'links.person', PropertyFilterOperator.EQUALS, '${felles.person}/fodselsnummer/01010122222')

        then:
        test
    }

    def "List contains"() {
        given:
        def resource = newSamtykkeResource('system-id', '01010122222')

        when:
        def test = PropertyFilter.filter(resource, 'links.person', PropertyFilterOperator.CONTAINS, '01010122222')

        then:
        test
    }

    def "Stream equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id', '01010122222'),
                newSamtykkeResource('system-id', '01010122222'),
                newSamtykkeResource('system-id', '01010133333'))

        when:
        def test = PropertyFilter.of(resources, 'links.person=${felles.person}/fodselsnummer/01010122222')

        then:
        test.count() == 2
    }

    def "Stream contains"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id', '01010122222'),
                newSamtykkeResource('system-id', '01010122222'),
                newSamtykkeResource('system-id', '01010133333'))

        when:
        def test = PropertyFilter.of(resources, 'links.person~01010122222')

        then:
        test.count() == 2
    }

    def newSamtykkeResource(String systemId, String fodselsnummer) {
        def dateTime = LocalDateTime.of(2020, 11, 25, 10, 30, 30).atZone(ZoneId.of("Z"))

        return new SamtykkeResource(
                systemId: new Identifikator(identifikatorverdi: systemId, gyldighetsperiode: new Periode(start: Date.from(dateTime.toInstant()), slutt: null)),
                opprettet: Date.from(dateTime.toInstant()),
                links: [('person'): [Link.with(Person.class, 'fodselsnummer', fodselsnummer)]]
        )
    }

    def newBehandlingResource() {
        return new BehandlingResource(
                aktiv: true
        )
    }
}
