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


package org.openspcoop2.protocol.spcoop.sica;

import it.gov.spcoop.sica.dao.Costanti;
import it.gov.spcoop.sica.manifest.DocumentoCoordinamento;
import it.gov.spcoop.sica.manifest.DocumentoLivelloServizio;
import it.gov.spcoop.sica.manifest.DocumentoSemiformale;
import it.gov.spcoop.sica.manifest.DocumentoSicurezza;
import it.gov.spcoop.sica.manifest.ElencoPartecipanti;
import it.gov.spcoop.sica.manifest.ElencoServiziComponenti;
import it.gov.spcoop.sica.manifest.SpecificaCoordinamento;
import it.gov.spcoop.sica.manifest.SpecificaLivelliServizio;
import it.gov.spcoop.sica.manifest.SpecificaSicurezza;
import it.gov.spcoop.sica.manifest.driver.TipiAdesione;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoConversazione;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoInterfaccia;

import java.io.File;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Definition;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.wsdl.RegistroOpenSPCoopUtilities;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
* Utilities di conversione da oggetto OpenSPCoop a oggetti SICA  e viceversa
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/
public class SICAtoOpenSPCoopUtilities {

	
	/*
	 * uri:dn:<identificativo soggetto>
	 * Potrebbe essere dn=
	 * uri:dn:o=m_sa,c=it
	 * */
	
