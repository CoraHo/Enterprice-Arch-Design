package edu.stevens.cs548.clinic.micro.domain;

import edu.stevens.cs548.clinic.gson.GsonProvider;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

// TODO annotate
@RegisterRestClient(configKey="clinic-domain.api")
@RegisterProvider(GsonProvider.class)
@Path("patient")
public interface IPatientMicroService {

	@POST
	@Consumes("application/json")
	public Response addPatient(PatientDto dto);

	@GET
	@Produces("application/json")
	public List<PatientDto> getPatients();

	@GET
	@Produces("application/json")
	@Path("/{id}")
	public PatientDto getPatient(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);

	@GET
	@Produces("application/json")
	@Path("/{id}/treatment/{tid}")
	public TreatmentDto getTreatment(@PathParam("id") String patientId, @PathParam("tid") String treatmentId);

	@DELETE
	public void removeAll();
	
}
