package edu.stevens.cs548.clinic.micro.domain.rest;

import edu.stevens.cs548.clinic.gson.GsonProvider;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.CommonProperties;


// TODO
@ApplicationPath("/")
@ApplicationScoped
@DataSourceDefinition(
		name="java:global/jdbc/cs548",
		className="org.postgresql.ds.PGSimpleDataSource",
		user="${ENV=DATABASE_USERNAME}",
		password="${ENV=DATABASE_PASSWORD}",
		databaseName="${ENV=DATABASE}",
		serverName="${ENV=DATABASE_HOST}",
		portNumber=5432)
public class AppConfig extends Application {
	
	@Override
	/*
	 * Register provider and resources classes IN THAT ORDER with JAX-RS implementation.
	 */
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();;;
		classes.add(GsonProvider.class);
		classes.add(PatientMicroService.class);
		classes.add(ProviderMicroService.class);
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
