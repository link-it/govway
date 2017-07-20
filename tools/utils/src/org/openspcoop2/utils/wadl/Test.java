/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package org.openspcoop2.utils.wadl;

import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.jvnet.ws.wadl.Doc;
import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ast.ApplicationNode;
import org.jvnet.ws.wadl.ast.FaultNode;
import org.jvnet.ws.wadl.ast.MethodNode;
import org.jvnet.ws.wadl.ast.PathSegment;
import org.jvnet.ws.wadl.ast.RepresentationNode;
import org.jvnet.ws.wadl.ast.ResourceNode;
import org.jvnet.ws.wadl.ast.WadlAstBuilder;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;


/**
 * Test
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Test {

	public static void main(String[] args) throws Exception {
		
		AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
		
		SchemaCallback callback = new SchemaCallback(null, xmlUtils, true, true);
		callback.addResource("test2.xsd", Utilities.getAsByteArray(Test.class.getResourceAsStream("/org/openspcoop2/utils/wadl/test2.xsd")));
		//callback.addResource("test.xsd", Utilities.getAsByteArray(Test.class.getResourceAsStream("/org/openspcoop2/utils/wadl/test.xsd")));
		MessageListener mListener = new MessageListener(null);
		
		WadlAstBuilder astBuilder = new WadlAstBuilder(callback,mListener);

		URI uri = Test.class.getResource("/org/openspcoop2/utils/wadl/test.wadl").toURI();
		
		ApplicationNode an = astBuilder.buildAst(uri);
        List<ResourceNode> rs = an.getResources();
        System.out.println("RESOURCE NODE: "+rs.size());
        for (ResourceNode resourceNode : rs) {
        	print(resourceNode,"");        	
		}
        
        System.out.println("Schemi Letti: "+callback.getResources().size());
        Enumeration<String> keys = callback.getResources().keys();
        while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			System.out.println("Schema ["+key+"]");
		}
        System.out.println("Namespaces:");
        keys = callback.getMappingNamespaceLocations().keys();
        while (keys.hasMoreElements()) {
			String namespace = (String) keys.nextElement();
			String key = callback.getMappingNamespaceLocations().get(namespace);
			System.out.println("Schema ["+key+"]: "+namespace);
		}  
        
        IApiReader apiReader = ApiFactory.newApiReader(ApiFormats.WADL);
        apiReader.init(LoggerWrapperFactory.getLogger(Test.class), uri, new ApiReaderConfig());
        Api api = apiReader.read();
        System.out.println("API: "+api);
        
        // http://localhost:8080/smartproxy/ws/rest/allineamentopendenze
        String test = "http://localhost:8080/smartproxy/ws/rest/allineamentopendenze/id23/ulterioreParametro/id45";
        System.out.println("API-Op ["+test+"]: "+api.findOperation(HttpRequestMethod.GET, test));
        
        String testSenzaBaseUri = "/allineamentopendenze/id23/ulterioreParametro/id45";
        System.out.println("API-Op ["+testSenzaBaseUri+"]: "+api.findOperation(HttpRequestMethod.GET, testSenzaBaseUri));
        
        String testParametriRisposta = "/prova2";
        System.out.println("API-Op ["+testParametriRisposta+"]: "+api.findOperation(HttpRequestMethod.GET, testParametriRisposta));
	}

	private static void print(ResourceNode resourceNode, String indent){
		System.out.println(indent+"========================== INIZIO ["+resourceNode.getUriTemplate()+"]============================");
		System.out.println(indent+"RESOURCE NODE getAllResourceUriTemplate ["+resourceNode.getAllResourceUriTemplate()+"]");
		System.out.println(indent+"RESOURCE NODE getUriTemplate ["+resourceNode.getUriTemplate()+"]");
		System.out.println(indent+"RESOURCE NODE DOC size ["+resourceNode.getDoc().size()+"]");
		System.out.println(indent+"RESOURCE NODE Header Params size ["+resourceNode.getHeaderParams().size()+"]");
		System.out.println(indent+"RESOURCE NODE Matrix Params size ["+resourceNode.getMatrixParams().size()+"]");
		
		System.out.println(indent+"RESOURCE NODE Methods size ["+resourceNode.getMethods().size()+"]");
		printMethod(resourceNode.getMethods(), indent);
		
		System.out.println(indent+"RESOURCE Parent Resource ["+resourceNode.getParentResource()+"]");
		
		try{
			System.out.println(indent+"RESOURCE Location ["+resourceNode.getLocation()+"]");
		}catch(Exception e){
			System.out.println(indent+"RESOURCE Location ["+e.getMessage()+"]");
		}
		System.out.println(indent+"RESOURCE PathSegment ["+resourceNode.getPathSegment()+"]");
		
		System.out.println(indent+"RESOURCE PathSegment size ["+resourceNode.getPathSegments().size()+"]");
		printPathSegment(resourceNode.getPathSegments(),indent);
		
		System.out.println(indent+"RESOURCE QueryParams size ["+resourceNode.getQueryParams().size()+"]");
		printParams(resourceNode.getQueryParams(),indent);
		
		System.out.println(indent+"RESOURCE ResourceTypes size ["+resourceNode.getResourceTypes().size()+"]");
		
		System.out.println(indent+"RESOURCE NODE getChildResources.size ["+resourceNode.getChildResources().size()+"]");
		for (ResourceNode child : resourceNode.getChildResources()) {
			print(child, indent+"\t");
		}
		System.out.println(indent+"========================== FINE ["+resourceNode.getUriTemplate()+"]============================");
	}
	
	private static void printParams(List<Param> list,String indent){
    	for (Param param : list) {
    		System.out.println(indent+"  PARAM NAME["+param.getName()+"]");
    		System.out.println(indent+"    default ["+param.getDefault()+"]");
    		System.out.println(indent+"    fixed ["+param.getFixed()+"]");
    		System.out.println(indent+"    href ["+param.getHref()+"]");
    		System.out.println(indent+"    id ["+param.getId()+"]");
    		System.out.println(indent+"    path ["+param.getPath()+"]");
    		System.out.println(indent+"    any.size ["+param.getAny().size()+"]");
    		System.out.println(indent+"    doc.size ["+param.getDoc().size()+"]");
    		printDoc(param.getDoc(),indent+"    ");
    		System.out.println(indent+"    link ["+param.getLink()+"]");
    		System.out.println(indent+"    option.size ["+param.getOption().size()+"]");
    		System.out.println(indent+"    otherAttributes.size ["+param.getOtherAttributes().size()+"]");
    		System.out.println(indent+"    style ["+param.getStyle().name()+"]");
    		System.out.println(indent+"    type ["+param.getType()+"]");
		}
	}
	
	private static void printMethod(List<MethodNode> list,String indent){
		for (MethodNode param : list) {
    		System.out.println(indent+"  METHOD NAME["+param.getName()+"]");
    		System.out.println(indent+"    id ["+param.getId()+"]");
    		System.out.println(indent+"    getQueryParameters.size ["+param.getQueryParameters().size()+"]");
    		printParams(param.getQueryParameters(), indent+"    ");
    		System.out.println(indent+"    getOptionalParameters.size ["+param.getOptionalParameters().size()+"]");
    		printParams(param.getOptionalParameters(), indent+"    ");
    		System.out.println(indent+"    getMatrixParameters.size ["+param.getMatrixParameters().size()+"]");
    		printParams(param.getMatrixParameters(), indent+"    ");
    		System.out.println(indent+"    getHeaderParameters.size ["+param.getHeaderParameters().size()+"]");
    		printParams(param.getHeaderParameters(), indent+"    ");
    		
    		System.out.println(indent+"    getSupportedInputs.size ["+param.getSupportedInputs().size()+"]");
    		for (RepresentationNode representationNode : param.getSupportedInputs()) {
    			System.out.println(indent+"       mediaType ["+representationNode.getMediaType()+"]");
    			System.out.println(indent+"       element ["+representationNode.getElement()+"]");
    			if(representationNode.getParam()!=null){
    				System.out.println(indent+"    inputParams.size ["+representationNode.getParam().size()+"]");
    	    		printParams(representationNode.getParam(), indent+"    ");
    			}
			}
    		
    		System.out.println(indent+"    getSupportedOutputs.size ["+param.getSupportedOutputs().size()+"]");
    		MultivaluedMap<List<Long>, RepresentationNode> mapOutput = param.getSupportedOutputs();
    		Iterator<List<Long>> itOutput = mapOutput.keySet().iterator();
    		while (itOutput.hasNext()) {
				List<java.lang.Long> listLong = (List<java.lang.Long>) itOutput.next();
				List<RepresentationNode> representationNode = mapOutput.get(listLong);
				for (int i = 0; i < listLong.size(); i++) {
					System.out.println(indent+"       status ["+listLong.get(i)+"]");
					System.out.println(indent+"         mediaType ["+representationNode.get(i).getMediaType()+"]");
	    			System.out.println(indent+"         element ["+representationNode.get(i).getElement()+"]");
	    			if(representationNode.get(i).getParam()!=null){
	    				System.out.println(indent+"    outputParams.size ["+representationNode.get(i).getParam().size()+"]");
	    	    		printParams(representationNode.get(i).getParam(), indent+"    ");
	    			}
				}
			}
    		
    		System.out.println(indent+"    getFaults.size ["+param.getFaults().size()+"]");
    		MultivaluedMap<List<Long>, FaultNode> mapFault = param.getFaults();
    		Iterator<List<Long>> itFault = mapFault.keySet().iterator();
    		while (itFault.hasNext()) {
				List<java.lang.Long> listLong = (List<java.lang.Long>) itFault.next();
				List<FaultNode> faultNode = mapFault.get(listLong);
				for (int i = 0; i < listLong.size(); i++) {
					System.out.println(indent+"       status ["+listLong.get(i)+"]");
					System.out.println(indent+"         mediaType ["+faultNode.get(i).getMediaType()+"]");
	    			System.out.println(indent+"         element ["+faultNode.get(i).getElement()+"]");
	    			if(faultNode.get(i).getParam()!=null){
	    				System.out.println(indent+"    faultParams.size ["+faultNode.get(i).getParam().size()+"]");
	    	    		printParams(faultNode.get(i).getParam(), indent+"    ");
	    			}
				}
			}
		}
	}
	
	private static void printDoc(List<Doc> list,String indent){
    	for (Doc doc : list) {
    		System.out.println(indent+"  DOC TITLE["+doc.getTitle()+"]");
    		System.out.println(indent+"    lang ["+doc.getLang()+"]");
    		System.out.println(indent+"    content.size ["+doc.getContent().size()+"]");
    		System.out.println(indent+"    otherAttributes.size ["+doc.getOtherAttributes().size()+"]");
		}
	}
	
	private static void printPathSegment(List<PathSegment> list,String indent){
    	for (PathSegment pathSegment : list) {
    		System.out.println(indent+"  PATH SEGMENT ["+pathSegment.getTemplate()+"]");
   		 	System.out.println(indent+"    headerParameters.size() ["+pathSegment.getHeaderParameters().size()+"]");
   		 	System.out.println(indent+"    matrixParameters.size() ["+pathSegment.getMatrixParameters().size()+"]");
   		 	System.out.println(indent+"    queryParameters.size() ["+pathSegment.getQueryParameters().size()+"]");
   		 	System.out.println(indent+"    templateParameters.size() ["+pathSegment.getTemplateParameters().size()+"]");
		}
	}
}
