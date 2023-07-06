package edu.stevens.cs548.clinic.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.CommonProperties;

import edu.stevens.cs548.clinic.gson.GsonProvider;


@ApplicationPath("/")
public class AppConfig extends Application {
	
	@Override
	/*
	 * Register resource classes with JAX-RS implementation.
	 */
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(GsonProvider.class);
		classes.add(PatientResource.class);
		classes.add(ProviderResource.class);
		return classes;
	}
	
    @Override
    /*
     * Turn off the default JSON provider in Payara/Jersey.
     */
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
		props.put(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
		props.put(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
		return props;
    }

}
