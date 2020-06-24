
package no.fint.consumer.config;

public enum Constants {
;

    public static final String COMPONENT = "personvern-samtykke";
    public static final String COMPONENT_CONSUMER = COMPONENT + " consumer";
    public static final String CACHE_SERVICE = "CACHE_SERVICE";

    
    public static final String CACHE_INITIALDELAY_BEHANDLING = "${fint.consumer.cache.initialDelay.behandling:900000}";
    public static final String CACHE_FIXEDRATE_BEHANDLING = "${fint.consumer.cache.fixedRate.behandling:900000}";
    
    public static final String CACHE_INITIALDELAY_SAMTYKKE = "${fint.consumer.cache.initialDelay.samtykke:960000}";
    public static final String CACHE_FIXEDRATE_SAMTYKKE = "${fint.consumer.cache.fixedRate.samtykke:900000}";
    
    public static final String CACHE_INITIALDELAY_TJENESTE = "${fint.consumer.cache.initialDelay.tjeneste:1020000}";
    public static final String CACHE_FIXEDRATE_TJENESTE = "${fint.consumer.cache.fixedRate.tjeneste:900000}";
    

}
