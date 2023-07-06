package edu.stevens.cs548.clinic.rest;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;

// TODO
@RequestScoped
@Path("/provider")
public class ProviderResource extends ResourceBase {
	
	private final Logger logger = Logger.getLogger(ProviderResource.class.getCanonicalName());
	
	/*
	 * Complete definition of GsonFactory class (initialization of Gson).
	 */
	private final Gson gson = GsonFactory.createGson();
	
	private static final String PROVIDERS = "providers";
	
	private static final String PATIENTS = "patients";
	
	private static final String TREATMENTS = "treatments";
	
	// TODO
	@Context
	private UriInfo uriInfo;
	
	// TODO
	@Inject
	private IPatientService patientService;
	
	// TODO
	@Inject
	private IProviderService providerService;
	
	// TODO
	@GET
	@Path("/{id}")
	@Produces
	/*
	 * Return a provider DTO including the list of treatments they are administering.
	 */
	public Response getProvider(@PathParam("id") String id) {
		try {
			UUID providerId = UUID.fromString(id);
			ProviderDto provider = providerService.getProvider(providerId, true);
			ResponseBuilder responseBuilder = Response.ok(provider);
			/* 
			 * TODO Add links for treatments in response headers.
			 */
			for (TreatmentDto treatment : provider.getTreatments()) {
				responseBuilder.link(getTreatmentUri(uriInfo, treatment.getProviderId(), treatment.getId()), TREATMENT);
			}
			
			return responseBuilder.build();
		} catch (ProviderServiceExn e) {
			logger.info("Failed to find provider with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed provider id: "+id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	// TODO
	@GET
	@Path("/{id}/treatment/{tid}")
	@Produces
	/*
	 * Return a provider DTO including the list of treatments they are administering.
	 */
	public Response getTreatment(@PathParam("id") String id, @PathParam("tid") String tid) {
		try {
			UUID providerId = UUID.fromString(id);
			UUID treatmentId = UUID.fromString(tid);
			TreatmentDto treatment = providerService.getTreatment(providerId, treatmentId);
			ResponseBuilder responseBuilder = Response.ok(treatment);
			/* 
			 * TODO Add links for patient and provider in response headers.
			 */
			responseBuilder.link(getPatientUri(uriInfo, treatment.getPatientId()), PATIENT);
			responseBuilder.link(getProviderUri(uriInfo, treatment.getProviderId()), PROVIDER);

			
			return responseBuilder.build();
		} catch (ProviderServiceExn e) {
			logger.info("Failed to find provider with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed provider id: "+id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	// TODO
	@POST
	@Path("/")
	/*
	 * This operation is intended to upload lists of providers, patients and treatments
	 * from the client.  Imagine the client is a mobile app on which data is entered and later
	 * synchronized with the server.  We just do the synchronization in one direction.
	 * The input data is expected to be a JSON objects with three lists:
	 * {
	 *    "providers" : [ provider1, ...., providerk ],
	 *    "patients" : [ patient1, ...., patientm ],
	 *    "treatments" : [ treatment1, ...., treatmentn ]
	 * }
	 */
	public Response upload(InputStream is) {
		try (JsonReader rd = gson.newJsonReader(new BufferedReader(new InputStreamReader(is)))) {
			
			logger.info("Receiving upload of clinic data...");
			
			
			rd.beginObject();
			
			/*
			 * Read stream of providers and add to database
			 */
			logger.info("...reading provider data...");
			String label = rd.nextName();
			if (!PROVIDERS.equals(label)) {
				logger.log(Level.SEVERE, String.format("Unexpected label, expected %s, found %s",  PROVIDERS, label));
				return Response.status(Status.BAD_REQUEST).build();
			}
			rd.beginArray();
			while (rd.hasNext()) {
				ProviderDto provider = gson.fromJson(rd, ProviderDto.class);
				logger.info("......uploading provider "+provider.getId());
				providerService.addProvider(provider);
			}
			rd.endArray();
			
			logger.info("...reading patient data...");
			/*
			 * Read stream of patients and add to database
			 */
			label = rd.nextName();
			if (!PATIENTS.equals(label)) {
				logger.log(Level.SEVERE, String.format("Unexpected label, expected %s, found %s",  PATIENTS, label));
				return Response.status(Status.BAD_REQUEST).build();
			}
			rd.beginArray();
			while (rd.hasNext()) {
				PatientDto patient = gson.fromJson(rd, PatientDto.class);
				logger.info("......uploading patient "+patient.getId());
				patientService.addPatient(patient);
			}
			rd.endArray();
			
			/*
			 * TODO read stream of treatments and add to database
			 */
			logger.info("...reading treatment data...");
			label = rd.nextName();
			if (!TREATMENTS.equals(label)) {
				logger.log(Level.SEVERE, String.format("Unexpected label, expected %s, found %s", TREATMENTS, label));
				return Response.status(Status.BAD_REQUEST).build();
			}
			rd.beginArray();
			while (rd.hasNext()) {
				TreatmentDto treatment = gson.fromJson(rd, TreatmentDto.class);
				logger.info("......uploading treatment" + treatment.getId());
				providerService.addTreatment(treatment);
			}
			rd.endArray();


			rd.endObject();
			
			logger.info("...upload complete!");
			
			return Response.ok().build();
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to open Json stream!", e);
			return Response.serverError().build();
		} catch (ProviderServiceExn e) {
			logger.log(Level.SEVERE, "Failed to add provider or treatment!", e);
			return Response.serverError().build();
		} catch (PatientServiceExn e) {
			logger.log(Level.SEVERE, "Failed to add patient!", e);
			return Response.serverError().build();
		}
	}
	
}
