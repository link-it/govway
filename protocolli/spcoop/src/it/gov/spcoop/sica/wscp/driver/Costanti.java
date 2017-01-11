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


package it.gov.spcoop.sica.wscp.driver;
/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public static final String VERSIONE_BUSTA = "e-govV1.1";
	
	public static final String SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV = "ProfiloDiCollaborazione.wscp";
	
	public static final String ROOT_LOCAL_NAME = "profiloCollaborazioneEGOV";
	
	public static final String PREFIX_CLIENT_SICA = "wscp";
	
	public static final String CHILD_ELEMENT_EGOV_VERSIONE_LOCAL_NAME = "versioneEGOV";
	public static final String CHILD_ELEMENT_DEFINIZIONE_INTERFACCIA_LOCAL_NAME = "riferimentoDefinizioneInterfaccia";
	public static final String CHILD_ELEMENT_OPERATION_LIST_LOCAL_NAME = "listaCollaborazioni";
	public static final String CHILD_ELEMENT_OPERATION_LOCAL_NAME = "collaborazione";
	
	public static final String CHILD_ELEMENT_OPERATION_ATTRIBUTE_PROFILO = "profiloDiCollaborazione";
	public static final String CHILD_ELEMENT_OPERATION_ATTRIBUTE_SERVIZIO = "servizio";
	public static final String CHILD_ELEMENT_OPERATION_ATTRIBUTE_SERVIZIO_CORRELATO = "servizioCorrelato";
	public static final String CHILD_ELEMENT_OPERATION_ATTRIBUTE_AZIONE = "operazione";
	public static final String CHILD_ELEMENT_OPERATION_ATTRIBUTE_AZIONE_CORRELATA = "operazioneCorrelata";
	
	public static final String TARGET_NAMESPACE = "http://spcoop.gov.it/sica/wscp";
}


