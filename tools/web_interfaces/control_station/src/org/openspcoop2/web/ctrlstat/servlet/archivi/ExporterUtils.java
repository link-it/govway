/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveActivePolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ExporterUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExporterUtils {

	private ArchiviCore archiviCore;
	private SoggettiCore soggettiCore;
	private AccordiServizioParteComuneCore aspcCore;
	@SuppressWarnings("unused")
	private AccordiServizioParteSpecificaCore aspsCore;
	private AccordiCooperazioneCore acCore;
	
	
	public ExporterUtils(ArchiviCore archiviCore) throws Exception{
		this.archiviCore = archiviCore;
		this.soggettiCore = new SoggettiCore(archiviCore);
		this.aspcCore = new AccordiServizioParteComuneCore(archiviCore);
		this.aspsCore = new AccordiServizioParteSpecificaCore(archiviCore);
		this.acCore = new AccordiCooperazioneCore(archiviCore);
	}

	public List<ExportMode> getExportModesCompatibleWithAllProtocol(List<String> protocolli,ArchiveType archiveType) throws ProtocolException{
		
		List<ExportMode> exportModes = new ArrayList<ExportMode>();
		
		for (int i = 0; i < protocolli.size(); i++) {
			String protocolName = protocolli.get(i);
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
			IArchive archiveEngine = pf.createArchive();
			List<ExportMode> exportModesByProtocol = archiveEngine.getExportModes(archiveType);
			for (ExportMode exportMode : exportModesByProtocol) {
				
				boolean found = true;
				
				for (int j = 0; j < protocolli.size(); j++) {
					if(j==i)
						continue;
					String protocolCheck = protocolli.get(j);
					IProtocolFactory<?> pfCheck = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolCheck);
					IArchive archiveEngineCheck = pfCheck.createArchive();
					List<ExportMode> exportModesByProtocolCheck = archiveEngineCheck.getExportModes(archiveType);
					if(exportModesByProtocolCheck.contains(exportMode)==false){
						found = false;
						break;
					}
				}
				
				if(found){
					if(exportModes.contains(exportMode)==false)
						exportModes.add(exportMode);
				}
				
			}
		}
		
		return exportModes;
	}
	
	public Hashtable<ExportMode,String> getExportModesWithProtocol(List<String> protocolli,ArchiveType archiveType) throws ProtocolException{

		Hashtable<ExportMode,String> exportModes = new Hashtable<ExportMode,String>();
		for (int i = 0; i < protocolli.size(); i++) {
			String protocolName = protocolli.get(i);
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(protocolName)==false){
				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
				IArchive archiveEngine = pf.createArchive();
				List<ExportMode> exportModesByProtocol = archiveEngine.getExportModes(archiveType);
				for (ExportMode exp : exportModesByProtocol) {
					if(exportModes.containsKey(exp)==false){
						exportModes.put(exp,protocolName);
					}
				}
			}
		}

		return exportModes;
	}

	
	public boolean existsAtLeastOneExportMpde(ArchiveType archiveType, HttpSession session) throws ProtocolException, DriverRegistroServiziException{
		List<String> protocolli = this.archiviCore.getProtocolli(session);
		return this.getExportModesWithProtocol(protocolli, archiveType).size()>0;
	}

	public List<?> getIdsSoggetti(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDSoggetto> idsSoggetti = new ArrayList<IDSoggetto>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			long idLong = Long.parseLong(id);
			idsSoggetti.add(this.soggettiCore.getIdSoggettoRegistro(idLong));
		}
		return idsSoggetti;
	}
	
	public List<?> getIdsAccordiServizioComposti(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		return this.getIdsAccordiServizioParteComune(ids);
	}
	public List<?> getIdsAccordiServizioParteComune(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDAccordo> idsAccordi = new ArrayList<IDAccordo>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			long idLong = Long.parseLong(id);
			idsAccordi.add(this.aspcCore.getIdAccordoServizio(idLong));
		}
		return idsAccordi;
	}
	
	public List<?> getIdsAccordiServizioParteSpecifica(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDServizio> idsAccordi = new ArrayList<IDServizio>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			IDServizio idS = IDServizioFactory.getInstance().getIDServizioFromUri(id);
			idsAccordi.add(idS);
		}
		return idsAccordi;
	}
	
	public List<?> getIdsAccordiCooperazione(String ids) throws DriverRegistroServiziNotFound, DriverRegistroServiziException{
		List<IDAccordoCooperazione> idsAccordi = new ArrayList<IDAccordoCooperazione>();
		ArrayList<String> idsToExport = Utilities.parseIdsToRemove(ids);
		for (String id : idsToExport) {
			long idLong = Long.parseLong(id);
			idsAccordi.add(this.acCore.getIdAccordoCooperazione(idLong));
		}
		return idsAccordi;
	}
	
	public void filterByProtocol(List<String> tipiSoggetti,List<String> tipiServizi,Archive archive) throws ProtocolException {
		
		// soggetti
		if(archive.getSoggetti()!=null && archive.getSoggetti().size()>0) {
			List<ArchiveSoggetto> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				if(archive.getSoggetti().get(i).getIdSoggetto()!=null &&
						tipiSoggetti.contains(archive.getSoggetti().get(i).getIdSoggetto().getTipo())) {
					listFiltrata.add(archive.getSoggetti().get(i));
				}
			}
			while(archive.getSoggetti().size()>0) {
				archive.getSoggetti().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveSoggetto archiveFiltrato : listFiltrata) {
					archive.getSoggetti().add(archiveFiltrato);		
				}
			}
		}
		
		// servizio applicativo
		if(archive.getServiziApplicativi()!=null && archive.getServiziApplicativi().size()>0) {
			List<ArchiveServizioApplicativo> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
				if(archive.getServiziApplicativi().get(i).getIdSoggettoProprietario()!=null &&
						tipiSoggetti.contains(archive.getServiziApplicativi().get(i).getIdSoggettoProprietario().getTipo())) {
					listFiltrata.add(archive.getServiziApplicativi().get(i));
				}
			}
			while(archive.getServiziApplicativi().size()>0) {
				archive.getServiziApplicativi().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveServizioApplicativo archiveFiltrato : listFiltrata) {
					archive.getServiziApplicativi().add(archiveFiltrato);		
				}
			}
		}
		
		// porta delegata
		if(archive.getPorteDelegate()!=null && archive.getPorteDelegate().size()>0) {
			List<ArchivePortaDelegata> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
				if(archive.getPorteDelegate().get(i).getIdSoggettoProprietario()!=null &&
						tipiSoggetti.contains(archive.getPorteDelegate().get(i).getIdSoggettoProprietario().getTipo())) {
					listFiltrata.add(archive.getPorteDelegate().get(i));
				}
			}
			while(archive.getPorteDelegate().size()>0) {
				archive.getPorteDelegate().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchivePortaDelegata archiveFiltrato : listFiltrata) {
					archive.getPorteDelegate().add(archiveFiltrato);		
				}
			}
		}
		
		// porta applicativa
		if(archive.getPorteApplicative()!=null && archive.getPorteApplicative().size()>0) {
			List<ArchivePortaApplicativa> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
				if(archive.getPorteApplicative().get(i).getIdSoggettoProprietario()!=null &&
						tipiSoggetti.contains(archive.getPorteApplicative().get(i).getIdSoggettoProprietario().getTipo())) {
					listFiltrata.add(archive.getPorteApplicative().get(i));
				}
			}
			while(archive.getPorteApplicative().size()>0) {
				archive.getPorteApplicative().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchivePortaApplicativa archiveFiltrato : listFiltrata) {
					archive.getPorteApplicative().add(archiveFiltrato);		
				}
			}
		}
		
		// accordi cooperazione
		if(archive.getAccordiCooperazione()!=null && archive.getAccordiCooperazione().size()>0) {
			List<ArchiveAccordoCooperazione> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
				if(archive.getAccordiCooperazione().get(i).getIdSoggettoReferente()!=null &&
						tipiSoggetti.contains(archive.getAccordiCooperazione().get(i).getIdSoggettoReferente().getTipo())) {
					listFiltrata.add(archive.getAccordiCooperazione().get(i));
				}
			}
			while(archive.getAccordiCooperazione().size()>0) {
				archive.getAccordiCooperazione().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveAccordoCooperazione archiveFiltrato : listFiltrata) {
					archive.getAccordiCooperazione().add(archiveFiltrato);		
				}
			}
		}
		
		// accordi parte comune
		if(archive.getAccordiServizioParteComune()!=null && archive.getAccordiServizioParteComune().size()>0) {
			List<ArchiveAccordoServizioParteComune> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				if(archive.getAccordiServizioParteComune().get(i).getIdSoggettoReferente()!=null &&
						tipiSoggetti.contains(archive.getAccordiServizioParteComune().get(i).getIdSoggettoReferente().getTipo())) {
					listFiltrata.add(archive.getAccordiServizioParteComune().get(i));
				}
			}
			while(archive.getAccordiServizioParteComune().size()>0) {
				archive.getAccordiServizioParteComune().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveAccordoServizioParteComune archiveFiltrato : listFiltrata) {
					archive.getAccordiServizioParteComune().add(archiveFiltrato);		
				}
			}
		}
		
		// accordi composto
		if(archive.getAccordiServizioComposto()!=null && archive.getAccordiServizioComposto().size()>0) {
			List<ArchiveAccordoServizioComposto> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				if(archive.getAccordiServizioComposto().get(i).getIdSoggettoReferente()!=null &&
						tipiSoggetti.contains(archive.getAccordiServizioComposto().get(i).getIdSoggettoReferente().getTipo())) {
					listFiltrata.add(archive.getAccordiServizioComposto().get(i));
				}
			}
			while(archive.getAccordiServizioComposto().size()>0) {
				archive.getAccordiServizioComposto().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveAccordoServizioComposto archiveFiltrato : listFiltrata) {
					archive.getAccordiServizioComposto().add(archiveFiltrato);		
				}
			}
		}
		
		// accordi parte specifica
		if(archive.getAccordiServizioParteSpecifica()!=null && archive.getAccordiServizioParteSpecifica().size()>0) {
			List<ArchiveAccordoServizioParteSpecifica> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				if(archive.getAccordiServizioParteSpecifica().get(i).getIdSoggettoErogatore()!=null &&
						tipiSoggetti.contains(archive.getAccordiServizioParteSpecifica().get(i).getIdSoggettoErogatore().getTipo())) {
					listFiltrata.add(archive.getAccordiServizioParteSpecifica().get(i));
				}
			}
			while(archive.getAccordiServizioParteSpecifica().size()>0) {
				archive.getAccordiServizioParteSpecifica().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveAccordoServizioParteSpecifica archiveFiltrato : listFiltrata) {
					archive.getAccordiServizioParteSpecifica().add(archiveFiltrato);		
				}
			}
		}
		
		// fruitori
		if(archive.getAccordiFruitori()!=null && archive.getAccordiFruitori().size()>0) {
			List<ArchiveFruitore> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
				if(archive.getAccordiFruitori().get(i).getIdSoggettoFruitore()!=null &&
						tipiSoggetti.contains(archive.getAccordiFruitori().get(i).getIdSoggettoFruitore().getTipo())) {
					listFiltrata.add(archive.getAccordiFruitori().get(i));
				}
			}
			while(archive.getAccordiFruitori().size()>0) {
				archive.getAccordiFruitori().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveFruitore archiveFiltrato : listFiltrata) {
					archive.getAccordiFruitori().add(archiveFiltrato);		
				}
			}
		}
		
		// controllo del traffico
		if(archive.getControlloTraffico_activePolicies()!=null && archive.getControlloTraffico_activePolicies().size()>0) {
			List<ArchiveActivePolicy> listFiltrata = new ArrayList<>();
			for (int i = 0; i < archive.getControlloTraffico_activePolicies().size(); i++) {
				ArchiveActivePolicy archivePolicy = archive.getControlloTraffico_activePolicies().get(i);
				boolean filtra = false;
				if(archivePolicy.getPolicy().getFiltro()!=null) {
					if(archivePolicy.getPolicy().getFiltro().getTipoErogatore()!=null) {
						if(tipiSoggetti.contains(archivePolicy.getPolicy().getFiltro().getTipoErogatore())==false) {
							filtra = true;
						}
					}
					if(archivePolicy.getPolicy().getFiltro().getTipoFruitore()!=null) {
						if(tipiSoggetti.contains(archivePolicy.getPolicy().getFiltro().getTipoFruitore())==false) {
							filtra = true;
						}
					}
					if(archivePolicy.getPolicy().getFiltro().getTipoServizio()!=null) {
						if(tipiServizi.contains(archivePolicy.getPolicy().getFiltro().getTipoServizio())==false) {
							filtra = true;
						}
					}
				}
				if(!filtra) {
					listFiltrata.add(archivePolicy);
				}
			}
			while(archive.getControlloTraffico_activePolicies().size()>0) {
				archive.getControlloTraffico_activePolicies().remove(0);
			}
			if(listFiltrata.size()>0) {
				for (ArchiveActivePolicy archiveFiltrato : listFiltrata) {
					archive.getControlloTraffico_activePolicies().add(archiveFiltrato);		
				}
			}
		}
		
	}
}
