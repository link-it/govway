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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneServizioCompostoSintetico;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.beans.ResourceSintetica;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiSinteticiDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_accordiSinteticiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico getAccordoServizioParteComuneSintetico(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// conrollo consistenza
		if (idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComuneSintetico] Parametro idAccordo is null");
		if (idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComuneSintetico] Parametro idAccordo.getNome is null");
		if (idAccordo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getAccordoServizioParteComuneSintetico] Parametro idAccordo.getNome non e' definito");

		this.driver.logDebug("richiesto getAccordoServizioParteComuneSintetico: " + idAccordo.toString());

		long idAccordoLong = -1;
		Connection con = null;
		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoServizioParteComuneSintetico(idAccordo)");
			else
				con = this.driver.globalConnection;
			
			idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.driver.tipoDB);
			if(idAccordoLong<=0){
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizioParteComuneSintetico] Accordo non trovato (id:"+idAccordo+")");
			}
		}
		catch (DriverRegistroServiziNotFound se) {
			throw se;
		}
		catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteComuneSintetico] Exception :" + se.getMessage(),se);
		} finally {

			this.driver.closeConnection(con);

		}
		return getAccordoServizioParteComuneSintetico(idAccordoLong);
	}	
	
	protected org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico getAccordoServizioParteComuneSintetico(long idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComuneSintetico(idAccordo, null);
	}
			
	protected org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico getAccordoServizioParteComuneSintetico(long idAccordo, Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
			
		org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico accordoServizio = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			if(conParam==null) {
				// prendo la connessione dal pool
				if (this.driver.atomica)
					con = this.driver.getConnectionFromDatasource("getAccordoServizioParteComuneSintetico(idAccordoLong)");
				else
					con = this.driver.globalConnection;
			}
			else {
				con = conParam;
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"id");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"descrizione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"service_binding");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"profilo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"formato_specifica");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"wsdl_definitorio");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"wsdl_concettuale");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"wsdl_logico_erogatore");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"wsdl_logico_fruitore");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"spec_conv_concettuale");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"spec_conv_erogatore");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"spec_conv_fruitore");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"utilizzo_senza_azione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"superuser");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"privato");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"ora_registrazione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"id_referente");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI,"tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI,"nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"versione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"stato");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"canale");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idAccordo);


			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));
			rs = stm.executeQuery();

			if (rs.next()) {
				accordoServizio = new org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico();

				accordoServizio.setId(rs.getLong("id"));

				String tmp = rs.getString("nome");
				// se tmp==null oppure tmp=="" then setNome(null) else
				// setNome(tmp)
				accordoServizio.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				accordoServizio.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("service_binding");
				accordoServizio.setServiceBinding(DriverRegistroServiziDB_LIB.getEnumServiceBinding((tmp == null || tmp.equals("")) ? null : tmp));
				
				// controllare i vari casi di profcoll (one-way....)
				tmp = rs.getString("profilo_collaborazione");
				accordoServizio.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione((tmp == null || tmp.equals("")) ? null : tmp));
				if(accordoServizio.getProfiloCollaborazione()==null){
					// puo' essere null se e' stato ridefinito nei port type e nelle operation
					// inserisco comunque un default (usato anche nelle interfacce)
					accordoServizio.setProfiloCollaborazione(ProfiloCollaborazione.ONEWAY);
				}

				tmp = rs.getString("formato_specifica");
				accordoServizio.setFormatoSpecifica(DriverRegistroServiziDB_LIB.getEnumFormatoSpecifica((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("wsdl_definitorio");
				accordoServizio.setByteWsdlDefinitorio(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_concettuale");
				accordoServizio.setByteWsdlConcettuale(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_logico_erogatore");
				accordoServizio.setByteWsdlLogicoErogatore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_logico_fruitore");
				accordoServizio.setByteWsdlLogicoFruitore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_concettuale");
				accordoServizio.setByteSpecificaConversazioneConcettuale(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_erogatore");
				accordoServizio.setByteSpecificaConversazioneErogatore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_fruitore");
				accordoServizio.setByteSpecificaConversazioneFruitore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				accordoServizio.setUtilizzoSenzaAzione(rs.getInt("utilizzo_senza_azione") == CostantiDB.TRUE ? true : false);

				tmp = rs.getString("superuser");
				accordoServizio.setSuperUser(((tmp == null || tmp.equals("")) ? null : tmp));

				if(rs.getInt("privato")==1)
					accordoServizio.setPrivato(true);
				else
					accordoServizio.setPrivato(false);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					accordoServizio.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// Soggetto referente
				if(rs.getLong("id_referente")>0) {
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(rs.getString("tipo_soggetto"));
					assr.setNome(rs.getString("nome_soggetto"));
					assr.setId(rs.getLong("id_referente"));
					accordoServizio.setSoggettoReferente(assr);
				}

				//Versione
				if(rs.getString("versione")!=null && !"".equals(rs.getString("versione")))
					accordoServizio.setVersione(rs.getInt("versione"));

				// Stato
				tmp = rs.getString("stato");
				accordoServizio.setStatoPackage(((tmp == null || tmp.equals("")) ? null : tmp));

				// Canali
				String canale = rs.getString("canale");
				accordoServizio.setCanale(canale);
				
				rs.close();
				stm.close();


				// Aggiungo azione

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("correlata");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordo);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));

				rs = stm.executeQuery();

				org.openspcoop2.core.registry.beans.AzioneSintetica azione = null;
				while (rs.next()) {

					azione = new org.openspcoop2.core.registry.beans.AzioneSintetica();

					tmp = rs.getString("nome");
					azione.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("profilo_collaborazione");
					azione.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("correlata");
					azione.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("profilo_azione");
					if (tmp == null || tmp.equals(""))
						azione.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
					else
						azione.setProfAzione(tmp);

					long idAzione = rs.getLong("id");
					azione.setId(idAzione);

					accordoServizio.getAzione().add(azione);

				}
				rs.close();
				stm.close();


				// read port type
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("profilo_pt");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordo);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));
				rs = stm.executeQuery();

				while (rs.next()) {

					PortTypeSintetico pt = new PortTypeSintetico();

					tmp = rs.getString("nome");
					pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("descrizione");
					pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("profilo_collaborazione");
					pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("profilo_pt");
					if (tmp == null || tmp.equals(""))
						pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
					else
						pt.setProfiloPT(tmp);

					pt.setIdAccordo(idAccordo);

					long idPortType = rs.getLong("id");
					pt.setId(idPortType);
					
					accordoServizio.getPortType().add(pt);

				}
				rs.close();
				stm.close();

				// port type azioni
				if(!accordoServizio.getPortType().isEmpty()) {
					for (PortTypeSintetico pt : accordoServizio.getPortType()) {
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addSelectField("profilo_collaborazione");
						sqlQueryObject.addSelectField("correlata_servizio");
						sqlQueryObject.addSelectField("correlata");
						sqlQueryObject.addSelectField("profilo_pt_azione");
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("id_port_type = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addOrderBy("nome");
						sqlQueryObject.setSortType(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, pt.getId());

						this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, pt.getId()));

						rs = stm.executeQuery();

						org.openspcoop2.core.registry.beans.OperationSintetica azionePT = null;
						while (rs.next()) {

							azionePT = new org.openspcoop2.core.registry.beans.OperationSintetica();

							tmp = rs.getString("nome");
							azionePT.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

							tmp = rs.getString("profilo_collaborazione");
							azionePT.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

							tmp = rs.getString("correlata_servizio");
							azionePT.setCorrelataServizio(((tmp == null || tmp.equals("")) ? null : tmp));

							tmp = rs.getString("correlata");
							azionePT.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));

							tmp = rs.getString("profilo_pt_azione");
							if (tmp == null || tmp.equals(""))
								azionePT.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
							else
								azionePT.setProfAzione(tmp);

							azionePT.setIdPortType(pt.getId());

							long idAzionePortType = rs.getLong("id");
							azionePT.setId(idAzionePortType);
							
							pt.getAzione().add(azionePT);

						}
						rs.close();
						stm.close();
						
					}
				}
				
				
				// read resources
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("http_method");
				sqlQueryObject.addSelectField("path");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("path");
				sqlQueryObject.addOrderBy("http_method");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordo);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));
				rs = stm.executeQuery();

				while (rs.next()) {

					ResourceSintetica resource = new ResourceSintetica();

					tmp = rs.getString("nome");
					resource.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("descrizione");
					resource.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("http_method");
					resource.setMethod(DriverRegistroServiziDB_LIB.getEnumHttpMethod(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("path");
					if(tmp!=null) {
						if(CostantiDB.API_RESOURCE_PATH_ALL_VALUE.equals(tmp.trim())==false) {
							resource.setPath(tmp);
						}
					}
					
					tmp = rs.getString("profilo_azione");
					if (tmp == null || tmp.equals(""))
						resource.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
					else
						resource.setProfAzione(tmp);
								
					
					resource.setIdAccordo(idAccordo);

					long idResource = rs.getLong("id");
					resource.setId(idResource);

					accordoServizio.getResource().add(resource);

				}
				rs.close();
				stm.close();
				
				
				// read gruppi
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "id", "identificativoGruppo");
				sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "nome", "nomeGruppo");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo = "+CostantiDB.GRUPPI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.GRUPPI+".nome");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordo);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));
				rs = stm.executeQuery();

				while (rs.next()) {
					GruppoSintetico gruppo = new GruppoSintetico();
					gruppo.setId(rs.getLong("identificativoGruppo"));
					gruppo.setNome(rs.getString("nomeGruppo"));
					accordoServizio.getGruppo().add(gruppo);
				}
				rs.close();
				stm.close();
			


				// read AccordoServizioComposto

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordo);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));
				rs = stm.executeQuery();

				AccordoServizioParteComuneServizioCompostoSintetico asComposto = null;
				if (rs.next()) {

					asComposto = new AccordoServizioParteComuneServizioCompostoSintetico();
					asComposto.setId(rs.getLong("id"));
					asComposto.setIdAccordoCooperazione(rs.getLong("id_accordo_cooperazione"));

					IDAccordoCooperazione idAccordoCooperazione = this.driver.getIdAccordoCooperazione(asComposto.getIdAccordoCooperazione(), con);
					String uriAccordo = this.driver.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoCooperazione);
					asComposto.setAccordoCooperazione(uriAccordo);
				}
				rs.close();
				stm.close();

				if(asComposto!=null){

					// read servizi componenti
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_servizio_composto = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, asComposto.getId());
					this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, asComposto.getId()));
					rs = stm.executeQuery();

					while (rs.next()) {

						AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico asComponente = new AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico();
						asComponente.setIdServizioComponente(rs.getLong("id_servizio_componente"));
						asComponente.setAzione(rs.getString("azione"));

						AccordoServizioParteSpecifica aspsServizioComponente = this.driver.getAccordoServizioParteSpecifica(asComponente.getIdServizioComponente(),con);
						asComponente.setTipo(aspsServizioComponente.getTipo());
						asComponente.setNome(aspsServizioComponente.getNome());
						asComponente.setVersione(aspsServizioComponente.getVersione());
						asComponente.setTipoSoggetto(aspsServizioComponente.getTipoSoggettoErogatore());
						asComponente.setNomeSoggetto(aspsServizioComponente.getNomeSoggettoErogatore());

						asComposto.getServizioComponente().add(asComponente);
					}
					rs.close();
					stm.close();


					// setto all'interno dell'accordo
					accordoServizio.setServizioComposto(asComposto);
				}

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizioParteComuneSintetico] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordo));
			}


