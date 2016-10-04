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

package org.openspcoop2.protocol.spcoop.archive;

import it.gov.spcoop.sica.dao.AccordoServizioComposto;
import it.gov.spcoop.sica.dao.Costanti;
import it.gov.spcoop.sica.dao.driver.XMLUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiArchivi;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopUtilities;

/**
 * SPCoopArchiveExport 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopArchiveExport {

	private IProtocolFactory protocolFactory = null;
	private Logger logger = null;
	public SPCoopArchiveExport(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
		this.logger = this.protocolFactory.getLogger();
	}


	public void exportArchive(Archive archive,ArchiveMode archiveMode,IRegistryReader registryReader,OutputStream out) throws ProtocolException{

		int countArchiveForExport = 0;
		if(archive.getAccordiServizioParteComune()!=null){
			countArchiveForExport+=archive.getAccordiServizioParteComune().size();
		}
		if(archive.getAccordiServizioComposto()!=null){
			countArchiveForExport+=archive.getAccordiServizioComposto().size();
		}
		if(archive.getAccordiCooperazione()!=null){
			countArchiveForExport+=archive.getAccordiCooperazione().size();
		}
		if(archive.getAccordiServizioParteSpecifica()!=null){
			countArchiveForExport+=archive.getAccordiServizioParteSpecifica().size();
		}

		boolean multiArchive = countArchiveForExport>1;
		boolean clientSICACompatibility = SPCoopCostantiArchivi.EXPORT_MODE_COMPATIBILITA_CLIENT_SICA.equals(archiveMode);
		
		try{
		
			if(multiArchive){
				multiArchive(out, archive, clientSICACompatibility, registryReader);
			}
			else{
				if(archive.getAccordiServizioParteComune()!=null && archive.getAccordiServizioParteComune().size()==1){
					this.exportAccordoServizio(archive.getAccordiServizioParteComune().get(0).getAccordoServizioParteComune(), 
							out, clientSICACompatibility, registryReader, false);
				}
				else if(archive.getAccordiServizioComposto()!=null && archive.getAccordiServizioComposto().size()==1){
					this.exportAccordoServizio(archive.getAccordiServizioComposto().get(0).getAccordoServizioParteComune(), 
							out, clientSICACompatibility, registryReader, true);
				}
				else if(archive.getAccordiServizioParteSpecifica()!=null && archive.getAccordiServizioParteSpecifica().size()==1){
					this.exportAccordoServizio(archive.getAccordiServizioParteSpecifica().get(0).getAccordoServizioParteSpecifica(), 
							out, clientSICACompatibility, registryReader);
				}
				else if(archive.getAccordiCooperazione()!=null && archive.getAccordiCooperazione().size()==1){
					this.exportAccordoCooperazione(archive.getAccordiCooperazione().get(0).getAccordoCooperazione(), 
							out, clientSICACompatibility, registryReader);
				}
			}
			
		}catch(ProtocolException pExc){
			throw pExc;
		}
		catch(Exception e){
			throw new ProtocolException("Conversione archivio non riuscita: "+e.getMessage(),e);
		}

	}


	private void multiArchive(OutputStream out,Archive archive,boolean clientSICACompatibility,IRegistryReader registryReader) throws Exception{
		
		ZipOutputStream zip = new ZipOutputStream(out);
		
		if(archive.getAccordiServizioParteComune()!=null && archive.getAccordiServizioParteComune().size()>1){
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				AccordoServizioParteComune accordo = archive.getAccordiServizioParteComune().get(i).getAccordoServizioParteComune();
				
				String filename = accordo.getNome();
                if(accordo.getSoggettoReferente()!=null){
                        filename+="_"+accordo.getSoggettoReferente().getTipo()+accordo.getSoggettoReferente().getNome();
                }
                if(accordo.getVersione()!=null && !"".equals(accordo.getVersione())){
                        filename+="_"+accordo.getVersione();
                }
                filename += "." + Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE;
                
                zip.putNextEntry(new ZipEntry(filename));
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                this.exportAccordoServizio(accordo, bout, clientSICACompatibility, registryReader,false);
                bout.flush();
                bout.close();
                zip.write(bout.toByteArray());
                zip.closeEntry();
			}
		}
		
		if(archive.getAccordiServizioComposto()!=null && archive.getAccordiServizioComposto().size()>1){
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				AccordoServizioParteComune accordo = archive.getAccordiServizioComposto().get(i).getAccordoServizioParteComune();
				
				String filename = accordo.getNome();
                if(accordo.getSoggettoReferente()!=null){
                        filename+="_"+accordo.getSoggettoReferente().getTipo()+accordo.getSoggettoReferente().getNome();
                }
                if(accordo.getVersione()!=null && !"".equals(accordo.getVersione())){
                        filename+="_"+accordo.getVersione();
                }
                filename += "." + Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO;
                
                zip.putNextEntry(new ZipEntry(filename));
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                this.exportAccordoServizio(accordo, bout, clientSICACompatibility, registryReader,true);
                bout.flush();
                bout.close();
                zip.write(bout.toByteArray());
                zip.closeEntry();
			}
		}
		
		if(archive.getAccordiServizioParteSpecifica()!=null && archive.getAccordiServizioParteSpecifica().size()>1){
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				AccordoServizioParteSpecifica accordo = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
				
				String filename = accordo.getNome();
                filename+="_"+accordo.getServizio().getTipoSoggettoErogatore()+accordo.getServizio().getNomeSoggettoErogatore();
                if(accordo.getVersione()!=null && !"".equals(accordo.getVersione())){
                        filename+="_"+accordo.getVersione();
                }
                filename += "." + Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA;
                
                zip.putNextEntry(new ZipEntry(filename));
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                this.exportAccordoServizio(accordo, bout, clientSICACompatibility, registryReader);
                bout.flush();
                bout.close();
                zip.write(bout.toByteArray());
                zip.closeEntry();
			}
		}
		
		if(archive.getAccordiCooperazione()!=null && archive.getAccordiCooperazione().size()>1){
			for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
				AccordoCooperazione accordo = archive.getAccordiCooperazione().get(i).getAccordoCooperazione();
				
				String filename = accordo.getNome();
                if(accordo.getSoggettoReferente()!=null){
                        filename+="_"+accordo.getSoggettoReferente().getTipo()+accordo.getSoggettoReferente().getNome();
                }
                if(accordo.getVersione()!=null && !"".equals(accordo.getVersione())){
                        filename+="_"+accordo.getVersione();
                }
                filename += "." + Costanti.ESTENSIONE_ACCORDO_COOPERAZIONE;
                
                zip.putNextEntry(new ZipEntry(filename));
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                this.exportAccordoCooperazione(accordo, bout, clientSICACompatibility, registryReader);
                bout.flush();
                bout.close();
                zip.write(bout.toByteArray());
                zip.closeEntry();
			}
		}
		
        zip.flush();
        zip.close();
	}
	
	private void exportAccordoServizio(AccordoServizioParteComune accordo,OutputStream out,
			boolean clientSICACompatibility,IRegistryReader registryReader,boolean servizioComposto) throws ProtocolException, SPCoopConvertToPackageCNIPAException{

		String uriAccordo = null;
		try{
			uriAccordo = IDAccordoFactory.getInstance().getUriFromAccordo(accordo);

			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			sicaContext.setSICAClientCompatibility(clientSICACompatibility);

			String tipoAccordo = "Accordo di Servizio Parte Comune";
			if(servizioComposto){
				tipoAccordo = "Accordo di Servizio Composto";
			}


			// *** Verifica informazioni presenti nel package ***

			// stato
			if(StatiAccordo.finale.toString().equals(accordo.getStatoPackage())==false){
				throw new Exception(tipoAccordo+" in uno stato non finale ["+accordo.getStatoPackage()+"]");
			}

			// descrizione
			if(accordo.getDescrizione()==null){
				if(accordo.getSoggettoReferente()!=null)
					accordo.setDescrizione(tipoAccordo+" (Versione:"+accordo.getVersione()+") "+
							accordo.getNome()+" con soggetto referente "+accordo.getSoggettoReferente().getTipo()+"/"+accordo.getSoggettoReferente().getNome());
				else
					accordo.setDescrizione(tipoAccordo+" (Versione:"+accordo.getVersione()+") "+
							accordo.getNome());
			}


			// *** impostazione mapping soggetto con codice IPA ***

			// Imposto CodiceIPA per Referente e erogatori servizi componenti (in caso di servizio composto) 
			SPCoopArchiveExportUtils.setCodiceIPA(accordo, sicaContext, registryReader);

			// Imposto uriAPS per Servizi che sono servizi componenti
			SPCoopArchiveExportUtils.setURI_APS(accordo, sicaContext, registryReader);



			// *** Trasformazione in package CNIPA ***
			XMLUtils xmlSICAUtilities = new XMLUtils(sicaContext,this.logger);
			byte[]archivio = null;
			if(servizioComposto){
				AccordoServizioComposto asc = SICAtoOpenSPCoopUtilities.accordoServizioComposto_openspcoopToSica(accordo,sicaContext,this.logger);
				archivio = xmlSICAUtilities.generateAccordoServizioComposto(asc);
			}else{
				it.gov.spcoop.sica.dao.AccordoServizioParteComune aspc = SICAtoOpenSPCoopUtilities.accordoServizioParteComune_openspcoopToSica(accordo,sicaContext,this.logger);
				archivio = xmlSICAUtilities.generateAccordoServizioParteComune(aspc);
			}
			out.write(archivio);

		}catch(Exception e){
			throw new ProtocolException("Conversione archivio ["+uriAccordo+"] non riuscita: "+e.getMessage(),e);
		}

	} 
	
	

	


	private void exportAccordoServizio(AccordoServizioParteSpecifica accordo,OutputStream out,
			boolean clientSICACompatibility,IRegistryReader registryReader) throws ProtocolException, SPCoopConvertToPackageCNIPAException{

		String uriAccordo = null;
		try{
			uriAccordo = IDAccordoFactory.getInstance().getUriFromAccordo(accordo);

			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			sicaContext.setSICAClientCompatibility(clientSICACompatibility);

			String tipoAccordo = "Accordo di Servizio Parte Specifica";


			// *** Verifica informazioni presenti nel package ***

			// stato
			if(StatiAccordo.finale.toString().equals(accordo.getStatoPackage())==false){
				throw new Exception(tipoAccordo+" in uno stato non finale ["+accordo.getStatoPackage()+"]");
			}

			// descrizione
			if(accordo.getDescrizione()==null){
				accordo.setDescrizione(tipoAccordo+" (Versione:"+accordo.getVersione()+") "+
						accordo.getNome()+" con soggetto referente "+accordo.getServizio().getTipoSoggettoErogatore()+"/"+accordo.getServizio().getNomeSoggettoErogatore());
			}

			// connettore
			if(accordo.getServizio().getConnettore()!=null && !TipiConnettore.DISABILITATO.toString().equals(accordo.getServizio().getConnettore().getTipo()) && 
					!TipiConnettore.HTTP.toString().equals(accordo.getServizio().getConnettore().getTipo()) && 
					!TipiConnettore.HTTPS.toString().equals(accordo.getServizio().getConnettore().getTipo())){
				throw new Exception("Accordo di servizio parte specifica possiede un connettore ("+accordo.getServizio().getConnettore().getTipo()+") non utilizzabile nella rete SPC");
			}
			if(accordo.getServizio().getConnettore()==null || TipiConnettore.DISABILITATO.toString().equals(accordo.getServizio().getConnettore().getTipo())){
				// imposto connettore del soggetto erogatore
				Soggetto soggettoErogatore = registryReader.getSoggetto(new IDSoggetto(accordo.getServizio().getTipoSoggettoErogatore(),accordo.getServizio().getNomeSoggettoErogatore()));
				if(soggettoErogatore.getConnettore()!=null && !TipiConnettore.DISABILITATO.toString().equals(soggettoErogatore.getConnettore().getTipo()) && 
						!TipiConnettore.HTTP.toString().equals(soggettoErogatore.getConnettore().getTipo()) && 
						!TipiConnettore.HTTPS.toString().equals(soggettoErogatore.getConnettore().getTipo())){
					throw new Exception("Accordo di servizio parte specifica non possiede un connettore e soggetto erogatore "+
							accordo.getServizio().getTipoSoggettoErogatore()+"/"+accordo.getServizio().getNomeSoggettoErogatore()+
							" possiede un connettore ("+soggettoErogatore.getConnettore().getTipo()+") non utilizzabile nella rete SPC");
				}
				else if(soggettoErogatore.getConnettore()==null || TipiConnettore.DISABILITATO.toString().equals(soggettoErogatore.getConnettore().getTipo()) ){
					throw new Exception("Sia l'Accordo di servizio parte specifica che il soggetto erogatore non possiedono un connettore");
				}
				else{
					Connettore cSoggettoErogatore = soggettoErogatore.getConnettore();
					accordo.getServizio().getConnettore().setCustom(cSoggettoErogatore.getCustom()!=null && cSoggettoErogatore.getCustom());
					accordo.getServizio().getConnettore().setTipo(cSoggettoErogatore.getTipo());
					while(accordo.getServizio().getConnettore().sizePropertyList()>0){
						accordo.getServizio().getConnettore().removeProperty(0);
					}
					for(int i=0; i<cSoggettoErogatore.sizePropertyList(); i++){
						Property cp = new Property();
						cp.setNome(cSoggettoErogatore.getProperty(i).getNome());
						cp.setValore(cSoggettoErogatore.getProperty(i).getValore());
						accordo.getServizio().getConnettore().addProperty(cp);
					}
				}
			}



			// *** impostazione mapping soggetto con codice IPA ***

            IDAccordo idAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(accordo.getAccordoServizioParteComune());
            IDServizio idS = new IDServizio(accordo.getServizio().getTipoSoggettoErogatore(), accordo.getServizio().getNomeSoggettoErogatore(), 
            		accordo.getServizio().getTipo(), accordo.getServizio().getNome());
            SPCoopArchiveExportUtils.setCodiceIPA(idS, idAccordoServizioParteComune, sicaContext, registryReader);




			// *** Trasformazione in package CNIPA ***
            
			XMLUtils xmlSICAUtilities = new XMLUtils(sicaContext,this.logger);
			AccordoServizioParteComune as = null;
			AccordoServizioParteComune asIncludereWSDLParteSpecifica = null;
            if(sicaContext.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune()){
            	as = registryReader.getAccordoServizioParteComune(idAccordoServizioParteComune,true);
            	asIncludereWSDLParteSpecifica = as;
            }else{
            	as = registryReader.getAccordoServizioParteComune(idAccordoServizioParteComune);
            }
            boolean implementazioneAccordoServizioComposto = as.getServizioComposto()!=null;
            
            // Trasformazione da openspcoop a sica
            it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica aspc = 
            		SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_openspcoopToSica(accordo,implementazioneAccordoServizioComposto,
            				asIncludereWSDLParteSpecifica,sicaContext,this.logger);
            byte[]archivio = xmlSICAUtilities.generateAccordoServizioParteSpecifica(aspc);
			out.write(archivio);

		}catch(Exception e){
			throw new ProtocolException("Conversione archivio ["+uriAccordo+"] non riuscita: "+e.getMessage(),e);
		}

	} 
	
	
	
	
	
	private void exportAccordoCooperazione(AccordoCooperazione accordo,OutputStream out,
			boolean clientSICACompatibility,IRegistryReader registryReader) throws ProtocolException, SPCoopConvertToPackageCNIPAException{

		String uriAccordo = null;
		try{
			uriAccordo = IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo);

			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			sicaContext.setSICAClientCompatibility(clientSICACompatibility);

			String tipoAccordo = "Accordo di Cooperazione";


			// *** Verifica informazioni presenti nel package ***

			// stato
			if(StatiAccordo.finale.toString().equals(accordo.getStatoPackage())==false){
				throw new Exception(tipoAccordo+" in uno stato non finale ["+accordo.getStatoPackage()+"]");
			}

			// descrizione
			if(accordo.getDescrizione()==null){
				if(accordo.getSoggettoReferente()!=null)
					accordo.setDescrizione(tipoAccordo+" (Versione:"+accordo.getVersione()+") "+
							accordo.getNome()+" con soggetto referente "+accordo.getSoggettoReferente().getTipo()+"/"+accordo.getSoggettoReferente().getNome());
				else
					accordo.setDescrizione(tipoAccordo+" (Versione:"+accordo.getVersione()+") "+
							accordo.getNome());
			}



			// *** impostazione mapping soggetto con codice IPA ***

            // Imposto CodiceIPA memorizzato per Coordinatore, partecipanti e erogatori servizi composti
			SPCoopArchiveExportUtils.setCodiceIPA(accordo, sicaContext, registryReader);





			// *** Trasformazione in package CNIPA ***
            
			XMLUtils xmlSICAUtilities = new XMLUtils(sicaContext,this.logger);
			it.gov.spcoop.sica.dao.AccordoCooperazione ac_sica = SICAtoOpenSPCoopUtilities.accordoCooperazione_openspcoopToSica(accordo,sicaContext,this.logger);
			byte[]archivio = xmlSICAUtilities.generateAccordoCooperazione(ac_sica);
			out.write(archivio);

		}catch(Exception e){
			throw new ProtocolException("Conversione archivio ["+uriAccordo+"] non riuscita: "+e.getMessage(),e);
		}

	} 


}
