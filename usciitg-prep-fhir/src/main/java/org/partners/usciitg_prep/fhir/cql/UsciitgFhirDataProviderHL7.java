package org.partners.usciitg_prep.fhir.cql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.DateTimeType;
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.Enumeration;
import org.hl7.fhir.instance.model.InstantType;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Quantity;
import org.hl7.fhir.instance.model.TimeType;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.instance.model.api.IBaseDatatypeElement;
import org.hl7.fhir.instance.model.api.IBaseElement;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.opencds.cqf.cql.data.fhir.FhirDataProviderHL7;
import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.runtime.Concept;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.runtime.Interval;
import org.opencds.cqf.cql.runtime.Time;

import ca.uhn.fhir.context.BaseRuntimeChildDefinition;
import ca.uhn.fhir.context.BaseRuntimeElementCompositeDefinition;
import ca.uhn.fhir.context.RuntimeChildChoiceDefinition;
import ca.uhn.fhir.context.RuntimeCompositeDatatypeDefinition;

/**
 * Custom data provider for DSTU2 HL7_ORG that extends org.opencds.cqf.cql.data.fhir.FhirDataProviderHL7
 * Adds support for nested property paths.
 *
 */
public class UsciitgFhirDataProviderHL7 extends FhirDataProviderHL7
{
	private Boolean includesFhirHelpers = false;
	
    public UsciitgFhirDataProviderHL7() {
    	super();
    }
    
    public UsciitgFhirDataProviderHL7(Boolean includesFhirHelpers) {
    	super();
    	this.includesFhirHelpers = includesFhirHelpers;
    }    
    
    public Boolean getIncludesFhirHelpers() {
		return includesFhirHelpers;
	}

	public void setIncludesFhirHelpers(Boolean includesFhirHelpers) {
		this.includesFhirHelpers = includesFhirHelpers;
	}

	protected Interval toInterval(Period value) {
         return new Interval(toDateTime(value.getStart()), true, toDateTime(value.getEnd()), true); 
    }
    
    protected org.opencds.cqf.cql.runtime.Quantity toQuantity(Quantity value) {
        return new org.opencds.cqf.cql.runtime.Quantity().withValue(value.getValue()).withUnit(value.getUnit()); 
   }

    protected Code toCode(Coding value) {
        Code code = new Code();
        return code.withCode(value.getCode())
        		.withDisplay(value.getDisplay())
        		.withSystem(value.getSystem())
        		.withVersion(value.getVersion());
    }

    protected Concept toConcept(CodeableConcept value) {
        Concept concept = new Concept();
        List<Code> codes = new ArrayList<Code>();
        for(Coding coding : value.getCoding()){
            codes.add(toCode(coding));
        }
        concept.setCodes(codes);
        concept.setDisplay(value.getText());
        return concept;
    }

	@Override
    protected Object fromJavaPrimitive(Object value, Object target) {
        if (target instanceof DateTimeType) {
            return new Date(); // TODO: Why is this so hard?
        }
        else if (target instanceof DateType) {
            return new Date();
        }
        else if (target instanceof TimeType) {
            if (value instanceof Time) {
                return ((Time) value).getPartial().toString();
            }
            return new Date();
        }
        else {
            return value;
        }
    }
	
	@Override
    protected Object toJavaPrimitive(Object result, Object source) {
		if(source instanceof Iterable){
            List<Object> mappedResults = new ArrayList<Object>();
            for (Object item : (Iterable<?>)source) {             
            	Object mappedItem = toJavaPrimitive(item, item);
                if(mappedItem instanceof IPrimitiveType){
                    mappedResults.add(((IPrimitiveType<?>) mappedItem).getValue());
                }else{
                    mappedResults.add(mappedItem);
                }
            }

            return mappedResults;
        }
		else if (source instanceof DateTimeType) {
            return DateTime.fromJavaDate(((DateTimeType) source).getValue()); //toDateTime((DateTimeType)source);
        }
        else if (source instanceof DateType) {
            return DateTime.fromJavaDate(((DateType) source).getValue()); //toDateTime((DateType)source);
        }
        else if (source instanceof TimeType) {
            return toTime((TimeType)source);
        }
        else if (source instanceof InstantType) {
            return toDateTime((InstantType)source);
        }
        else {
            return result;
        }
}
    
	/**
	 * This is required conversion because we are not including FHIRHelpers CQL library when using ELM directly
	 * @param result
	 * @param source
	 * @return CQL Engine runtime version of FHIR objects 
	 * (CodeableConcept to Concept, Coding to Code, Period to Interval, Quantity to Quantity)
	 * and java value of primitives
	 */
	protected Object toRuntimePrimitive(Object result, Object source) {
		if(source instanceof Iterable){
            List<Object> mappedResults = new ArrayList<Object>();
            for (Object item : (Iterable<?>)source) {
                Object mappedItem = toRuntimePrimitive(item, item);
                mappedResults.add(mappedItem);
            }

            return mappedResults;
        }
		else if (source instanceof Coding) {
            return toCode((Coding)source);
        }
        else if (source instanceof CodeableConcept) {
            return toConcept((CodeableConcept)source);
        }
        else if (source instanceof Period) {
            return toInterval((Period)source);
        }
        else if (source instanceof Quantity) {
            return toQuantity((Quantity)source);
        }
        else if (source instanceof Enumeration) {
            return ((Enumeration<?>)source).getValueAsString();
        }
		else if (source instanceof IPrimitiveType) {
            return ((IPrimitiveType<?>) source).getValue();
        }
        else {
            return result;
        }
    }
    
