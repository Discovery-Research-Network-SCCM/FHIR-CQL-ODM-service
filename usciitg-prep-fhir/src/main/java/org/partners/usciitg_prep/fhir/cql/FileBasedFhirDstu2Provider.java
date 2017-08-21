package org.partners.usciitg_prep.fhir.cql;

import java.io.File;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.BaseResource;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.DateTimeType;
import org.hl7.fhir.instance.model.Period;
import org.opencds.cqf.cql.elm.execution.InEvaluator;
import org.opencds.cqf.cql.elm.execution.IncludesEvaluator;
import org.opencds.cqf.cql.file.fhir.FileBasedFhirProvider;
import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.runtime.Interval;
import org.opencds.cqf.cql.terminology.ValueSetInfo;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.RuntimeSearchParam;

/**
 * DSTU2 version of org.opencds.cqf.cql.file.fhir.FileBasedFhirProvider
 *
 */
public class FileBasedFhirDstu2Provider extends UsciitgFhirDataProviderHL7 {
	private Path path;
	private FileBasedFhirProvider fileBasedFhirProvider;

	public FileBasedFhirDstu2Provider(String path, String endpoint) {
		this(path, endpoint, false);
	}
	
	public FileBasedFhirDstu2Provider(String path, String endpoint, Boolean includesFhirHelpers) {
		super(includesFhirHelpers);		
		if (path.isEmpty()) {
			throw new InvalidPathException(path, "Invalid path!");
		}
		this.path = Paths.get(path);
		setFhirContext(FhirContext.forDstu2Hl7Org());

		// wrapper around FileBasedFhirProvider
		fileBasedFhirProvider = new FileBasedFhirProvider(path, endpoint);
	}

	@SuppressWarnings("unused")
	private URL pathToModelJar;

	public void setPathToModelJar(URL pathToModelJar) {
		this.pathToModelJar = pathToModelJar;
		fileBasedFhirProvider.setPathToModelJar(pathToModelJar);
	}

