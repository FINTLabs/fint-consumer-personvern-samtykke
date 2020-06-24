package no.fint.consumer.config;

import no.fint.consumer.utils.RestEndpoints;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

import no.fint.model.personvern.samtykke.*;

public class LinkMapper {

	public static Map<String, String> linkMapper(String contextPath) {
		return ImmutableMap.<String,String>builder()
			.put(Behandling.class.getName(), contextPath + RestEndpoints.BEHANDLING)
			.put(Samtykke.class.getName(), contextPath + RestEndpoints.SAMTYKKE)
			.put(Tjeneste.class.getName(), contextPath + RestEndpoints.TJENESTE)
			/* .put(TODO,TODO) */
			.build();
	}

}
