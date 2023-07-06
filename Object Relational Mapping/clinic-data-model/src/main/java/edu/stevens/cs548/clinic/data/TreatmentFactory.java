package edu.stevens.cs548.clinic.data;

public class TreatmentFactory implements ITreatmentFactory {

	@Override
	public DrugTreatment createDrugTreatment() {
		return new DrugTreatment();
	}
	
	// TODO define other factory methods

	public SurgeryTreatment createSurgeryTreatment() {
		return new SurgeryTreatment();
	}

	public RadiologyTreatment createRadiologyTreatment() {
		return new RadiologyTreatment();
	}

	public PhysiotherapyTreatment createPhysiotherapyTreatment() {
		return new PhysiotherapyTreatment();
	}

}
