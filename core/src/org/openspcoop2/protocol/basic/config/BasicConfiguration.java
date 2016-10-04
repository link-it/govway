/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.basic.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.protocol.manifest.Funzionalita;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.Profilo;
import org.openspcoop2.protocol.manifest.RegistroServizi;
import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader;
import org.openspcoop2.protocol.sdk.BypassMustUnderstandCheck;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Classe che implementa, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolConfiguration} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicConfiguration implements org.openspcoop2.protocol.sdk.config.IProtocolConfiguration {

	private IProtocolFactory factory;
	@SuppressWarnings("unused")
	private Logger log;
	private RegistroServizi registroManifest;
	private Openspcoop2 manifest;

	public BasicConfiguration(IProtocolFactory factory) throws ProtocolException {
		this.factory = factory;
		this.log = this.factory.getLogger();
		this.manifest = this.factory.getManifest();
		this.registroManifest = this.manifest.getRegistroServizi();
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}
	
	@Override
	public boolean isSupportoCodiceIPA() {
		return this.registroManifest.getSoggetti().getCodiceIPA();
	}

	@Override
	public boolean isSupportoIndirizzoRisposta(){
		return this.registroManifest.getSoggetti().getIndirizzoRisposta();
	}
	
	@Override
	public boolean isSupportoWsdlDefinitorio(){
		return this.registroManifest.getServizi().getWsdlDefinitorio();
	}
	
	@Override
	public boolean isSupportoSpecificaConversazioni(){
		return this.registroManifest.getServizi().getSpecificaConversazioni();
	}
		
	@Override
	public List<String> getTipiSoggetti() throws ProtocolException {
		return this.registroManifest.getSoggetti().getTipi().getTipoList();
	}
	
	@Override
	public String getTipoSoggettoDefault() throws ProtocolException {
		if(this.registroManifest.getSoggetti().getTipi().getDefault()!=null){
			return this.registroManifest.getSoggetti().getTipi().getDefault(); 
		}
		else{
			return this.registroManifest.getSoggetti().getTipi().getTipo(0);
		}
	}

	@Override
	public List<String> getTipiServizi() throws ProtocolException {
		return this.registroManifest.getServizi().getTipi().getTipoList();
	}

	@Override
	public String getTipoServizioDefault() throws ProtocolException {
		if(this.registroManifest.getServizi().getTipi().getDefault()!=null){
			return this.registroManifest.getServizi().getTipi().getDefault(); 
		}
		else{
			return this.registroManifest.getServizi().getTipi().getTipo(0);
		}
	}
	
	@Override
	public List<String> getVersioni() throws ProtocolException{
		return this.registroManifest.getVersioni().getVersioneList();
	}

	@Override
	public String getVersioneDefault() throws ProtocolException {
		if(this.registroManifest.getVersioni().getDefault()!=null){
			return this.registroManifest.getVersioni().getDefault(); 
		}
		else{
			return this.registroManifest.getVersioni().getVersione(0);
		}
	}
	
	@Override
	public boolean isSupportato(ProfiloDiCollaborazione profiloCollaborazione)
			throws ProtocolException {
		if(profiloCollaborazione==null){
			throw new ProtocolException("Param not defined");
		}
		Profilo profilo = this.registroManifest.getServizi().getProfilo();
		switch (profiloCollaborazione) {
		case ONEWAY:
			return (profilo!=null ? profilo.getOneway() : true); 
		case SINCRONO:		
			return (profilo!=null ? profilo.getSincrono() : true); 
		case ASINCRONO_SIMMETRICO:		
			return (profilo!=null ? profilo.getAsincronoSimmetrico() : false); 
		case ASINCRONO_ASIMMETRICO:		
			return (profilo!=null ? profilo.getAsincronoAsimmetrico() : false); 
		case UNKNOWN:
			throw new ProtocolException("Param ["+ProfiloDiCollaborazione.UNKNOWN.name()+"] not valid for this method");
		default:
			throw new ProtocolException("Param ["+profiloCollaborazione.getEngineValue()+"] not supported");
		}
	}

	@Override
	public boolean isSupportato(FunzionalitaProtocollo funzionalitaProtocollo)
			throws ProtocolException {
		if(funzionalitaProtocollo==null){
			throw new ProtocolException("Param not defined");
		}
		Funzionalita funzionalita = this.registroManifest.getServizi().getFunzionalita();
		switch (funzionalitaProtocollo) {
		case FILTRO_DUPLICATI:
			return (funzionalita!=null ? funzionalita.getFiltroDuplicati() : false); 
		case CONFERMA_RICEZIONE:		
			return (funzionalita!=null ? funzionalita.getConfermaRicezione() : false); 
		case COLLABORAZIONE:		
			return (funzionalita!=null ? funzionalita.getCollaborazione() : false); 
		case CONSEGNA_IN_ORDINE:		
			return (funzionalita!=null ? funzionalita.getConsegnaInOrdine() : false); 
		case SCADENZA:		
			return (funzionalita!=null ? funzionalita.getScadenza() : false); 
		case MANIFEST_ATTACHMENTS:		
			return (funzionalita!=null ? funzionalita.getManifestAttachments() : false); 
		default:
			throw new ProtocolException("Param ["+funzionalitaProtocollo.getEngineValue()+"] not supported");
		}
	}
	
	@Override
	public boolean isSupportoSOAP11() {
		return this.manifest.getBinding().getSoap11();
	}

	@Override
	public boolean isSupportoSOAP12() {
		return this.manifest.getBinding().getSoap12();
	}

	@Override
	public List<BypassMustUnderstandCheck> getBypassMustUnderstandCheck(){
		List<BypassMustUnderstandCheck> list = new ArrayList<BypassMustUnderstandCheck>();
		if(this.manifest.getBinding().getSoapHeaderBypassMustUnderstand()!=null && this.manifest.getBinding().getSoapHeaderBypassMustUnderstand().sizeHeaderList()>0){
			for (SoapHeaderBypassMustUnderstandHeader header : this.manifest.getBinding().getSoapHeaderBypassMustUnderstand().getHeaderList()) {
				BypassMustUnderstandCheck bypassMustUnderstandCheck = new BypassMustUnderstandCheck();
				bypassMustUnderstandCheck.setElementName(header.getLocalName());
				bypassMustUnderstandCheck.setNamespace(header.getNamespace());
				list.add(bypassMustUnderstandCheck);
			}
		}
		return list;
	}
}
