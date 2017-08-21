package org.partners.usciitg_prep.odm;

import org.cdisc.ns.odm.v1.*;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nk940
 *
 */
/**
 * @author nk940
 * 
 */
public class OdmUtil {
	public static final String SCHEMA_LANGUAGE="http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3_XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	public static final String SCHEMA_SOURCE_NS="http://java.sun.com/xml/jaxp/properties/schemaSource";
	public static final String ODM_SCHEMA_LOCATION="resources/ODM1-3-2.xsd";

	/**
	 * @param xml
	 * @return ODM object
	 * @throws JAXBException
	 */
	public static ODM unmarshall(File xml) throws JAXBException {
		ODM odm = new ODM();
		try {
			JAXBContext context = JAXBContext.newInstance("org.cdisc.ns.odm.v1");

			Unmarshaller unmarshaller = context.createUnmarshaller();
			odm = (ODM) unmarshaller.unmarshal(xml);

		} catch (JAXBException jaxbEx) {
			jaxbEx.printStackTrace();
		}

		return odm;
	}

    /**
     * @param is
     * @return ODM object
     * @throws JAXBException
     */
    public static ODM unmarshall(InputStream is) throws JAXBException {
        ODM odm = new ODM();
        try {
            JAXBContext context = JAXBContext.newInstance("org.cdisc.ns.odm.v1");

            Unmarshaller unmarshaller = context.createUnmarshaller();
            odm = (ODM) unmarshaller.unmarshal(is);

        } catch (JAXBException jaxbEx) {
            jaxbEx.printStackTrace();
        }

        return odm;
    }
	/**
	 * @param odm
	 * @param writer
	 * @throws JAXBException
	 */
	public static void marshall(Object odm, Writer writer) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance("org.cdisc.ns.odm.v1");

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

			// marshaller.marshal(odm, System.out);
			marshaller.marshal(odm, writer);

		} catch (JAXBException jaxbEx) {
			jaxbEx.printStackTrace();
		}
	}

	/**
	 * @param odm
	 * @return
	 */
	public static String getODMXMLString(ODM odm) {
		StringWriter strWriter = null;

		try {
			strWriter = new StringWriter();
			marshall(odm, strWriter);

		} catch (JAXBException jaxbEx) {
			jaxbEx.printStackTrace();
		}

		return strWriter.toString();
	}

	public static Document validateOdmXml(InputStream OdmXml) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);

		factory.setAttribute(SCHEMA_LANGUAGE, W3_XML_SCHEMA_NS);
		factory.setAttribute(SCHEMA_SOURCE_NS, ODM_SCHEMA_LOCATION);

		try {
			DocumentBuilder doc = factory.newDocumentBuilder();
			Document parse = doc.parse(OdmXml);
			
			return parse;
		} catch (Exception e) {
			System.out.println("XML Doc not parsed: " + e);

		}
		return null;
	}

	public static List<ODMcomplexTypeDefinitionItemData> getOdmItems(ODM studyOdm) throws JAXBException {
		List<ODMcomplexTypeDefinitionItemData> result = new ArrayList<ODMcomplexTypeDefinitionItemData>();

		for(ODMcomplexTypeDefinitionClinicalData clinicalData : studyOdm.getClinicalData()){
			for(ODMcomplexTypeDefinitionSubjectData subjectData : clinicalData.getSubjectData()){
				for(ODMcomplexTypeDefinitionStudyEventData studyEventData : subjectData.getStudyEventData()){
					for(ODMcomplexTypeDefinitionFormData formData : studyEventData.getFormData()){
						for(ODMcomplexTypeDefinitionItemGroupData groupData : formData.getItemGroupData()){
							result.addAll(groupData.getItemDataGroup());
						}
					}
				}
			}
		}
		return result;
	}

    public static List<ODMcomplexTypeDefinitionItemData> getOdmGroupData(ODM studyOdm) throws JAXBException {
        return studyOdm.getClinicalData().get(0).getSubjectData().get(0).getStudyEventData().get(0).getFormData().get(0).getItemGroupData().get(0).getItemDataGroup();
    }
}
