/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.statistiche.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULL;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdFruitore;
import org.openspcoop2.core.commons.search.IdPortType;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA.DettaglioSA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPD;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;

/**
 * ConfigurazioniUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioniUtils {

	public static void fillAzioniPD(DettaglioPD dettaglioPD, org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		//		if(dettaglioPD.getPortaDelegata().getNomeAzione()==null){
		AccordoServizioParteSpecifica asps = getAspsFromValues(dettaglioPD.getPortaDelegata().getTipoSoggettoErogatore(), 
				dettaglioPD.getPortaDelegata().getNomeSoggettoErogatore(), dettaglioPD.getPortaDelegata().getTipoServizio(),
				dettaglioPD.getPortaDelegata().getNomeServizio(), dettaglioPD.getPortaDelegata().getVersioneServizio(), serviceManager);

		dettaglioPD.setIdAccordoServizioParteComune(asps.getIdAccordoServizioParteComune());
		dettaglioPD.setPortType(asps.getPortType());

		fillAzioni(dettaglioPD.getAzioni(), serviceManager, asps);
		//			}
	}

	public static ServiceBinding getServiceBindingFromValues(String tipoSoggettoErogatore, String nomeSoggettoErogatore,
			String tipoServizio, String nomeServizio, Integer versioneServizio, org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager)
					throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {
		IdAccordoServizioParteSpecifica idAPS = new IdAccordoServizioParteSpecifica();
		IdSoggetto idErogatore = new IdSoggetto();
		idErogatore.setTipo(tipoSoggettoErogatore);
		idErogatore.setNome(nomeSoggettoErogatore);
		idAPS.setIdErogatore(idErogatore);
		idAPS.setTipo(tipoServizio);
		idAPS.setNome(nomeServizio);
		idAPS.setVersione(versioneServizio);
		AccordoServizioParteSpecifica asps = serviceManager.getAccordoServizioParteSpecificaServiceSearch().get(idAPS);
		
		AccordoServizioParteComune aspc = serviceManager.getAccordoServizioParteComuneServiceSearch().get(asps.getIdAccordoServizioParteComune());
		
		return ServiceBinding.valueOf(aspc.getServiceBinding().toUpperCase());
	}
	
	private static AccordoServizioParteSpecifica getAspsFromValues(String tipoSoggettoErogatore, String nomeSoggettoErogatore,
			String tipoServizio, String nomeServizio, Integer versioneServizio, org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager)
					throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException {
		IdAccordoServizioParteSpecifica idAPS = new IdAccordoServizioParteSpecifica();
		IdSoggetto idErogatore = new IdSoggetto();
		idErogatore.setTipo(tipoSoggettoErogatore);
		idErogatore.setNome(nomeSoggettoErogatore);
		idAPS.setIdErogatore(idErogatore);
		idAPS.setTipo(tipoServizio);
		idAPS.setNome(nomeServizio);
		idAPS.setVersione(versioneServizio);
		AccordoServizioParteSpecifica asps = serviceManager.getAccordoServizioParteSpecificaServiceSearch().get(idAPS);
		return asps;
	}

	public static void fillAzioniPA(DettaglioPA dettaglioPA, org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		//		if(dettaglioPA.getPortaApplicativa().getNomeAzione()==null){
		AccordoServizioParteSpecifica asps = getAspsFromValues(dettaglioPA.getPortaApplicativa().getIdSoggetto().getTipo(),
				dettaglioPA.getPortaApplicativa().getIdSoggetto().getNome(), dettaglioPA.getPortaApplicativa().getTipoServizio(),	
				dettaglioPA.getPortaApplicativa().getNomeServizio(),  dettaglioPA.getPortaApplicativa().getVersioneServizio(), serviceManager);

		dettaglioPA.setIdAccordoServizioParteComune(asps.getIdAccordoServizioParteComune());
		dettaglioPA.setPortType(asps.getPortType());

		fillAzioni(dettaglioPA.getAzioni(), serviceManager, asps);
		//		}
	}

	private static void fillAzioni(List<String> azioni, org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager,
			AccordoServizioParteSpecifica asps) throws ServiceException, NotFoundException, MultipleResultException,
	NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		if(asps.getPortType()!=null){
			IdPortType idPT = new IdPortType();
			idPT.setIdAccordoServizioParteComune(asps.getIdAccordoServizioParteComune());
			idPT.setNome(asps.getPortType());
			PortType pt = serviceManager.getPortTypeServiceSearch().get(idPT);
			if(pt!=null){
				IPaginatedExpression expr = serviceManager.getOperationServiceSearch().newPaginatedExpression();
				expr.and();
				expr.equals(Operation.model().ID_PORT_TYPE.NOME, pt.getNome());
				expr.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, asps.getIdAccordoServizioParteComune().getNome());
				expr.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, asps.getIdAccordoServizioParteComune().getVersione());
				expr.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, asps.getIdAccordoServizioParteComune().getIdSoggetto().getTipo());
				expr.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, asps.getIdAccordoServizioParteComune().getIdSoggetto().getNome());
				List<Operation> listAz = serviceManager.getOperationServiceSearch().findAll(expr);
				for (Operation accordoServizioParteComuneAzione : listAz) {
					azioni.add(accordoServizioParteComuneAzione.getNome());
				}
			}
		}
		else{
			IPaginatedExpression expr = serviceManager.getAccordoServizioParteComuneAzioneServiceSearch().newPaginatedExpression();
			expr.and();
			expr.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, asps.getIdAccordoServizioParteComune().getNome());
			expr.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, asps.getIdAccordoServizioParteComune().getVersione());
			expr.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, asps.getIdAccordoServizioParteComune().getIdSoggetto().getTipo());
			expr.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, asps.getIdAccordoServizioParteComune().getIdSoggetto().getNome());
			List<AccordoServizioParteComuneAzione> listAz = serviceManager.getAccordoServizioParteComuneAzioneServiceSearch().findAll(expr);
			for (AccordoServizioParteComuneAzione accordoServizioParteComuneAzione : listAz) {
				azioni.add(accordoServizioParteComuneAzione.getNome());
			}
		}
	}

	public static void fillFruitori( String tipoSoggettoErogatore, String nomeSoggettoErogatore, 
			String tipoServizio, String nomeServizio,
			List<String> fruitori, org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		IPaginatedExpression expr = serviceManager.getFruitoreServiceSearch().newPaginatedExpression();
		expr.and();
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO, tipoSoggettoErogatore);
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME, nomeSoggettoErogatore);
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, tipoServizio);
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME, nomeServizio);
		List<IdFruitore> ids = serviceManager.getFruitoreServiceSearch().findAllIds(expr);
		for (IdFruitore idFruitore : ids) {
			fruitori.add(idFruitore.getIdFruitore().getTipo()+"/"+idFruitore.getIdFruitore().getNome());
		}
	}

	public static Connettore getConnettore( 
			String tipoFruitore, String nomeFruitore, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, 
			String tipoServizio, String nomeServizio, Integer versioneServizio,
			DriverRegistroServiziDB driverRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {

		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, tipoSoggettoErogatore, nomeSoggettoErogatore, versioneServizio);
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica aspsOp2 = driverRegistro.getAccordoServizioParteSpecifica(idServizio);
		for (org.openspcoop2.core.registry.Fruitore frOp2 : aspsOp2.getFruitoreList()) {
			if(frOp2.getTipo().equals(tipoFruitore) && frOp2.getNome().equals(nomeFruitore)){
				if(frOp2.getConnettore()!=null && !TipiConnettore.DISABILITATO.getNome().equals(frOp2.getConnettore().getTipo())){
					return frOp2.getConnettore().mappingIntoConnettoreConfigurazione();
				}
				break;
			}
		}

		if(aspsOp2.getConfigurazioneServizio() !=null && aspsOp2.getConfigurazioneServizio().getConnettore() !=null && !TipiConnettore.DISABILITATO.getNome().equals(aspsOp2.getConfigurazioneServizio().getConnettore().getTipo())){
			return aspsOp2.getConfigurazioneServizio().getConnettore().mappingIntoConnettoreConfigurazione();
		}

		return driverRegistro.getSoggetto(idServizio.getSoggettoErogatore()).getConnettore().mappingIntoConnettoreConfigurazione();
	}

	public static List<Property> printConnettore(Connettore connettore,String labelTipoConnettore ,InvocazioneCredenziali invCredenziali){
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();
		p.setId(idx++); 
		p.setNome(labelTipoConnettore); p.setValore(connettore.getTipo());
		lst.add(p);

		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_URL); p.setValore(getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList()));
			lst.add(p);
			if(invCredenziali!=null){
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_HTTP_BASIC_USERNAME); p.setValore(invCredenziali.getUser());
				lst.add(p);
			}
			else{
				String username = getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
				if(username!=null){
					p = new Property();
					p.setId(idx++);
					p.setNome(CostantiConfigurazioni.LABEL_HTTP_BASIC_USERNAME); p.setValore(username);
					lst.add(p);
				}
			}

			if(TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_SSL_TYPE); p.setValore(getProperty(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE, connettore.getPropertyList()));
				lst.add(p);
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_HOSTNAME_VERIFIER); p.setValore(getProperty(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER, connettore.getPropertyList()));
				lst.add(p);
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_TRUST_STORE + " ("+getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE, connettore.getPropertyList())+")");
				p.setValore(getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, connettore.getPropertyList()));
				lst.add(p);
				boolean invioCertificatoClient = false;
				String cert = getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION, connettore.getPropertyList());
				if(cert!=null){
					invioCertificatoClient = true;
				}
				//System.out.println("Client Certificate: "+invioCertificatoClient);
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_CLIENT_CERTIFICATE); p.setValore(invioCertificatoClient + "");
				lst.add(p);

				if(invioCertificatoClient){
					p = new Property();
					p.setId(idx++);
					p.setNome(CostantiConfigurazioni.LABEL_KEY_STORE + " ("+getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE, connettore.getPropertyList()) +")"); 
					p.setValore(getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION, connettore.getPropertyList()));
					lst.add(p);
				}
			}

			String proxy = getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE, connettore.getPropertyList());
			if(proxy!=null){
				p = new Property();
				p.setNome(CostantiConfigurazioni.LABEL_PROXY); 
				p.setId(idx++);
				p.setValore(getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME, connettore.getPropertyList())+CostantiConfigurazioni.LABEL_DOTS+
						getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT, connettore.getPropertyList()));
				lst.add(p);

				String username = getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME, connettore.getPropertyList());
				if(username!=null){
					p = new Property();
					p.setId(idx++);
					p.setNome(CostantiConfigurazioni.LABEL_PROXY_USERNAME); p.setValore(username);
					lst.add(p);
				}
			}
		}
		else if(TipiConnettore.JMS.getNome().equals(connettore.getTipo())){
			String tipoCoda = getProperty(CostantiConnettori.CONNETTORE_JMS_TIPO, connettore.getPropertyList());
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_NOME + " ("+tipoCoda+")"); p.setValore(getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList()));
			lst.add(p);
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_CONNECTION_FACTORY); p.setValore(getProperty(CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY, connettore.getPropertyList()));
			lst.add(p);
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_SEND_AS); p.setValore(getProperty(CostantiConnettori.CONNETTORE_JMS_SEND_AS, connettore.getPropertyList()));
			lst.add(p);

			if(invCredenziali!=null){
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_USERNAME); p.setValore(invCredenziali.getUser());
				lst.add(p);
			}
			else{
				String username = getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
				if(username!=null){
					p = new Property();
					p.setId(idx++);
					p.setNome(CostantiConfigurazioni.LABEL_USERNAME); p.setValore(username);
					lst.add(p);
				}
			}
		}
		else if(TipiConnettore.FILE.getNome().equals(connettore.getTipo())){
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_OUTPUT_FILE); p.setValore(getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, connettore.getPropertyList()));
			lst.add(p);
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_OUTPUT_FILE_HEADER); p.setValore(getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, connettore.getPropertyList()));
			lst.add(p);
			String risposta = getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE, connettore.getPropertyList());
			if(risposta!=null){
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_INPUT_FILE); p.setValore(getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE, connettore.getPropertyList()));
				lst.add(p);
				p = new Property();
				p.setId(idx++);
				p.setNome(CostantiConfigurazioni.LABEL_INPUT_FILE_HEADER); p.setValore(getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS, connettore.getPropertyList()));
				lst.add(p);
			}
		}
		else if(TipiConnettore.NULL.getNome().equals(connettore.getTipo())){
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_URL); p.setValore(ConnettoreNULL.LOCATION);
			lst.add(p);
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettore.getTipo())){
			p = new Property();
			p.setId(idx++);
			p.setNome(CostantiConfigurazioni.LABEL_URL); p.setValore(ConnettoreNULLEcho.LOCATION);
			lst.add(p);
		}
		else{
			List<Property> list = connettore.getPropertyList();
			if(list!=null && list.size()>0){
				for (Property property : list) {
					p = new Property();
					p.setId(idx++);
					p.setNome(property.getNome()); p.setValore(property.getValore());
					lst.add(p);
				}
			}
		}
		String debug = "false";
		if(getProperty(CostantiConnettori.CONNETTORE_DEBUG, connettore.getPropertyList())!=null){
			debug = getProperty(CostantiConnettori.CONNETTORE_DEBUG, connettore.getPropertyList());
		}
		p = new Property();
		p.setId(idx++);
		p.setNome(CostantiConfigurazioni.LABEL_DEBUG); p.setValore(debug);
		lst.add(p);

		return lst;
	}

	public static String getProperty(String nome,List<Property> list){
		if(list!=null && list.size()>0){
			for (Property property : list) {
				if(property.getNome().equals(nome)){
					return property.getValore();
				}
			}
		}
		return null;
	}


	public static List<Property> getPropertiesAutenticazionePD(DettaglioPD dettaglioPD){
		org.openspcoop2.core.config.PortaDelegata pdOp2 = dettaglioPD.getPortaDelegataOp2();
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();

		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(pdOp2.getAutenticazione())){
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(CostantiConfigurazione.DISABILITATO.getValue());
			lst.add(p);
		}
		else{
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(pdOp2.getAutenticazione());
			lst.add(p);
		}
		if(CostantiConfigurazione.ABILITATO.equals(pdOp2.getAutenticazioneOpzionale())){
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_OPZIONALE); p.setValore(CostantiConfigurazione.ABILITATO.getValue());
			lst.add(p);
		}
		else{
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_OPZIONALE); p.setValore(CostantiConfigurazione.DISABILITATO.getValue());
			lst.add(p);
		}

		return lst;
	}


	public static List<Property> getPropertiesAutorizzazionePD(DettaglioPD dettaglioPD,IDPortaDelegata idPD, DriverConfigurazioneDB driverConfigDB ,DriverRegistroServiziDB driverRegistroDB) 
			throws DriverConfigurazioneException, DriverConfigurazioneNotFound, DriverRegistroServiziException, DriverRegistroServiziNotFound{
		org.openspcoop2.core.config.PortaDelegata pdOp2 = dettaglioPD.getPortaDelegataOp2();
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();

		// Autorizzazione
		// Tipo: disabilitato/abilitato/xacmlPolicy/NomeCustom
		String autorizzazione = pdOp2.getAutorizzazione();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(CostantiConfigurazione.DISABILITATO.getValue());
			lst.add(p);
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(TipoAutorizzazione.XACML_POLICY.getValue());
			lst.add(p);
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
				||
				autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			String valore = "";
			if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase()) && autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
				valore = CostantiConfigurazione.ABILITATO.getValue() + " ("+ CostantiConfigurazioni.LABEL_APPLICATIVI_RUOLI_AUTORIZZATI +")"; 
			} else {
				if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase()))
					valore = CostantiConfigurazione.ABILITATO.getValue() + " ("+ CostantiConfigurazioni.LABEL_RUOLI +")";
				else
					valore = CostantiConfigurazione.ABILITATO.getValue() + " ("+ CostantiConfigurazioni.LABEL_APPLICATIVI_AUTORIZZATI +")";
			} 
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(valore);
			lst.add(p);
		}
		else{
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(autorizzazione);
			lst.add(p);
		}

		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1 (user:xxx)
		//                                  sa2 (user:xxx)
		//                                  sa3 (user:xxx)
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			List<String> sa = new ArrayList<>();
			boolean first = true;
			for (PortaDelegataServizioApplicativo pdSA : pdOp2.getServizioApplicativoList()) {
				p = new Property();
				p.setId(idx++); 
				if(first)
					p.setNome(CostantiConfigurazioni.LABEL_APPLICATIVI_AUTORIZZATI);
				else 
					p.setNome("");
				first = false;

				String saNome = pdSA.getNome();
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(idPD.getIdentificativiFruizione().getSoggettoFruitore());
				idServizioApplicativo.setNome(saNome);
				ServizioApplicativo saOp2 = driverConfigDB.getServizioApplicativo(idServizioApplicativo);
				if(saOp2.getInvocazionePorta()!=null && saOp2.getInvocazionePorta().sizeCredenzialiList()>0){
					String credenziale = null;
					Credenziali cr = saOp2.getInvocazionePorta().getCredenziali(0);
					switch (cr.getTipo()) {
					case BASIC:
						credenziale = CostantiConfigurazioni.VALUE_USER + CostantiConfigurazioni.LABEL_DOTS+cr.getUser();
						break;
					case APIKEY:
						credenziale = CostantiConfigurazioni.VALUE_APP_ID + CostantiConfigurazioni.LABEL_DOTS+cr.getUser();
						break;
					case SSL:
						credenziale = CostantiConfigurazioni.VALUE_SUBJECT + CostantiConfigurazioni.LABEL_DOTS+cr.getSubject();
						break;
					case PRINCIPAL:
						credenziale = CostantiConfigurazioni.VALUE_PRINCIPAL + CostantiConfigurazioni.LABEL_DOTS+cr.getUser();
						break;
					}

					sa.add(saNome+" ("+credenziale+")");
					p.setValore(saNome+" ("+credenziale+")");
				}
				else{
					sa.add(saNome);
					p.setValore(saNome);
				}


				lst.add(p);
			}
			if(sa.size() == 0) {
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_APPLICATIVI_AUTORIZZATI);
				p.setValore("-");
				lst.add(p);
			}
			dettaglioPD.setSa(sa);
		}

		// Ruoli: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			List<String> ruoli = new ArrayList<>();
			String match = "-";
			if(pdOp2.getRuoli()!=null){
				boolean first = true;
				if( pdOp2.getRuoli().getMatch()!=null){
					switch (pdOp2.getRuoli().getMatch()) {
					case ALL:
						match = CostantiConfigurazioni.LABEL_RUOLI_ALL;
						break;
					case ANY:
						match = CostantiConfigurazioni.LABEL_RUOLI_ANY;
						break;
					}
				}
				for (Ruolo pdRuolo : pdOp2.getRuoli().getRuoloList()) {
					p = new Property();
					p.setId(idx++); 
					if(first)
						p.setNome(CostantiConfigurazioni.LABEL_RUOLI+" ("+match+")");
					else 
						p.setNome("");
					first = false;

					String ruoloName = pdRuolo.getNome();
					IDRuolo idRuolo = new IDRuolo(ruoloName);
					org.openspcoop2.core.registry.Ruolo ruoloReg = driverRegistroDB.getRuolo(idRuolo);
					String fonte = null;
					switch (ruoloReg.getTipologia()) {
					case INTERNO:
						fonte = CostantiConfigurazioni.VALUE_FONTE_INTERNA;
						break;
					case ESTERNO:
						fonte = CostantiConfigurazioni.VALUE_FONTE_ESTERNA;
						break;
					case QUALSIASI:
						fonte = CostantiConfigurazioni.VALUE_FONTE_QUALISIASI;
						break;
					}
					ruoli.add(ruoloName+" ("+fonte+")");
					p.setValore(ruoloName+" ("+fonte+")");
					lst.add(p);
				}
			}
			dettaglioPD.setRuoli(ruoli);
			dettaglioPD.setMatchRuoli(match);

			if(ruoli.size() == 0) {
				p = new Property();
				p.setId(idx++); 
				if(StringUtils.isEmpty(match))
					p.setNome(CostantiConfigurazioni.LABEL_RUOLI);
				else 
					p.setNome(CostantiConfigurazioni.LABEL_RUOLI+" ("+match+")");
				p.setValore("-");
				lst.add(p);
			}
		}  

		return lst;
	}

	public static List<Property> getPropertiesIntegrazionePD(DettaglioPD dettaglioPD){
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();
		
		PortaDelegataAzione pdAzione = dettaglioPD.getPortaDelegataOp2().getAzione();
		if(dettaglioPD.getPortaDelegata().getNomeAzione()!=null &&
				pdAzione!=null &&
				(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC.equals(pdAzione.getIdentificazione()))){
			// Azione: _XXX
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_AZIONE); p.setValore(dettaglioPD.getPortaDelegata().getNomeAzione());
			lst.add(p);

			// URL di Invocazione: (Endpoint Applicativo PD)/PD/SPCEnte/SPCMinistero/SPCAnagrafica
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE); p.setValore(dettaglioPD.getUrlInvocazione());
			lst.add(p);
		}
		else{

			List<String> azioni = dettaglioPD.getAzioni();
			
			if(pdAzione==null && (azioni == null || azioni.size() == 0)){
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE); p.setValore("");
				lst.add(p);
			}
			else{

				// Azioni: XXXs
				//System.out.println("Azioni: ["+dettaglioPD.getAzioni()+"]");

				if(azioni != null && azioni.size() > 0){
					boolean first = true;
					for (String azione : azioni) {
						p = new Property();
						p.setId(idx++); 
						if(first)
							p.setNome(CostantiConfigurazioni.LABEL_AZIONI);
						else 
							p.setNome("");
						first = false;

						p.setValore(azione);
						lst.add(p);
					}
				}

				// URL di Base: (Endpoint Applicativo PD)/PD/SPCEnte/SPCMinistero/SPCAnagrafica
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE); p.setValore(dettaglioPD.getUrlInvocazione());
				lst.add(p);

				// Identificazione Azione:  urlBased/wsdlBased
				String suffix = "";
				if(pdAzione!= null && CostantiConfigurazione.ABILITATO.equals(pdAzione.getForceInterfaceBased())){
					suffix = "/"+CostantiConfigurazione.PORTA_DELEGATA_AZIONE_WSDL_BASED.getValue();
				}
				if(pdAzione!= null){
					p = new Property();
					p.setId(idx++); 
					p.setNome(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE); p.setValore(pdAzione.getIdentificazione().getValue()+suffix);
					lst.add(p);
				}

				if(pdAzione!= null && CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(pdAzione.getIdentificazione())){
					// Expressione XPath: _XXX
					p = new Property();
					p.setId(idx++); 
					p.setNome(CostantiConfigurazioni.LABEL_EXPRESSIONE_X_PATH); p.setValore(pdAzione.getPattern());
					lst.add(p);
				}
				else if(pdAzione!= null && CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED.equals(pdAzione.getIdentificazione())){

					String exprDefault = ".*"+dettaglioPD.getPortaDelegata().getNome()+"/([^/|^?]*).*";
					if(exprDefault.equals(pdAzione.getPattern())==false){
						// Expressione Regolare: _XXX
						p = new Property();
						p.setId(idx++); 
						p.setNome(CostantiConfigurazioni.LABEL_EXPRESSIONE_REGOLARE); p.setValore(pdAzione.getPattern());
						lst.add(p);
					}
				}
			}
		}

		return lst;
	}
	
	public static List<Property> getPropertiesIntegrazionePA(DettaglioPA dettaglioPA){
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();
		
		if(dettaglioPA.getPortaApplicativa().getNomeAzione()!=null){
			// Azione: _XXX
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_AZIONE); p.setValore(dettaglioPA.getPortaApplicativa().getNomeAzione());
			lst.add(p);

			// URL di Invocazione: (Endpoint Applicativo PD)/PA/SPCEnte/SPCMinistero/SPCAnagrafica
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE); p.setValore(dettaglioPA.getUrlInvocazione());
			lst.add(p);
		}
		else{

			List<String> azioni = dettaglioPA.getAzioni();
			// Azioni: XXXs
			//System.out.println("Azioni: ["+dettaglioPD.getAzioni()+"]");

			if(azioni != null && azioni.size() > 0){
				boolean first = true;
				for (String azione : azioni) {
					p = new Property();
					p.setId(idx++); 
					if(first)
						p.setNome(CostantiConfigurazioni.LABEL_AZIONI);
					else 
						p.setNome("");
					first = false;

					p.setValore(azione);
					lst.add(p);
				}
			}

			// URL di Base: (Endpoint Applicativo PD)/PA/SPCEnte/SPCMinistero/SPCAnagrafica
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE); p.setValore(dettaglioPA.getUrlInvocazione());
			lst.add(p);
			
			String value = CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED.getValue()+"/"+CostantiConfigurazione.PORTA_DELEGATA_AZIONE_WSDL_BASED.getValue();
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE); p.setValore(value);
			lst.add(p);

		}

		return lst;
	}


	public static List<Property> getPropertiesGeneraliPA(DettaglioPA dettaglioPA) throws DriverRegistroServiziException{
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();
		PortaApplicativa portaApplicativa = dettaglioPA.getPortaApplicativa();
		org.openspcoop2.core.config.PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 

		// Nome PA: SPC/_XXX
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_PORTA_APPLICATIVA); p.setValore(portaApplicativa.getNome());
		lst.add(p);
		
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(portaApplicativa.getStato());
		lst.add(p);

		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_ASPC);
		IdAccordoServizioParteComune aspc = dettaglioPA.getIdAccordoServizioParteComune();
		String nomeAspc = aspc.getNome();

		Integer versioneAspc = aspc.getVersione();

		String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

		String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;

		p.setValore(IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc));
		lst.add(p);

		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_PORT_TYPE);
		p.setValore(dettaglioPA.getPortType());
		lst.add(p);

		// Erogatore: SPC/_XXX
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_EROGATORE); p.setValore(portaApplicativa.getIdSoggetto().getTipo()+"/"+portaApplicativa.getIdSoggetto().getNome());
		lst.add(p);

		// Servizio: SPC/_XXX
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_SERVIZIO); p.setValore(portaApplicativa.getTipoServizio()+"/"+portaApplicativa.getNomeServizio());
		lst.add(p);

		if(!dettaglioPA.isTrasparente()) {
			if(portaApplicativa.getNomeAzione()!=null){
				// Azione: _XXX
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_AZIONE); p.setValore(portaApplicativa.getNomeAzione());
				lst.add(p);
			}
			else{
				PortaApplicativaAzione paAzione = paOp2.getAzione();
				List<String> azioni = dettaglioPA.getAzioni(); 
				if(paAzione==null && (azioni == null || azioni.size() == 0)){
					p = new Property();
					p.setId(idx++); 
					p.setNome(CostantiConfigurazioni.LABEL_AZIONE); p.setValore(CostantiConfigurazioni.LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE);
					lst.add(p);
				}
				else{
					// Azioni: XXXs
					if(azioni != null && azioni.size() > 0){
						boolean first = true;
						for (String azione : azioni) {
							p = new Property();
							p.setId(idx++); 
							if(first)
								p.setNome(CostantiConfigurazioni.LABEL_AZIONI);
							else 
								p.setNome("");
							first = false;
	
							p.setValore(azione);
							lst.add(p);
						}
					}
				}
			}
		}
		return lst;
	}

	public static List<Property> getPropertiesGeneraliPD(DettaglioPD dettaglioPD) throws DriverRegistroServiziException{
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();
		PortaDelegata portaDelegata = dettaglioPD.getPortaDelegata();

		// Nome PA: SPC/_XXX
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_PORTA_DELEGATA); p.setValore(portaDelegata.getNome());
		lst.add(p);
		
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(portaDelegata.getStato());
		lst.add(p);

		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_ASPC);
		IdAccordoServizioParteComune aspc = dettaglioPD.getIdAccordoServizioParteComune();
		String nomeAspc = aspc.getNome();

		Integer versioneAspc = aspc.getVersione();

		String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

		String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;

		p.setValore(IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc));
		lst.add(p);

		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_PORT_TYPE);
		p.setValore(dettaglioPD.getPortType());
		lst.add(p);

		// Fruitore
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_FRUITORE);
		p.setValore(portaDelegata.getIdSoggetto().getTipo()+CostantiConfigurazioni.SEPARATORE_TIPONOME+portaDelegata.getIdSoggetto().getNome());
		lst.add(p);

		// Erogatore: SPC/_XXX
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_EROGATORE); p.setValore(portaDelegata.getIdSoggetto().getTipo()+"/"+portaDelegata.getIdSoggetto().getNome());
		lst.add(p);

		// Servizio: SPC/_XXX
		p = new Property();
		p.setId(idx++); 
		p.setNome(CostantiConfigurazioni.LABEL_SERVIZIO); p.setValore(portaDelegata.getTipoServizio()+"/"+portaDelegata.getNomeServizio());
		lst.add(p);

		return lst;
	}

	public static List<Property> getPropertiesAutenticazionePA(DettaglioPA dettaglioPA){
		org.openspcoop2.core.config.PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();

		if(dettaglioPA.isSupportatoAutenticazione()){
			if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(paOp2.getAutenticazione())){
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(CostantiConfigurazione.DISABILITATO.getValue());
				lst.add(p);
			}
			else{
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(paOp2.getAutenticazione());
				lst.add(p);
			}
			if(CostantiConfigurazione.ABILITATO.equals(paOp2.getAutenticazioneOpzionale())){
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_OPZIONALE); p.setValore(CostantiConfigurazione.ABILITATO.getValue());
				lst.add(p);
			}
			else{
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_OPZIONALE); p.setValore(CostantiConfigurazione.DISABILITATO.getValue());
				lst.add(p);
			}
		}
		return lst;
	}

	public static List<Property> getPropertiesAutorizzazionePA(DettaglioPA dettaglioPA,org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager,DriverRegistroServiziDB driverRegistroDB) 
			throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, DriverRegistroServiziException, DriverRegistroServiziNotFound{
		PortaApplicativa portaApplicativa = dettaglioPA.getPortaApplicativa();
		org.openspcoop2.core.config.PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 
		List<Property> lst = new ArrayList<Property>();
		long idx = 0;
		Property p = new Property();

		// Autorizzazione
		// Tipo: disabilitato/abilitato/xacmlPolicy/NomeCustom
		String autorizzazione = paOp2.getAutorizzazione();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(CostantiConfigurazione.DISABILITATO.getValue());
			lst.add(p);
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(TipoAutorizzazione.XACML_POLICY.getValue());
			lst.add(p);
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
				||
				autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){

			String valore = "";
			if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase()) && autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
				valore = CostantiConfigurazione.ABILITATO.getValue() + " ("+ CostantiConfigurazioni.LABEL_RUOLI_SOGGETTI_AUTORIZZATI +")"; 
			} else {
				if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase()))
					valore = CostantiConfigurazione.ABILITATO.getValue() + " ("+ CostantiConfigurazioni.LABEL_RUOLI +")";
				else
					valore = CostantiConfigurazione.ABILITATO.getValue() + " ("+ CostantiConfigurazioni.LABEL_SOGGETTI_AUTORIZZATI +")";
			} 

			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(valore);
			lst.add(p);
		}
		else{
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_STATO); p.setValore(autorizzazione);
			lst.add(p);
		}

		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1 (user:xxx)
		//                                  sa2 (user:xxx)
		//                                  sa3 (user:xxx)
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			List<String> fruitori = new ArrayList<>();
			fillFruitori(portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), 
					portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(), 
					fruitori, utilsServiceManager);

			dettaglioPA.setFruitori(fruitori);

			if(fruitori != null && fruitori.size() > 0){
				boolean first = true;
				for (String fruitore : fruitori) {
					p = new Property();
					p.setId(idx++); 
					if(first)
						p.setNome(CostantiConfigurazioni.LABEL_SOGGETTI_AUTORIZZATI);
					else 
						p.setNome("");
					first = false;

					p.setValore(fruitore);
					lst.add(p);
				}
			} else {
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_SOGGETTI_AUTORIZZATI);
				p.setValore("-");
				lst.add(p);
			}

		}

		// Ruoli: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			List<String> ruoli = new ArrayList<>();
			String match = "-";
			if(paOp2.getRuoli()!=null){
				if( paOp2.getRuoli().getMatch()!=null){
					switch (paOp2.getRuoli().getMatch()) {
					case ALL:
						match = CostantiConfigurazioni.LABEL_RUOLI_ALL;
						break;
					case ANY:
						match = CostantiConfigurazioni.LABEL_RUOLI_ANY;
						break;
					}
				}
				boolean first = true;
				for (Ruolo pdRuolo : paOp2.getRuoli().getRuoloList()) {
					p = new Property();
					p.setId(idx++); 
					if(first)
						p.setNome(CostantiConfigurazioni.LABEL_RUOLI+" ("+match+")");
					else 
						p.setNome("");
					first = false;

					String ruoloName = pdRuolo.getNome();
					IDRuolo idRuolo = new IDRuolo(ruoloName);
					org.openspcoop2.core.registry.Ruolo ruoloReg = driverRegistroDB.getRuolo(idRuolo);
					String fonte = null;
					switch (ruoloReg.getTipologia()) {
					case INTERNO:
						fonte = CostantiConfigurazioni.VALUE_FONTE_INTERNA;
						break;
					case ESTERNO:
						fonte = CostantiConfigurazioni.VALUE_FONTE_ESTERNA;
						break;
					case QUALSIASI:
						fonte = CostantiConfigurazioni.VALUE_FONTE_QUALISIASI;
						break;
					}
					ruoli.add(ruoloName+" ("+fonte+")");
					p.setValore(ruoloName+" ("+fonte+")");
					lst.add(p);
				}
			}
			dettaglioPA.setRuoli(ruoli);
			dettaglioPA.setMatchRuoli(match);
			
			if(ruoli.size() == 0) {
				p = new Property();
				p.setId(idx++); 
				if(StringUtils.isEmpty(match))
					p.setNome(CostantiConfigurazioni.LABEL_RUOLI);
				else 
					p.setNome(CostantiConfigurazioni.LABEL_RUOLI+" ("+match+")");
				p.setValore("-");
				lst.add(p);
			}
		}
		return lst;
	}


	public static List<DettaglioSA> getPropertiesServiziApplicativiPA(DettaglioPA dettaglioPA,
			DriverConfigurazioneDB driverConfigDB, IDPortaApplicativa idPA ) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		org.openspcoop2.core.config.PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2();

		List<DettaglioSA> listaSA = new ArrayList<DettaglioSA>();
		// --- Informazioni di Integrazione ---
		for (PortaApplicativaServizioApplicativo saOp : paOp2.getServizioApplicativoList()) {
			DettaglioSA sa = new DettaglioPA().new DettaglioSA();


			List<Property> lstPropertySA = new ArrayList<Property>();
			long idx = 0;
			Property p = new Property();

			String saNome = saOp.getNome();
			p = new Property();
			p.setId(idx++); 
			p.setNome(CostantiConfigurazioni.LABEL_SERVIZIO_APPLICATIVO); p.setValore(saOp.getNome());
			lstPropertySA.add(p);

			IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
			idServizioApplicativo.setIdSoggettoProprietario(idPA.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore());
			idServizioApplicativo.setNome(saNome);
			ServizioApplicativo saOp2 = driverConfigDB.getServizioApplicativo(idServizioApplicativo);
			sa.setSaOp2(saOp2);

			if(saOp2.getInvocazioneServizio()!=null){
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_MESSAGE_BOX); p.setValore(saOp2.getInvocazioneServizio().getGetMessage().getValue());
				lstPropertySA.add(p);
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_SBUSTAMENTO_SOAP); p.setValore(saOp2.getInvocazioneServizio().getSbustamentoSoap().getValue());
				lstPropertySA.add(p);
				p = new Property();
				p.setId(idx++); 
				p.setNome(CostantiConfigurazioni.LABEL_SBUSTAMENTO_PROTOCOLLO); p.setValore(saOp2.getInvocazioneServizio().getSbustamentoInformazioniProtocollo().getValue());
				lstPropertySA.add(p);

				if(saOp2.getInvocazioneServizio().getConnettore()!=null){
					Connettore connettore = saOp2.getInvocazioneServizio().getConnettore();
					sa.setPropertyConnettore(printConnettore(connettore, CostantiConfigurazioni.LABEL_TIPO, saOp2.getInvocazioneServizio().getCredenziali()));
				}

			}

			sa.setPropertySA(lstPropertySA); 

			listaSA.add(sa);
		}

		return listaSA; 
	}
}
