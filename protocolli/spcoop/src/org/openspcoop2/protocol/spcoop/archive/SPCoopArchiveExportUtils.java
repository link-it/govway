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

package org.openspcoop2.protocol.spcoop.archive;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;

/**
 * SPCoopArchiveExportUtils 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopArchiveExportUtils {


	/* ------------ SetCodiceIPA -------------------------- */
	
    public static void setCodiceIPA(AccordoServizioParteComune aspc,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
        // Imposto CodiceIPA memorizzato per Referente
        if(aspc.getSoggettoReferente()!=null){
                IDSoggetto soggettoReferente = new IDSoggetto(aspc.getSoggettoReferente().getTipo(),aspc.getSoggettoReferente().getNome());
                String codiceIPAReferente = registryReader.getCodiceIPA(soggettoReferente);
                contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoReferente, codiceIPAReferente);
        }
        // Imposto CodiceIPA memorizzato per erogatori servizi componenti
        if(aspc.getServizioComposto()!=null){
                for(int i=0;i<aspc.getServizioComposto().sizeServizioComponenteList();i++){
                        IDSoggetto soggettoErogatoreServizioComponente = 
                                new IDSoggetto(aspc.getServizioComposto().getServizioComponente(i).getTipoSoggetto(),
                                		aspc.getServizioComposto().getServizioComponente(i).getNomeSoggetto());
                        String codiceIPASoggettoErogatoreServizioComponente = registryReader.getCodiceIPA(soggettoErogatoreServizioComponente);
                        contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoErogatoreServizioComponente, codiceIPASoggettoErogatoreServizioComponente);
                }
        }
    }
	
    public static void setCodiceIPA(org.openspcoop2.core.registry.AccordoCooperazione ac,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
        // Imposto CodiceIPA memorizzato per Coordinatore
        if(ac.getSoggettoReferente()!=null){
                IDSoggetto soggettoCoordinatore = new IDSoggetto(ac.getSoggettoReferente().getTipo(),ac.getSoggettoReferente().getNome());
                String codiceIPACoordinatore = registryReader.getCodiceIPA(soggettoCoordinatore);
                contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoCoordinatore, codiceIPACoordinatore);
        }
        // Imposto CodiceIPA per Partecipanti
        if(ac.getElencoPartecipanti()!=null){
                AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
                for(int i=0; i<partecipanti.sizeSoggettoPartecipanteList();i++){
                        IDSoggetto soggettoPartecipante = new IDSoggetto(partecipanti.getSoggettoPartecipante(i).getTipo(),partecipanti.getSoggettoPartecipante(i).getNome());
                        String codiceIPAPartecipante = registryReader.getCodiceIPA(soggettoPartecipante);
                        contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoPartecipante, codiceIPAPartecipante);
                }
        }
        // Imposto CodiceIPA per erogatori servizi composti
        for(int i=0; i<ac.sizeUriServiziCompostiList(); i++){
                String uriServizioComposto =  ac.getUriServiziComposti(i);
                IDAccordo idAccordoServizioComposto = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriServizioComposto);
                if(idAccordoServizioComposto.getSoggettoReferente()!=null){
                        String codiceIPASoggettoErogatoreServizioComposto = registryReader.getCodiceIPA(idAccordoServizioComposto.getSoggettoReferente());
                        contextSICA.addMappingSoggettoSPCoopToCodiceIPA(idAccordoServizioComposto.getSoggettoReferente(), codiceIPASoggettoErogatoreServizioComposto);
                }
        }
    }

    public static void setCodiceIPA(IDServizio idS,IDAccordo idAccordoServizioParteComune,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
        // Imposto CodiceIPA memorizzato per Erogatore
        String codiceIPASoggettoErogatore = registryReader.getCodiceIPA(idS.getSoggettoErogatore());
        contextSICA.addMappingSoggettoSPCoopToCodiceIPA(idS.getSoggettoErogatore(), codiceIPASoggettoErogatore);
        // Imposto CodiceIPA memorizzato per referente Accordo di Servizio Parte Comune
        if(idAccordoServizioParteComune.getSoggettoReferente()!=null){
                IDSoggetto referenteParteComune = new IDSoggetto(idAccordoServizioParteComune.getSoggettoReferente().getTipo(),idAccordoServizioParteComune.getSoggettoReferente().getNome());
                String codiceIPAReferenteParteComune = registryReader.getCodiceIPA(referenteParteComune);
                contextSICA.addMappingSoggettoSPCoopToCodiceIPA(referenteParteComune, codiceIPAReferenteParteComune);
        }
	}
	
    
    
	/* ------------ SetURIAccordiServizioParteSpecifica -------------------------- */
    
    public static void setURI_APS(AccordoServizioParteComune as,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
	        // Imposto uriAPS per servizi componenti
	        if(as.getServizioComposto()!=null){
	                for(int i=0;i<as.getServizioComposto().sizeServizioComponenteList();i++){
	                        IDSoggetto soggettoErogatoreServizioComponente = 
	                                new IDSoggetto(as.getServizioComposto().getServizioComponente(i).getTipoSoggetto(),as.getServizioComposto().getServizioComponente(i).getNomeSoggetto());
	                        IDServizio idServizioComponente = IDServizioFactory.getInstance().getIDServizioFromValues(as.getServizioComposto().getServizioComponente(i).getTipo(),
                                    as.getServizioComposto().getServizioComponente(i).getNome(),
                                    soggettoErogatoreServizioComponente, 
                                    as.getServizioComposto().getServizioComponente(i).getVersione()); 
	                        contextSICA.addMappingServizioToUriAPS(registryReader, idServizioComponente);
	                }
	        }
	}
	
	
}
