INSERT INTO dag_fhir_servers (group_id, fhir_endpoint_url, fhir_endpoint_username, fhir_endpoint_password)
VALUES (2, 'https://open-ic.epic.com/FHIR/api/FHIR/DSTU2', NULL, NULL);
INSERT INTO dag_fhir_servers (group_id, fhir_endpoint_url, fhir_endpoint_username, fhir_endpoint_password)
VALUES (4, 'http://fhirtest.uhn.ca/baseDstu2', NULL, NULL);

INSERT INTO crf_elm_queries (project_id, event_id, instrument, path, identifier)
VALUES (29, NULL, 'usciit_prep_flu_study', '../usciitg-prep-fhir/src/test/resources/org/partners/usciitg_prep/fhir/cql', 'usciitg_flu_study');