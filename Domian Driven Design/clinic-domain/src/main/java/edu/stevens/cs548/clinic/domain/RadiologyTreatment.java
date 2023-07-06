package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//TODO JPA annotations
@Entity
public class RadiologyTreatment extends Treatment {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3656673416179492428L;

	// TODO  including order by date
	@ElementCollection
	protected List<LocalDate> treatmentDates;

	public void addTreatmentDate(LocalDate date) {
		treatmentDates.add(date);
	}

	@Override
	public <T> T export(ITreatmentExporter<T> visitor) {
		// TODO
		return visitor.exportRadiology(treatmentId,
				patient.getPatientId(),
				patient.getName(),
				provider.getProviderId(),
				provider.getName(),
				diagnosis,
				treatmentDates,
				() -> exportFollowupTreatments(visitor)
		);
	}
	
	public RadiologyTreatment() {
		super();
		treatmentDates = new ArrayList<>();
	}
	
}
