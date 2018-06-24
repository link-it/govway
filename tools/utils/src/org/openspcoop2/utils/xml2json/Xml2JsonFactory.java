/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.util.Map;

import org.codehaus.jettison.mapped.Configuration;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Xml2JsonFactory {
	
	public static IXml2Json getXml2JsonJsonML(){
		return new JsonMLXml2Json();
	}
	public static IXml2Json getXml2JsonBadgerFish() {
		return new BadgerFishXml2Json(); 
	}
	public static IXml2Json getXml2JsonMapped() {
		return new MappedXml2Json();
	}
	public static IXml2Json getXml2JsonMapped(Configuration configuration) {
		return new MappedXml2Json(configuration);
	}
	public static IXml2Json getXml2JsonMapped(Map<String, String> xmlToJsonNamespaces) {
		return new MappedXml2Json(xmlToJsonNamespaces);
	}

	public static IJson2Xml getJson2XmlJsonML(){
		return new JsonMLJson2Xml();
	}
	public static IJson2Xml getJson2XmlBadgerFish() {
		return new BadgerFishJson2Xml(); 
	}
	public static IJson2Xml getJson2XmlMapped(Configuration configuration) {
		return new MappedJson2Xml(configuration);
	}
	public static IJson2Xml getJson2XmlMapped(Map<String, String> xmlToJsonNamespaces) {
		return new MappedJson2Xml(xmlToJsonNamespaces);
	}
}