	@SuppressWarnings("rawtypes")
	@Override
    protected BaseRuntimeChildDefinition resolveChoiceProperty(BaseRuntimeElementCompositeDefinition definition, String path) {
        for (Object child :  definition.getChildren()) {
            if (child instanceof RuntimeChildChoiceDefinition) {
                RuntimeChildChoiceDefinition choiceDefinition = (RuntimeChildChoiceDefinition) child;
            	if(choiceDefinition.getValidChildNames().contains(path)){
            		return choiceDefinition;
            	}
            }
        }

        throw new IllegalArgumentException(String.format("Unable to resolve path %s for %s", path, definition.getName()));
    }

    @Override
    protected Object resolveProperty(Object target, String path) {
        if (target == null) {
            return null;
        }

        IBase base = (IBase) target;
        BaseRuntimeElementCompositeDefinition<?> definition;
        if (base instanceof IPrimitiveType) {
            return toJavaPrimitive(path.equals("value") ? ((IPrimitiveType<?>) target).getValue() : target, base);
        }
        else {
            definition = resolveRuntimeDefinition(base);
        }
        
        //BaseRuntimeChildDefinition child = definition.getChildByName(path);
        BaseRuntimeChildDefinition child = getChildByName(definition, path);
        if (child == null) {
            child = resolveChoiceProperty(definition, path);
        }

        List<IBase> values = child.getAccessor().getValues(base);

        if (values == null || values.isEmpty()) {
            return null;
        }

        if (child instanceof RuntimeChildChoiceDefinition && !child.getElementName().equalsIgnoreCase(path)) {
            if (!values.get(0).getClass().getSimpleName().equalsIgnoreCase(child.getChildByName(path).getImplementingClass().getSimpleName()))
            {
                return null;
            }
        }

        Object result = child.getMax() < 1 ? values : values.get(0);
        return toJavaPrimitive(result, result);  
    }

	@Override
	public Object resolvePath(Object target, String path) {
		if (path == null || path.isEmpty()) {
			return null;
		}
		if (target == null) {
			return null;
		}

		try{
			String[] identifiers = path.split("\\.");
			for (String identifier : identifiers) {
				// handling indexes: item[0].code
				if (identifier.contains("[")) {
					int j = Character.getNumericValue(identifier.charAt(identifier.indexOf("[") + 1));
					target = resolveProperty(target, identifier.replaceAll("\\[\\d\\]", ""));
					target = ((ArrayList<?>) target).get(j);
				} else
					if(target instanceof Iterable){
						List<Object> results = new ArrayList<Object>();
						for(Object item : (Iterable<?>)target){
							Object itemResults = resolveProperty(item, identifier);
							if(itemResults instanceof Iterable){
								//need to flatten results so we don't end up with list of lists
								for(Object resultItem : (Iterable<?>)itemResults){
									results.add(resultItem);
								}
							}else{
								results.add(itemResults);
							}							
						}
						target = results;
					}else{
						target = resolveProperty(target, identifier);
					}
			}
			
			if(includesFhirHelpers == true){
				return target;
			}else{						
				return toRuntimePrimitive(target,target);
			}
		}catch(IllegalArgumentException e){
			throw e;
		}
	}
	
	@Override
	// added support for IBaseElement types
    protected BaseRuntimeElementCompositeDefinition<?> resolveRuntimeDefinition(IBase base) {
        if (base instanceof IAnyResource) {
            return getFhirContext().getResourceDefinition((IAnyResource) base);
        }
        else if (base instanceof IBaseBackboneElement || base instanceof IBaseDatatypeElement || base instanceof IBaseElement) {
            return (BaseRuntimeElementCompositeDefinition<?>) getFhirContext().getElementDefinition(base.getClass());
        }
        else if (base instanceof ICompositeType) {
            return (RuntimeCompositeDatatypeDefinition) getFhirContext().getElementDefinition(base.getClass());
        }

        throw new IllegalArgumentException(String.format("Unable to resolve the runtime definition for %s", base.getClass().getName()));
    }
	
	private BaseRuntimeChildDefinition getChildByName(BaseRuntimeElementCompositeDefinition<?> definition, String name){
		//BaseRuntimeElementCompositeDefinition.getChildByName() method does not work with FileBasedFhirDstu2Provider
		//fails with search fields that don't exactly match child name, for instance Observation.effective 
    	//getChildren() works
		for(BaseRuntimeChildDefinition child : definition.getChildren()){
            if(child.getElementName().equalsIgnoreCase(name)){
                return child;
            }
		}
		return null;
	}
}