//			if(accordoServizio!=null){
//				// nomiAzione setting 
//				accordoServizio.setNomiAzione(accordoServizio.readNomiAzione());
//			}
			return accordoServizio;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteComuneSintetico] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}
	
	
	protected List<AccordoServizioParteComuneSintetico> accordiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return accordiListEngine(superuser,ricerca,false,false);
	}
	protected List<AccordoServizioParteComuneSintetico> accordiServizioParteComuneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return accordiListEngine(superuser,ricerca,false,true);
	}
	protected List<AccordoServizioParteComuneSintetico> accordiServizioCompostiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return accordiListEngine(superuser,ricerca,true,false);
	}
	private List<AccordoServizioParteComuneSintetico> accordiListEngine(String superuser, ISearch ricerca,boolean excludeASParteComune,boolean excludeASComposti) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiList";
		int idLista = Liste.ACCORDI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		
		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		boolean searchByTipoSoggetto = (tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0);
		
		String filterTipoAPI = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
		
		String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
		
		String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
		
		String filterCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CANALE);
		
		String filtroModISicurezzaCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_CANALE);
		String filtroModISicurezzaMessaggio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO);
		String filtroModISorgenteToken = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SORGENTE_TOKEN);
		String filtroModIDigestRichiesta = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_DIGEST_RICHIESTA);
		String filtroModIInfoUtente = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_INFORMAZIONI_UTENTE);
		if((filtroModISicurezzaCanale!=null && "".equals(filtroModISicurezzaCanale))) {
			filtroModISicurezzaCanale=null;
		}
		if((filtroModISicurezzaMessaggio!=null && "".equals(filtroModISicurezzaMessaggio))) {
			filtroModISicurezzaMessaggio=null;
		}
		if((filtroModISorgenteToken!=null && "".equals(filtroModISorgenteToken))) {
			filtroModISorgenteToken=null;
		}
		Boolean filtroModIDigestRichiestaEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModIDigestRichiesta)) {
			filtroModIDigestRichiestaEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModIDigestRichiesta)) {
			filtroModIDigestRichiestaEnabled = false;
		}
		if((filtroModIInfoUtente!=null && "".equals(filtroModIInfoUtente))) {
			filtroModIInfoUtente=null;
		}
		boolean filtroModI = filtroModISicurezzaCanale!=null || filtroModISicurezzaMessaggio!=null ||
				filtroModISorgenteToken!=null ||
				filtroModIDigestRichiestaEnabled!=null || filtroModIInfoUtente!=null;
		
		boolean searchCanale = false;
		boolean canaleDefault = false;
		if(filterCanale!=null && !filterCanale.equals("")) {
			searchCanale = true;
			if(filterCanale.startsWith(Filtri.PREFIX_VALUE_CANALE_DEFAULT)) {
				filterCanale = filterCanale.substring(Filtri.PREFIX_VALUE_CANALE_DEFAULT.length());
				canaleDefault = true;
			}
		}

		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);
		this.driver.logDebug("filterTipoAPI : " + filterTipoAPI);
		this.driver.logDebug("filterStatoAccordo : " + filterStatoAccordo);
		this.driver.logDebug("filterGruppo : " + filterGruppo);
		this.driver.logDebug("filterCanale : " + filterCanale);
		this.driver.logDebug("filtroModISicurezzaCanale : " + filtroModISicurezzaCanale);
		this.driver.logDebug("filtroModISicurezzaMessaggio : " + filtroModISicurezzaMessaggio);
		this.driver.logDebug("filtroModISorgenteToken : " + filtroModISorgenteToken);
		this.driver.logDebug("filtroModIDigestRichiesta : " + filtroModIDigestRichiesta);
		this.driver.logDebug("filtroModIInfoUtente : " + filtroModIInfoUtente);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComuneSintetico> lista = new ArrayList<AccordoServizioParteComuneSintetico>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiListEngine");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if(excludeASComposti && excludeASParteComune){
				throw new Exception("Non e' possibile invocare il metodo accordiListEngine con entrambi i parametri excludeASParteComune,excludeASComposti impostati al valore true");
			}


			ISQLQueryObject sqlQueryObjectExclude = null;
			if(excludeASComposti || excludeASParteComune){
				sqlQueryObjectExclude = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExclude.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObjectExclude.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id_accordo");
				sqlQueryObjectExclude.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addSelectCountField("*", "cont");
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".superuser = ?");
			
			//query con search
			if (!search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", search, true, true));
						//sqlQueryObject.getWhereLikeCondition("versione", search, true, true), // la versione e' troppo, tutte hanno 1 ....
						//sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true));  // fa confusone nei protocolli che non supportano il referente
			}
			if (searchByTipoSoggetto) {
				sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));		
			}
			
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".stato = ?");
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
			}
			
			if(excludeASParteComune){
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
			}
			if(excludeASComposti){
				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
			}
			
			if(searchCanale) {
				if(canaleDefault) {
					sqlQueryObject.addWhereCondition(false, CostantiDB.ACCORDI+".canale = ?", CostantiDB.ACCORDI+".canale is null");
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".canale = ?");
				}
			}
			
			if(filtroModI) {
				DBUtils.setFiltriModI(sqlQueryObject, this.driver.tipoDB,
						filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
						filtroModISorgenteToken,
						filtroModIDigestRichiestaEnabled, filtroModIInfoUtente);
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				stmt.setString(index++, filterTipoAPI);
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				stmt.setString(index++, filterGruppo);
			}
			if(searchCanale) {
				stmt.setString(index++, filterCanale);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI, "id", "idAccordo");
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI, "nome", "nomeAccordo");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "versione");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			//sqlQueryObject.addSelectField("id_referente");
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".superuser = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", search, true, true));
				//sqlQueryObject.getWhereLikeCondition("versione", search, true, true), // la versione e' troppo, tutte hanno 1 ....
				//sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true));  // fa confusone nei protocolli che non supportano il referente
			}
			if (searchByTipoSoggetto) {
				sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));		
			}

			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) { // con filter
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".stato = ?");
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
			}
			
			if(excludeASParteComune){
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
			}
			if(excludeASComposti){
				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
			}
			
			if(searchCanale) {
				if(canaleDefault) {
					sqlQueryObject.addWhereCondition(false, CostantiDB.ACCORDI+".canale = ?", CostantiDB.ACCORDI+".canale is null");
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".canale = ?");
				}
			}
			
			if(filtroModI) {
				DBUtils.setFiltriModI(sqlQueryObject, this.driver.tipoDB,
						filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
						filtroModISorgenteToken,
						filtroModIDigestRichiestaEnabled, filtroModIInfoUtente);
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nomeAccordo");
			sqlQueryObject.addOrderBy("versione");
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				stmt.setString(index++, filterTipoAPI);
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				stmt.setString(index++, filterGruppo);
			}
			if(searchCanale) {
				stmt.setString(index++, filterCanale);
			}
			risultato = stmt.executeQuery();

			AccordoServizioParteComuneSintetico accordo = null;

			while (risultato.next()) {

				Long id = risultato.getLong("idAccordo");
				accordo = this.getAccordoServizioParteComuneSintetico(id, con);
				lista.add(accordo);

			}

			this.driver.logDebug("size lista :" + ((lista == null) ? null : lista.size()));

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}
	}
}
