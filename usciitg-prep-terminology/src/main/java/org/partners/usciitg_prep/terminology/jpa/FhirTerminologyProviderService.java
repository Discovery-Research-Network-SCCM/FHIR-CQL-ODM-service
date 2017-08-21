package org.partners.usciitg_prep.terminology.jpa;

import ca.uhn.fhir.jpa.provider.dstu3.JpaResourceProviderDstu3;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FhirTerminologyProviderService {
	
	@Autowired
	FhirValueSetDaoDstu3 myValueSetDao;
	
	public JpaResourceProviderDstu3<ValueSet> getValueSetProvider(){
		JpaResourceProviderDstu3<ValueSet> valueSetProvider = new JpaResourceProviderDstu3<ValueSet>();				
		valueSetProvider.setDao(myValueSetDao);
		return valueSetProvider;
	}
	
	public JpaResourceProviderDstu3<CodeSystem> getCodeSystemProvider(){
		return null;  //not yet implemented
	}
}
