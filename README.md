# FHIR-CQL-ODM service

This project is a prototype web service architecture designed to integrate electronic data capture (EDC) systems with HL7 FHIR services to allow secondary use of clinical data in research case report forms (CRFs). It executes logic defined in a Clinical Quality Language (CQL) library against a FHIR endpoint and returns patient data formatted as CDISC ODM XML, the prevailing data transport format used by EDC systems. See the REDCap plugin project for an example user interface that uses this web service to import data and pre-populate an electronic CRF.

This project uses an open source Java-based CQL evaluation engine developed by the University of Utah. It expands on the core engine code by adding support for directly executing ELM XML libraries (in addition to CQL libraries) and support for nested property paths (but not full FhirPath). The service currently does not support repeating CRF fields or CRF item grouping. It includes a basic JPA implementation of the FHIR Terminology API that supports value set expansion only, for testing purposes.


# License

Copyright 2017 Discovery, the Critical Care Research Network

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.