	public FileBasedFhirDstu2Provider withPathToModelJar(URL pathToModelJar) {
		setPathToModelJar(pathToModelJar);
		fileBasedFhirProvider.setPathToModelJar(pathToModelJar);
		return this;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Object> retrieve(String context, Object contextValue,
			String dataType, String templateId, String codePath,
			Iterable<Code> codes, String valueSet, String datePath,
			String dateLowPath, String dateHighPath, Interval dateRange) {

		List<Object> results = new ArrayList<>();
		List<String> patientFiles;
		Path toResults = path;

		// default context is Patient
		if (context == null) {
			context = "Patient";
		}

		if (templateId != null && !templateId.equals("")) {
			// TODO: do something?
		}

		if (codePath == null && (codes != null || valueSet != null)) {
			throw new IllegalArgumentException(
					"A code path must be provided when filtering on codes or a valueset.");
		}

		if (context.equals("Patient") && contextValue != null) {
			toResults = toResults.resolve((String) contextValue);
		}

		// Need the context value (patient id) to resolve the toResults path
		// correctly
		else if (context.equals("Patient") && contextValue == null) {
			toResults = toResults.resolve(getDefaultPatient(toResults));
		}

		if (dataType != null) {
			// TODO: this isn't right - Patient is a valid fhir resource.
			if (!dataType.equals("Patient"))
				toResults = toResults.resolve(dataType.toLowerCase());
		} else { // Just in case -- probably redundant error checking...
			throw new IllegalArgumentException(
					"A data type (i.e. Procedure, Valueset, etc...) must be specified for clinical data retrieval");
		}

		// No filtering
		if (dateRange == null && codePath == null) {
			patientFiles = getPatientFiles(toResults, context);
			for (String resource : patientFiles) {
				results.add(fhirContext.newJsonParser().parseResource(resource));
			}
			return results;
		}

		patientFiles = getPatientFiles(toResults, context);

		// filtering
		// NOTE: retrieves can include both date and code filtering,
		// so even though I may include a record if it is within the date range,
		// that record may be excluded later during the code filtering stage
		for (String resource : patientFiles) {
			Object res = fhirContext.newJsonParser().parseResource(resource);

			// since retrieves can include both date and code filtering, I need
			// this flag
			// to determine inclusion of codes -- if date is no good -- don't
			// test code
			boolean includeRes = true;

			// dateRange element optionally allows a date range to be provided.
			// The clinical statements returned would be only those clinical
			// statements whose date
			// fell within the range specified.
			if (dateRange != null) {

				// Expand Interval DateTimes to avoid InEvalutor returning null
				// TODO: account for possible null for high or low? - No issues
				// with this yet...
				Interval expanded = new Interval(DateTime.expandPartialMin(
						(DateTime) dateRange.getLow(), 7), true,
						DateTime.expandPartialMin(
								(DateTime) dateRange.getHigh(), 7), true);
				if (datePath != null) {
					if (dateHighPath != null || dateLowPath != null) {
						throw new IllegalArgumentException(
								"If the datePath is specified, the dateLowPath and dateHighPath attributes must not be present.");
					}

					DateTime date = null;
					Interval dateInterval = null;
                    List<String> codeProperties = mapSearchParamToProperty(res, datePath);
                    Object path = null;

                    for(String property: codeProperties){
                    	path = resolvePath(res, property);
                        if(path != null){
                            break;
                        }
                    }					

					if (path instanceof DateTime) {
						date = (DateTime) path;
					} else if (path instanceof DateTimeType) {
						date = toDateTime((DateTimeType) path);
					}

					else if (path instanceof Interval) {
						dateInterval = (Interval) path;
					}
					// Interval could be represented as a Period
					else if (path instanceof Period) {
						DateTime start = toDateTime(((Period) path).getStart());
						DateTime end = toDateTime(((Period) path).getEnd());
						dateInterval = new Interval(start, true, end, true);
					}

					if (date != null && InEvaluator.in(date, expanded)) {
						results.add(res);
					} else if (dateInterval != null
							&& (Boolean) IncludesEvaluator.includes(expanded,
									dateInterval)) {
						results.add(res);
					} else {
						includeRes = false;
					}
				}

				else {
					if (dateHighPath == null && dateLowPath == null) {
						throw new IllegalArgumentException(
								"If the datePath is not given, either the lowDatePath or highDatePath must be provided.");
					}

					// get the high and low dates if present
					// if not present, set to corresponding value in the
					// expanded Interval
					DateTime highDt = dateHighPath != null ? (DateTime) resolvePath(
							res, dateHighPath) : (DateTime) expanded.getHigh();
					DateTime lowDt = dateLowPath != null ? (DateTime) resolvePath(
							res, dateLowPath) : (DateTime) expanded.getLow();

					// the low and high dates are resolved -- create the
					// Interval
					Interval highLowDtInterval = new Interval(lowDt, true,
							highDt, true);

					// Now the Includes operation
					if ((Boolean) IncludesEvaluator.includes(expanded,
							highLowDtInterval)) {
						results.add(res);
					} else {
						includeRes = false;
					}
				}
			}

			// codePath specifies which property/path of the model contains the
			// Code or Codes for the clinical statement
			if (codePath != null && !codePath.equals("") && includeRes) {
                List<String> codeProperties = mapSearchParamToProperty(res, codePath);
                Object resCodes = null;

                for(String property: codeProperties){
                    resCodes = resolvePath(res, property);
                    if(resCodes != null){
                        break;
                    }
                }
                
				if (valueSet != null && !valueSet.equals("")) {
					// now we need to get the codes in the resource and check
					// for membership in the valueset
					
					if (resCodes instanceof Iterable) {
						for (Object codeObj : (Iterable) resCodes) {
							boolean inValSet = checkCodeMembership(codeObj,
									valueSet);
							if (inValSet && results.indexOf(res) == -1)
								results.add(res);
							else if (!inValSet)
								results.remove(res);
						}
					} else if (resCodes instanceof CodeableConcept) {
						if (checkCodeMembership(resCodes, valueSet)
								&& results.indexOf(res) == -1)
							results.add(res);
					}
				} else if (codes != null) {
					for (Code code : codes) {						
						if (resCodes instanceof Iterable) {
							for (Object codeObj : (Iterable) resCodes) {
								Iterable<Coding> conceptCodes = ((CodeableConcept) codeObj)
										.getCoding();
								for (Coding c : conceptCodes) {
									if (c.getCodeElement().getValue()
											.equals(code.getCode())
											&& c.getSystem().equals(
													code.getSystem())) {
										if (results.indexOf(res) == -1)
											results.add(res);
									} else if (results.indexOf(res) != -1)
										results.remove(res);
								}
							}
						} else if (resCodes instanceof CodeableConcept) {
							for (Coding c : ((CodeableConcept) resCodes)
									.getCoding()) {
								if (c.getCodeElement().getValue()
										.equals(code.getCode())
										&& c.getSystem().equals(
												code.getSystem())) {
									if (results.indexOf(res) == -1)
										results.add(res);
								} else if (results.indexOf(res) != -1)
									results.remove(res);
							}
						}
					}
				}
			}
		} // end of filtering for each loop

		return results;
	}

    public boolean checkCodeMembership(Object codeObj, String vsId) {
        Iterable<Coding> conceptCodes = ((CodeableConcept)codeObj).getCoding();
        for (Coding code : conceptCodes) {
            if (terminologyProvider.in(new Code()
                            .withCode(code.getCodeElement().getValue())
                            .withSystem(code.getSystem()),
                    new ValueSetInfo().withId(vsId)))
            {
                return true;
            }
        }
        return false;
    }

	public String getDefaultPatient(Path evalPath) {
		return fileBasedFhirProvider.getDefaultPatient(evalPath);
	}

	public List<String> getPatientFiles(Path evalPath, String context) {
		return fileBasedFhirProvider.getPatientFiles(evalPath, context);
	}

	public String readFile(File f) {
		return fileBasedFhirProvider.readFile(f);
	}

	public DateTime toDateTime(DateTimeType hapiDt) {
		return DateTime.fromJavaDate(hapiDt.getValue());
	}
	
    private List<String> mapSearchParamToProperty(Object resource, String paramName){
        //map search parameter to property name, for testing only, this is not always going to work!!
        List<String> properties = new ArrayList<String>();
        if(resource instanceof BaseResource){
            RuntimeSearchParam searchParam = getFhirContext().getResourceDefinition((BaseResource)resource).getSearchParam(paramName);
            List<String> params = searchParam.getPathsSplit();
            for(String param: params){
                param = param.substring(param.indexOf(".") + 1);
                if(param.contains("[")){
                    properties.add(param.substring(0, param.indexOf("[")));
                }else{
                    properties.add(param);
                }
            }
        }

        return properties;
    }
}