	/* IDSoggetti */
	private static String [] getValoriDN(String dn) throws SICAToOpenSPCoopUtilitiesException{
		String [] valori;
		int indexOf = dn.indexOf("=");
		if(indexOf<=0){
			throw new SICAToOpenSPCoopUtilitiesException("Separatore validi per il dn non trovati");
		}
		valori =  dn.split(",");
		if(valori==null || valori.length<1){
			throw new SICAToOpenSPCoopUtilitiesException("Comprensione dn non riuscita: null??");
		}
		return valori;
	}
	public static void validateIDSoggettoSICA(String dn) throws SICAToOpenSPCoopUtilitiesException{
		String tmp = new String(dn);
		if(tmp.startsWith("uri:dn:")){
			tmp = tmp.substring("uri:dn:".length());
		}
		if(tmp==null || "".equals(tmp))
			throw new SICAToOpenSPCoopUtilitiesException("DN non fornita");
		if(tmp.contains("c=it")==false){
			throw new SICAToOpenSPCoopUtilitiesException("c=it non presente");
		}
		if(tmp.contains(",")==false){
			throw new SICAToOpenSPCoopUtilitiesException("Nessuna amministrazione definita nella radice c=it");
		}
		if(tmp.contains("=")==false){
			throw new SICAToOpenSPCoopUtilitiesException("Nessuna amministrazione definita nella radice c=it");
		}
		String [] valoriDN = SICAtoOpenSPCoopUtilities.getValoriDN(tmp);
		boolean campoObbligatorioO = false;
		for(int i=0; i<valoriDN.length; i++){
			if(valoriDN[i].contains("=")==false){
				throw new SICAToOpenSPCoopUtilitiesException("Comprensione dn non riuscita: ["+valoriDN[i]+"] non separata dal carattere \"=\"");
			}
			String [] keyValue = valoriDN[i].trim().split("=");
			if(keyValue.length!=2){
				throw new SICAToOpenSPCoopUtilitiesException("Comprensione dn non riuscita: ["+valoriDN[i]+"] contiene piu' di un carattere \"=\"");
			}
			if(keyValue[0].trim().contains(" ")){
				throw new SICAToOpenSPCoopUtilitiesException("Comprensione dn non riuscita: il campo ["+valoriDN[i]+"] contiene spazi nella chiave identificativa ["+keyValue[0].trim()+"]");
			}
			if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("O")){
				campoObbligatorioO = true;
			}
		}
		if(!campoObbligatorioO){
			throw new SICAToOpenSPCoopUtilitiesException("Nessuna amministrazione definita nella radice c=it (o=XX non trovato)");
		}
		
	}
	public static String appendURI_IDSoggettoSica(String dn) throws SICAToOpenSPCoopUtilitiesException{
		SICAtoOpenSPCoopUtilities.validateIDSoggettoSICA(dn);
		return "uri:dn:" + dn;	
	}
	public static String removeURI_IDSoggettoSica(String uri_dn) throws SICAToOpenSPCoopUtilitiesException{
		SICAtoOpenSPCoopUtilities.validateIDSoggettoSICA(uri_dn);
		if(uri_dn.startsWith("uri:dn:")){
			uri_dn = uri_dn.substring("uri:dn:".length());
		}
		return uri_dn;
	}
	// NOTA: Metodo che permette di costruire DistinguishedName non standard: gestisce i tipi diversi da SPC.
	// Da utilizzare con cautela
	public static String buildIDSoggettoSica(IDSoggetto idSoggetto,boolean createURI) throws SICAToOpenSPCoopUtilitiesException{
		String soggetto = null;
		if("SPC".equals(idSoggetto.getTipo())){
			// standard, utilizzo solo il nome
			soggetto = idSoggetto.getNome();
		}else{
			// non standard, utilizzo tipo/nome per avere l'univocita'
			soggetto = idSoggetto.toString();
		}
		return SICAtoOpenSPCoopUtilities.buildIDSoggettoSica(soggetto, createURI);
	}
	public static String buildIDSoggettoSica(String soggetto,boolean createURI) throws SICAToOpenSPCoopUtilitiesException{
		if(soggetto==null || "".equals(soggetto))
			throw new SICAToOpenSPCoopUtilitiesException("Identificativo soggetto non fornito");
		StringBuffer bf = new StringBuffer();
		if(createURI){
			bf.append("uri:dn:");
		}
		bf.append("o=");
		bf.append(soggetto);
		bf.append(",c=it");
		return bf.toString();
	}
	public static String idSoggetto_openspcoopToSica(org.openspcoop2.core.id.IDSoggetto idSoggetto) throws SICAToOpenSPCoopUtilitiesException{

		if(idSoggetto==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDSoggetto non definito");
		if(idSoggetto.getTipo()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDSoggetto.tipo non definito");
		if(idSoggetto.getNome()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDSoggetto.nome non definito");
		if("SPC".equals(idSoggetto.getTipo())==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo soggetto diverso da SPC non utilizzabile");
		
		return SICAtoOpenSPCoopUtilities.buildIDSoggettoSica(idSoggetto.getNome(),true);
	}
	public static org.openspcoop2.core.id.IDSoggetto idSoggetto_sicaToOpenspcoop(String uriSoggetto) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(uriSoggetto,true);
	}
	public static org.openspcoop2.core.id.IDSoggetto idSoggetto_sicaToOpenspcoop(String uriSoggetto,boolean uri) throws SICAToOpenSPCoopUtilitiesException{
		
		if(uriSoggetto==null)
			throw new SICAToOpenSPCoopUtilitiesException("uriSoggetto non definito");
		
		String senzaSpazi = uriSoggetto.replaceAll("" , "");
		
		if(senzaSpazi.startsWith("uri:dn:")==false && uri)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriSoggetto ("+uriSoggetto+") non corretto (uri:dn:o=<identificativo soggetto>,c=it)");
		if( (senzaSpazi.indexOf(",c=it")<=0) && senzaSpazi.indexOf(" c=it")<=0)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriSoggetto ("+uriSoggetto+") non corretto (uri:dn:o=<identificativo soggetto>,c=it)");
		if(uri){
			if(senzaSpazi.indexOf(",o=")<=0 && senzaSpazi.indexOf(":o=")<=0 && senzaSpazi.indexOf(" o=")<=0){
				throw new SICAToOpenSPCoopUtilitiesException("Formato uriSoggetto ("+uriSoggetto+") non corretto (uri:dn:o=<identificativo soggetto>,c=it)");
			}
		}else{
			if(senzaSpazi.indexOf(",o=")<=0 && senzaSpazi.indexOf("o=")<0){
				throw new SICAToOpenSPCoopUtilitiesException("Formato uriSoggetto ("+uriSoggetto+") non corretto (o=<identificativo soggetto>,c=it)");
			}
		}
		
		int index = -1;
		int lenghtPrefixO = 3;
		if(uri){
			index = senzaSpazi.indexOf(",o=");
			if(index<=0){
				index = senzaSpazi.indexOf(":o=");
			}
			if(index<=0){
				index = senzaSpazi.indexOf(" o=");
			}
		}else{
			index = senzaSpazi.indexOf(",o=");
			if(index<=0){
				index = senzaSpazi.indexOf("o=");
				lenghtPrefixO = 2;
 			}
		}
		
		
		int endSoggetto = senzaSpazi.length();
		if(senzaSpazi.indexOf(",",index+1)>0){
			endSoggetto = senzaSpazi.indexOf(",",index+1);
		}
		
		org.openspcoop2.core.id.IDSoggetto idSoggetto = 
			 new org.openspcoop2.core.id.IDSoggetto("SPC",senzaSpazi.substring(index+lenghtPrefixO,endSoggetto));
		return idSoggetto;
	}
	public static String readDNSoggettoFromUriAccordo(String uriAccordo) throws SICAToOpenSPCoopUtilitiesException{
		if(uriAccordo==null)
			throw new SICAToOpenSPCoopUtilitiesException("uriAccordo non definito");
		String [] values = uriAccordo.split(":");
		if(values.length != 5)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordo+") non valido (urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>)");
		String urn = values[0];
		if("urn".equals(urn)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordo+") non valido (urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>)");
		String soggetto = values[2];
		if(soggetto==null)
			throw new SICAToOpenSPCoopUtilitiesException("Soggetto non presente nell'uriAccordo ("+uriAccordo+")");
		return soggetto;
	}
	
	
	
	
	
	
	
	
	
	/*
	 * urn:adc:<nome_accordo>:<versione>
	 * */
	public static String buildIDAccordoCooperazioneSica(String nome_accordo,String versione)throws SICAToOpenSPCoopUtilitiesException{
		
		if(nome_accordo==null || "".equals(nome_accordo))
			throw new SICAToOpenSPCoopUtilitiesException("Nome accordo non fornito");
		if(versione==null || "".equals(versione))
			throw new SICAToOpenSPCoopUtilitiesException("Versione non fornita");
		
		StringBuffer bf = new StringBuffer();
		bf.append("urn:");
		bf.append(Costanti.TIPO_ACCORDO_COOPERAZIONE);
		bf.append(":");
		bf.append(nome_accordo);
		bf.append(":");
		bf.append(versione);
		return bf.toString();
	}
	
	public static String idAccordoCooperazione_openspcoopToSica(IDAccordoCooperazione idAccordo) throws SICAToOpenSPCoopUtilitiesException{
		if(idAccordo==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo non definito");
		if(idAccordo.getNome()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.nome non definito");
		if(idAccordo.getVersione()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.versione non definito");
		
		return SICAtoOpenSPCoopUtilities.buildIDAccordoCooperazioneSica(idAccordo.getNome(),idAccordo.getVersione());
	}
	public static IDAccordoCooperazione idAccordoCooperazione_sicaToOpenspcoop(String uriAccordo) throws SICAToOpenSPCoopUtilitiesException{
		if(uriAccordo==null)
			throw new SICAToOpenSPCoopUtilitiesException("uriAccordo non definito");
		String [] values = uriAccordo.split(":");
		if(values.length != 4)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordo+") non valido (urn:adc:<nome_accordo>:<versione>)");
		String urn = values[0];
		if("urn".equals(urn)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordo+") non valido (urn:adc:<nome_accordo>:<versione>)");
		String tipoAccordo = values[1];
		if("adc".equals(tipoAccordo)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo presente nell'uriAccordo ("+tipoAccordo+") non corrisponde quello atteso (adc)");
		String nome = values[2];
		if(nome==null)
			throw new SICAToOpenSPCoopUtilitiesException("Nome non presente nell'uriAccordo ("+uriAccordo+")");
		String versione = values[3];
		if(versione==null)
			throw new SICAToOpenSPCoopUtilitiesException("Versione non presente nell'uriAccordo ("+uriAccordo+")");
		IDAccordoCooperazione idAccordo = null;
		try{
			idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nome, versione);
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("Costruzione IDAccordo ("+uriAccordo+") non riuscita: "+e.getMessage(),e);
		}
		return idAccordo;
	}
	
	
	
	
	
	
	
	
	
	/*
	 * urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>
	 * */
	public static String buildIDAccordoSica(String tipo_accordo,String soggetto,String nome_accordo,String versione)throws SICAToOpenSPCoopUtilitiesException{
		
		if(tipo_accordo==null || "".equals(tipo_accordo))
			throw new SICAToOpenSPCoopUtilitiesException("Tipo accordo non fornito");
		if(soggetto==null || "".equals(soggetto))
			throw new SICAToOpenSPCoopUtilitiesException("Soggetto organizzativo non fornito");
		if(nome_accordo==null || "".equals(nome_accordo))
			throw new SICAToOpenSPCoopUtilitiesException("Nome accordo non fornito");
		if(versione==null || "".equals(versione))
			throw new SICAToOpenSPCoopUtilitiesException("Versione non fornita");
		
		StringBuffer bf = new StringBuffer();
		bf.append("urn:");
		bf.append(tipo_accordo);
		bf.append(":");
		bf.append(soggetto);
		bf.append(":");
		bf.append(nome_accordo);
		bf.append(":");
		bf.append(versione);
		return bf.toString();
	}
	
	public static String idAccordoServizioParteComune_openspcoopToSica(IDAccordo idAccordo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idAccordo_openspcoopToSica(idAccordo,Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_COMUNE,sicaToOpenSPCoopContext);
	}
	public static IDAccordo idAccordoServizioParteComune_sicaToOpenspcoop(String uriAccordo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idAccordo_sicaToOpenspcoop(uriAccordo,Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_COMUNE,sicaToOpenSPCoopContext);
	}
		
	public static String idAccordoServizioComposto_openspcoopToSica(IDAccordo idAccordo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idAccordo_openspcoopToSica(idAccordo,Costanti.TIPO_ACCORDO_SERVIZIO_COMPOSTO,sicaToOpenSPCoopContext);
	}
	public static IDAccordo idAccordoServizioComposto_sicaToOpenspcoop(String uriAccordo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idAccordo_sicaToOpenspcoop(uriAccordo,Costanti.TIPO_ACCORDO_SERVIZIO_COMPOSTO,sicaToOpenSPCoopContext);
	}
		
	/* IDServizio */
	/*
	@Deprecated
	public static String idAccordoServizioParteSpecifica_openspcoopToSica(IDServizio idAccordoParteSpecifica,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		if(idAccordoParteSpecifica==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo non definito");
		if(idAccordoParteSpecifica.getTipoServizio()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.servizio.tipo non definito");
		if("SPC".equals(idAccordoParteSpecifica.getTipoServizio())==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo servizio diverso da SPC non utilizzabile");
		if(idAccordoParteSpecifica.getServizio()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.servizio.nome non definito");
		if(idAccordoParteSpecifica.getSoggettoErogatore()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.soggettoErogatore non definito");
		if(idAccordoParteSpecifica.getSoggettoErogatore().getTipo()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.soggettoErogatore.tipo non definito");
		if("SPC".equals(idAccordoParteSpecifica.getSoggettoErogatore().getTipo())==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo soggetto erogatore diverso da SPC non utilizzabile");
		if(idAccordoParteSpecifica.getSoggettoErogatore().getNome()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.soggettoErogatore.nome non definito");
		

		String codiceIPASoggettoErogatore = 
			sicaToOpenSPCoopContext.getCodiceIPA(new IDSoggetto(idAccordoParteSpecifica.getSoggettoErogatore().getTipo(), idAccordoParteSpecifica.getSoggettoErogatore().getNome()));
		if(codiceIPASoggettoErogatore==null){
			codiceIPASoggettoErogatore = buildIDSoggettoSica(idAccordoParteSpecifica.getSoggettoErogatore().getNome(),false);	
		}
		
		return buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA,
				codiceIPASoggettoErogatore,
					idAccordoParteSpecifica.getServizio(),
					"1");
	}
	@Deprecated
	public static IDServizio idAccordoServizioParteSpecifica_sicaToOpenspcoop(String uriAccordoParteSpecifica,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		if(uriAccordoParteSpecifica==null)
			throw new SICAToOpenSPCoopUtilitiesException("uriAccordo non definito");
		String [] values = uriAccordoParteSpecifica.split(":");
		if(values.length != 5)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordoParteSpecifica+") non valido (urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>)");
		String urn = values[0];
		if("urn".equals(urn)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordoParteSpecifica+") non valido (urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>)");
		String tipoAccordo = values[1];
		if(Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA.equals(tipoAccordo)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo presente nell'uriAccordo ("+tipoAccordo+") non corrisponde quello atteso ("+Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA+")");
		String soggetto = values[2];
		if(soggetto==null)
			throw new SICAToOpenSPCoopUtilitiesException("Soggetto non presente nell'uriAccordo ("+uriAccordoParteSpecifica+")");
		IDSoggetto idSoggetto = sicaToOpenSPCoopContext.getIDSoggetto(soggetto);
		if(idSoggetto==null)
			idSoggetto = idSoggetto_sicaToOpenspcoop(soggetto,false);
		String nome = values[3];
		if(nome==null)
			throw new SICAToOpenSPCoopUtilitiesException("Nome non presente nell'uriAccordo ("+uriAccordoParteSpecifica+")");
		String versione = values[4];
		if(versione==null)
			throw new SICAToOpenSPCoopUtilitiesException("Versione non presente nell'uriAccordo ("+uriAccordoParteSpecifica+")");
		IDServizio idServizio = new IDServizio(idSoggetto,"SPC",nome);
		return idServizio;
	}
	*/
	public static String idAccordoServizioParteSpecifica_openspcoopToSica(IDAccordo idAccordo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idAccordo_openspcoopToSica(idAccordo,Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA,sicaToOpenSPCoopContext);
	}
	public static IDAccordo idAccordoServizioParteSpecifica_sicaToOpenspcoop(String uriAccordo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.idAccordo_sicaToOpenspcoop(uriAccordo,Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA,sicaToOpenSPCoopContext);
	}
	
	
	
	private static String idAccordo_openspcoopToSica(IDAccordo idAccordo,String tipo,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		if(idAccordo==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo non definito");
		if(idAccordo.getNome()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.nome non definito");
		if(idAccordo.getVersione()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.versione non definito");
		if(idAccordo.getSoggettoReferente()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.soggettoReferente non definito");
		if(idAccordo.getSoggettoReferente().getTipo()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.soggettoReferente.tipo non definito");
		if(idAccordo.getSoggettoReferente().getNome()==null)
			throw new SICAToOpenSPCoopUtilitiesException("IDAccordo.soggettoReferente.nome non definito");
		if("SPC".equals(idAccordo.getSoggettoReferente().getTipo())==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo soggetto referente diverso da SPC non utilizzabile");
		
		String codiceIPASoggettoReferente = 
			sicaToOpenSPCoopContext.getCodiceIPA(new IDSoggetto(idAccordo.getSoggettoReferente().getTipo(), idAccordo.getSoggettoReferente().getNome()));
		if(codiceIPASoggettoReferente==null){
			codiceIPASoggettoReferente = SICAtoOpenSPCoopUtilities.buildIDSoggettoSica(idAccordo.getSoggettoReferente().getNome(),false);	
		}
		
		return SICAtoOpenSPCoopUtilities.buildIDAccordoSica(tipo,
				codiceIPASoggettoReferente,
				idAccordo.getNome(),
				idAccordo.getVersione());
	}
	public static IDAccordo idAccordo_sicaToOpenspcoop(String uriAccordo,String tipoAtteso,SICAtoOpenSPCoopContext sicaToOpenSPCoopContext) throws SICAToOpenSPCoopUtilitiesException{
		if(uriAccordo==null)
			throw new SICAToOpenSPCoopUtilitiesException("uriAccordo non definito");
		String [] values = uriAccordo.split(":");
		if(values.length != 5)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordo+") non valido (urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>)");
		String urn = values[0];
		if("urn".equals(urn)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Formato uriAccordo ("+uriAccordo+") non valido (urn:<tipo_accordo>:<soggetto_organizzativo>:<nome_accordo>:<versione>)");
		String tipoAccordo = values[1];
		if(tipoAtteso.equals(tipoAccordo)==false)
			throw new SICAToOpenSPCoopUtilitiesException("Tipo presente nell'uriAccordo ("+tipoAccordo+") non corrisponde quello atteso ("+tipoAtteso+")");
		String soggetto = values[2];
		if(soggetto==null)
			throw new SICAToOpenSPCoopUtilitiesException("Soggetto non presente nell'uriAccordo ("+uriAccordo+")");
		IDSoggetto idSoggetto = sicaToOpenSPCoopContext.getIDSoggetto(soggetto);
		if(idSoggetto==null)
			idSoggetto = SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(soggetto,false);
		String nome = values[3];
		if(nome==null)
			throw new SICAToOpenSPCoopUtilitiesException("Nome non presente nell'uriAccordo ("+uriAccordo+")");
		String versione = values[4];
		if(versione==null)
			throw new SICAToOpenSPCoopUtilitiesException("Versione non presente nell'uriAccordo ("+uriAccordo+")");
		IDAccordo idAccordo = null;
		try{
			idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nome, idSoggetto, versione);
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("Costruzione IDAccordo ("+uriAccordo+") non riuscita: "+e.getMessage(),e);
		}
		return idAccordo;
	}
	
	
	
	
	
	
	
	
	
	/* WSDL Utilities */
	public static String readConnettoreFromWsdlImplementativo(byte[] wsdlImplementativo) throws SICAToOpenSPCoopUtilitiesException{
		
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document documentLogico = xmlUtils.newDocument(wsdlImplementativo);
			
			NodeList list = documentLogico.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("service".equals(childDefinition.getLocalName())){
									NodeList listPort = childDefinition.getChildNodes();
									if(listPort!=null){
										for(int z=0; z<listPort.getLength(); z++){
											Node port = listPort.item(z);
											if("port".equals(port.getLocalName())){
												NodeList addressList = port.getChildNodes();
												if(addressList!=null){
													for(int m=0; m<addressList.getLength(); m++){
														Node address = addressList.item(m);
														if("address".equals(address.getLocalName())){
															return address.getAttributes().getNamedItem("location").getNodeValue();
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			return null;
			
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("readWsdlImplementativo error: "+e.getMessage(),e);
		}
	}
	public static byte[] saveConnettoreIntoWsdlImplementativo(byte[] wsdlImplementativo,String url) throws SICAToOpenSPCoopUtilitiesException{
		
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document documentLogico = xmlUtils.newDocument(wsdlImplementativo);
			
			NodeList list = documentLogico.getChildNodes();
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("definitions".equals(child.getLocalName())){
						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if("service".equals(childDefinition.getLocalName())){
									NodeList listPort = childDefinition.getChildNodes();
									if(listPort!=null){
										for(int z=0; z<listPort.getLength(); z++){
											Node port = listPort.item(z);
											if("port".equals(port.getLocalName())){
												NodeList addressList = port.getChildNodes();
												if(addressList!=null){
													for(int m=0; m<addressList.getLength(); m++){
														Node address = addressList.item(m);
														if("address".equals(address.getLocalName())){
															address.getAttributes().getNamedItem("location").setNodeValue(url);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			return xmlUtils.toByteArray(documentLogico);
			
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("readWsdlImplementativo error: "+e.getMessage(),e);
		}
	}
	
	public static boolean isWsdlEmpty(byte[] wsdl){
		
		try{
			
			// <wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"/>
			Element el = XMLUtils.getInstance().newElement(wsdl);
			
			if("http://schemas.xmlsoap.org/wsdl/".equals(el.getNamespaceURI())==false){
				return false;
			}
			if("definitions".equals(el.getLocalName())==false){
				return false;
			}
			
			Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(el, false);
			if(childs.size()<=0){
				return true;
			}
			else{
				return false;
			}
		
		}catch(Exception e){
			//e.printStackTrace();
			return false;
		}
	}
	
	private static String resizeDescriptionForMaxLength(String description){
		if(description!=null && description.length()>255){
			return description.substring(0, 252)+"...";
		}
		else{
			return description;
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* Accordi di cooperazione */
	public static org.openspcoop2.core.registry.AccordoCooperazione accordoCooperazione_sicaToOpenspcoop(it.gov.spcoop.sica.dao.AccordoCooperazione accordoCooperazioneSICA,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log) throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		org.openspcoop2.core.registry.AccordoCooperazione accCooperazioneOpenspcoop = new org.openspcoop2.core.registry.AccordoCooperazione();
		
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.AccordoCooperazione manifest = accordoCooperazioneSICA.getManifesto();
		accCooperazioneOpenspcoop.setDescrizione(resizeDescriptionForMaxLength(manifest.getDescrizione()));
		accCooperazioneOpenspcoop.setNome(manifest.getNome());
		accCooperazioneOpenspcoop.setVersione(manifest.getVersione());
		accCooperazioneOpenspcoop.setOraRegistrazione(manifest.getDataCreazione());
		//accCooperazioneOpenspcoop.setDataPubblicazione(manifest.getDataPubblicazione());
		//manifest.getRiservato() ???
		
		// Coordinatore:
		if(manifest.getCoordinatore()!=null){
			IdSoggetto soggettoReferente = 
				new IdSoggetto(); 
			IDSoggetto soggettoCoordinatore = sicaToOpenspcoopContext.getIDSoggetto(SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(manifest.getCoordinatore()));
			if(soggettoCoordinatore==null){
				soggettoCoordinatore = SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(manifest.getCoordinatore());
			}
			soggettoReferente.setNome(soggettoCoordinatore.getNome());
			soggettoReferente.setTipo(soggettoCoordinatore.getTipo());
			accCooperazioneOpenspcoop.setSoggettoReferente(soggettoReferente);
		}
				
		// Allegati
		if(manifest.getAllegati()!=null){
			for(int i=0; i<manifest.getAllegati().sizeGenericoDocumentoList(); i++){
				String fileName = manifest.getAllegati().getGenericoDocumento(i);
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.allegato.toString());			
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoCooperazioneSICA.sizeAllegati(); j++){
					if(fileName.equals(accordoCooperazioneSICA.getAllegato(j).getNome())){
						docSICA = accordoCooperazioneSICA.getAllegato(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("Allegato con nome["+fileName+"] non trovato");
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				accCooperazioneOpenspcoop.addAllegato(docOpenspcoop);
			}
		}
		
		// SpecificaSemiformale
		if(manifest.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifest.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale specificaSemiformale = manifest.getSpecificaSemiformale().getDocumentoSemiformale(i);
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = specificaSemiformale.getBase();
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.specificaSemiformale.toString());			
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoCooperazioneSICA.sizeSpecificheSemiformali(); j++){
					if(fileName.equals(accordoCooperazioneSICA.getSpecificaSemiformale(j).getNome())){
						docSICA = accordoCooperazioneSICA.getSpecificaSemiformale(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaSemiformale con nome["+fileName+"] non trovato");
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				accCooperazioneOpenspcoop.addSpecificaSemiformale(docOpenspcoop);
			}
		}
		
		// Partecipanti
		ElencoPartecipanti elencoPartecipantiSICA = manifest.getElencoPartecipanti();
		if(elencoPartecipantiSICA!=null){
			if(elencoPartecipantiSICA.sizePartecipanteList()>0){
				accCooperazioneOpenspcoop.setElencoPartecipanti(new AccordoCooperazionePartecipanti());
			}
			for(int i=0; i<elencoPartecipantiSICA.sizePartecipanteList(); i++){
				String partecipante = elencoPartecipantiSICA.getPartecipante(i);
				
				IdSoggetto soggetto = 
					new IdSoggetto(); 
				IDSoggetto soggettoPartecipante = sicaToOpenspcoopContext.getIDSoggetto(SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(partecipante));
				if(soggettoPartecipante==null){
					soggettoPartecipante = SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(partecipante);
				}
				soggetto.setNome(soggettoPartecipante.getNome());
				soggetto.setTipo(soggettoPartecipante.getTipo());
				accCooperazioneOpenspcoop.getElencoPartecipanti().addSoggettoPartecipante(soggetto);
			}
		}
		
		// ServiziComposti
		it.gov.spcoop.sica.manifest.ElencoServiziComposti serviziComposti = manifest.getServiziComposti();
		if(serviziComposti!=null){
			for(int i=0; i<serviziComposti.sizeServizioCompostoList(); i++){
				String servizioComposto = serviziComposti.getServizioComposto(i);
				IDAccordo idAccordo = SICAtoOpenSPCoopUtilities.idAccordoServizioComposto_sicaToOpenspcoop(servizioComposto,sicaToOpenspcoopContext);
				try{
					accCooperazioneOpenspcoop.addUriServiziComposti(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("uri IDAccordo servizio composto ["+servizioComposto+"] non costruibile: "+e.getMessage(),e);
				}
			}
		}
		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		//if(manifest.getFirmato() && accordoCooperazioneSICA.getFirma()!=null)
		//	accCooperazioneOpenspcoop.setByteFirma(accordoCooperazioneSICA.getFirma().getBytes());
		
		
		return accCooperazioneOpenspcoop;
	}
	public static it.gov.spcoop.sica.dao.AccordoCooperazione accordoCooperazione_openspcoopToSica(org.openspcoop2.core.registry.AccordoCooperazione accordoCooperazioneOpenspcoop,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
				
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean includiInfoRegistroGenerale = sicaToOpenspcoopContext.isSICAClient_includiInfoRegistroGenerale();
				
		it.gov.spcoop.sica.dao.AccordoCooperazione accCooperazioneSICA = 
			new it.gov.spcoop.sica.dao.AccordoCooperazione();
		
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.AccordoCooperazione manifest = new it.gov.spcoop.sica.manifest.AccordoCooperazione();
		manifest.setDescrizione(accordoCooperazioneOpenspcoop.getDescrizione());
		manifest.setNome(accordoCooperazioneOpenspcoop.getNome());
		manifest.setDataCreazione(accordoCooperazioneOpenspcoop.getOraRegistrazione());
		//manifest.getRiservato() ???
		
		if(includiInfoRegistroGenerale){
			
			manifest.setVersione(accordoCooperazioneOpenspcoop.getVersione());
			//manifest.setDataPubblicazione(accordoCooperazioneOpenspcoop.getDataPubblicazione());
			//if(accordoCooperazioneOpenspcoop.getByteFirma()!=null){
			//	manifest.setFirmato(true);
			//}
			
			// Coordinatore
			if(accordoCooperazioneOpenspcoop.getSoggettoReferente()==null)
				throw new SICAToOpenSPCoopUtilitiesException("Soggetto referente non definito");
			IDSoggetto soggettoCoordinatore = new IDSoggetto(accordoCooperazioneOpenspcoop.getSoggettoReferente().getTipo(),accordoCooperazioneOpenspcoop.getSoggettoReferente().getNome());
			String uriCoordinatore = sicaToOpenspcoopContext.getCodiceIPA(soggettoCoordinatore);
			if(uriCoordinatore==null)
				uriCoordinatore = SICAtoOpenSPCoopUtilities.idSoggetto_openspcoopToSica(soggettoCoordinatore);
			else
				uriCoordinatore = SICAtoOpenSPCoopUtilities.appendURI_IDSoggettoSica(uriCoordinatore);
			manifest.setCoordinatore(uriCoordinatore);
			
			// ServiziComposti
			it.gov.spcoop.sica.manifest.ElencoServiziComposti sComposti = null;
			for(int i=0; i<accordoCooperazioneOpenspcoop.sizeUriServiziCompostiList(); i++){
				String uriServizioComposto =  accordoCooperazioneOpenspcoop.getUriServiziComposti(i);
					
				if(sComposti==null)
					sComposti = new it.gov.spcoop.sica.manifest.ElencoServiziComposti();
				try{
					sComposti.addServizioComposto(SICAtoOpenSPCoopUtilities.idAccordoServizioComposto_openspcoopToSica(IDAccordoFactory.getInstance().getIDAccordoFromUri(uriServizioComposto),sicaToOpenspcoopContext));
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Costruzione IDAccordo servizio composto ["+uriServizioComposto+"] non riuscito: "+e.getMessage(),e);
				}
			}
			if(sComposti!=null)
				manifest.setServiziComposti(sComposti);
			
		} 
		
		// Allegati
		if(accordoCooperazioneOpenspcoop.sizeAllegatoList()>0){
			it.gov.spcoop.sica.manifest.ElencoAllegati allegato = new it.gov.spcoop.sica.manifest.ElencoAllegati();
			for(int i=0; i<accordoCooperazioneOpenspcoop.sizeAllegatoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = accordoCooperazioneOpenspcoop.getAllegato(i);
				allegato.addGenericoDocumento(docOpenspcoop.getFile());
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte dell'allegato "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accCooperazioneSICA.addAllegato(docSICA);
			}
			manifest.setAllegati(allegato);
		}
		
		// SpecificheSemiformali
		if(accordoCooperazioneOpenspcoop.sizeSpecificaSemiformaleList()>0){
			it.gov.spcoop.sica.manifest.SpecificaSemiformale specificaSemiformale = new it.gov.spcoop.sica.manifest.SpecificaSemiformale();
			for(int i=0; i<accordoCooperazioneOpenspcoop.sizeSpecificaSemiformaleList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = accordoCooperazioneOpenspcoop.getSpecificaSemiformale(i);
				
				it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = new it.gov.spcoop.sica.manifest.DocumentoSemiformale();
				docSemiformale.setTipo(docOpenspcoop.getTipo());
				docSemiformale.setBase(docOpenspcoop.getFile());
				specificaSemiformale.addDocumentoSemiformale(docSemiformale);
				
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica semiformale "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accCooperazioneSICA.addSpecificaSemiformale(docSICA);
			}
			manifest.setSpecificaSemiformale(specificaSemiformale);
		}

		// Partecipanti
		it.gov.spcoop.sica.manifest.ElencoPartecipanti partecipanti = null;
		if(accordoCooperazioneOpenspcoop.getElencoPartecipanti()!=null){
			AccordoCooperazionePartecipanti partecipantiOpenSPCoop = accordoCooperazioneOpenspcoop.getElencoPartecipanti();
			for(int i=0; i<partecipantiOpenSPCoop.sizeSoggettoPartecipanteList(); i++){
				IdSoggetto soggetto = partecipantiOpenSPCoop.getSoggettoPartecipante(i);
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
				
				if(partecipanti==null)
					partecipanti = new it.gov.spcoop.sica.manifest.ElencoPartecipanti();
				String codiceIPAPartecipante = sicaToOpenspcoopContext.getCodiceIPA(idSoggetto);
				if(codiceIPAPartecipante==null){
					codiceIPAPartecipante = SICAtoOpenSPCoopUtilities.idSoggetto_openspcoopToSica(idSoggetto);
				}
				else
					codiceIPAPartecipante = SICAtoOpenSPCoopUtilities.appendURI_IDSoggettoSica(codiceIPAPartecipante);
				partecipanti.addPartecipante(codiceIPAPartecipante);
			}
		}
		if(partecipanti!=null)
			manifest.setElencoPartecipanti(partecipanti);
		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		if(includiInfoRegistroGenerale){
			//if(accordoCooperazioneOpenspcoop.getByteFirma()!=null){
			//	it.gov.spcoop.sica.firma.Firma firma = new it.gov.spcoop.sica.firma.Firma();
			//	accCooperazioneSICA.setFirma(firma);
			//}
		}
		
		accCooperazioneSICA.setManifesto(manifest);
		return accCooperazioneSICA;
	}
	
	
	
	
	
	
	
	
	
	
	
	/* Accordi di servizio, parte comune */
	public static org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizioParteComune_sicaToOpenspcoop(it.gov.spcoop.sica.dao.AccordoServizioParteComune accordoServizioSICA,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean verificaCorreggiLocationWSDL = sicaToOpenspcoopContext.isWSDL_XSD_allineaImportInclude();
		boolean prettyDocument = sicaToOpenspcoopContext.isWSDL_XSD_prettyDocuments();
		boolean documentoSpecificaEGOV_asClientSICADisabled_childUnquilified = sicaToOpenspcoopContext.isInformazioniEGov_wscpDisabled_childUnqualified();
		boolean documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified = sicaToOpenspcoopContext.isInformazioniEGov_wscpEnabled_childUnqualified();
		
		org.openspcoop2.core.registry.AccordoServizioParteComune accServizioOpenspcoop = new org.openspcoop2.core.registry.AccordoServizioParteComune();
		
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.AccordoServizio manifest = accordoServizioSICA.getManifesto();
		accServizioOpenspcoop.setDescrizione(resizeDescriptionForMaxLength(manifest.getDescrizione()));
		accServizioOpenspcoop.setNome(manifest.getNome());
		accServizioOpenspcoop.setVersione(manifest.getVersione());
		accServizioOpenspcoop.setOraRegistrazione(manifest.getDataCreazione());
		//accServizioOpenspcoop.setDataPubblicazione(manifest.getDataPubblicazione());
		accServizioOpenspcoop.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY); // Default per AS
		//manifest.getRiservato() ???
		
		it.gov.spcoop.sica.manifest.AccordoServizioParteComune parteComune = manifest.getParteComune();
		
		// SpecificaInterfaccia
		it.gov.spcoop.sica.manifest.SpecificaInterfaccia specificaInterfaccia = parteComune.getSpecificaInterfaccia();
		if( (accordoServizioSICA.getInterfacciaConcettuale()!=null) ||  (accordoServizioSICA.getInterfacciaLogicaLatoErogatore()!=null) || (accordoServizioSICA.getInterfacciaLogicaLatoFruitore()!=null) ){
			if(specificaInterfaccia.getInterfacciaConcettuale()!=null &&  accordoServizioSICA.getInterfacciaConcettuale()!=null &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getInterfacciaConcettuale().getContenuto()) ){
				accServizioOpenspcoop.setByteWsdlConcettuale(accordoServizioSICA.getInterfacciaConcettuale().getContenuto());
			}
			if(specificaInterfaccia.getInterfacciaLogicaLatoErogatore()!=null && accordoServizioSICA.getInterfacciaLogicaLatoErogatore()!=null &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getInterfacciaLogicaLatoErogatore().getContenuto()) ){
				accServizioOpenspcoop.setByteWsdlLogicoErogatore(accordoServizioSICA.getInterfacciaLogicaLatoErogatore().getContenuto());
			}
			if(specificaInterfaccia.getInterfacciaLogicaLatoFruitore()!=null && accordoServizioSICA.getInterfacciaLogicaLatoFruitore()!=null &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getInterfacciaLogicaLatoFruitore().getContenuto()) ){
				accServizioOpenspcoop.setByteWsdlLogicoFruitore(accordoServizioSICA.getInterfacciaLogicaLatoFruitore().getContenuto());
			}
		}
		
		// SpecificaConversazione
		it.gov.spcoop.sica.manifest.SpecificaConversazione specificaConversazione = parteComune.getSpecificaConversazione();
		if( (accordoServizioSICA.getConversazioneConcettuale()!=null) ||  (accordoServizioSICA.getConversazioneLogicaErogatore()!=null) || (accordoServizioSICA.getConversazioneLogicaFruitore()!=null) ){
			if(specificaConversazione.getConversazioneConcettuale()!=null && accordoServizioSICA.getConversazioneConcettuale()!=null  &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getConversazioneConcettuale().getContenuto()) ){
				accServizioOpenspcoop.setByteSpecificaConversazioneConcettuale(accordoServizioSICA.getConversazioneConcettuale().getContenuto());
			}
			if(specificaConversazione.getConversazioneLogicaLatoErogatore()!=null && accordoServizioSICA.getConversazioneLogicaErogatore()!=null  &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getConversazioneLogicaErogatore().getContenuto()) ){
				accServizioOpenspcoop.setByteSpecificaConversazioneErogatore(accordoServizioSICA.getConversazioneLogicaErogatore().getContenuto());
			}
			if(specificaConversazione.getConversazioneLogicaLatoFruitore()!=null && accordoServizioSICA.getConversazioneLogicaFruitore()!=null  &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getConversazioneLogicaFruitore().getContenuto()) ){
				accServizioOpenspcoop.setByteSpecificaConversazioneFruitore(accordoServizioSICA.getConversazioneLogicaFruitore().getContenuto());
			}
		}
		
		// Pubblicatore:
		if(parteComune.getPubblicatore()!=null){
			IdSoggetto soggettoReferente = 
				new IdSoggetto(); 
			IDSoggetto soggettoPubblicatore = sicaToOpenspcoopContext.getIDSoggetto(SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(parteComune.getPubblicatore()));
			if(soggettoPubblicatore==null){
				soggettoPubblicatore = SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(parteComune.getPubblicatore());
			}
			soggettoReferente.setNome(soggettoPubblicatore.getNome());
			soggettoReferente.setTipo(soggettoPubblicatore.getTipo());
			accServizioOpenspcoop.setSoggettoReferente(soggettoReferente);
		}
		
		boolean findDocumentoSpecificaEGOV = false;
		String tipoDocumentoSpecificaEGovTrovato = null;
		String fileNameSpecificaEGovTrovata = null;
		
		// Allegati
		if(manifest.getAllegati()!=null){
			for(int i=0; i<manifest.getAllegati().sizeGenericoDocumentoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = manifest.getAllegati().getGenericoDocumento(i);
				
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.allegato.toString());			
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeAllegati(); j++){
					if(fileName.equals(accordoServizioSICA.getAllegato(j).getNome())){
						docSICA = accordoServizioSICA.getAllegato(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("Allegato con nome["+fileName+"] non trovato");
				
				if(Costanti.ALLEGATO_DEFINITORIO_XSD.equals(fileName)){
					
					accServizioOpenspcoop.setByteWsdlDefinitorio(docSICA.getContenuto());
				
				}else{
					
					// tipo: http://spcoop.gov.it/sica/wscp
					boolean specificaEGovAsClientSICA = it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
					// tipo: http://www.cnipa.it/collProfiles
					boolean specificaEGovAsDocumentoCNIPA = it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
					
					// Check vincoli di unicita
					if(findDocumentoSpecificaEGOV){
						String fileNameTmp = Costanti.ALLEGATI_DIR+File.separatorChar+fileName;
						if(specificaEGovAsClientSICA){
							throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
						}
						if(specificaEGovAsDocumentoCNIPA){
							throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
						}
					}else{
						if(specificaEGovAsClientSICA && specificaEGovAsDocumentoCNIPA){
							throw new SICAToOpenSPCoopUtilitiesException("Trovata specifica delle informazioni egov sia di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" che di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ??");
						}
						if(specificaEGovAsClientSICA || specificaEGovAsDocumentoCNIPA){
							findDocumentoSpecificaEGOV = true;
							fileNameSpecificaEGovTrovata = Costanti.ALLEGATI_DIR+File.separatorChar+fileName;
						}
					}
					
					// Gestione
					if(specificaEGovAsClientSICA){
						try{
							it.gov.spcoop.sica.wscp.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
							tipoDocumentoSpecificaEGovTrovato = it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE;
						}catch(Exception e){
							throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
						}
					}
					else if(specificaEGovAsDocumentoCNIPA){
						try{
							it.cnipa.collprofiles.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICADisabled_childUnquilified);
							tipoDocumentoSpecificaEGovTrovato = it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE;
						}catch(Exception e){
							throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
						}
					}
					else{
						//allegatoGenerico
						docOpenspcoop.setTipo(docSICA.getTipo());
						docOpenspcoop.setByteContenuto(docSICA.getContenuto());
						accServizioOpenspcoop.addAllegato(docOpenspcoop);
					}
				}
			}
		}
		
		// SpecificaSemiformale
		if(manifest.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifest.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale specificaSemiformale = manifest.getSpecificaSemiformale().getDocumentoSemiformale(i);
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = specificaSemiformale.getBase();
				
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeSpecificheSemiformali(); j++){
					if(fileName.equals(accordoServizioSICA.getSpecificaSemiformale(j).getNome())){
						docSICA = accordoServizioSICA.getSpecificaSemiformale(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaSemiformale con nome["+fileName+"] non trovato");
				
				// tipo: http://spcoop.gov.it/sica/wscp
				boolean specificaEGovAsClientSICA = it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
				// tipo: http://www.cnipa.it/collProfiles
				boolean specificaEGovAsDocumentoCNIPA = it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
				
				// Check vincoli di unicita
				if(findDocumentoSpecificaEGOV){
					String fileNameTmp = Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+fileName;
					if(specificaEGovAsClientSICA){
						throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
					}
					if(specificaEGovAsDocumentoCNIPA){
						throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
					}
				}else{
					if(specificaEGovAsClientSICA && specificaEGovAsDocumentoCNIPA){
						throw new SICAToOpenSPCoopUtilitiesException("La specifica delle informazioni egov e' sia di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" che di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ??");
					}
					if(specificaEGovAsClientSICA || specificaEGovAsDocumentoCNIPA){
						findDocumentoSpecificaEGOV = true;
						fileNameSpecificaEGovTrovata = Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+fileName;
					}
				}
				
				// Gestione
				if(specificaEGovAsClientSICA){
					try{
						it.gov.spcoop.sica.wscp.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
						tipoDocumentoSpecificaEGovTrovato = it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE;
						findDocumentoSpecificaEGOV = true;
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
					}
				}
				else if(specificaEGovAsDocumentoCNIPA){
					try{
						it.cnipa.collprofiles.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICADisabled_childUnquilified);
						tipoDocumentoSpecificaEGovTrovato = it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE;
						findDocumentoSpecificaEGOV = true;
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
					}
				}
				else{
					//specificaSemiformaleGenerica
					docOpenspcoop.setFile(fileName);
					docOpenspcoop.setRuolo(RuoliDocumento.specificaSemiformale.toString());			
					docOpenspcoop.setTipo(docSICA.getTipo());
					docOpenspcoop.setByteContenuto(docSICA.getContenuto());
					accServizioOpenspcoop.addSpecificaSemiformale(docOpenspcoop);
				}
			}
		}
	
		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		//if(manifest.getFirmato() && accordoServizioSICA.getFirma()!=null)
		//	accServizioOpenspcoop.setByteFirma(accordoServizioSICA.getFirma().getBytes());
		
		// Correggo eventuali import/include malformati in maniera conforme alla struttura del package CNIPA
		if(verificaCorreggiLocationWSDL){
			try{
				RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
				registroOpenSPCoopUtilities.updateLocation(accServizioOpenspcoop,false,prettyDocument);
			}catch(Exception e){
				throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.setImportLocation error: "+e.getMessage(),e);
			}
		}
		
		if(findDocumentoSpecificaEGOV==false){
			accServizioOpenspcoop.setProfiloCollaborazione(null); // In modo da riconoscere il caso e farlo gestire
		}
		
		return accServizioOpenspcoop;
	}
	public static it.gov.spcoop.sica.dao.AccordoServizioParteComune accordoServizioParteComune_openspcoopToSica(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizioOpenspcoop,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean documentoSpecificaEGOV_asClientSICA = sicaToOpenspcoopContext.isInformazioniEGov_wscp();
		boolean documentoSpecificaEGOV_asSpecificaSemiformale = sicaToOpenspcoopContext.isInformazioniEGov_specificaSemiformale();
		boolean documentoSpecificaEGOV_asClientSICADisabled_namespaceCNIPA = sicaToOpenspcoopContext.isInformazioniEGov_wscpDisabled_namespaceCnipa();
		boolean documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified = sicaToOpenspcoopContext.isInformazioniEGov_wscpEnabled_childUnqualified();
		boolean documentoSpecificaEGOV_nomiSPCoopQualified = sicaToOpenspcoopContext.isInformazioniEGov_nomiSPCoop_qualified();
		boolean verificaCorreggiLocationWSDL = sicaToOpenspcoopContext.isWSDL_XSD_allineaImportInclude();
		boolean prettyDocument = sicaToOpenspcoopContext.isWSDL_XSD_prettyDocuments();
		boolean includiInfoRegistroGenerale = sicaToOpenspcoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean wsdlEmptySeNonDefiniti = sicaToOpenspcoopContext.isWSDL_XSD_accordiParteSpecifica_wsdlEmpty();

		// Correggo eventuali import/include malformati in maniera conforme alla struttura del package CNIPA
		if(verificaCorreggiLocationWSDL){
			try{
				RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
				registroOpenSPCoopUtilities.updateLocation(accordoServizioOpenspcoop,true,prettyDocument);
			}catch(Exception e){
				throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.setImportLocation error: "+e.getMessage(),e);
			}
		}
		
		it.gov.spcoop.sica.dao.AccordoServizioParteComune accServParteComuneSICA = 
			new it.gov.spcoop.sica.dao.AccordoServizioParteComune();
		
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.AccordoServizio manifest = new it.gov.spcoop.sica.manifest.AccordoServizio();
		manifest.setDescrizione(accordoServizioOpenspcoop.getDescrizione());
		manifest.setNome(accordoServizioOpenspcoop.getNome());
		manifest.setDataCreazione(accordoServizioOpenspcoop.getOraRegistrazione());
		//manifest.getRiservato() ???
		
		it.gov.spcoop.sica.manifest.AccordoServizioParteComune parteComune = new it.gov.spcoop.sica.manifest.AccordoServizioParteComune();
		
		if(includiInfoRegistroGenerale){
			manifest.setVersione(accordoServizioOpenspcoop.getVersione());
			//manifest.setDataPubblicazione(accordoServizioOpenspcoop.getDataPubblicazione());
			//if(accordoServizioOpenspcoop.getByteFirma()!=null){
			//	manifest.setFirmato(true);
			//}
			
			// Pubblicatore
			if(accordoServizioOpenspcoop.getSoggettoReferente()==null)
				throw new SICAToOpenSPCoopUtilitiesException("Soggetto referente non definito");
			IDSoggetto soggettoPubblicatore = new IDSoggetto(accordoServizioOpenspcoop.getSoggettoReferente().getTipo(),accordoServizioOpenspcoop.getSoggettoReferente().getNome());
			String uriPubblicatore = sicaToOpenspcoopContext.getCodiceIPA(soggettoPubblicatore);
			if(uriPubblicatore==null){
				uriPubblicatore = SICAtoOpenSPCoopUtilities.idSoggetto_openspcoopToSica(soggettoPubblicatore);
			}
			else
				uriPubblicatore = SICAtoOpenSPCoopUtilities.appendURI_IDSoggettoSica(uriPubblicatore);
			parteComune.setPubblicatore(uriPubblicatore);
		}
		
		
		// SpecificaInterfaccia
		it.gov.spcoop.sica.manifest.SpecificaInterfaccia specificaInterfaccia = null;
		if(  (accordoServizioOpenspcoop.getByteWsdlConcettuale()!=null) || (accordoServizioOpenspcoop.getByteWsdlLogicoErogatore()!=null) || (accordoServizioOpenspcoop.getByteWsdlLogicoFruitore()!=null) ){
			specificaInterfaccia = new it.gov.spcoop.sica.manifest.SpecificaInterfaccia();
		}
		if(wsdlEmptySeNonDefiniti || (accordoServizioOpenspcoop.getByteWsdlConcettuale()!=null)){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfaccia = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfaccia.setBase(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
			docInterfaccia.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaInterfaccia.setInterfacciaConcettuale(docInterfaccia);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			doc.setNome(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
			if(accordoServizioOpenspcoop.getByteWsdlConcettuale()!=null){
				doc.setContenuto(accordoServizioOpenspcoop.getByteWsdlConcettuale());
			}else{
				// Imposto WSDL Empty per compatibilita Client SICA
				doc.setContenuto(Costanti.WSDL_EMPTY.getBytes());
			}
			accServParteComuneSICA.setInterfacciaConcettuale(doc);
		}
		if(wsdlEmptySeNonDefiniti || (accordoServizioOpenspcoop.getByteWsdlLogicoErogatore()!=null)){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfaccia = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfaccia.setBase(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			docInterfaccia.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaInterfaccia.setInterfacciaLogicaLatoErogatore(docInterfaccia);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			doc.setNome(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			if(accordoServizioOpenspcoop.getByteWsdlLogicoErogatore()!=null){
				doc.setContenuto(accordoServizioOpenspcoop.getByteWsdlLogicoErogatore());
			}else{
				// Imposto WSDL Empty per compatibilita Client SICA
				doc.setContenuto(Costanti.WSDL_EMPTY.getBytes());
			}
			accServParteComuneSICA.setInterfacciaLogicaLatoErogatore(doc);
		}
		if(wsdlEmptySeNonDefiniti || (accordoServizioOpenspcoop.getByteWsdlLogicoFruitore()!=null)){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfaccia = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfaccia.setBase(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
			docInterfaccia.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaInterfaccia.setInterfacciaLogicaLatoFruitore(docInterfaccia);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			doc.setNome(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
			if(accordoServizioOpenspcoop.getByteWsdlLogicoFruitore()!=null){
				doc.setContenuto(accordoServizioOpenspcoop.getByteWsdlLogicoFruitore());
			}else{
				// Imposto WSDL Empty per compatibilita Client SICA
				doc.setContenuto(Costanti.WSDL_EMPTY.getBytes());
			}
			accServParteComuneSICA.setInterfacciaLogicaLatoFruitore(doc);
		}
		if(specificaInterfaccia!=null){
			parteComune.setSpecificaInterfaccia(specificaInterfaccia);
		}
		
		
		// SpecificaConversazione (li cerco sia negli allegati che nelle specifiche semiformali)
		it.gov.spcoop.sica.manifest.SpecificaConversazione specificaConversazione = null;
		if(  (accordoServizioOpenspcoop.getByteSpecificaConversazioneConcettuale()!=null) || 
				(accordoServizioOpenspcoop.getByteSpecificaConversazioneErogatore()!=null) || 
				(accordoServizioOpenspcoop.getByteSpecificaConversazioneFruitore()!=null) ){
			specificaConversazione = new it.gov.spcoop.sica.manifest.SpecificaConversazione();
		}
		if(accordoServizioOpenspcoop.getByteSpecificaConversazioneConcettuale()!=null){
			it.gov.spcoop.sica.manifest.DocumentoConversazione docConversazione = new it.gov.spcoop.sica.manifest.DocumentoConversazione();
			docConversazione.setBase(Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL);
			docConversazione.setTipo(TipiDocumentoConversazione.WSBL.toString());
			specificaConversazione.setConversazioneConcettuale(docConversazione);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoConversazione.WSBL.toString());
			doc.setNome(Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL);
			doc.setContenuto(accordoServizioOpenspcoop.getByteSpecificaConversazioneConcettuale());
			accServParteComuneSICA.setConversazioneConcettuale(doc);
		}
		if(accordoServizioOpenspcoop.getByteSpecificaConversazioneErogatore()!=null){
			it.gov.spcoop.sica.manifest.DocumentoConversazione docConversazione = new it.gov.spcoop.sica.manifest.DocumentoConversazione();
			docConversazione.setBase(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL);
			docConversazione.setTipo(TipiDocumentoConversazione.WSBL.toString());
			specificaConversazione.setConversazioneLogicaLatoErogatore(docConversazione);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoConversazione.WSBL.toString());
			doc.setNome(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL);
			doc.setContenuto(accordoServizioOpenspcoop.getByteSpecificaConversazioneErogatore());
			accServParteComuneSICA.setConversazioneLogicaErogatore(doc);
		}
		if(accordoServizioOpenspcoop.getByteSpecificaConversazioneFruitore()!=null){
			it.gov.spcoop.sica.manifest.DocumentoConversazione docConversazione = new it.gov.spcoop.sica.manifest.DocumentoConversazione();
			docConversazione.setBase(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL);
			docConversazione.setTipo(TipiDocumentoConversazione.WSBL.toString());
			specificaConversazione.setConversazioneLogicaLatoFruitore(docConversazione);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoConversazione.WSBL.toString());
			doc.setNome(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL);
			doc.setContenuto(accordoServizioOpenspcoop.getByteSpecificaConversazioneFruitore());
			accServParteComuneSICA.setConversazioneLogicaFruitore(doc);
		}
		if(specificaConversazione!=null){
			parteComune.setSpecificaConversazione(specificaConversazione);
		}
		
				
		/* Allegati */
		it.gov.spcoop.sica.manifest.ElencoAllegati allegati = null;
		// ProfiloCollaborazioneEGOV
		if(accordoServizioOpenspcoop.sizePortTypeList()>0 && !documentoSpecificaEGOV_asSpecificaSemiformale){
			String docGenerico = null;
			if(documentoSpecificaEGOV_asClientSICA){
				try{
					docGenerico = it.gov.spcoop.sica.wscp.driver.XMLUtils.generaGenericoDocumento(accordoServizioOpenspcoop, accServParteComuneSICA,
							documentoSpecificaEGOV_nomiSPCoopQualified, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in Allegati (http://spcoop.gov.it/sica/wscp): "+e.getMessage(),e);
				}
			}else{
				try{
					docGenerico = it.cnipa.collprofiles.driver.XMLUtils.generaGenericoDocumento(accordoServizioOpenspcoop, accServParteComuneSICA,
							documentoSpecificaEGOV_asClientSICADisabled_namespaceCNIPA,
							documentoSpecificaEGOV_nomiSPCoopQualified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in Allegati (http://www.cnipa.it/collProfiles): "+e.getMessage(),e);
				}
			}
			if(docGenerico!=null){
				if(allegati==null){
					allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
				}
				allegati.addGenericoDocumento(docGenerico);
				manifest.setAllegati(allegati);
			}
		}
		// Altri allegati generici
		if(accordoServizioOpenspcoop.sizeAllegatoList()>0){
			if(allegati==null){
				allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeAllegatoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = accordoServizioOpenspcoop.getAllegato(i);
				allegati.addGenericoDocumento(docOpenspcoop.getFile());
				
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte dell'allegato "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accServParteComuneSICA.addAllegato(docSICA);
			}
			manifest.setAllegati(allegati);
		}
		// WSDL Definitorio
		if(accordoServizioOpenspcoop.getByteWsdlDefinitorio()!=null){
			if(allegati==null) {
				allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
			}
			allegati.addGenericoDocumento(Costanti.ALLEGATO_DEFINITORIO_XSD);
			manifest.setAllegati(allegati);
			
			it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
			docSICA.setTipo("XSD");
			docSICA.setNome(Costanti.ALLEGATO_DEFINITORIO_XSD);
			docSICA.setContenuto(accordoServizioOpenspcoop.getByteWsdlDefinitorio());
			accServParteComuneSICA.addAllegato(docSICA);
		}
		
		
		
		/* SpecificheSemiformali */
		it.gov.spcoop.sica.manifest.SpecificaSemiformale specificaSemiformale = null;
		// ProfiloCollaborazioneEGOV
		if(accordoServizioOpenspcoop.sizePortTypeList()>0 && documentoSpecificaEGOV_asSpecificaSemiformale){
			it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = null;
			if(documentoSpecificaEGOV_asClientSICA){
				try{
					docSemiformale = it.gov.spcoop.sica.wscp.driver.XMLUtils.generaDocumentoSemiformale(accordoServizioOpenspcoop, accServParteComuneSICA,
							documentoSpecificaEGOV_nomiSPCoopQualified, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in SpecificaSemiformale (http://spcoop.gov.it/sica/wscp): "+e.getMessage(),e);
				}
			}else{
				try{
					docSemiformale = it.cnipa.collprofiles.driver.XMLUtils.generaDocumentoSemiformale(accordoServizioOpenspcoop, accServParteComuneSICA, 
							documentoSpecificaEGOV_asClientSICADisabled_namespaceCNIPA,
							documentoSpecificaEGOV_nomiSPCoopQualified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in SpecificaSemiformale (http://www.cnipa.it/collProfiles): "+e.getMessage(),e);
				}
			}
			if(docSemiformale!=null){
				if(specificaSemiformale==null){
					specificaSemiformale = new it.gov.spcoop.sica.manifest.SpecificaSemiformale();
				}
				specificaSemiformale.addDocumentoSemiformale(docSemiformale);
				manifest.setSpecificaSemiformale(specificaSemiformale);
			}
		}
		// Altre Specifiche semiformali
		if(accordoServizioOpenspcoop.sizeSpecificaSemiformaleList()>0){
			if(specificaSemiformale==null){
				specificaSemiformale = new it.gov.spcoop.sica.manifest.SpecificaSemiformale();
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeSpecificaSemiformaleList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = accordoServizioOpenspcoop.getSpecificaSemiformale(i);
				
				it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = new it.gov.spcoop.sica.manifest.DocumentoSemiformale();
				docSemiformale.setTipo(docOpenspcoop.getTipo());
				docSemiformale.setBase(docOpenspcoop.getFile());
				specificaSemiformale.addDocumentoSemiformale(docSemiformale);
							
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica semiformale "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accServParteComuneSICA.addSpecificaSemiformale(docSICA);
			}
			manifest.setSpecificaSemiformale(specificaSemiformale);
		}

		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		if(includiInfoRegistroGenerale){
			//if(accordoServizioOpenspcoop.getByteFirma()!=null){
			//	it.gov.spcoop.sica.firma.Firma firma = new it.gov.spcoop.sica.firma.Firma();
			//	accServParteComuneSICA.setFirma(firma);
			//}
		}
		
		
		// Setto parte comune
		manifest.setParteComune(parteComune);
		
		accServParteComuneSICA.setManifesto(manifest);
		return accServParteComuneSICA;

	}
	
	
	
	
	
	
	/* Accordi di servizio, parte specifica */
	public static org.openspcoop2.core.registry.AccordoServizioParteSpecifica accordoServizioParteSpecifica_sicaToOpenspcoop(it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica accordoServizioSICA,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		return SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_sicaToOpenspcoop(accordoServizioSICA, sicaToOpenspcoopContext,null,log);
	}
	public static org.openspcoop2.core.registry.AccordoServizioParteSpecifica accordoServizioParteSpecifica_sicaToOpenspcoop(it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica accordoServizioSICA,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,String Servizio,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean verificaCorreggiLocationWSDL = sicaToOpenspcoopContext.isWSDL_XSD_allineaImportInclude();
		boolean prettyDocument = sicaToOpenspcoopContext.isWSDL_XSD_prettyDocuments();
		boolean eliminaInformazioniASParteComuneWSDL = sicaToOpenspcoopContext.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune();
		boolean sicaToOpenspcoopAggiuntaImportParteComune = sicaToOpenspcoopContext.isWSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune();
		
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica aspsOpenSPCoop = new org.openspcoop2.core.registry.AccordoServizioParteSpecifica();
		org.openspcoop2.core.registry.Servizio servizioOpenSPCoop = new org.openspcoop2.core.registry.Servizio();
		aspsOpenSPCoop.setServizio(servizioOpenSPCoop);
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
				
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.AccordoServizio manifest = accordoServizioSICA.getManifesto();
		
		aspsOpenSPCoop.setNome(manifest.getNome());
		aspsOpenSPCoop.setVersione(manifest.getVersione());
		
		aspsOpenSPCoop.setDescrizione(resizeDescriptionForMaxLength(manifest.getDescrizione()));
		aspsOpenSPCoop.setOraRegistrazione(manifest.getDataCreazione());
		//aspsOpenSPCoop.setDataPubblicazione(manifest.getDataPubblicazione());
		//manifest.getRiservato() ???
		
		it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica parteSpecifica = manifest.getParteSpecifica();
		
		// riferimentoParteComune
		try{
			String[] split =  parteSpecifica.getRiferimentoParteComune().split(":");
			if(split.length<2)
				throw new Exception("Riferimento parte comune non corretto (split non riuscito) ["+parteSpecifica.getRiferimentoParteComune()+"]");
			if(split[1].equals(Costanti.TIPO_ACCORDO_SERVIZIO_COMPOSTO)){
				aspsOpenSPCoop.setAccordoServizioParteComune(idAccordoFactory.getUriFromIDAccordo(SICAtoOpenSPCoopUtilities.idAccordoServizioComposto_sicaToOpenspcoop(parteSpecifica.getRiferimentoParteComune(),sicaToOpenspcoopContext)));
			}else if(split[1].equals(Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_COMUNE)){
				aspsOpenSPCoop.setAccordoServizioParteComune(idAccordoFactory.getUriFromIDAccordo(SICAtoOpenSPCoopUtilities.idAccordoServizioParteComune_sicaToOpenspcoop(parteSpecifica.getRiferimentoParteComune(),sicaToOpenspcoopContext)));
			}else{
				throw new Exception("Tipo accordo ["+split[1]+"] non conosciuto");
			}
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("Trasformazione riferimento parte comune ["+parteSpecifica.getRiferimentoParteComune()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Specifica Porti di Accesso
		String nomeServizio = Servizio; // Servizio puo' essere null, in tal caso calcolo il nome dal wsdl
		String nomeServizioCorrelato = Servizio; // Servizio puo' essere null, in tal caso calcolo il nome dal wsdl
		it.gov.spcoop.sica.manifest.SpecificaPortiAccesso specificaInterfaccia = parteSpecifica.getSpecificaPortiAccesso();
		if( (accordoServizioSICA.getPortiAccessoFruitore()!=null) ||  (accordoServizioSICA.getPortiAccessoErogatore()!=null) ){
			if(specificaInterfaccia.getPortiAccessoErogatore()!=null &&  accordoServizioSICA.getPortiAccessoErogatore()!=null
					&& accordoServizioSICA.getPortiAccessoErogatore().getContenuto()!=null
					&& !SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getPortiAccessoErogatore().getContenuto()) ){
				byte[] doc = accordoServizioSICA.getPortiAccessoErogatore().getContenuto();
				if(eliminaInformazioniASParteComuneWSDL){
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						doc = registroOpenSPCoopUtilities.eliminaASParteComune(doc,true);
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.eliminaASParteComune error: "+e.getMessage(),e);
					}
				}else if(sicaToOpenspcoopAggiuntaImportParteComune){
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						doc = registroOpenSPCoopUtilities.aggiungiImportASParteComune(doc,true);
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.aggiungiImportASParteComune error: "+e.getMessage(),e);
					}
				}
				aspsOpenSPCoop.setByteWsdlImplementativoErogatore(doc);
				servizioOpenSPCoop.setTipologiaServizio(TipologiaServizio.NORMALE);
				
				// comprensione servizio SPCoop
				if(nomeServizio==null){
					try{
						Document d = xmlUtils.newDocument(doc);
						wsdlUtilities.removeImports(d);
						DefinitionWrapper wsdl = new DefinitionWrapper(d,xmlUtils);
						java.util.Map<?,?> bindings = wsdl.getAllBindings();
						if(bindings==null || bindings.size()<=0){
							throw new Exception("Bindings non presenti");
						}
						java.util.Iterator<?> bindingIterator = bindings.values().iterator();
						while(bindingIterator.hasNext()) {
							Binding binding = (Binding) bindingIterator.next();
							if(binding.getPortType()==null){
								throw new Exception("Un binding non specifica il port type implementato");
							}
							if(binding.getPortType().getQName()==null){
								throw new Exception("Binding con port type che non possiede QName");
							}
							if(nomeServizio==null)
								nomeServizio=binding.getPortType().getQName().getLocalPart();
							else{
								if(nomeServizio.equals(binding.getPortType().getQName().getLocalPart())==false){
									throw new Exception("Trovato piu' di un port-type implementato dai binding definiti nell'interfaccia");
								}
							}
						}
					}catch(Exception e){
						log.error("Comprensione servizio spcoop da wsdl implementativo erogatore non riuscita: "+e.getMessage(),e);
						//throw new SICAToOpenSPCoopUtilitiesException("Comprensione servizio spcoop da wsdl implementativo erogatore non riuscita: "+e.getMessage(),e);
						// Il Servizio verra' selezionato tramite select list
						nomeServizio = "Errore lettura dati Wsdl: "+e.getMessage();
					}
				}
			}
			if(specificaInterfaccia.getPortiAccessoFruitore()!=null &&  accordoServizioSICA.getPortiAccessoFruitore()!=null
					&& accordoServizioSICA.getPortiAccessoFruitore().getContenuto()!=null
					&& !SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getPortiAccessoFruitore().getContenuto()) ){
				byte[] doc = accordoServizioSICA.getPortiAccessoFruitore().getContenuto();
				if(eliminaInformazioniASParteComuneWSDL){
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						doc = registroOpenSPCoopUtilities.eliminaASParteComune(doc,false);
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.eliminaASParteComune error: "+e.getMessage(),e);
					}
				}else if(sicaToOpenspcoopAggiuntaImportParteComune){
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						doc = registroOpenSPCoopUtilities.aggiungiImportASParteComune(doc,false);
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.aggiungiImportASParteComune error: "+e.getMessage(),e);
					}
				}
				aspsOpenSPCoop.setByteWsdlImplementativoFruitore(doc);
				servizioOpenSPCoop.setTipologiaServizio(TipologiaServizio.CORRELATO);
				
				// comprensione servizio SPCoop
				if(nomeServizioCorrelato==null){
					try{
						Document d = xmlUtils.newDocument(doc);
						wsdlUtilities.removeImports(d);
						DefinitionWrapper wsdl = new DefinitionWrapper(d,xmlUtils);
						java.util.Map<?,?> bindings = wsdl.getAllBindings();
						if(bindings==null || bindings.size()<=0){
							throw new Exception("Bindings non presenti");
						}
						java.util.Iterator<?> bindingIterator = bindings.values().iterator();
						while(bindingIterator.hasNext()) {
							Binding binding = (Binding) bindingIterator.next();
							if(binding.getPortType()==null){
								throw new Exception("Un binding non specifica il port type implementato");
							}
							if(binding.getPortType().getQName()==null){
								throw new Exception("Binding con port type che non possiede QName");
							}
							if(nomeServizioCorrelato==null)
								nomeServizioCorrelato=binding.getPortType().getQName().getLocalPart();
							else{
								if(nomeServizioCorrelato.equals(binding.getPortType().getQName().getLocalPart())==false){
									throw new Exception("Trovato piu' di un port-type implementato dai binding definiti nell'interfaccia");
								}
							}
						}
					}catch(Exception e){
						log.error("Comprensione servizio spcoop da wsdl implementativo fruitore non riuscita: "+e.getMessage(),e);
						// throw new SICAToOpenSPCoopUtilitiesException("Comprensione servizio spcoop da wsdl implementativo fruitore non riuscita: "+e.getMessage(),e);
						// Il Servizio verra' selezionato tramite select list
						nomeServizioCorrelato = "Errore lettura dati Wsdl: "+e.getMessage();
					}
				}
			}
		}
		if(TipologiaServizio.CORRELATO.equals(servizioOpenSPCoop.getTipologiaServizio())){
			if(nomeServizioCorrelato==null){
				//throw new SICAToOpenSPCoopUtilitiesException("Comprensione nome del Servizio SPCoop correlato non riuscita tramite la lettura dei wsdl implementativi");
				// Il Servizio verra' selezionato tramite select list
				nomeServizioCorrelato = "Errore lettura dati Wsdl: wsdl implementativo fruitore non esistente o corrotto";
			}
			//else{
			servizioOpenSPCoop.setTipo("SPC");
			servizioOpenSPCoop.setNome(nomeServizioCorrelato);
			aspsOpenSPCoop.setPortType(nomeServizioCorrelato);
			//}
		}else{
			if(nomeServizio==null){
				//throw new SICAToOpenSPCoopUtilitiesException("Comprensione nome del Servizio SPCoop non riuscita tramite la lettura dei wsdl implementativi");
				// Il Servizio verra' selezionato tramite select list
				nomeServizio = "Errore lettura dati Wsdl: wsdl implementativo erogatore non esistente o corrotto";
			}
			//else{
			servizioOpenSPCoop.setTipo("SPC");
			servizioOpenSPCoop.setNome(nomeServizio);
			aspsOpenSPCoop.setPortType(nomeServizio);
			//}
		}
				
		// Adesione
		//aspsOpenSPCoop.setTipoAdesione(parteSpecifica.getAdesione());
		
		
		// Erogatore:
		IDSoggetto soggettoErogatore = null;
		if(parteSpecifica.getErogatore()!=null){
			soggettoErogatore = sicaToOpenspcoopContext.getIDSoggetto(SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(parteSpecifica.getErogatore()));
			if(soggettoErogatore==null){
				soggettoErogatore = SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(parteSpecifica.getErogatore());
			}
			servizioOpenSPCoop.setTipoSoggettoErogatore(soggettoErogatore.getTipo());
			servizioOpenSPCoop.setNomeSoggettoErogatore(soggettoErogatore.getNome());
		}
	
						
		// Allegati
		if(manifest.getAllegati()!=null){
			for(int i=0; i<manifest.getAllegati().sizeGenericoDocumentoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = manifest.getAllegati().getGenericoDocumento(i);
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.allegato.toString());			
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeAllegati(); j++){
					if(fileName.equals(accordoServizioSICA.getAllegato(j).getNome())){
						docSICA = accordoServizioSICA.getAllegato(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("Allegato con nome["+fileName+"] non trovato");
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				aspsOpenSPCoop.addAllegato(docOpenspcoop);
			}
		}
		
		// SpecificaSemiformale	
		if(manifest.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifest.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale specificaSemiformale = manifest.getSpecificaSemiformale().getDocumentoSemiformale(i);
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = specificaSemiformale.getBase();
				
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeSpecificheSemiformali(); j++){
					if(fileName.equals(accordoServizioSICA.getSpecificaSemiformale(j).getNome())){
						docSICA = accordoServizioSICA.getSpecificaSemiformale(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaSemiformale con nome["+fileName+"] non trovato");
				
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.specificaSemiformale.toString());			
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				aspsOpenSPCoop.addSpecificaSemiformale(docOpenspcoop);
			}
		}
		
		
		// SpecificaSicurezza
		SpecificaSicurezza specificaSicurezza = parteSpecifica.getSpecificaSicurezza();
		if(specificaSicurezza!=null){
			for(int i=0; i<specificaSicurezza.sizeDocumentoSicurezzaList(); i++){
				DocumentoSicurezza docSicurezzaSICA = specificaSicurezza.getDocumentoSicurezza(i);
				
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = docSicurezzaSICA.getBase();
				
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeSpecificheSicurezza(); j++){
					if(fileName.equals(accordoServizioSICA.getSpecificaSicurezza(j).getNome())){
						docSICA = accordoServizioSICA.getSpecificaSicurezza(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaSicurezza con nome["+fileName+"] non trovato");
				
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.specificaSicurezza.toString());			
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				aspsOpenSPCoop.addSpecificaSicurezza(docOpenspcoop);
			}
		}
		
		
		// Specifica Livelli di Servizio
		SpecificaLivelliServizio specificaLivelliServizio = parteSpecifica.getSpecificaLivelliServizio();
		if(specificaLivelliServizio!=null){
			for(int i=0; i<specificaLivelliServizio.sizeDocumentoLivelloServizioList(); i++){
				DocumentoLivelloServizio docLS = specificaLivelliServizio.getDocumentoLivelloServizio(i);
				
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = docLS.getBase();
				
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeSpecificheLivelliServizio(); j++){
					if(fileName.equals(accordoServizioSICA.getSpecificaLivelloServizio(j).getNome())){
						docSICA = accordoServizioSICA.getSpecificaLivelloServizio(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaLivelloServizio con nome["+fileName+"] non trovato");
				
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.specificaLivelloServizio.toString());			
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				aspsOpenSPCoop.addSpecificaLivelloServizio(docOpenspcoop);
			}
		}
	
		
		
		// Connettore servizio
		String url = null;
		if(aspsOpenSPCoop.getByteWsdlImplementativoErogatore()!=null){
			try{
				url = SICAtoOpenSPCoopUtilities.readConnettoreFromWsdlImplementativo(aspsOpenSPCoop.getByteWsdlImplementativoErogatore());
			}catch(Exception e){
				log.info("Lettura WsdlLocation non riuscita (ImplementativoErogatore): "+e.getMessage(),e);
			}
		}else if(aspsOpenSPCoop.getByteWsdlImplementativoFruitore()!=null){
			try{
				url = SICAtoOpenSPCoopUtilities.readConnettoreFromWsdlImplementativo(aspsOpenSPCoop.getByteWsdlImplementativoFruitore());
			}catch(Exception e){
				log.info("Lettura WsdlLocation non riuscita (ImplementativoFruitore): "+e.getMessage(),e);
			}
		}
		Connettore connettore = new Connettore();
		if(soggettoErogatore!=null){
			String nomeConn = "CNT_" + soggettoErogatore.getTipo() + "/" + soggettoErogatore.getNome() + "_" +
				servizioOpenSPCoop.getTipo() + "/" + servizioOpenSPCoop.getNome();
			connettore.setNome(nomeConn);
		}
		if(url!=null){
			connettore.setTipo(CostantiDB.CONNETTORE_TIPO_HTTP);
			org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
			prop.setValore(url);
			connettore.addProperty(prop);
		}else{
			connettore.setTipo(CostantiDB.CONNETTORE_TIPO_DISABILITATO);
		}
		servizioOpenSPCoop.setConnettore(connettore);
		
		
		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		//if(manifest.getFirmato() && accordoServizioSICA.getFirma()!=null)
		//	aspsOpenSPCoop.setByteFirma(accordoServizioSICA.getFirma().getBytes());
		
		
		// Correggo eventuali import/include malformati in maniera conforme alla struttura del package CNIPA
		if(verificaCorreggiLocationWSDL){
			try{
				RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
				registroOpenSPCoopUtilities.updateLocation(aspsOpenSPCoop,false,prettyDocument);
			}catch(Exception e){
				//throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.setImportLocation error: "+e.getMessage(),e);
				// Verra impostato uno stato bozza
				log.error("Lettura connettore indicato nel WSDL Implementativo (Port) non riuscita: "+e.getMessage(),e);	
			}
		}
		
		return aspsOpenSPCoop;
	}
	/*public static it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica accordoServizioParteSpecifica_openspcoopToSica(org.openspcoop2.core.registry.Servizio accordoServizioOpenspcoop,
			boolean implementazioneAccordoServizioComposto,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext)throws SICAToOpenSPCoopUtilitiesException{
		return accordoServizioParteSpecifica_openspcoopToSica(accordoServizioOpenspcoop, implementazioneAccordoServizioComposto, null, sicaToOpenspcoopContext);
	}*/
	public static it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica accordoServizioParteSpecifica_openspcoopToSica(org.openspcoop2.core.registry.AccordoServizioParteSpecifica aspsOpenspcoop,
			boolean implementazioneAccordoServizioComposto,
			org.openspcoop2.core.registry.AccordoServizioParteComune parteComuneDaIncludereWSDLImplementativo,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean verificaCorreggiLocationWSDL = sicaToOpenspcoopContext.isWSDL_XSD_allineaImportInclude();
		boolean prettyDocument = sicaToOpenspcoopContext.isWSDL_XSD_prettyDocuments();
		boolean includiInfoRegistroGenerale = sicaToOpenspcoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean wsdlEmptySeNonDefiniti = sicaToOpenspcoopContext.isWSDL_XSD_accordiParteSpecifica_wsdlEmpty();
		boolean openspcoopToSicaEliminazioneImportParteComune = sicaToOpenspcoopContext.isWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune();
		
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
		
		Servizio servizioOpenSPCoop = aspsOpenspcoop.getServizio();
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		// Correggo eventuali import/include malformati in maniera conforme alla struttura del package CNIPA
		if(verificaCorreggiLocationWSDL){
			try{
				RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
				registroOpenSPCoopUtilities.updateLocation(aspsOpenspcoop,true,prettyDocument);
			}catch(Exception e){
				throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.setImportLocation error: "+e.getMessage(),e);
			}
		}
					
		it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica accServParteSpecificaSICA = 
			new it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica();
		
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.AccordoServizio manifest = new it.gov.spcoop.sica.manifest.AccordoServizio();
		manifest.setDescrizione(aspsOpenspcoop.getDescrizione());
		manifest.setNome(aspsOpenspcoop.getNome());
		manifest.setDataCreazione(aspsOpenspcoop.getOraRegistrazione());
		//manifest.getRiservato() ???
		
		it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica parteSpecifica = new it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica();
				
		if(includiInfoRegistroGenerale){
			manifest.setVersione(aspsOpenspcoop.getVersione());
			//manifest.setDataPubblicazione(aspsOpenspcoop.getDataPubblicazione());
		
			// Erogatore:
			IDSoggetto soggettoErogatore = new IDSoggetto(servizioOpenSPCoop.getTipoSoggettoErogatore(),servizioOpenSPCoop.getNomeSoggettoErogatore());
			String uriErogatore = sicaToOpenspcoopContext.getCodiceIPA(soggettoErogatore);
			if(uriErogatore==null){
				uriErogatore =  SICAtoOpenSPCoopUtilities.idSoggetto_openspcoopToSica(soggettoErogatore);
			}
			else
				uriErogatore = SICAtoOpenSPCoopUtilities.appendURI_IDSoggettoSica(uriErogatore);
			parteSpecifica.setErogatore(uriErogatore);
			
			//if(aspsOpenspcoop.getByteFirma()!=null){
			//	manifest.setFirmato(true);
			//}
		}
		
		
		// riferimentoParteComune
		try{
			if(implementazioneAccordoServizioComposto)
				parteSpecifica.setRiferimentoParteComune(SICAtoOpenSPCoopUtilities.idAccordoServizioComposto_openspcoopToSica(idAccordoFactory.getIDAccordoFromUri(aspsOpenspcoop.getAccordoServizioParteComune()),sicaToOpenspcoopContext));
			else
				parteSpecifica.setRiferimentoParteComune(SICAtoOpenSPCoopUtilities.idAccordoServizioParteComune_openspcoopToSica(idAccordoFactory.getIDAccordoFromUri(aspsOpenspcoop.getAccordoServizioParteComune()),sicaToOpenspcoopContext));
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("Trasformazione riferimento parte comune ["+aspsOpenspcoop.getAccordoServizioParteComune()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Calcolo connettore servizioOpenspcoop
		String urlConnettore = null;
		if(servizioOpenSPCoop.getConnettore()!=null){
			if(CostantiDB.CONNETTORE_TIPO_HTTP.equals(servizioOpenSPCoop.getConnettore().getTipo()) || CostantiDB.CONNETTORE_TIPO_HTTPS.equals(servizioOpenSPCoop.getConnettore().getTipo())){
				if(servizioOpenSPCoop.getConnettore().sizePropertyList()>0){
					for(int i=0; i<servizioOpenSPCoop.getConnettore().sizePropertyList(); i++){
						if(CostantiDB.CONNETTORE_HTTP_LOCATION.equals(servizioOpenSPCoop.getConnettore().getProperty(i).getNome())){
							urlConnettore = servizioOpenSPCoop.getConnettore().getProperty(i).getValore();
						}
					}
				}
			}
		}
		
		// Specifica Porti di Accesso
		it.gov.spcoop.sica.manifest.SpecificaPortiAccesso specificaPortiAccesso = null;
		if(  (aspsOpenspcoop.getByteWsdlImplementativoErogatore()!=null) || (aspsOpenspcoop.getByteWsdlImplementativoFruitore()!=null) ){
			specificaPortiAccesso = new it.gov.spcoop.sica.manifest.SpecificaPortiAccesso();
		}
		
		// ImplementativoErogatore
		if(wsdlEmptySeNonDefiniti || aspsOpenspcoop.getByteWsdlImplementativoErogatore()!=null){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfacciaErogatore = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfacciaErogatore.setBase(Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL);
			docInterfacciaErogatore.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaPortiAccesso.setPortiAccessoErogatore(docInterfacciaErogatore);
			it.gov.spcoop.sica.dao.Documento docErogatore = new it.gov.spcoop.sica.dao.Documento();
			docErogatore.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			docErogatore.setNome(Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL);
			byte [] wsdlImplementativoErogatore = null;
			if(aspsOpenspcoop.getByteWsdlImplementativoErogatore()!=null){
				
				wsdlImplementativoErogatore = aspsOpenspcoop.getByteWsdlImplementativoErogatore();
				if(parteComuneDaIncludereWSDLImplementativo!=null){
					// Includo parte comune
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						Definition wsdl = registroOpenSPCoopUtilities.buildWsdlFromObjects(parteComuneDaIncludereWSDLImplementativo, wsdlImplementativoErogatore, true);
						DefinitionWrapper wsdlOpenSPCoop = new DefinitionWrapper(wsdl,xmlUtils);
						wsdlImplementativoErogatore = wsdlOpenSPCoop.toByteArray();
					}catch(Exception e){
						log.info("Inserimento parte comune nel wsdl non riuscito (ImplementativoErogatore): "+e.getMessage(),e);
					}
				}
				else if(openspcoopToSicaEliminazioneImportParteComune){
					// Elimino import parte comune
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						wsdlImplementativoErogatore = registroOpenSPCoopUtilities.eliminaImportASParteComune(wsdlImplementativoErogatore);
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.eliminaImportASParteComune error: "+e.getMessage(),e);
					}
				}
				if(urlConnettore!=null){
					// salvo il connettore
					try{
						wsdlImplementativoErogatore = SICAtoOpenSPCoopUtilities.saveConnettoreIntoWsdlImplementativo(wsdlImplementativoErogatore, 
								urlConnettore);
					}catch(Exception e){
						log.info("Impostazione WsdlLocation non riuscita (ImplementativoErogatore): "+e.getMessage(),e);
					}
				}	
			}
			else{
				// Imposto WSDL Empty per compatibilita Client SICA
				wsdlImplementativoErogatore = Costanti.WSDL_EMPTY.getBytes();
			}
			docErogatore.setContenuto(wsdlImplementativoErogatore);
			accServParteSpecificaSICA.setPortiAccessoErogatore(docErogatore);
		}
		
		// ImplementativoFruitore
		if(wsdlEmptySeNonDefiniti || aspsOpenspcoop.getByteWsdlImplementativoFruitore()!=null){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfacciaFruitore = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfacciaFruitore.setBase(Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL);
			docInterfacciaFruitore.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaPortiAccesso.setPortiAccessoFruitore(docInterfacciaFruitore);
			it.gov.spcoop.sica.dao.Documento docFruitore = new it.gov.spcoop.sica.dao.Documento();
			docFruitore.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			docFruitore.setNome(Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL);
			byte [] wsdlImplementativoFruitore = null;
			if(aspsOpenspcoop.getByteWsdlImplementativoFruitore()!=null){
				
				wsdlImplementativoFruitore = aspsOpenspcoop.getByteWsdlImplementativoFruitore();
				if(parteComuneDaIncludereWSDLImplementativo!=null){
					// Includo parte comune
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						Definition wsdl = registroOpenSPCoopUtilities.buildWsdlFromObjects(parteComuneDaIncludereWSDLImplementativo, wsdlImplementativoFruitore, false);
						DefinitionWrapper wsdlOpenSPCoop = new DefinitionWrapper(wsdl,xmlUtils);
						wsdlImplementativoFruitore = wsdlOpenSPCoop.toByteArray();
					}catch(Exception e){
						log.info("Inserimento parte comune nel wsdl non riuscito (ImplementativoFruitore): "+e.getMessage(),e);
					}
				}
				else if(openspcoopToSicaEliminazioneImportParteComune){
					// Elimino import parte comune
					try{
						RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
						wsdlImplementativoFruitore = registroOpenSPCoopUtilities.eliminaImportASParteComune(wsdlImplementativoFruitore);
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.eliminaImportASParteComune error: "+e.getMessage(),e);
					}
				}
				if(urlConnettore!=null){
					// salvo il connettore
					try{
						wsdlImplementativoFruitore = SICAtoOpenSPCoopUtilities.saveConnettoreIntoWsdlImplementativo(wsdlImplementativoFruitore, 
								urlConnettore);
					}catch(Exception e){
						log.info("Impostazione WsdlLocation non riuscita (ImplementativoFruitore): "+e.getMessage(),e);
					}
				}
			}
			else{
				// Imposto WSDL Empty per compatibilita Client SICA
				wsdlImplementativoFruitore = Costanti.WSDL_EMPTY.getBytes();
			}
			docFruitore.setContenuto(wsdlImplementativoFruitore);
			accServParteSpecificaSICA.setPortiAccessoFruitore(docFruitore);
		}
		
		// Imposto specificaPortiAccesso
		if(specificaPortiAccesso!=null){
			parteSpecifica.setSpecificaPortiAccesso(specificaPortiAccesso);
		}
			
		// Adesione
		//parteSpecifica.setAdesione(aspsOpenspcoop.getTipoAdesione());
		parteSpecifica.setAdesione(TipiAdesione.AUTOMATICA.toString());
		
				
		// Allegati
		if(aspsOpenspcoop.sizeAllegatoList()>0){
			it.gov.spcoop.sica.manifest.ElencoAllegati allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
			for(int i=0; i<aspsOpenspcoop.sizeAllegatoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = aspsOpenspcoop.getAllegato(i);
				
				allegati.addGenericoDocumento(docOpenspcoop.getFile());
				
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte dell'allegato "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accServParteSpecificaSICA.addAllegato(docSICA);
			}
			manifest.setAllegati(allegati);
		}
		
		// SpecificheSemiformali
		if(aspsOpenspcoop.sizeSpecificaSemiformaleList()>0){
			it.gov.spcoop.sica.manifest.SpecificaSemiformale specificaSemiformale = new it.gov.spcoop.sica.manifest.SpecificaSemiformale();
			for(int i=0; i<aspsOpenspcoop.sizeSpecificaSemiformaleList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = aspsOpenspcoop.getSpecificaSemiformale(i);
				
				it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = new it.gov.spcoop.sica.manifest.DocumentoSemiformale();
				docSemiformale.setTipo(docOpenspcoop.getTipo());
				docSemiformale.setBase(docOpenspcoop.getFile());
				specificaSemiformale.addDocumentoSemiformale(docSemiformale);			
				
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica semiformale "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accServParteSpecificaSICA.addSpecificaSemiformale(docSICA);
			}
			manifest.setSpecificaSemiformale(specificaSemiformale);
		}
		
		
		// SpecificheSicurezza
		it.gov.spcoop.sica.manifest.SpecificaSicurezza specificaSicurezza = null;
		for(int i=0; i<aspsOpenspcoop.sizeSpecificaSicurezzaList(); i++){
			org.openspcoop2.core.registry.Documento docOpenspcoop = aspsOpenspcoop.getSpecificaSicurezza(i);
			
			if(specificaSicurezza==null)
				specificaSicurezza = new it.gov.spcoop.sica.manifest.SpecificaSicurezza();
			
			it.gov.spcoop.sica.manifest.DocumentoSicurezza docSicurezza = new it.gov.spcoop.sica.manifest.DocumentoSicurezza();
			docSicurezza.setTipo(docOpenspcoop.getTipo());
			docSicurezza.setBase(docOpenspcoop.getFile());
			specificaSicurezza.addDocumentoSicurezza(docSicurezza);
					
			it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
			docSICA.setTipo(docOpenspcoop.getTipo());
			docSICA.setNome(docOpenspcoop.getFile());
			if(docOpenspcoop.getByteContenuto()==null){
				throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica di sicurezza "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
			}
			docSICA.setContenuto(docOpenspcoop.getByteContenuto());
			accServParteSpecificaSICA.addSpecificaSicurezza(docSICA);
		}
		if(specificaSicurezza!=null)
			parteSpecifica.setSpecificaSicurezza(specificaSicurezza);
		
		
		// Specifiche Livelli di Servizio
		it.gov.spcoop.sica.manifest.SpecificaLivelliServizio specificaLivelliServizio = null;
		for(int i=0; i<aspsOpenspcoop.sizeSpecificaLivelloServizioList(); i++){
			org.openspcoop2.core.registry.Documento docOpenspcoop = aspsOpenspcoop.getSpecificaLivelloServizio(i);
			
			if(specificaLivelliServizio==null)
				specificaLivelliServizio = new it.gov.spcoop.sica.manifest.SpecificaLivelliServizio();
			it.gov.spcoop.sica.manifest.DocumentoLivelloServizio docLivelliServizio = new it.gov.spcoop.sica.manifest.DocumentoLivelloServizio();
			
			docLivelliServizio.setTipo(docOpenspcoop.getTipo());
			docLivelliServizio.setBase(docOpenspcoop.getFile());
			specificaLivelliServizio.addDocumentoLivelloServizio(docLivelliServizio);
			
			it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
			docSICA.setTipo(docOpenspcoop.getTipo());
			docSICA.setNome(docOpenspcoop.getFile());
			if(docOpenspcoop.getByteContenuto()==null){
				throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica del livello di servizio "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
			}
			docSICA.setContenuto(docOpenspcoop.getByteContenuto());
			accServParteSpecificaSICA.addSpecificaLivelloServizio(docSICA);
		}
		if(specificaLivelliServizio!=null)
			parteSpecifica.setSpecificaLivelliServizio(specificaLivelliServizio);

		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		if(includiInfoRegistroGenerale){
			//if(aspsOpenspcoop.getByteFirma()!=null){
			//	it.gov.spcoop.sica.firma.Firma firma = new it.gov.spcoop.sica.firma.Firma();
			//	accServParteSpecificaSICA.setFirma(firma);
			//}
		}
		
		
		// Setto parte comune
		manifest.setParteSpecifica(parteSpecifica);
		
		accServParteSpecificaSICA.setManifesto(manifest);
		return accServParteSpecificaSICA;
	}
	
	
	
	
	
	
	
	/* Servizio Composto */
	public static org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizioComposto_sicaToOpenspcoop(it.gov.spcoop.sica.dao.AccordoServizioComposto accordoServizioSICA,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean verificaCorreggiLocationWSDL = sicaToOpenspcoopContext.isWSDL_XSD_allineaImportInclude();
		boolean prettyDocument = sicaToOpenspcoopContext.isWSDL_XSD_prettyDocuments();
		boolean documentoSpecificaEGOV_asClientSICADisabled_childUnquilified = sicaToOpenspcoopContext.isInformazioniEGov_wscpDisabled_childUnqualified();
		boolean documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified = sicaToOpenspcoopContext.isInformazioniEGov_wscpEnabled_childUnqualified();
		
		org.openspcoop2.core.registry.AccordoServizioParteComune accServizioOpenspcoop = new org.openspcoop2.core.registry.AccordoServizioParteComune();
		
		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.ServizioComposto manifest = accordoServizioSICA.getManifesto();
		accServizioOpenspcoop.setDescrizione(resizeDescriptionForMaxLength(manifest.getDescrizione()));
		accServizioOpenspcoop.setNome(manifest.getNome());
		accServizioOpenspcoop.setVersione(manifest.getVersione());
		accServizioOpenspcoop.setOraRegistrazione(manifest.getDataCreazione());
		//accServizioOpenspcoop.setDataPubblicazione(manifest.getDataPubblicazione());
		accServizioOpenspcoop.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY); // Default per AS
		//manifest.getRiservato() ???
		
		// SpecificaInterfaccia
		it.gov.spcoop.sica.manifest.SpecificaInterfaccia specificaInterfaccia = manifest.getSpecificaInterfaccia();
		if( (accordoServizioSICA.getInterfacciaConcettuale()!=null) ||  (accordoServizioSICA.getInterfacciaLogicaLatoErogatore()!=null) || (accordoServizioSICA.getInterfacciaLogicaLatoFruitore()!=null) ){
			if(specificaInterfaccia.getInterfacciaConcettuale()!=null &&  accordoServizioSICA.getInterfacciaConcettuale()!=null &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getInterfacciaConcettuale().getContenuto()) ){
				accServizioOpenspcoop.setByteWsdlConcettuale(accordoServizioSICA.getInterfacciaConcettuale().getContenuto());
			}
			if(specificaInterfaccia.getInterfacciaLogicaLatoErogatore()!=null && accordoServizioSICA.getInterfacciaLogicaLatoErogatore()!=null &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getInterfacciaLogicaLatoErogatore().getContenuto()) ){
				accServizioOpenspcoop.setByteWsdlLogicoErogatore(accordoServizioSICA.getInterfacciaLogicaLatoErogatore().getContenuto());
			}
			if(specificaInterfaccia.getInterfacciaLogicaLatoFruitore()!=null && accordoServizioSICA.getInterfacciaLogicaLatoFruitore()!=null &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getInterfacciaLogicaLatoFruitore().getContenuto()) ){
				accServizioOpenspcoop.setByteWsdlLogicoFruitore(accordoServizioSICA.getInterfacciaLogicaLatoFruitore().getContenuto());
			}
		}
		
		// SpecificaConversazione
		it.gov.spcoop.sica.manifest.SpecificaConversazione specificaConversazione = manifest.getSpecificaConversazione();
		if( (accordoServizioSICA.getConversazioneConcettuale()!=null) ||  (accordoServizioSICA.getConversazioneLogicaErogatore()!=null) || (accordoServizioSICA.getConversazioneLogicaFruitore()!=null) ){
			if(specificaConversazione.getConversazioneConcettuale()!=null && accordoServizioSICA.getConversazioneConcettuale()!=null  &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getConversazioneConcettuale().getContenuto()) ){
				accServizioOpenspcoop.setByteSpecificaConversazioneConcettuale(accordoServizioSICA.getConversazioneConcettuale().getContenuto());
			}
			if(specificaConversazione.getConversazioneLogicaLatoErogatore()!=null && accordoServizioSICA.getConversazioneLogicaErogatore()!=null  &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getConversazioneLogicaErogatore().getContenuto()) ){
				accServizioOpenspcoop.setByteSpecificaConversazioneErogatore(accordoServizioSICA.getConversazioneLogicaErogatore().getContenuto());
			}
			if(specificaConversazione.getConversazioneLogicaLatoFruitore()!=null && accordoServizioSICA.getConversazioneLogicaFruitore()!=null  &&
					!SICAtoOpenSPCoopUtilities.isWsdlEmpty(accordoServizioSICA.getConversazioneLogicaFruitore().getContenuto()) ){
				accServizioOpenspcoop.setByteSpecificaConversazioneFruitore(accordoServizioSICA.getConversazioneLogicaFruitore().getContenuto());
			}
		}
		
		// Pubblicatore:
		if(manifest.getPubblicatore()!=null){
			IdSoggetto soggettoReferente = 
				new IdSoggetto(); 
			IDSoggetto soggettoPubblicatore = sicaToOpenspcoopContext.getIDSoggetto(SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(manifest.getPubblicatore()));
			if(soggettoPubblicatore==null){
				soggettoPubblicatore = SICAtoOpenSPCoopUtilities.idSoggetto_sicaToOpenspcoop(manifest.getPubblicatore());
			}
			soggettoReferente.setNome(soggettoPubblicatore.getNome());
			soggettoReferente.setTipo(soggettoPubblicatore.getTipo());
			accServizioOpenspcoop.setSoggettoReferente(soggettoReferente);
		}
		

		// Riferimento accordo di cooperazione:
		org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto servizioComposto = 
			new org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto();
		IDAccordoCooperazione idAccordo = SICAtoOpenSPCoopUtilities.idAccordoCooperazione_sicaToOpenspcoop(manifest.getRiferimentoAccordoCooperazione());
		try{
			servizioComposto.setAccordoCooperazione(idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo));
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("Trasformazione IDAccordo di cooperazione ["+manifest.getRiferimentoAccordoCooperazione()+"] non riuscito: "+e.getMessage(),e);
		}
		
		// Servizi componenti
		if(manifest.getServiziComponenti()!=null){
			for(int i=0; i<manifest.getServiziComponenti().sizeServizioComponenteList(); i++){
				String servComponente = manifest.getServiziComponenti().getServizioComponente(i); 
				//IDServizio idServizioComponente = idAccordoServizioParteSpecifica_sicaToOpenspcoop(servComponente,sicaToOpenspcoopContext);
				IDServizio idServizioComponente = sicaToOpenspcoopContext.getIDServizio(servComponente);
				if(idServizioComponente==null){
					throw new SICAToOpenSPCoopUtilitiesException("Trasformazione uriAPS["+servComponente+"] in IDServizio SPCoop non riuscita");
				}
				org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente servComponenteOpenspcoop = 
					new org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente();
				try{
					servComponenteOpenspcoop.setTipo(idServizioComponente.getTipoServizio());
					servComponenteOpenspcoop.setNome(idServizioComponente.getServizio());
					servComponenteOpenspcoop.setTipoSoggetto(idServizioComponente.getSoggettoErogatore().getTipo());
					servComponenteOpenspcoop.setNomeSoggetto(idServizioComponente.getSoggettoErogatore().getNome());
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Trasformazione IDAccordo di servizio composto ["+servComponente+"] non riuscito: "+e.getMessage(),e);
				}
				servizioComposto.addServizioComponente(servComponenteOpenspcoop);
			}
		}

		boolean findDocumentoSpecificaEGOV = false;
		String tipoDocumentoSpecificaEGovTrovato = null;
		String fileNameSpecificaEGovTrovata = null;
	
		// Allegati
		if(manifest.getAllegati()!=null){
			for(int i=0; i<manifest.getAllegati().sizeGenericoDocumentoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = manifest.getAllegati().getGenericoDocumento(i);
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.allegato.toString());			
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeAllegati(); j++){
					if(fileName.equals(accordoServizioSICA.getAllegato(j).getNome())){
						docSICA = accordoServizioSICA.getAllegato(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("Allegato con nome["+fileName+"] non trovato");
				
				if(Costanti.ALLEGATO_DEFINITORIO_XSD.equals(fileName)){
					
					accServizioOpenspcoop.setByteWsdlDefinitorio(docSICA.getContenuto());
				
				}else{
					
					// tipo: http://spcoop.gov.it/sica/wscp
					boolean specificaEGovAsClientSICA = it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
					// tipo: http://www.cnipa.it/collProfiles
					boolean specificaEGovAsDocumentoCNIPA = it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
					
					// Check vincoli di unicita
					if(findDocumentoSpecificaEGOV){
						String fileNameTmp = Costanti.ALLEGATI_DIR+File.separatorChar+fileName;
						if(specificaEGovAsClientSICA){
							throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
						}
						if(specificaEGovAsDocumentoCNIPA){
							throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
						}
					}else{
						if(specificaEGovAsClientSICA && specificaEGovAsDocumentoCNIPA){
							throw new SICAToOpenSPCoopUtilitiesException("Trovata specifica delle informazioni egov sia di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" che di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ??");
						}
						if(specificaEGovAsClientSICA || specificaEGovAsDocumentoCNIPA){
							findDocumentoSpecificaEGOV = true;
							fileNameSpecificaEGovTrovata = Costanti.ALLEGATI_DIR+File.separatorChar+fileName;
						}
					}
					
					// Gestione
					if(specificaEGovAsClientSICA){
						try{
							it.gov.spcoop.sica.wscp.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
							tipoDocumentoSpecificaEGovTrovato = it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE;
						}catch(Exception e){
							throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
						}
					}
					else if(specificaEGovAsDocumentoCNIPA){
						try{
							it.cnipa.collprofiles.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICADisabled_childUnquilified);
							tipoDocumentoSpecificaEGovTrovato = it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE;
						}catch(Exception e){
							throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
						}
					}
					else{
						//allegatoGenerico
						docOpenspcoop.setTipo(docSICA.getTipo());
						docOpenspcoop.setByteContenuto(docSICA.getContenuto());
						accServizioOpenspcoop.addAllegato(docOpenspcoop);
					}
				}
			}
		}
		
		
		
		// SpecificaSemiformale
		if(manifest.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifest.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale specificaSemiformale = manifest.getSpecificaSemiformale().getDocumentoSemiformale(i);
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = specificaSemiformale.getBase();
				
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeSpecificheSemiformali(); j++){
					if(fileName.equals(accordoServizioSICA.getSpecificaSemiformale(j).getNome())){
						docSICA = accordoServizioSICA.getSpecificaSemiformale(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaSemiformale con nome["+fileName+"] non trovato");
				
				// tipo: http://spcoop.gov.it/sica/wscp
				boolean specificaEGovAsClientSICA = it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
				// tipo: http://www.cnipa.it/collProfiles
				boolean specificaEGovAsDocumentoCNIPA = it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto());
				
				// Check vincoli di unicita
				if(findDocumentoSpecificaEGOV){
					String fileNameTmp = Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+fileName;
					if(specificaEGovAsClientSICA){
						throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
					}
					if(specificaEGovAsDocumentoCNIPA){
						throw new SICAToOpenSPCoopUtilitiesException("Nel package sono presenti piu' di una specifica delle informazioni egov. E' stata gia' processata una specifica di tipo "+tipoDocumentoSpecificaEGovTrovato+" ("+fileNameSpecificaEGovTrovata+") e adesso e' stato riscontrato una specifica di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ("+fileNameTmp+")");
					}
				}else{
					if(specificaEGovAsClientSICA && specificaEGovAsDocumentoCNIPA){
						throw new SICAToOpenSPCoopUtilitiesException("La specifica delle informazioni egov e' sia di tipo "+it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE+" che di tipo "+it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE+" ??");
					}
					if(specificaEGovAsClientSICA || specificaEGovAsDocumentoCNIPA){
						findDocumentoSpecificaEGOV = true;
						fileNameSpecificaEGovTrovata = Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+fileName;
					}
				}
				
				// Gestione
				if(specificaEGovAsClientSICA){
					try{
						it.gov.spcoop.sica.wscp.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
						tipoDocumentoSpecificaEGovTrovato = it.gov.spcoop.sica.wscp.driver.Costanti.TARGET_NAMESPACE;
						findDocumentoSpecificaEGOV = true;
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
					}
				}
				else if(specificaEGovAsDocumentoCNIPA){
					try{
						it.cnipa.collprofiles.driver.XMLUtils.mapProfiloCollaborazioneEGOVIntoAS(log,docSICA.getContenuto(), accServizioOpenspcoop, documentoSpecificaEGOV_asClientSICADisabled_childUnquilified);
						tipoDocumentoSpecificaEGovTrovato = it.cnipa.collprofiles.driver.Costanti.TARGET_NAMESPACE;
						findDocumentoSpecificaEGOV = true;
					}catch(Exception e){
						throw new SICAToOpenSPCoopUtilitiesException("Errore durante la verifica dell'esistenza del documento di tipo 'ProfiloCollaborazioneEGOV' per il file ["+fileName+"] : "+e.getMessage(),e);
					}
				}
				else{
					//specificaSemiformaleGenerica
					docOpenspcoop.setFile(fileName);
					docOpenspcoop.setRuolo(RuoliDocumento.specificaSemiformale.toString());			
					docOpenspcoop.setTipo(docSICA.getTipo());
					docOpenspcoop.setByteContenuto(docSICA.getContenuto());
					accServizioOpenspcoop.addSpecificaSemiformale(docOpenspcoop);
				}
			}
		}
	
		
		// Specifica coordinamento
		SpecificaCoordinamento specificaCoordinamento = manifest.getSpecificaCoordinamento();
		if(specificaCoordinamento!=null){
			for(int i=0; i<specificaCoordinamento.sizeDocumentoCoordinamentoList(); i++){
				DocumentoCoordinamento docCoordinamento = specificaCoordinamento.getDocumentoCoordinamento(i);
				
				org.openspcoop2.core.registry.Documento docOpenspcoop = new org.openspcoop2.core.registry.Documento();
				String fileName = docCoordinamento.getBase();
				docOpenspcoop.setFile(fileName);
				docOpenspcoop.setRuolo(RuoliDocumento.specificaCoordinamento.toString());			
				it.gov.spcoop.sica.dao.Documento docSICA = null;
				for(int j=0; j<accordoServizioSICA.sizeSpecificheCoordinamento(); j++){
					if(fileName.equals(accordoServizioSICA.getSpecificaCoordinamento(j).getNome())){
						docSICA = accordoServizioSICA.getSpecificaCoordinamento(j);
					}
				}
				if(docSICA==null)
					throw new SICAToOpenSPCoopUtilitiesException("SpecificaCoordinamento con nome["+fileName+"] non trovato");
				docOpenspcoop.setTipo(docSICA.getTipo());
				docOpenspcoop.setByteContenuto(docSICA.getContenuto());
				servizioComposto.addSpecificaCoordinamento(docOpenspcoop);
			}
		}
		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		//if(manifest.getFirmato() && accordoServizioSICA.getFirma()!=null)
		//	accServizioOpenspcoop.setByteFirma(accordoServizioSICA.getFirma().getBytes());
		
		// Imposto servizio composto
		accServizioOpenspcoop.setServizioComposto(servizioComposto);
		
		// Correggo eventuali import/include malformati in maniera conforme alla struttura del package CNIPA
		if(verificaCorreggiLocationWSDL){
			try{
				RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
				registroOpenSPCoopUtilities.updateLocation(accServizioOpenspcoop,false,prettyDocument);
			}catch(Exception e){
				throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.setImportLocation error: "+e.getMessage(),e);
			}
		}
		
		
		if(findDocumentoSpecificaEGOV==false){
			accServizioOpenspcoop.setProfiloCollaborazione(null); // In modo da riconoscere il caso e farlo gestire
		}
		
		return accServizioOpenspcoop;
	}
	public static it.gov.spcoop.sica.dao.AccordoServizioComposto accordoServizioComposto_openspcoopToSica(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizioOpenspcoop,
			SICAtoOpenSPCoopContext sicaToOpenspcoopContext,
			Logger log)throws SICAToOpenSPCoopUtilitiesException{
		
		if(log==null){
			log = LoggerWrapperFactory.getLogger(SICAtoOpenSPCoopUtilities.class);
		}
		
		boolean documentoSpecificaEGOV_asClientSICA = sicaToOpenspcoopContext.isInformazioniEGov_wscp();
		boolean documentoSpecificaEGOV_asSpecificaSemiformale = sicaToOpenspcoopContext.isInformazioniEGov_specificaSemiformale();
		boolean documentoSpecificaEGOV_asClientSICADisabled_namespaceCNIPA = sicaToOpenspcoopContext.isInformazioniEGov_wscpDisabled_namespaceCnipa();
		boolean documentoSpecificaEGOV_nomiSPCoopQualified = sicaToOpenspcoopContext.isInformazioniEGov_nomiSPCoop_qualified();
		boolean documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified = sicaToOpenspcoopContext.isInformazioniEGov_wscpEnabled_childUnqualified();
		boolean verificaCorreggiLocationWSDL = sicaToOpenspcoopContext.isWSDL_XSD_allineaImportInclude();
		boolean prettyDocument = sicaToOpenspcoopContext.isWSDL_XSD_prettyDocuments();
		boolean includiInfoRegistroGenerale = sicaToOpenspcoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean wsdlEmptySeNonDefiniti = sicaToOpenspcoopContext.isWSDL_XSD_accordiParteSpecifica_wsdlEmpty();
		
		// Correggo eventuali import/include malformati in maniera conforme alla struttura del package CNIPA
		if(verificaCorreggiLocationWSDL){
			try{
				RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(log);
				registroOpenSPCoopUtilities.updateLocation(accordoServizioOpenspcoop,true,prettyDocument);
			}catch(Exception e){
				throw new SICAToOpenSPCoopUtilitiesException("RegistroOpenSPCoopUtilities.setImportLocation error: "+e.getMessage(),e);
			}
		}
		
		it.gov.spcoop.sica.dao.AccordoServizioComposto accServCompostoSICA = 
			new it.gov.spcoop.sica.dao.AccordoServizioComposto();
		
		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
				
		/* Metadati presenti nel Manifest dell'Accordo di Cooperazione. */
		it.gov.spcoop.sica.manifest.ServizioComposto manifest = new it.gov.spcoop.sica.manifest.ServizioComposto();
		manifest.setDescrizione(accordoServizioOpenspcoop.getDescrizione());
		manifest.setNome(accordoServizioOpenspcoop.getNome());
		manifest.setDataCreazione(accordoServizioOpenspcoop.getOraRegistrazione());
		//manifest.getRiservato() ???
		
		if(includiInfoRegistroGenerale){
			manifest.setVersione(accordoServizioOpenspcoop.getVersione());
			//manifest.setDataPubblicazione(accordoServizioOpenspcoop.getDataPubblicazione());
		
			// Pubblicatore
			if(accordoServizioOpenspcoop.getSoggettoReferente()==null)
				throw new SICAToOpenSPCoopUtilitiesException("Soggetto referente non definito");
			IDSoggetto soggettoPubblicatore = new IDSoggetto(accordoServizioOpenspcoop.getSoggettoReferente().getTipo(),accordoServizioOpenspcoop.getSoggettoReferente().getNome());
			String uriPubblicatore = sicaToOpenspcoopContext.getCodiceIPA(soggettoPubblicatore);
			if(uriPubblicatore==null){
				uriPubblicatore = SICAtoOpenSPCoopUtilities.idSoggetto_openspcoopToSica(soggettoPubblicatore);
			}
			else
				uriPubblicatore = SICAtoOpenSPCoopUtilities.appendURI_IDSoggettoSica(uriPubblicatore);
			manifest.setPubblicatore(uriPubblicatore);
			
			//if(accordoServizioOpenspcoop.getByteFirma()!=null){
			//	manifest.setFirmato(true);
			//}
		}
		
		
		// SpecificaInterfaccia
		it.gov.spcoop.sica.manifest.SpecificaInterfaccia specificaInterfaccia = null;
		if(  (accordoServizioOpenspcoop.getByteWsdlConcettuale()!=null) || (accordoServizioOpenspcoop.getByteWsdlLogicoErogatore()!=null) || (accordoServizioOpenspcoop.getByteWsdlLogicoFruitore()!=null) ){
			specificaInterfaccia = new it.gov.spcoop.sica.manifest.SpecificaInterfaccia();
		}
		if(wsdlEmptySeNonDefiniti || (accordoServizioOpenspcoop.getByteWsdlConcettuale()!=null)){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfaccia = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfaccia.setBase(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
			docInterfaccia.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaInterfaccia.setInterfacciaConcettuale(docInterfaccia);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			doc.setNome(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
			if(accordoServizioOpenspcoop.getByteWsdlConcettuale()!=null){
				doc.setContenuto(accordoServizioOpenspcoop.getByteWsdlConcettuale());
			}else{
				// Imposto WSDL Empty per compatibilita Client SICA
				doc.setContenuto(Costanti.WSDL_EMPTY.getBytes());
			}
			accServCompostoSICA.setInterfacciaConcettuale(doc);
		}
		if(wsdlEmptySeNonDefiniti || (accordoServizioOpenspcoop.getByteWsdlLogicoErogatore()!=null)){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfaccia = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfaccia.setBase(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			docInterfaccia.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaInterfaccia.setInterfacciaLogicaLatoErogatore(docInterfaccia);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			doc.setNome(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			if(accordoServizioOpenspcoop.getByteWsdlLogicoErogatore()!=null){
				doc.setContenuto(accordoServizioOpenspcoop.getByteWsdlLogicoErogatore());
			}else{
				// Imposto WSDL Empty per compatibilita Client SICA
				doc.setContenuto(Costanti.WSDL_EMPTY.getBytes());
			}
			accServCompostoSICA.setInterfacciaLogicaLatoErogatore(doc);
		}
		if(wsdlEmptySeNonDefiniti || (accordoServizioOpenspcoop.getByteWsdlLogicoFruitore()!=null)){
			it.gov.spcoop.sica.manifest.DocumentoInterfaccia docInterfaccia = new it.gov.spcoop.sica.manifest.DocumentoInterfaccia();
			docInterfaccia.setBase(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
			docInterfaccia.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			specificaInterfaccia.setInterfacciaLogicaLatoFruitore(docInterfaccia);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoInterfaccia.WSDL.toString());
			doc.setNome(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
			if(accordoServizioOpenspcoop.getByteWsdlLogicoFruitore()!=null){
				doc.setContenuto(accordoServizioOpenspcoop.getByteWsdlLogicoFruitore());
			}else{
				// Imposto WSDL Empty per compatibilita Client SICA
				doc.setContenuto(Costanti.WSDL_EMPTY.getBytes());
			}
			accServCompostoSICA.setInterfacciaLogicaLatoFruitore(doc);
		}
		if(specificaInterfaccia!=null){
			manifest.setSpecificaInterfaccia(specificaInterfaccia);
		}
		
		
		// SpecificaConversazione
		it.gov.spcoop.sica.manifest.SpecificaConversazione specificaConversazione = null;
		if(  (accordoServizioOpenspcoop.getByteSpecificaConversazioneConcettuale()!=null) || 
				(accordoServizioOpenspcoop.getByteSpecificaConversazioneErogatore()!=null) || 
				(accordoServizioOpenspcoop.getByteSpecificaConversazioneFruitore()!=null) ){
			specificaConversazione = new it.gov.spcoop.sica.manifest.SpecificaConversazione();
		}
		if(accordoServizioOpenspcoop.getByteSpecificaConversazioneConcettuale()!=null){
			it.gov.spcoop.sica.manifest.DocumentoConversazione docConversazione = new it.gov.spcoop.sica.manifest.DocumentoConversazione();
			docConversazione.setBase(Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL);
			docConversazione.setTipo(TipiDocumentoConversazione.WSBL.toString());
			specificaConversazione.setConversazioneConcettuale(docConversazione);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoConversazione.WSBL.toString());
			doc.setNome(Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL);
			doc.setContenuto(accordoServizioOpenspcoop.getByteSpecificaConversazioneConcettuale());
			accServCompostoSICA.setConversazioneConcettuale(doc);
		}
		if(accordoServizioOpenspcoop.getByteSpecificaConversazioneErogatore()!=null){
			it.gov.spcoop.sica.manifest.DocumentoConversazione docConversazione = new it.gov.spcoop.sica.manifest.DocumentoConversazione();
			docConversazione.setBase(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL);
			docConversazione.setTipo(TipiDocumentoConversazione.WSBL.toString());
			specificaConversazione.setConversazioneLogicaLatoErogatore(docConversazione);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoConversazione.WSBL.toString());
			doc.setNome(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL);
			doc.setContenuto(accordoServizioOpenspcoop.getByteSpecificaConversazioneErogatore());
			accServCompostoSICA.setConversazioneLogicaErogatore(doc);
		}
		if(accordoServizioOpenspcoop.getByteSpecificaConversazioneFruitore()!=null){
			it.gov.spcoop.sica.manifest.DocumentoConversazione docConversazione = new it.gov.spcoop.sica.manifest.DocumentoConversazione();
			docConversazione.setBase(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL);
			docConversazione.setTipo(TipiDocumentoConversazione.WSBL.toString());
			specificaConversazione.setConversazioneLogicaLatoFruitore(docConversazione);
			
			it.gov.spcoop.sica.dao.Documento doc = new it.gov.spcoop.sica.dao.Documento();
			doc.setTipo(TipiDocumentoConversazione.WSBL.toString());
			doc.setNome(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL);
			doc.setContenuto(accordoServizioOpenspcoop.getByteSpecificaConversazioneFruitore());
			accServCompostoSICA.setConversazioneLogicaFruitore(doc);
		}
		if(specificaConversazione!=null){
			manifest.setSpecificaConversazione(specificaConversazione);
		}
		
		
			

		// Riferimento accordo di cooperazione:
		String uriAccordoCooperazione = null;
		if(accordoServizioOpenspcoop.getServizioComposto()==null){
			throw new SICAToOpenSPCoopUtilitiesException("ServizioComposto non definito");
		}
		try{
			uriAccordoCooperazione =  SICAtoOpenSPCoopUtilities.idAccordoCooperazione_openspcoopToSica(idAccordoCooperazioneFactory.getIDAccordoFromUri(accordoServizioOpenspcoop.getServizioComposto().getAccordoCooperazione()));
		}catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException("Trasformazione IDAccordo di cooperazione ["+accordoServizioOpenspcoop.getServizioComposto().getAccordoCooperazione()+"] non riuscito: "+e.getMessage(),e);
		}
		manifest.setRiferimentoAccordoCooperazione(uriAccordoCooperazione);
		
		
		
		// Servizi componenti
		ElencoServiziComponenti servComponentiSICA = null;
		for(int i=0; i<accordoServizioOpenspcoop.getServizioComposto().sizeServizioComponenteList(); i++){
			AccordoServizioParteComuneServizioCompostoServizioComponente servComponente = accordoServizioOpenspcoop.getServizioComposto().getServizioComponente(i);
		
			if(servComponentiSICA==null)
				servComponentiSICA = new ElencoServiziComponenti();
			IDServizio idServ = new IDServizio(servComponente.getTipoSoggetto(),servComponente.getNomeSoggetto(),
					servComponente.getTipo(),servComponente.getNome());
			try{
				String uriAPS = sicaToOpenspcoopContext.getUriAPS(idServ);
				if(uriAPS==null){
					throw new SICAToOpenSPCoopUtilitiesException("Trasformazione IDServizio ["+idServ+"] in uri accordo servizio parte specifica non riuscita");
				}
				servComponentiSICA.addServizioComponente(uriAPS);
			}catch(Exception e){
				throw new SICAToOpenSPCoopUtilitiesException("Trasformazione IDServizio ["+idServ+"] non riuscito: "+e.getMessage(),e);
			}
		}
		if(servComponentiSICA!=null)
			manifest.setServiziComponenti(servComponentiSICA);
		
		
		/* Allegati */
		it.gov.spcoop.sica.manifest.ElencoAllegati allegati = null;
		// ProfiloCollaborazioneEGOV
		if(accordoServizioOpenspcoop.sizePortTypeList()>0 && !documentoSpecificaEGOV_asSpecificaSemiformale){
			String docGenerico = null;
			if(documentoSpecificaEGOV_asClientSICA){
				try{
					docGenerico = it.gov.spcoop.sica.wscp.driver.XMLUtils.generaGenericoDocumento(accordoServizioOpenspcoop, accServCompostoSICA,
							documentoSpecificaEGOV_nomiSPCoopQualified, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in Allegati (http://spcoop.gov.it/sica/wscp): "+e.getMessage(),e);
				}
			}else{
				try{
					docGenerico = it.cnipa.collprofiles.driver.XMLUtils.generaGenericoDocumento(accordoServizioOpenspcoop, accServCompostoSICA, 
							documentoSpecificaEGOV_asClientSICADisabled_namespaceCNIPA,
							documentoSpecificaEGOV_nomiSPCoopQualified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in Allegati (http://www.cnipa.it/collProfiles): "+e.getMessage(),e);
				}
			}
			if(docGenerico!=null){
				if(allegati==null){
					allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
				}
				allegati.addGenericoDocumento(docGenerico);
				manifest.setAllegati(allegati);
			}
		}
		// Altri allegati generici
		if(accordoServizioOpenspcoop.sizeAllegatoList()>0){
			if(allegati==null){
				allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeAllegatoList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = accordoServizioOpenspcoop.getAllegato(i);
				
				allegati.addGenericoDocumento(docOpenspcoop.getFile());
					
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte dell'allegato "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accServCompostoSICA.addAllegato(docSICA);
			}
			manifest.setAllegati(allegati);
		}
		// WSDL Definitorio
		if(accordoServizioOpenspcoop.getByteWsdlDefinitorio()!=null){
			if(allegati==null){
				allegati = new it.gov.spcoop.sica.manifest.ElencoAllegati();
			}
			allegati.addGenericoDocumento(Costanti.ALLEGATO_DEFINITORIO_XSD);
			manifest.setAllegati(allegati);
			
			it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
			docSICA.setTipo("XSD");
			docSICA.setNome(Costanti.ALLEGATO_DEFINITORIO_XSD);
			docSICA.setContenuto(accordoServizioOpenspcoop.getByteWsdlDefinitorio());
			accServCompostoSICA.addAllegato(docSICA);
		}
		
		
		/* SpecificheSemiformali */
		it.gov.spcoop.sica.manifest.SpecificaSemiformale specificaSemiformale = null;
		// ProfiloCollaborazioneEGOV
		if(accordoServizioOpenspcoop.sizePortTypeList()>0 && documentoSpecificaEGOV_asSpecificaSemiformale){
			it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = null;
			if(documentoSpecificaEGOV_asClientSICA){
				try{
					docSemiformale = it.gov.spcoop.sica.wscp.driver.XMLUtils.generaDocumentoSemiformale(accordoServizioOpenspcoop, accServCompostoSICA,
							documentoSpecificaEGOV_nomiSPCoopQualified, documentoSpecificaEGOV_asClientSICAEnabled_childUnquilified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in SpecificaSemiformale (http://spcoop.gov.it/sica/wscp): "+e.getMessage(),e);
				}
			}else{
				try{
					docSemiformale = it.cnipa.collprofiles.driver.XMLUtils.generaDocumentoSemiformale(accordoServizioOpenspcoop, accServCompostoSICA, 
							documentoSpecificaEGOV_asClientSICADisabled_namespaceCNIPA,
							documentoSpecificaEGOV_nomiSPCoopQualified);
				}catch(Exception e){
					throw new SICAToOpenSPCoopUtilitiesException("Errore durante la conversione delle informazioni eGov in SpecificaSemiformale (http://www.cnipa.it/collProfiles): "+e.getMessage(),e);
				}
			}
			if(docSemiformale!=null){
				if(specificaSemiformale==null){
					specificaSemiformale = new it.gov.spcoop.sica.manifest.SpecificaSemiformale();
				}
				specificaSemiformale.addDocumentoSemiformale(docSemiformale);
				manifest.setSpecificaSemiformale(specificaSemiformale);
			}
		}
		// Altre Specifiche semiformali
		if(accordoServizioOpenspcoop.sizeSpecificaSemiformaleList()>0){
			if(specificaSemiformale==null){
				specificaSemiformale = new it.gov.spcoop.sica.manifest.SpecificaSemiformale();
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeSpecificaSemiformaleList(); i++){
				org.openspcoop2.core.registry.Documento docOpenspcoop = accordoServizioOpenspcoop.getSpecificaSemiformale(i);
				
				it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = new it.gov.spcoop.sica.manifest.DocumentoSemiformale();
				docSemiformale.setTipo(docOpenspcoop.getTipo());
				docSemiformale.setBase(docOpenspcoop.getFile());
				specificaSemiformale.addDocumentoSemiformale(docSemiformale);
				
				it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
				docSICA.setTipo(docOpenspcoop.getTipo());
				docSICA.setNome(docOpenspcoop.getFile());
				if(docOpenspcoop.getByteContenuto()==null){
					throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica semiformale "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
				}
				docSICA.setContenuto(docOpenspcoop.getByteContenuto());
				accServCompostoSICA.addSpecificaSemiformale(docSICA);
			}
			manifest.setSpecificaSemiformale(specificaSemiformale);
		}

		
		// Specifiche di coordinamento
		it.gov.spcoop.sica.manifest.SpecificaCoordinamento coordinamento = null;
		for(int i=0; i<accordoServizioOpenspcoop.getServizioComposto().sizeSpecificaCoordinamentoList(); i++){
			org.openspcoop2.core.registry.Documento docOpenspcoop = accordoServizioOpenspcoop.getServizioComposto().getSpecificaCoordinamento(i);
			
			if(coordinamento==null)
				coordinamento = new it.gov.spcoop.sica.manifest.SpecificaCoordinamento();
			
			it.gov.spcoop.sica.manifest.DocumentoCoordinamento docCoordinamento = new it.gov.spcoop.sica.manifest.DocumentoCoordinamento();
			docCoordinamento.setTipo(docOpenspcoop.getTipo());
			docCoordinamento.setBase(docOpenspcoop.getFile());
			coordinamento.addDocumentoCoordinamento(docCoordinamento);
			
			it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
			docSICA.setTipo(docOpenspcoop.getTipo());
			docSICA.setNome(docOpenspcoop.getFile());
			if(docOpenspcoop.getByteContenuto()==null){
				throw new SICAToOpenSPCoopUtilitiesException("Byte della specifica di coordinamento "+docOpenspcoop.getFile()+" di tipo "+docOpenspcoop.getTipo()+" non forniti");
			}
			docSICA.setContenuto(docOpenspcoop.getByteContenuto());
			accServCompostoSICA.addSpecificaCoordinamento(docSICA);
		}
		if(coordinamento!=null)
			manifest.setSpecificaCoordinamento(coordinamento);
		
		
		/* Firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
		if(includiInfoRegistroGenerale){
			//if(accordoServizioOpenspcoop.getByteFirma()!=null){
			//	it.gov.spcoop.sica.firma.Firma firma = new it.gov.spcoop.sica.firma.Firma();
			//	accServCompostoSICA.setFirma(firma);
			//}
		}
		
		accServCompostoSICA.setManifesto(manifest);
		return accServCompostoSICA;
	}
}
