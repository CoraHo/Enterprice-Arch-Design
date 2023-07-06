package edu.stevens.cs548.clinic.service.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;

@Singleton
@LocalBean
@Startup
// @ApplicationScoped
// @Transactional
public class InitBean {

	private static final Logger logger = Logger.getLogger(InitBean.class.getCanonicalName());

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private PatientDtoFactory patientFactory = new PatientDtoFactory();

	private ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	// TODO
	@Inject
	private IPatientService patientService;

	// TODO
	@Inject
	private IProviderService providerService;

	/*
	 * Initialize using EJB logic
	 */
	@PostConstruct
	/*
	 * This should work to initialize with CDI bean, but there is a bug in
	 * Payara.....
	 */
	// public void init(@Observes @Initialized(ApplicationScoped.class)
	// ServletContext init) {
	public void init() {
		/*
		 * Put your testing logic here. Use the logger to display testing output in the
		 * server logs.
		 */
		logger.info("Your name here: ");
		System.err.println("Gai Li Ho");

		try {

			/*
			 * Clear the database and populate with fresh data.
			 * 
			 * Note that the service generates the external ids, when adding the entities.
			 */

			providerService.removeAll();
			patientService.removeAll();

			PatientDto john = patientFactory.createPatientDto();
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));

			john.setId(patientService.addPatient(john));

			ProviderDto jane = providerFactory.createProviderDto();
			jane.setName("Jane Doe");
			jane.setNpi("1234");

			jane.setId(providerService.addProvider(jane));

			DrugTreatmentDto drug01 = treatmentFactory.createDrugTreatmentDto();
			drug01.setPatientId(john.getId());
			drug01.setPatientName(john.getName());
			drug01.setProviderId(jane.getId());
			drug01.setProviderName(jane.getName());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setDosage(20);
			drug01.setFrequency(7);
			drug01.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug01.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			providerService.addTreatment(drug01);

			// TODO add more testing, including treatments and providers
			PatientDto richard = patientFactory.createPatientDto();
			richard.setName("Richard Franc");
			richard.setDob((LocalDate.parse("1989-11-23")));
			richard.setId(patientService.addPatient(richard));

			PatientDto joe = patientFactory.createPatientDto();
			joe.setName("Joe Jones");
			joe.setDob((LocalDate.parse("1994-01-13")));
			joe.setId(patientService.addPatient(joe));

			ProviderDto sarah = providerFactory.createProviderDto();
			sarah.setName("Sarah Medical");
			sarah.setNpi("45678");
			sarah.setId(providerService.addProvider(sarah));

			ProviderDto monica = providerFactory.createProviderDto();
			monica.setName("Monica Miller");
			monica.setNpi("45678");
			monica.setId(providerService.addProvider(monica));

			SurgeryTreatmentDto surgery01 = treatmentFactory.createSurgeryTreatmentDto();
			surgery01.setPatientId(richard.getId());
			surgery01.setPatientName(richard.getName());
			surgery01.setProviderId(sarah.getId());
			surgery01.setProviderName(sarah.getName());
			surgery01.setDiagnosis("tumor");
			surgery01.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			surgery01.setDischargeInstructions("rest for one month");


			List<LocalDate> dates = new ArrayList<>();
			dates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			dates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			dates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			RadiologyTreatmentDto radio01 = treatmentFactory.createRadiologyTreatmentDto();
			radio01.setPatientId(richard.getId());
			radio01.setPatientName(richard.getName());
			radio01.setProviderId(sarah.getId());
			radio01.setProviderName(sarah.getName());
			radio01.setDiagnosis("follow up radiology treatment for richard in surgery01");
			radio01.setTreatmentDates(dates);


			List<TreatmentDto> followUp = new ArrayList<>();
			followUp.add(radio01);
			surgery01.setFollowupTreatments(followUp);
			providerService.addTreatment(surgery01);

			PhysiotherapyTreatmentDto physio01 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physio01.setPatientId(john.getId());
			physio01.setPatientName(john.getName());
			physio01.setProviderId(jane.getId());
			physio01.setProviderName(jane.getName());
			physio01.setDiagnosis("sports injuries");
			physio01.setTreatmentDates(dates);

			providerService.addTreatment(physio01);

			SurgeryTreatmentDto surgery02 = treatmentFactory.createSurgeryTreatmentDto();
			surgery02.setPatientId(joe.getId());
			surgery02.setPatientName(joe.getName());
			surgery02.setProviderId(monica.getId());
			surgery02.setProviderName(monica.getName());
			surgery02.setDiagnosis("appendectomy");
			surgery02.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			surgery02.setDischargeInstructions("don't lift heavy stuff within two weeks");

			providerService.addTreatment(surgery02);


			RadiologyTreatmentDto radio02 = treatmentFactory.createRadiologyTreatmentDto();
			radio02.setPatientId(joe.getId());
			radio02.setPatientName(joe.getName());
			radio02.setProviderId(monica.getId());
			radio02.setProviderName(monica.getName());
			radio02.setDiagnosis("cancer");
			radio02.setTreatmentDates(dates);

			providerService.addTreatment(radio02);

			// Now show in the logs what has been added

			Collection<PatientDto> patients = patientService.getPatients();
			for (PatientDto p : patients) {
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId().toString(),
						p.getDob().toString()));
				logTreatments(p.getTreatments());
			}

			Collection<ProviderDto> providers = providerService.getProviders();
			for (ProviderDto p : providers) {
				logger.info(String.format("Provider %s, ID %s, NPI %s", p.getName(), p.getId().toString(), p.getNpi()));
				logTreatments(p.getTreatments());
			}

		} catch (Exception e) {

			throw new IllegalStateException("Failed to add record.", e);

		}
		
	}

	public void shutdown(@Observes @Destroyed(ApplicationScoped.class) ServletContext init) {
		logger.info("App shutting down....");
	}

	private void logTreatments(Collection<TreatmentDto> treatments) {
		for (TreatmentDto treatment : treatments) {
			if (treatment instanceof DrugTreatmentDto) {
				logTreatment((DrugTreatmentDto) treatment);
			} else if (treatment instanceof SurgeryTreatmentDto) {
				logTreatment((SurgeryTreatmentDto) treatment);
			} else if (treatment instanceof RadiologyTreatmentDto) {
				logTreatment((RadiologyTreatmentDto) treatment);
			} else if (treatment instanceof PhysiotherapyTreatmentDto) {
				logTreatment((PhysiotherapyTreatmentDto) treatment);
			}
			if (!treatment.getFollowupTreatments().isEmpty()) {
				logger.info("============= Follow-up Treatments");
				logTreatments(treatment.getFollowupTreatments());
				logger.info("============= End Follow-up Treatments");
			}
		}
	}

	private void logTreatment(DrugTreatmentDto t) {
		logger.info(String.format("...Drug treatment for %s, drug %s", t.getPatientName(), t.getDrug()));
	}

	private void logTreatment(RadiologyTreatmentDto t) {
		logger.info(String.format("...Radiology treatment for %s", t.getPatientName()));
	}

	private void logTreatment(SurgeryTreatmentDto t) {
		logger.info(String.format("...Surgery treatment for %s", t.getPatientName()));
	}

	private void logTreatment(PhysiotherapyTreatmentDto t) {
		logger.info(String.format("...Physiotherapy treatment for %s", t.getPatientName()));
	}

}
