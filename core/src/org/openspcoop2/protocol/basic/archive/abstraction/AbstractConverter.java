/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic.archive.abstraction;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.abstraction.Soggetto;
import org.openspcoop2.protocol.basic.archive.ZIPReadUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.slf4j.Logger;

/**     
 * AbstractConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConverter {


	
	protected IDAccordoFactory idAccordoFactory;
	protected IDServizioFactory idServizioFactory;
	protected Logger log;
	protected ZIPReadUtils filler;
	
	protected AbstractConverter(Logger log,ZIPReadUtils zipReader) throws ProtocolException{
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();
		this.log = log;
		this.filler = zipReader;
	}
			
	protected boolean existsPdd(Archive archive, IRegistryReader registryReader, String pdd) {
		
		// Cerco nel registro
		if(registryReader.existsPortaDominio(pdd)){
			return true;
		}
				
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getPdd().size()>0){
			for (int i = 0; i < archive.getPdd().size(); i++) {
				String nome = archive.getPdd().get(i).getPortaDominio().getNome();
				if(nome.equals(pdd)){
					return true;
				}
			}
		}
		
		return false;
				
	}
	
	protected boolean isPddOperativa(Archive archive, IRegistryReader registryReader, String pdd) throws RegistryException{
		
		// Cerco nel registro
		if(registryReader.existsPortaDominio(pdd)){
			try{
				if(registryReader.findIdPorteDominio(true).contains(pdd)){
					return true;
				}
			}catch(RegistryNotFound notFound){
				return false;
			}
		}
		
		return false; // le pdd presenti nell'archivio vengono aggiunte come esterne.
	}
	
	protected String getPddOperativa(IRegistryReader registryReader) throws RegistryNotFound, RegistryException{
		return registryReader.findIdPorteDominio(true).get(0); // una pdd operativa esiste sempre
	}
	
	protected boolean existsSoggetto(Archive archive, IRegistryReader registryReader, Soggetto idSoggetto) {
		
		// Cerco nel registro
		if(registryReader.existsSoggetto(new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome()))){
			return true;
		}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getSoggetti().size()>0){
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				org.openspcoop2.core.registry.Soggetto soggetto = archive.getSoggetti().get(i).getSoggettoRegistro();
				if(soggetto!=null){
					if(idSoggetto.getTipo().equals(soggetto.getTipo()) && idSoggetto.getNome().equals(soggetto.getNome())){
						return true;
					}
				}
			}
		}
		
		return false;
				
	}
	
	protected boolean existsServizioApplicativo(Archive archive, IConfigIntegrationReader configIntegrationReader, Soggetto idSoggetto, String nomeServizioApplicativo) {
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome()));
		idServizioApplicativo.setNome(nomeServizioApplicativo);
		
		// Cerco nel registro
		if(configIntegrationReader.existsServizioApplicativo(idServizioApplicativo)){
			return true;
		}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getServiziApplicativi().size()>0){
			for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
				org.openspcoop2.core.config.ServizioApplicativo sa = archive.getServiziApplicativi().get(i).getServizioApplicativo();
				if(sa!=null){
					if(idSoggetto.getTipo().equals(sa.getTipoSoggettoProprietario()) && 
							idSoggetto.getNome().equals(sa.getNomeSoggettoProprietario()) &&
							nomeServizioApplicativo.equals(sa.getNome())){
						return true;
					}
				}
			}
		}
		
		return false;
				
	}
	
	protected String getTipoCredenzialeServizioApplicativo(Archive archive, IConfigIntegrationReader configIntegrationReader, Soggetto idSoggetto, String nomeServizioApplicativo) throws Exception {
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome()));
		idServizioApplicativo.setNome(nomeServizioApplicativo);
		
		ServizioApplicativo sa = null;
		
		// Cerco nel registro
		if(configIntegrationReader.existsServizioApplicativo(idServizioApplicativo)){
			sa = configIntegrationReader.getServizioApplicativo(idServizioApplicativo);
		}
		else{
			// Cerco negli oggetti presenti nell'archivio
			if(archive.getServiziApplicativi().size()>0){
				for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
					org.openspcoop2.core.config.ServizioApplicativo saTmp = archive.getServiziApplicativi().get(i).getServizioApplicativo();
					if(saTmp!=null){
						if(idSoggetto.getTipo().equals(saTmp.getTipoSoggettoProprietario()) && 
								idSoggetto.getNome().equals(saTmp.getNomeSoggettoProprietario()) &&
								nomeServizioApplicativo.equals(saTmp.getNome())){
							sa = saTmp;
							break;
						}
					}
				}
			}
		}
		
		if(sa==null){
			throw new Exception("Servizio Applicativo ["+idServizioApplicativo.toString()+"] non esistente");
		}
		if(sa.getInvocazionePorta()==null){
			throw new Exception("Servizio Applicativo ["+idServizioApplicativo.toString()+"] non possiede delle credenziali di accesso alla PdD (Indicare un applicativo con tipologia 'fruitore')");
		}
		if(sa.getInvocazionePorta().sizeCredenzialiList()<=0){
			throw new Exception("Servizio Applicativo ["+idServizioApplicativo.toString()+"] non possiede delle credenziali di accesso alla PdD (Indicare un applicativo con tipologia 'fruitore') con credenziali");
		}
		return sa.getInvocazionePorta().getCredenziali(0).getTipo().getValue();
				
	}
	
	protected boolean existsAccordoServizioParteComune(Archive archive, IRegistryReader registryReader, IDAccordo idAccordo) throws RegistryException {
		
		// Cerco nel registro
		try{
			registryReader.getAccordoServizioParteComune(idAccordo);
			return true;
		}catch(RegistryNotFound notFound){}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getAccordiServizioParteComune().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				try{
					IDAccordo id = this.idAccordoFactory.getIDAccordoFromAccordo(archive.getAccordiServizioParteComune().get(i).getAccordoServizioParteComune());
					if(idAccordo.equals(id)){
						return true;
					}
				}catch(Exception e){}
			}
		}
		if(archive.getAccordiServizioComposto().size()>0){
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				try{
					IDAccordo id = this.idAccordoFactory.getIDAccordoFromAccordo(archive.getAccordiServizioComposto().get(i).getAccordoServizioParteComune());
					if(idAccordo.equals(id)){
						return true;
					}
				}catch(Exception e){}
			}
		}

		return false;
				
	}
	
	protected IDAccordo findIdAccordoServizioParteComune(Archive archive, IRegistryReader registryReader, 
			String nome, Soggetto idSoggetto, Integer versione) throws DriverRegistroServiziException, ProtocolException, RegistryException{
		
		List<IDAccordo> idAccordi = new ArrayList<IDAccordo>();
		List<String> uriAccordi = new ArrayList<String>();
		
		// Cerco nel registro
		FiltroRicercaAccordi filtroAccordi = new FiltroRicercaAccordi();
		filtroAccordi.setNome(nome);
		if(versione!=null){
			filtroAccordi.setVersione(versione);
		}
		if(idSoggetto!=null){
			IDSoggetto idSoggettoOp2 = new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome());
			filtroAccordi.setSoggetto(idSoggettoOp2);
		}
		try {
			List<IDAccordo> found = registryReader.findIdAccordiServizioParteComune(filtroAccordi);
			if(found.size()>0){
				for (IDAccordo id : found) {
					idAccordi.add(id);
					uriAccordi.add(this.idAccordoFactory.getUriFromIDAccordo(id));
				}
			}
		} catch (RegistryNotFound e) {
		}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getAccordiServizioParteComune().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				try{	
					IDAccordo id = this.idAccordoFactory.getIDAccordoFromAccordo(archive.getAccordiServizioParteComune().get(i).getAccordoServizioParteComune());
					if(nome.equals(id.getNome())==false){
						continue;
					}
					if(versione!=null && ((versione+"").equals(id.getVersione())==false) ){
						continue;
					}
					if(idSoggetto!=null){
						if(id.getSoggettoReferente()==null){
							continue;
						}
						if(idSoggetto.getTipo()!=null && idSoggetto.getTipo().equals(id.getSoggettoReferente().getTipo())==false){
							continue;
						}
						if(idSoggetto.getNome()!=null && idSoggetto.getNome().equals(id.getSoggettoReferente().getNome())==false){
							continue;
						}
					}
					String uri = this.idAccordoFactory.getUriFromIDAccordo(id);
					if(uriAccordi.contains(uri)==false){
						idAccordi.add(id);
						uriAccordi.add(uri);
					}
				}catch(Exception e){}
			}
		}
		if(archive.getAccordiServizioComposto().size()>0){
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				try{
					IDAccordo id = this.idAccordoFactory.getIDAccordoFromAccordo(archive.getAccordiServizioComposto().get(i).getAccordoServizioParteComune());
					if(nome.equals(id.getNome())==false){
						continue;
					}
					if(versione!=null && ((versione+"").equals(id.getVersione())==false) ){
						continue;
					}
					if(idSoggetto!=null){
						if(id.getSoggettoReferente()==null){
							continue;
						}
						if(idSoggetto.getTipo()!=null && idSoggetto.getTipo().equals(id.getSoggettoReferente().getTipo())==false){
							continue;
						}
						if(idSoggetto.getNome()!=null && idSoggetto.getNome().equals(id.getSoggettoReferente().getNome())==false){
							continue;
						}
					}
					String uri = this.idAccordoFactory.getUriFromIDAccordo(id);
					if(uriAccordi.contains(uri)==false){
						idAccordi.add(id);
						uriAccordi.add(uri);
					}
				}catch(Exception e){}
			}
		}
		
		// Verifico
		if(idAccordi.size()==1){
			return idAccordi.get(0);
		}
		else {
			String filtro = "Nome["+nome+"]";
			if(versione!=null){
				filtro = filtro + " Versione["+versione+"]";
			}
			if(idSoggetto!=null){
				filtro = filtro + " TipoSoggetto["+idSoggetto.getTipo()+"]";
				filtro = filtro + " NomeSoggetto["+idSoggetto.getNome()+"]";
			}
			if(idAccordi.size()<1){
				throw new ProtocolException("Non risultano accordi di servizio parte comune che corrispondono al seguente criterio di ricerca: "+filtro);
			}
			else{
				throw new ProtocolException("Risultano più accordi di servizio parte comune ("+uriAccordi.size()+") che corrispondono al seguente criterio di ricerca: "+filtro+""
						+ "\nGli accordi sono: "+uriAccordi.toString());
			}
		}
	}
	
	
	protected boolean existsAccordoServizioParteSpecifica(Archive archive, IRegistryReader registryReader, IDServizio idServizio) throws RegistryException {
		
		// Cerco nel registro
		try{
			registryReader.getAccordoServizioParteSpecifica(idServizio);
			return true;
		}catch(RegistryNotFound notFound){}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				try{
					AccordoServizioParteSpecifica asps = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
					IDServizio id = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(),
							asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore(), 
							asps.getVersione()); 
					if(idServizio.equals(id)){
						return true;
					}
				}catch(Exception e){}
			}
		}

		return false;
				
	}

	
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Archive archive, IRegistryReader registryReader, IDServizio idServizio) throws RegistryException {
	
		// Cerco nel registro
		try{
			return registryReader.getAccordoServizioParteSpecifica(idServizio);
		}catch(RegistryNotFound notFound){}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				try{
					AccordoServizioParteSpecifica asps = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
					if(idServizio.getTipo().equals(asps.getTipo())==false ){
						continue;
					}
					if(idServizio.getNome().equals(asps.getNome())==false){
						continue;
					}
					if(idServizio.getVersione().intValue() != asps.getVersione().intValue()){
						continue;
					}
					if(idServizio.getSoggettoErogatore().getTipo().equals(asps.getTipoSoggettoErogatore())==false){
						continue;
					}
					if(idServizio.getSoggettoErogatore().getNome().equals(asps.getNomeSoggettoErogatore())==false){
						continue;
					}
					return asps;
				}catch(Exception e){}
			}
		}
		return null;
				
	}
	
	protected IDServizio findIdAccordoServizioParteSpecifica(Archive archive, IRegistryReader registryReader, 
			String nome, String tipo, Integer versione, Soggetto idSoggetto) throws DriverRegistroServiziException, ProtocolException, RegistryException{
		
		List<IDServizio> idAccordi = new ArrayList<IDServizio>();
		List<String> uriAccordi = new ArrayList<String>();
		
		// Cerco nel registro
		FiltroRicercaServizi filtroAccordi = new FiltroRicercaServizi();
		filtroAccordi.setTipoServizio(tipo);
		filtroAccordi.setNomeServizio(nome);
		if(idSoggetto!=null){
			IDSoggetto idSoggettoOp2 = new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome());
			filtroAccordi.setSoggettoErogatore(idSoggettoOp2);
		}
		try {
			List<IDServizio> found = registryReader.findIdAccordiServizioParteSpecifica(filtroAccordi);
			if(found.size()>0){
				for (IDServizio id : found) {
					idAccordi.add(id);
					uriAccordi.add(id.toString());
				}
			}
		} catch (RegistryNotFound e) {
		}
		
		// Cerco negli oggetti presenti nell'archivio
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				try{
					AccordoServizioParteSpecifica asps = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
					if(tipo!=null && (tipo.equals(asps.getTipo())==false) ){
						continue;
					}
					if(nome.equals(asps.getNome())==false){
						continue;
					}
					if(versione!=null && (versione.intValue() != asps.getVersione().intValue()) ){
						continue;
					}
					if(idSoggetto!=null){
						if(idSoggetto.getTipo().equals(asps.getTipoSoggettoErogatore())==false){
							continue;
						}
						if(idSoggetto.getNome().equals(asps.getNomeSoggettoErogatore())==false){
							continue;
						}
					}
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo, nome,
							new IDSoggetto(idSoggetto.getTipo(),idSoggetto.getNome()), 
							versione);
					String uri = idServizio.toString();
					if(uriAccordi.contains(uri)==false){
						idAccordi.add(idServizio);
						uriAccordi.add(uri);
					}
				}catch(Exception e){}
			}
		}
		
		
		// Verifico
		if(idAccordi.size()==1){
			return idAccordi.get(0);
		}
		else {
			String filtro = "Nome["+nome+"]";
			if(tipo!=null){
				filtro = filtro + " Tipo["+tipo+"]";
			}
			if(idSoggetto!=null){
				filtro = filtro + " TipoSoggetto["+idSoggetto.getTipo()+"]";
				filtro = filtro + " NomeSoggetto["+idSoggetto.getNome()+"]";
			}
			if(idAccordi.size()<1){
				throw new ProtocolException("Non risultano accordi di servizio parte specifica che corrispondono al seguente criterio di ricerca: "+filtro);
			}
			else{
				throw new ProtocolException("Risultano più accordi di servizio parte specifica ("+uriAccordi.size()+") che corrispondono al seguente criterio di ricerca: "+filtro+""
						+ "\nGli accordi sono: "+uriAccordi.toString());
			}
		}
	}
		
	
}
