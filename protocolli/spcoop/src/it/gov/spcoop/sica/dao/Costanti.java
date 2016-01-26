/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package it.gov.spcoop.sica.dao;

/**
 * Costanti utilizzate in questo package
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public final static String FIRMA_XML = "firma.xml";

	public final static String MANIFESTO_XML = "manifesto.xml";

	public final static String SPECIFICA_CONVERSAZIONE_DIR = "specificaConversazione";
	public final static String SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL = 
		"ConversazioneConcettuale.wsbl";
	public final static String SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL = 
		"ConversazioneLogicaLatoErogatore.wsbl";
	public final static String SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL = 
		"ConversazioneLogicaLatoFruitore.wsbl";

	public final static String SPECIFICA_INTERFACCIA_DIR = "specificaInterfaccia";
	public final static String SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL = 
		"InterfacciaConcettuale.wsdl";
	public final static String SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL = 
		"InterfacciaLogicaErogatore.wsdl";
	public final static String SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL = 
		"InterfacciaLogicaFruitore.wsdl";
	
	public final static String ALLEGATI_DIR = "allegati";
	public final static String ALLEGATO_DEFINITORIO_XSD = "InterfacciaDefinitoria.xsd";
	
	public final static String SPECIFICA_SEMIFORMALE_DIR = "specificaSemiformale";		
	
	public final static String SPECIFICA_LIVELLI_SERVIZIO_DIR = "specificaLivelliServizio";	
	
	public final static String SPECIFICA_PORTI_ACCESSO_DIR = "specificaPortiAccesso";	
	public final static String SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL = "PortiAccessoErogatore.wsdl";
	public final static String SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL = "PortiAccessoFruitore.wsdl";
	
	public final static String SPECIFICA_SICUREZZA_DIR = "specificaSicurezza";	
	
	public final static String SPECIFICA_COORDINAMENTO_DIR = "specificaCoordinamento";	
	
	
	public final static String ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE = "apc";
	public final static String ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "aps";
	public final static String ESTENSIONE_ACCORDO_COOPERAZIONE = "adc";
	public final static String ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO = "asc";
	
	
	public final static String TIPO_ACCORDO_SERVIZIO_PARTE_COMUNE = "adsc";
	public final static String TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "ads";
	public final static String TIPO_ACCORDO_COOPERAZIONE = "adc";
	public final static String TIPO_ACCORDO_SERVIZIO_COMPOSTO = "sc";
	
	public final static String PROJECT_CLIENT_SICA = ".project";
	public final static String PROJECT_CLIENT_SICA_KEY_NOME = "@NOME@";
	public final static String PROJECT_CLIENT_SICA_CONTENUTO = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<projectDescription>\n"+
		"        <name>"+Costanti.PROJECT_CLIENT_SICA_KEY_NOME+"</name>\n"+
		"        <comment>Package generato tramite OpenSPCoop v2 (www.openspcoop2.org)</comment>\n"+
		"        <projects>\n"+
		"        </projects>\n"+
		"        <buildSpec>\n"+
		"                <buildCommand>\n"+
		"                        <name>com.ibm.rst.sica.workbench.agreementValidator</name>\n"+
		"                        <arguments>\n"+
		"                        </arguments>\n"+
		"                </buildCommand>\n"+
		"        </buildSpec>\n"+
		"        <natures>\n"+
		"                <nature>com.ibm.rst.sica.workbench.natures.commonPart</nature>\n"+
		"                <nature>com.ibm.rst.sica.workbench.natures.agreementProject</nature>\n"+
		"        </natures>\n"+
		"</projectDescription>";
	
	
	public final static String WSDL_EMPTY = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"/>";
}
