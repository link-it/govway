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



package org.openspcoop2.pdd.core.autorizzazione;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xacml.CachedMapBasedSimplePolicyRepository;
import org.openspcoop2.utils.xacml.MarshallUtilities;
import org.openspcoop2.utils.xacml.PolicyDecisionPoint;
import org.openspcoop2.utils.xacml.PolicyException;
import org.openspcoop2.utils.xacml.XacmlRequest;
import org.slf4j.Logger;


/**
 * XACMLPolicyUtilities
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XACMLPolicyUtilities {

	private static PolicyDecisionPoint pdp;
	private static synchronized void initPdD(Logger log) throws PolicyException{
		if(XACMLPolicyUtilities.pdp == null) {
			XACMLPolicyUtilities.pdp = new PolicyDecisionPoint(log);
		}
	}	
	public static PolicyDecisionPoint getPolicyDecisionPoint(Logger log) throws PolicyException{
		if(XACMLPolicyUtilities.pdp == null) {
			XACMLPolicyUtilities.initPdD(log);
		}
		return XACMLPolicyUtilities.pdp;
	}

	// Workaround: Serve nel caso di Porta Delegata per poter utilizzare una policy differente da quella utilizzata nell'erogazione
	private static final String  NOME_POLICY_FRUIZIONE = "fruizioneXacmlPolicy.xml";

	public static void loadPolicy(IDServizio idServizio, String key, boolean portaDelegata, Logger log) throws PolicyException{
		byte[] policy = null; 
		@SuppressWarnings("unused")
		String nomePolicy = null;
		int numeroPolicy = 0;
		@SuppressWarnings("unused")
		boolean numeroPolicyFruizione = false;
		try{
			AccordoServizioParteSpecifica asps = RegistroServiziManager.getInstance().getAccordoServizioParteSpecifica(idServizio, null, true);
			for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
				Documento d = asps.getSpecificaSicurezza(i);
				if(TipiDocumentoSicurezza.XACML_POLICY.getNome().equals(d.getTipo())){

					if(policy == null || (portaDelegata && NOME_POLICY_FRUIZIONE.equals(d.getFile()))){
						if(NOME_POLICY_FRUIZIONE.equals(d.getFile())){
							numeroPolicyFruizione = true;
						}
						else{
							numeroPolicy++;
						}
						if(d.getByteContenuto()!=null){
							policy = d.getByteContenuto();
							nomePolicy = d.getFile();
						}
						else if(d.getFile()!=null){
							if(d.getFile().startsWith("http://") || d.getFile().startsWith("file://")){
								URL url = new URL(d.getFile());
								policy = HttpUtilities.requestHTTPFile(url.toString());
							}
							else{
								File f = new File(d.getFile());
								policy = FileSystemUtilities.readBytesFromFile(f);
							}
						}
					}
				}
			}
			if(numeroPolicy>1){
				throw new PolicyException("Piu di una xacml policy trovata per il servizio "+idServizio.toString());
			}
		}catch(Exception e){
			throw new PolicyException("Errore durante la ricerca delle policies xacml per il servizio "+idServizio.toString()+": "+e.getMessage(),e);
		}
		if(policy== null){
			throw new PolicyException("Nessuna xacml policy trovata trovata per il servizio "+idServizio.toString());
		}

		try{
			// Caricamento in PdP vedendo che la policy non sia gia stata caricata ....
			XACMLPolicyUtilities.getPolicyDecisionPoint(log).addPolicy(MarshallUtilities.unmarshallPolicy(policy), key);
		}catch(Exception e){
			throw new PolicyException("Errore durante il caricamento della xacml policy sul PdD (servizio "+idServizio.toString()+"): "+e.getMessage(),e);
		}
	}

	public static XacmlRequest newXacmlRequest(IProtocolFactory<?> protocolFactory, AbstractDatiInvocazione datiInvocazione, boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			String policyKey) throws AutorizzazioneException{

		XacmlRequest xacmlRequest = new XacmlRequest();
		URLProtocolContext urlProtocolContext = null;
		if(datiInvocazione.getInfoConnettoreIngresso()==null ||
				datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null){
			throw new AutorizzazioneException("UrlProtocolContext non disponibile; risorsa richiesta dall'autorizzazione");
		}
		urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();

		HttpServletRequest httpServletRequest = null;
		if(checkRuoloEsterno){
			if(datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest()==null){
				throw new AutorizzazioneException("HttpServletRequest non disponibile; risorsa richiesta dall'autorizzazione");
			}
			httpServletRequest = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest();
		}

		if(datiInvocazione.getIdServizio()==null || 
				datiInvocazione.getIdServizio().getSoggettoErogatore()==null ||
				datiInvocazione.getIdServizio().getSoggettoErogatore().getTipo()==null ||
				datiInvocazione.getIdServizio().getSoggettoErogatore().getNome()==null ||
				datiInvocazione.getIdServizio().getTipo()==null ||
				datiInvocazione.getIdServizio().getNome() == null){
			throw new AutorizzazioneException("DatiServizio non disponibile; risorsa richiesta dall'autorizzazione");
		}
		String tipoSoggettoErogatore = datiInvocazione.getIdServizio().getSoggettoErogatore().getTipo();
		String nomeSoggettoErogatore = datiInvocazione.getIdServizio().getSoggettoErogatore().getNome();
		String tipoServizio = datiInvocazione.getIdServizio().getTipo();
		String nomeServizio = datiInvocazione.getIdServizio().getNome();
		String azione = datiInvocazione.getIdServizio().getAzione() != null ? datiInvocazione.getIdServizio().getAzione() : "";

		DatiInvocazionePortaDelegata datiPD = null;
		if(datiInvocazione instanceof DatiInvocazionePortaDelegata){
			datiPD = (DatiInvocazionePortaDelegata) datiInvocazione;
		}

		DatiInvocazionePortaApplicativa datiPA = null;
		if(datiInvocazione instanceof DatiInvocazionePortaApplicativa){
			datiPA = (DatiInvocazionePortaApplicativa) datiInvocazione;
		}



		// Action

		String azioneId = urlProtocolContext.getRequestURI();
		xacmlRequest.addAction(azioneId); // namespace standard xacml

		xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PROVIDER_ATTRIBUTE_ID, tipoSoggettoErogatore+"/"+nomeSoggettoErogatore);    	

		xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_SERVICE_ATTRIBUTE_ID, tipoServizio+"/"+nomeServizio);    	

		if(azione!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_ACTION_ATTRIBUTE_ID, azione);
		}

		xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_URL_ATTRIBUTE_ID, urlProtocolContext.getRequestURI());    	

		if(urlProtocolContext.getParametersFormBased()!=null && urlProtocolContext.getParametersFormBased().size()>0){
			Enumeration<Object> en = urlProtocolContext.getParametersFormBased().keys();
			while (en.hasMoreElements()) {
				Object o = (Object) en.nextElement();
				if(o instanceof String){
					String key = (String) o;
					String value = urlProtocolContext.getParametersFormBased().getProperty(key);
					if(key!=null && !key.contains(" ") && value!=null){
						xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_URL_PARAMETER_ATTRIBUTE_ID+key, value);    	
					}
				}
			}
		}

		if(urlProtocolContext.getParametersTrasporto()!=null && urlProtocolContext.getParametersTrasporto().size()>0){
			Enumeration<Object> en = urlProtocolContext.getParametersTrasporto().keys();
			while (en.hasMoreElements()) {
				Object o = (Object) en.nextElement();
				if(o instanceof String){
					String key = (String) o;
					String value = urlProtocolContext.getParametersFormBased().getProperty(key);
					if(key!=null && !key.contains(" ") && value!=null){
						xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_TRANSPORT_HEADER_ATTRIBUTE_ID+key, value);    	
					}
				}
			}
		}

		if(urlProtocolContext.getFunction()!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PDD_SERVICE_ATTRIBUTE_ID, urlProtocolContext.getFunction());    	
		}

		if(datiInvocazione.getInfoConnettoreIngresso().getSoapAction()!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_SOAP_ACTION_ATTRIBUTE_ID, datiInvocazione.getInfoConnettoreIngresso().getSoapAction());    	
		}

		if(protocolFactory!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PROTOCOL_ATTRIBUTE_ID, protocolFactory.getProtocol());    	
		}


		// Subject

		String nomeServizioApplicativo = null;
		IDSoggetto soggettoFruitore = null;
		if(datiPD!=null){
			if(datiPD.getIdServizioApplicativo()!=null){
				nomeServizioApplicativo = datiPD.getIdServizioApplicativo().getNome();
				soggettoFruitore = datiPD.getIdServizioApplicativo().getIdSoggettoProprietario();
			}
			else if(datiPD.getIdPD()!=null && datiPD.getIdPD().getIdentificativiFruizione()!=null &&
					datiPD.getIdPD().getIdentificativiFruizione().getSoggettoFruitore()!=null){
				soggettoFruitore = datiPD.getIdPD().getIdentificativiFruizione().getSoggettoFruitore();
			}
		}
		else if(datiPA!=null){
			if(datiPA.getIdSoggettoFruitore()!=null){
				soggettoFruitore = datiPA.getIdSoggettoFruitore();
			}
			if(datiPA.getIdentitaServizioApplicativoFruitore()!=null){
				nomeServizioApplicativo = datiPA.getIdentitaServizioApplicativoFruitore().getNome();
				if(soggettoFruitore==null){
					soggettoFruitore = datiPA.getIdentitaServizioApplicativoFruitore().getIdSoggettoProprietario();
				}
			}
		}
		
		String credential = null;
		TipoAutenticazione autenticazione = null;
		if(datiPD!=null){
			if(datiPD.getPd()!=null){
				autenticazione = TipoAutenticazione.toEnumConstant(datiPD.getPd().getAutenticazione());
			}
		}
		else if(datiPA!=null){
			if(datiPA.getPa()!=null){
				autenticazione = TipoAutenticazione.toEnumConstant(datiPA.getPa().getAutenticazione());
			}
		}
		if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali()!=null){
			
			if(autenticazione!=null){
				if(TipoAutenticazione.BASIC.equals(autenticazione)){
					if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername()!=null){
						credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername();
					}
				}
				else if(TipoAutenticazione.SSL.equals(autenticazione)){
					if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject()!=null){
						credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject();
					}
				}
				else if(TipoAutenticazione.PRINCIPAL.equals(autenticazione)){
					if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal()!=null){
						credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal();
					}
				}
			}
						
			if(credential == null){
				if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal()!=null){
					credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal();
				}
				else if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject()!=null){
					credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject();
				}
				else if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername()!=null){
					credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername();
				}
			}
		}
		
		List<String> roles = new ArrayList<>();
		if(checkRuoloRegistro){
			if(datiPD!=null){
				if(datiPD.getServizioApplicativo()==null){
					throw new AutorizzazioneException("Identità servizio applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
				}
				if(datiPD.getServizioApplicativo().getInvocazionePorta()!=null && 
						datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli()!=null &&
						datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli().sizeRuoloList(); i++) {
						roles.add(datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli().getRuolo(i).getNome());
					}
				}
			}
			else if(datiPA!=null){
				if(datiPA.getSoggettoFruitore()==null){
					throw new AutorizzazioneException("Identità soggetto fruitore non disponibile; tale informazione è richiesta dall'autorizzazione");
				}
				if(datiPA.getSoggettoFruitore().getRuoli()!=null &&
						datiPA.getSoggettoFruitore().getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < datiPA.getSoggettoFruitore().getRuoli().sizeRuoloList(); i++) {
						roles.add(datiPA.getSoggettoFruitore().getRuoli().getRuolo(i).getNome());
					}
				}
			}
		}
		if(checkRuoloEsterno){
			try{
				FiltroRicercaRuoli filtroRicerca = new FiltroRicercaRuoli();
				if(datiPD!=null){
					filtroRicerca.setContesto(RuoloContesto.PORTA_DELEGATA);
				}
				else if(datiPA!=null){
					filtroRicerca.setContesto(RuoloContesto.PORTA_APPLICATIVA);
				}
				filtroRicerca.setTipologia(RuoloTipologia.ESTERNO);
				List<IDRuolo> list = RegistroServiziManager.getInstance(datiInvocazione.getState()).getAllIdRuoli(filtroRicerca, null);
				if(list==null || list.size()<=0){
					throw new DriverRegistroServiziNotFound();
				}
				for (IDRuolo idRuolo : list) {
					if(httpServletRequest.isUserInRole(idRuolo.getNome())){
						roles.add(idRuolo.getNome());
					}
				}
			}
			catch(DriverRegistroServiziNotFound notFound){
				throw new AutorizzazioneException("Non sono stati registrati sulla PdD ruoli utilizzabili con un'autorizzazione esterna");
			}
			catch(Exception e){
				throw new AutorizzazioneException("E' avvenuto un errore durante la ricerca dei ruoli: "+e.getMessage(),e);
			}
		}

		if(soggettoFruitore!=null){
			String subjectId = soggettoFruitore.toString();
			if(nomeServizioApplicativo!=null){
				subjectId = nomeServizioApplicativo + "." +subjectId;
			}
			xacmlRequest.addSubject(subjectId); // namespace standard xacml
		}
		else if(credential!=null){
			String subjectId = credential;
			xacmlRequest.addSubject(subjectId); // namespace standard xacml
		}

		if(soggettoFruitore!=null){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_ORGANIZATION_ATTRIBUTE_ID, soggettoFruitore.toString());
		}
		if(nomeServizioApplicativo!=null){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_CLIENT_ATTRIBUTE_ID, nomeServizioApplicativo);
		}
		
		if(credential!=null){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_CREDENTIAL_ATTRIBUTE_ID, credential);
		}
		
		if(roles!=null && roles.size()>0){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_ROLE_ATTRIBUTE_ID, roles);
		}

		
		
		// Environment
		
		xacmlRequest.createEnvironment();
		
		
		
		// Resource
		
		//solo in caso di pdp locale inizializzo la resource __resource_id___ con il nome del servizio in modo da consentire l'identificazione della policy
		
		xacmlRequest.addResourceAttribute(CachedMapBasedSimplePolicyRepository.RESOURCE_ATTRIBUTE_ID_TO_MATCH, policyKey);
		
		
		return xacmlRequest;


	}

}
