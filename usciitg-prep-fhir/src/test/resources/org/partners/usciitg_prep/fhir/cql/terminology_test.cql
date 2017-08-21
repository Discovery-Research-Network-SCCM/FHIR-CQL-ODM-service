library terminology_test version '0.1'

using FHIR version '1.0.2'

include FHIRHelpers version '1.0.2' called FHIRHelpers

codesystem "LOINC": 'http://loinc.org'
codesystem "RXNORM": 'http://www.nlm.nih.gov/research/umls/rxnorm'

valueset "Influenza": 'Influenza.Test.OID'
valueset "Antiviral": 'Antiviral.Test.OID'

context Patient

define FluCode: Code '76077-7' from "LOINC"
define AntiviralCode: Code '69722' from "RXNORM"

define public fluinvalueset: 
ToConcept(FluCode) in "Influenza"

define public antiviralinvalueset: 
ToConcept(AntiviralCode) in "Antiviral"