package org.partners.usciitg_prep.fhir.cql;

import org.hl7.fhir.Id;
import org.hl7.fhir.ValueSet;
import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.cql.terminology.ValueSetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTerminologyProvider implements TerminologyProvider{
    private Map<String, ValueSet> valueSets = new HashMap<String, ValueSet>();
    private Map<String, List<Code>> codes = new HashMap<String, List<Code>>();

    TestTerminologyProvider(){
        //create test data
    	createValueSet("Influenza.Test.OID");
    	addCode("Influenza.Test.OID", "76077-7", "http://loinc.org");
    	
    	createValueSet("Influenza.Positive.Test.OID");
    	addCode("Influenza.Positive.Test.OID", "LA6576-8", "http://loinc.org");
    	
    	createValueSet("Influenza.Molecular.Test.OID");
    	addCode("Influenza.Molecular.Test.OID", "76077-7", "http://loinc.org");
    	
    	createValueSet("Platelet.Test.OID");
    	addCode("Platelet.Test.OID", "777-3", "http://loinc.org");
    	
    	createValueSet("Urine.Test.OID");
    	addCode("Urine.Test.OID", "9190-0", "http://loinc.org");
    	addCode("Urine.Test.OID", "9188-4", "http://loinc.org");
    	
    	createValueSet("Creatinine.Test.OID");
    	addCode("Creatinine.Test.OID", "2160-0", "http://loinc.org");

    	createValueSet("Fluvax.Test.OID");
    	addCode("Fluvax.Test.OID", "140", "http://hl7.org/fhir/sid/cvx");
    	
    	createValueSet("Vasopressor.Test.OID");
    	addCode("Vasopressor.Test.OID", "3628", "http://www.nlm.nih.gov/research/umls/rxnorm");
    	
    	createValueSet("Dopamine.Test.OID");
    	addCode("Dopamine.Test.OID", "1292750", "http://www.nlm.nih.gov/research/umls/rxnorm");

    	createValueSet("Dobutamine.Test.OID");    	
    	createValueSet("Milrinone.Test.OID");
    	createValueSet("Epinephrine.Test.OID");
    	createValueSet("Norepinephrine.Test.OID");
    	createValueSet("Vasopressin.Test.OID");
    	createValueSet("Phenylephrine.Test.OID");
    	
    	createValueSet("Antiviral.Test.OID");
    	addCode("Antiviral.Test.OID", "69722", "http://www.nlm.nih.gov/research/umls/rxnorm");
    	addCode("Antiviral.Test.OID", "260101", "http://www.nlm.nih.gov/research/umls/rxnorm");
    	
    	createValueSet("Oseltamivir.Test.OID");
    	addCode("Oseltamivir.Test.OID", "260101", "http://www.nlm.nih.gov/research/umls/rxnorm");
    	
    	createValueSet("Zanamivir.Test.OID");
    	addCode("Zanamivir.Test.OID", "582580", "http://www.nlm.nih.gov/research/umls/rxnorm");
    	
    	createValueSet("Peramivir.Test.OID");
    	createValueSet("Amantadine.Test.OID");
    	createValueSet("Rimantadine.Test.OID");
    	
    	createValueSet("Antibacterial.Test.OID");
    	addCode("Antibacterial.Test.OID", "82122", "http://www.nlm.nih.gov/research/umls/rxnorm");
    	
    	createValueSet("Corticosteroid.Test.OID");
    	addCode("Corticosteroid.Test.OID", "5492", "http://www.nlm.nih.gov/research/umls/rxnorm"); 
    	
    	createValueSet("Antifungal.Test.OID");
    	addCode("Antifungal.Test.OID", "121243", "http://www.nlm.nih.gov/research/umls/rxnorm"); 
    	
    	createValueSet("IcuAdmit.Test.OID");
    	addCode("IcuAdmit.Test.OID", "305351004", "http://snomed.info/sct");
    }

    @Override
    public boolean in(org.opencds.cqf.cql.runtime.Code code,
                      ValueSetInfo valueSet) {

        if(!valueSet.getId().isEmpty()){
        	List<Code> valueSetCodes = codes.get(valueSet.getId());
        	if(valueSetCodes == null){return false;}
        	for(Code valueSetCode: valueSetCodes){
        		if(valueSetCode.getCode().equalsIgnoreCase(code.getCode()) && valueSetCode.getSystem().equalsIgnoreCase(code.getSystem())){
        			//ignore display and version
        			return true;
        		}
        	}
        }

        return false;
    }

    @Override
    public Iterable<org.opencds.cqf.cql.runtime.Code> expand(
            ValueSetInfo valueSet) {
        if(!valueSet.getId().isEmpty()){
        	return codes.get(valueSet.getId());
        }

        return null;
    }

    @Override
    public org.opencds.cqf.cql.runtime.Code lookup(
            org.opencds.cqf.cql.runtime.Code code, CodeSystemInfo codeSystem) {
    	throw new UnsupportedOperationException();
    }
    
    private void createValueSet(String id){
        ValueSet valueSet = new ValueSet();
        Id oid = new Id();
        oid.setId(id);
        valueSet.setId(oid);
        valueSets.put(id, valueSet);
        
        List<Code> valueSetCodes = new ArrayList<Code>();        
        codes.put(id, valueSetCodes);
    }

    private void addCode(String id, String codeval, String system){
        Code code = new Code();
        code.setCode(codeval);
        code.setSystem(system);
        
        List<Code> valueSetCodes = codes.get(id);
        valueSetCodes.add(code);
    }
}
