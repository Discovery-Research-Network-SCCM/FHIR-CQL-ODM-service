library test_flu_study version '0.1'

using FHIR version '1.0.2'

include FHIRHelpers version '1.0.2' called FHIRHelpers

valueset "Influenza": 'Influenza.Test.OID'

parameter "EncounterStart" DateTime
parameter "EncounterEnd" DateTime

context Patient

define public sex: 
if Patient.gender = 'male'
then '1'
else '2'

define private TwoWeeksOfAdmit:
Interval[EncounterStart - 14 days, EncounterEnd + 14 days]

define public flu: 
if Count(
[Observation] Obs) > 0
then 'true'
else 'false'