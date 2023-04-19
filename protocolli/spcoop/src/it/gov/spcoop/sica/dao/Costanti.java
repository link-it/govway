/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

	public static final String FIRMA_XML = "firma.xml";

	public static final String MANIFESTO_XML = "manifesto.xml";

	public static final String SPECIFICA_CONVERSAZIONE_DIR = "specificaConversazione";
	public static final String SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL = 
		"ConversazioneConcettuale.wsbl";
	public static final String SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL = 
		"ConversazioneLogicaLatoErogatore.wsbl";
	public static final String SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL = 
		"ConversazioneLogicaLatoFruitore.wsbl";

	public static final String SPECIFICA_INTERFACCIA_DIR = "specificaInterfaccia";
	public static final String SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL = 
		"InterfacciaConcettuale.wsdl";
	public static final String SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL = 
		"InterfacciaLogicaErogatore.wsdl";
	public static final String SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL = 
		"InterfacciaLogicaFruitore.wsdl";
	
	public static final String ALLEGATI_DIR = "allegati";
	public static final String ALLEGATO_DEFINITORIO_XSD = "InterfacciaDefinitoria.xsd";
	
	public static final String SPECIFICA_SEMIFORMALE_DIR = "specificaSemiformale";		
	
	public static final String SPECIFICA_LIVELLI_SERVIZIO_DIR = "specificaLivelliServizio";	
	
	public static final String SPECIFICA_PORTI_ACCESSO_DIR = "specificaPortiAccesso";	
	public static final String SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL = "PortiAccessoErogatore.wsdl";
	public static final String SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL = "PortiAccessoFruitore.wsdl";
	
	public static final String SPECIFICA_SICUREZZA_DIR = "specificaSicurezza";	
	
	public static final String SPECIFICA_COORDINAMENTO_DIR = "specificaCoordinamento";	
	
	
	public static final String ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE = "apc";
	public static final String ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "aps";
	public static final String ESTENSIONE_ACCORDO_COOPERAZIONE = "adc";
	public static final String ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO = "asc";
	
	
	public static final String TIPO_ACCORDO_SERVIZIO_PARTE_COMUNE = "adsc";
	public static final String TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "ads";
	public static final String TIPO_ACCORDO_COOPERAZIONE = "adc";
	public static final String TIPO_ACCORDO_SERVIZIO_COMPOSTO = "sc";
	
	public static final String PROJECT_CLIENT_SICA = ".project";
	public static final String PROJECT_CLIENT_SICA_KEY_NOME = "@NOME@";
	public static final String PROJECT_CLIENT_SICA_CONTENUTO = 
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
	
	
	public static final String WSDL_EMPTY = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"/>";
}
