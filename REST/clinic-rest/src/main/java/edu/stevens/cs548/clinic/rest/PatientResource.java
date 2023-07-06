package edu.stevens.cs548.clinic.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.util.UUID;
import java.util.logging.Logger;


import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

// TODO
@RequestScoped
@Path("/patient")
public class PatientResource extends ResourceBase {
	
	private static final Logger logger = Logger.getLogger(PatientResource.class.getCanonicalName());
	
	@Context
	private UriInfo uriInfo;
	
	// TODO
	@Inject
	private IPatientService patientService;
	
	// TODO
	/*
	 * Return a provider DTO including the list of treatments they are administering.
	 */
	@GET
	@Path("/{id}")
	@Produces
	public Response getPatient(@PathParam("id") String id) {
		try {
			UUID patientId = UUID.fromString(id);
			PatientDto patient = patientService.getPatient(patientId, true);
			ResponseBuilder responseBuilder = Response.ok(patient);
			/* 
			 * Add links for treatments in response headers.
			 */
			for (TreatmentDto treatment : patient.getTreatments()) {
				responseBuilder.link(getTreatmentUri(uriInfo, treatment.getProviderId(), treatment.getId()), TREATMENT);
			}
			return responseBuilder.build();
		} catch (PatientServiceExn e) {
			logger.info("Failed to find patient with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed patient id: "+id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
}
