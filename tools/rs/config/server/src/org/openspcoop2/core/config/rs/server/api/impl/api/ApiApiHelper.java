/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.deserializev3;
import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.AllegatoGenerico;
import org.openspcoop2.core.config.rs.server.model.AllegatoGenericoItem;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaSemiformaleItem;
import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItem;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiItem;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.FormatoRestEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoSoapEnum;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.StatoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSemiformaleEnum;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.archive.APIUtils;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ApiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApiApiHelper {
	
	
	public static final AccordoServizioParteComune accordoApiToRegistro(Api body, ApiEnv env) throws Exception {
		AccordoServizioParteComune as = new AccordoServizioParteComune(); 
		
		as.setNome(body.getNome());
		as.setDescrizione(body.getDescrizione());
		as.setProfiloCollaborazione(ProfiloCollaborazione.SINCRONO);
	
		// Quando sono in SPCoopSoap Specifico di tutti i vari wsdl\wsbl solo il wsdlserv, ovvero  AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE
		// Quando invece sono in modalità ApiGateway e passo una OpenApi, imposto il wsdlconcettuale
		String interfaccia = body.getInterfaccia() != null ? new String(body.getInterfaccia()) : null;

		// defaults
		if (env.profilo != ProfiloEnum.APIGATEWAY) {
			
			if (body.getTipo() == null)
				body.setTipo(TipoApiEnum.SOAP);			
		}
		
		if ( env.profilo == ProfiloEnum.APIGATEWAY ) {
			if ( body.getTipo() == null )
				body.setTipo(TipoApiEnum.REST);
		
		}
		
		if ( body.getTipo() == null )	// Questo non può mai accadere, lo tengo perchè in futuro il codice sopra potrà cambiare.
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un tipo per la Api");
		
		switch (body.getTipo()) {
		case REST:
			as.setByteWsdlConcettuale(interfaccia != null && !interfaccia.trim().replaceAll("\n", "").equals("") ? interfaccia.trim().getBytes() : null);
			as.setFormatoSpecifica( BaseHelper.evalorElse( () -> 
				Enums.formatoSpecificaFromRest.get((FormatoRestEnum) body.getFormato()),
				FormatoSpecifica.OPEN_API_3
				)); 
			break;
		case SOAP: 
		
			as.setFormatoSpecifica( BaseHelper.evalorElse( 
					() -> Enums.formatoSpecificaFromSoap.get((FormatoSoapEnum) body.getFormato() ), 
					FormatoSpecifica.WSDL_11 
				));
			as.setByteWsdlLogicoErogatore(interfaccia != null && !interfaccia.trim().replaceAll("\n", "").equals("") ? interfaccia.trim().getBytes() : null);	// Da commenti e audit, WSDL solo logico ed erogatore
		}
		
		as.setServiceBinding(env.apcCore.fromMessageServiceBinding(Enums.serviceBindingFromTipo.get(body.getTipo())));
		
		boolean facilityUnicoWSDL_interfacciaStandard = false;
		if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
			as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
			facilityUnicoWSDL_interfacciaStandard = true;
		}

		boolean filtroDuplicatiSupportato = env.stationCore.isFunzionalitaProtocolloSupportataDalProtocollo(
				env.tipo_protocollo, 
				env.apcCore.toMessageServiceBinding(as.getServiceBinding())
				, FunzionalitaProtocollo.FILTRO_DUPLICATI
			);
				
		as.setFiltroDuplicati(Helper.boolToStatoFunzionalita(filtroDuplicatiSupportato));
		as.setConfermaRicezione(StatoFunzionalita.DISABILITATO);
		as.setIdCollaborazione(StatoFunzionalita.DISABILITATO);
		as.setConsegnaInOrdine(StatoFunzionalita.DISABILITATO);
		as.setIdRiferimentoRichiesta(StatoFunzionalita.DISABILITATO);
		as.setUtilizzoSenzaAzione(true);	// Default a true.
		as.setPrivato(false);	// Da Audit è false.
		as.setStatoPackage(StatoType.FINALE.getValue());	// Come da Audit
		
		if (body.getVersione() != null)
			as.setVersione(body.getVersione());
		
		as.setSuperUser(env.userLogin);

		// Questo resta a null dal debug anche quando specifico un wsdl
		// as.setMessageType(apcCore.fromMessageMessageType(this.messageType));

		// Setto il soggetto referente, true in caso di "spcoop" "sdi", "as4"
		// Se non lo gestisco (caso "trasparente") allora è il sistema a legare l'applicativo con un soggetto di default.
		IdSoggetto idSoggReferente = new IdSoggetto();
		if (env.gestisciSoggettoReferente) {			
			idSoggReferente.setNome(Helper.getSoggettoOrDefault(body.getReferente(), env.profilo));
			idSoggReferente.setTipo(env.tipo_soggetto);
		}
		else {
			IDSoggetto idSogg = env.apcCore.getSoggettoOperativoDefault(env.userLogin, env.tipo_protocollo);
			idSoggReferente.setNome(idSogg.getNome());
			idSoggReferente.setTipo(idSogg.getTipo());
		}
		
		if (!env.soggettiCore.existsSoggetto(idSoggReferente.toIDSoggetto())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto referente " + idSoggReferente.toString() + " non è presente nel registro");
		}
		
		idSoggReferente.setId(env.soggettiCore.getIdSoggetto(idSoggReferente.getNome(),idSoggReferente.getTipo()));
		
		as.setSoggettoReferente(idSoggReferente);
		
		// Automapping
		ServerProperties properties = ServerProperties.getInstance();
		InterfaceType interfaceType = Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica());

		AccordiServizioParteComuneUtilities.mapppingAutomaticoInterfaccia(
				as,
				env.apcCore, 
				properties.isEnabledAutoMapping(),
				properties.isValidazioneDocumenti(),
				properties.isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes(),
				facilityUnicoWSDL_interfacciaStandard, 
				env.tipo_protocollo, interfaceType);
		
		
		return as;
	}
	
	
	public static final Documento apiAllegatoToDocumento(ApiAllegato body, AccordoServizioParteComune as, ApiEnv env) {
		
		Documento documento = new Documento();
		documento.setIdProprietarioDocumento(as.getId());
		documento.setRuolo(Enums.ruoliDocumentoFromApi.get(body.getRuolo()).toString());
		
		switch (body.getRuolo()) {
		case ALLEGATO:
			AllegatoGenerico ag = deserializev3(body.getAllegato(), AllegatoGenerico.class).orElse(new AllegatoGenerico());
			documento.setByteContenuto(ag.getDocumento());
			documento.setFile(ag.getNome());
			documento.setTipo( evalnull( () -> ag.getNome().substring( ag.getNome().lastIndexOf('.')+1, ag.getNome().length())) );
			break;
		case SPECIFICASEMIFORMALE:
			AllegatoSpecificaSemiformale ass = deserializev3(body.getAllegato(), AllegatoSpecificaSemiformale.class).orElse(new AllegatoSpecificaSemiformale());
			documento.setByteContenuto(ass.getDocumento());
			documento.setFile(ass.getNome());	
			documento.setTipo( evalnull( () -> Enums.tipoDocumentoSemiFormaleFromSpecifica.get(ass.getTipo()).toString()) );
			break;
		}

		return documento;
	}
	
	
	public static final ApiAllegato documentoToApiAllegato(Documento d) {
		ApiAllegato ret = new ApiAllegato();
		
		ret.setRuolo(Enums.ruoliApiFromDocumento.get(Enum.valueOf(RuoliDocumento.class, d.getRuolo())));
		AllegatoGenerico ag = null;
		
		switch (ret.getRuolo()) {
		case ALLEGATO: {
			ag = new AllegatoGenerico();
			break;
		}
		case SPECIFICASEMIFORMALE: {
			ag = new AllegatoSpecificaSemiformale();
			TipiDocumentoSemiformale tipo = Enum.valueOf(TipiDocumentoSemiformale.class, d.getTipo());
			((AllegatoSpecificaSemiformale) ag).setTipo(Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class));
			break;
		}	
		}
		
		ag.setDocumento(d.getByteContenuto());
		ag.setNome(d.getFile());
		ret.setAllegato(ag);
		return ret;
	}
	
	public static final ApiAllegatoItem documentoToApiAllegatoItem(Documento d) {
		ApiAllegatoItem ret = new ApiAllegatoItem();
		
		ret.setRuolo(Enums.ruoliApiFromDocumento.get(Enum.valueOf(RuoliDocumento.class, d.getRuolo())));
		AllegatoGenericoItem ag = null;
		
		switch (ret.getRuolo()) {
		case ALLEGATO: {
			ag = new AllegatoGenericoItem();
			break;
		}
		case SPECIFICASEMIFORMALE: {
			ag = new AllegatoSpecificaSemiformaleItem();
			TipiDocumentoSemiformale tipo = Enum.valueOf(TipiDocumentoSemiformale.class, d.getTipo());
			((AllegatoSpecificaSemiformaleItem) ag).setTipo(Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class));
			break;
		}	
		}
		
		ag.setNome(d.getFile());
		ret.setAllegato(ag);
		return ret;
	}
	
	public static final void updatePortType(ApiServizio body, PortType pt, ApiEnv env) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		
		pt.setNome(body.getNome());
		pt.setDescrizione(body.getDescrizione());

		boolean filtroDuplicatiSupportato = env.stationCore.isFunzionalitaProtocolloSupportataDalProtocollo(
				env.tipo_protocollo, 
				ServiceBinding.SOAP
				, FunzionalitaProtocollo.FILTRO_DUPLICATI
			);
	
		pt.setFiltroDuplicati(Helper.boolToStatoFunzionalita(filtroDuplicatiSupportato));	
		pt.setIdCollaborazione(Helper.boolToStatoFunzionalita(body.isIdCollaborazione()));
		pt.setIdRiferimentoRichiesta(Helper.boolToStatoFunzionalita(body.isRiferimentoIdRichiesta()));
		pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
		pt.setProfiloCollaborazione(Enums.profiloCollaborazioneFromApiEnum.get(body.getProfiloCollaborazione()));			
	}
	
	
	public static final void updateOperation(ApiAzione azione, PortType parent, Operation to_update) {
		
		to_update.setNome(azione.getNome());	
		
		if (azione.isProfiloRidefinito()) {
			to_update.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			to_update.setIdCollaborazione(Helper.boolToStatoFunzionalita(azione.isIdCollaborazione()));
			to_update.setIdRiferimentoRichiesta(Helper.boolToStatoFunzionalita(azione.isRiferimentoIdRichiesta()));
			to_update.setProfiloCollaborazione(Enums.profiloCollaborazioneFromApiEnum.get(azione.getProfiloCollaborazione()));
		}
		else {
			to_update.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
			to_update.setFiltroDuplicati(parent.getFiltroDuplicati());
			to_update.setConfermaRicezione(parent.getConfermaRicezione());
			to_update.setConsegnaInOrdine(parent.getConsegnaInOrdine());
			to_update.setIdCollaborazione(parent.getIdCollaborazione());
			to_update.setIdRiferimentoRichiesta(parent.getIdRiferimentoRichiesta());
			to_update.setProfiloCollaborazione(parent.getProfiloCollaborazione());
			to_update.setScadenza(parent.getScadenza());
		}		
	}
	
		
	public static final Operation apiAzioneToOperazione(ApiAzione azione, PortType parent) {
		final Operation ret = new Operation();
		updateOperation(azione, parent, ret);
		
		final String useOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE;
		final String namespaceWsdlOp = "";

		final BindingUse use = BindingUse.toEnumConstant(useOp);
		ret.setMessageInput(new Message());
		ret.getMessageInput().setSoapNamespace(namespaceWsdlOp);
		ret.getMessageInput().setUse(use);
		
		// Se il profilo non è oneWay dobbiamo specificare anche in mesaggio di i uscita, la comunicazione diventa a due vie.
		if (ret.getProfiloCollaborazione() != ProfiloCollaborazione.ONEWAY) {
			ret.setMessageOutput(new Message());
			ret.getMessageOutput().setSoapNamespace(namespaceWsdlOp);
			ret.getMessageOutput().setUse(use);
		}
		
		return ret;
	}
	
	public static final ApiAzione operazioneToApiAzione(Operation op) {
		ApiAzione ret = new ApiAzione();
		
		ret.setNome(op.getNome());
		ret.setIdCollaborazione(op.getIdCollaborazione() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setRiferimentoIdRichiesta(op.getIdRiferimentoRichiesta() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setProfiloCollaborazione(Enums.profiloCollaborazioneApiFromRegistro.get(op.getProfiloCollaborazione()));
		ret.setProfiloRidefinito(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(op.getProfAzione()) ? true : false);
	
		return ret;
	}
	
	
	public static final PortType apiServizioToRegistro(ApiServizio body, AccordoServizioParteComune parent, ApiEnv env) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		
		PortType ret = new PortType();
		
		updatePortType(body, ret, env);
		
		ret.setScadenza("");
		ret.setConfermaRicezione(parent.getConfermaRicezione());
		ret.setConsegnaInOrdine(parent.getConsegnaInOrdine());
		
		return ret;
	}
	
	
	
	
	public static final ApiServizio servizioRegistroToApi(PortType pt) {
		ApiServizio ret = new ApiServizio();
		ret.setNome(pt.getNome());
		ret.setDescrizione(pt.getDescrizione());
		ret.setIdCollaborazione(Helper.statoFunzionalitaToBool(pt.getIdCollaborazione()));
		ret.setRiferimentoIdRichiesta(Helper.statoFunzionalitaToBool(pt.getIdRiferimentoRichiesta()));
		ret.setProfiloCollaborazione(Enums.profiloCollaborazioneApiFromRegistro.get(pt.getProfiloCollaborazione()));
		
		return ret;
	}
	
	
	public static final void updateRisorsa(ApiRisorsa body, Resource res) {
			
		res.setNome(body.getNome());
		res.setDescrizione(body.getDescrizione());
		
		// 1. se il path non inizia per '/' aggiungo all'inizio della stringa
		String pathNormalizzato = body.getPath();
		
		if(pathNormalizzato !=null && !"".equals(pathNormalizzato)) {
			pathNormalizzato = pathNormalizzato.trim();
			if(!pathNormalizzato.startsWith("/"))
				pathNormalizzato = "/" + pathNormalizzato;
		}
					
		res.setPath(pathNormalizzato);
		
		res.setMethod(Helper.apiEnumToGovway(body.getHttpMethod(), HttpMethod.class));
		
		if (res.getMethod() != null)
			res.set_value_method(res.getMethod().toString());
		
		if (StringUtils.isEmpty(res.getNome()) && res.getMethod() != null)
			res.setNome(APIUtils.normalizeResourceName(res.getMethod(), pathNormalizzato));
		
		res.setIdCollaborazione( Helper.boolToStatoFunzionalita(body.isIdCollaborazione()));
		res.setIdRiferimentoRichiesta( Helper.boolToStatoFunzionalita(body.isRiferimentoIdRichiesta()));		
		res.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
	}
	
	// Todo prendiFiltroDuplicati e roba dal parent.
	public static final Resource apiRisorsaToRegistro(ApiRisorsa body, AccordoServizioParteComune parent) {
		Resource ret = new Resource();
			
		ret.setMessageType(null);
		ret.setRequestMessageType(null);
		ret.setResponseMessageType(null);
		ret.setFiltroDuplicati(parent.getFiltroDuplicati());
		ret.setConfermaRicezione(parent.getConfermaRicezione());
		ret.setIdCollaborazione(parent.getIdCollaborazione());
		ret.setIdRiferimentoRichiesta(parent.getIdRiferimentoRichiesta());
		ret.setConsegnaInOrdine(parent.getConsegnaInOrdine());
		ret.setScadenza(parent.getScadenza());		
				
		updateRisorsa(body,ret);
	
		return ret;
	}
	
	
	public static final ApiRisorsa risorsaRegistroToApi(Resource r) {
		ApiRisorsa ret = new ApiRisorsa();
		
		ret.setNome(r.getNome());
		ret.setDescrizione(r.getDescrizione());
		
		ret.setHttpMethod( HttpMethodEnum.valueOf(r.get_value_method()) == null 
				? HttpMethodEnum.QUALSIASI 
				: HttpMethodEnum.valueOf(r.get_value_method())
				);
		
		ret.setIdCollaborazione(r.getIdCollaborazione() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setRiferimentoIdRichiesta(r.getIdRiferimentoRichiesta() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setPath(r.getPath());
		
		return ret;
	}

	
	public static final ApiItem apiToItem(Api api, AccordoServizioParteComuneSintetico as, ApiEnv env) {
		ApiItem ret = new ApiItem();

		ret.setDescrizione(api.getDescrizione());
		ret.setFormato(api.getFormato());
		ret.setNome(api.getNome());
		ret.setProfilo(env.profilo);	// TODO: In multitenant questo va cambiato al profilo relativo al tip del soggetto referente dell'as
		ret.setSoggetto(api.getReferente());
		ret.setTipo(api.getTipo());
		ret.setVersione(api.getVersione());
		ret.setReferente(api.getReferente());
			
		
		StatoApiEnum stato = null;
		String descrizioneStato = "";
		Search searchForCount = new Search(true);
		switch (ret.getTipo()) {
			case REST:
				// caso REST: l'API e' abilitata se ha almeno una risorsa
				try {
					env.apcCore.accordiResourceList(as.getId().intValue(), searchForCount);
				} catch (Exception e) { throw new RuntimeException(e); }
				
				int numRisorse = searchForCount.getNumEntries(Liste.ACCORDI_API_RESOURCES);
				
				if(numRisorse > 0) {
					stato = StatoApiEnum.OK;
				}
				else {
					stato = StatoApiEnum.ERROR;
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP;
				}
				break;
			case SOAP:
			default:
				
				List<PortType> accordiPorttypeList = null;
				try {
					accordiPorttypeList = env.apcCore.accordiPorttypeList(as.getId().intValue(), searchForCount);
				} catch (Exception e) { throw new RuntimeException(e); }
				
				int numeroTotaleServizi = accordiPorttypeList.size();
				int numeroServiziAbilitati = 0;
				
				for (PortType portType : accordiPorttypeList) {
					if(portType.sizeAzioneList()>0) {
						numeroServiziAbilitati ++;
					}	
				}
				
				if(numeroTotaleServizi == 0) {
					stato = StatoApiEnum.ERROR;
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP;
				}
				else if(numeroTotaleServizi==1 && numeroServiziAbilitati==0) {
					stato = StatoApiEnum.ERROR;
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP;
				} else if(numeroServiziAbilitati == numeroTotaleServizi) {
					stato = StatoApiEnum.OK;
				} else {
					stato = StatoApiEnum.WARN;
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP;
				}
				break;
			}
		
		ret.setStatoDescrizione(descrizioneStato);
		ret.setStato(stato);
		
		return ret;
		
	}
	
	public static final Api accordoSpcRegistroToApi(AccordoServizioParteComuneSintetico as, SoggettiCore soggettiCore) {
		Api ret = new Api();
		
		ret.setNome(as.getNome());
		ret.setDescrizione(as.getDescrizione());
		
		switch (as.getServiceBinding()) {
		case REST:
			ret.setTipo(TipoApiEnum.REST);
			ret.setFormato(Enums.formatoRestFromSpecifica.get(as.getFormatoSpecifica()));
			ret.setInterfaccia(as.getByteWsdlConcettuale());
			break;
		case SOAP:
			ret.setTipo(TipoApiEnum.SOAP);
			ret.setFormato(Enums.formatoSoapFromSpecifica.get(as.getFormatoSpecifica()));
			ret.setInterfaccia(as.getByteWsdlLogicoErogatore());
			break;
		}
		
		ret.setVersione(as.getVersione());
		ret.setReferente(as.getSoggettoReferente().getNome());
		
		return ret;	
	}
	
	
	public static final void correggiDeserializzazione(Api body) {
		
		if (body.getTipo() == TipoApiEnum.REST) body.setFormato(FormatoRestEnum.fromValue((String) body.getFormato()));
		if (body.getTipo() == TipoApiEnum.SOAP) body.setFormato(FormatoSoapEnum.fromValue((String) body.getFormato()));
	}
		
	
	// Versione "deprecata" in favore di quella più generica nell'Helper
	public static final AccordoServizioParteComune getAccordoFull(String nome, Integer versione, ApiEnv env) throws CoreException {
		return Helper.getAccordoFull(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);
	}
	public static final AccordoServizioParteComuneSintetico getAccordoSintetico(String nome, Integer versione, ApiEnv env) throws CoreException {
		return Helper.getAccordoSintetico(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);
	}

}
