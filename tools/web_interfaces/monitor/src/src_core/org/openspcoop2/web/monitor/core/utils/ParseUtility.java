package org.openspcoop2.web.monitor.core.utils;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;



public class ParseUtility {

	public static Integer parseVersione(String servizio) {
		if (servizio == null)
			return null;

		String[] res = StringUtils.split(servizio, ":");
		if (res.length > 1) {
			return Integer.parseInt(res[1]);
		}
		return null;
	}
	
	/**
	 * Effettua il parsing di una stringa del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeSoggetto
	 * @return il nome del soggetto (nomeSoggetto) se la stringa contiene il
	 *         carattere separatore, altrimenti l'intera stringa
	 */
	public static String parseNomeSoggetto(String tipoNomeSoggetto) {
		if (tipoNomeSoggetto == null)
			return null;

		String[] res = StringUtils.split(tipoNomeSoggetto, "/");
		if (res.length > 1) {
			return res[1];
		}
		return res[0];
	}

	/**
	 * Effettua il parsing di una stringa del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeSoggetto
	 * @return il tipo del soggetto (tipoSoggetto) se la stringa contiene il
	 *         carattere separatore, altrimenti l'intera stringa
	 */
	public static String parseTipoSoggetto(String tipoNomeSoggetto) {
		if (tipoNomeSoggetto == null)
			return null;

		String[] res = StringUtils.split(tipoNomeSoggetto, "/");
		return res[0];
	}
	
	public static IDServizio parseSoggettoServizio(String input){
		String tipoSoggetto = null; String nomeSoggetto =  null;
		String tipoServizio = null; String nomeServizio =  null; Integer versioneServizio =  1;
		// servizio
		if(input.contains(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA)){
			String[] split = input.split(" \\(");
			String tipoNomeSoggetto = split[0];

			String tipoNomeServizio = split[1].substring(0, split[1].indexOf(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA));

			tipoSoggetto = ParseUtility.parseTipoSoggetto(tipoNomeSoggetto);
			nomeSoggetto = ParseUtility.parseNomeSoggetto(tipoNomeSoggetto);

			tipoServizio = ParseUtility.parseTipoSoggetto(tipoNomeServizio);
			nomeServizio = ParseUtility.parseNomeSoggetto(tipoNomeServizio);
			versioneServizio = ParseUtility.parseVersione(nomeServizio);

		} else { // soggetto
			tipoSoggetto= ParseUtility.parseTipoSoggetto(input);
			nomeSoggetto = ParseUtility.parseNomeSoggetto(input);
			
		}
		
		return IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(tipoServizio, nomeServizio, tipoSoggetto, nomeSoggetto, versioneServizio);
	}
	
	public static String convertToSoggettoServizio(IDServizio idServizio){
		StringBuilder uri = new StringBuilder();
		uri.append(idServizio.getSoggettoErogatore().toString()); // soggetto

		
		if(idServizio.getNome() != null){ // servizio
			uri.append(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA);
			String nomeAsps = idServizio.getNome();

			String tipoAsps = idServizio.getTipo();
			if(tipoAsps != null)
				uri.append(tipoAsps).append(Costanti.SEPARATORE_TIPO_NOME);

			uri.append(nomeAsps);
			uri.append(Costanti.SEPARATORE_VERSIONE);
			uri.append(idServizio.getVersione());
			uri.append(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA);
		}
		return uri.toString();
	}
	
	public static IDServizio parseServizioSoggetto(String input) throws CoreException{
		String tipoSoggetto = null; String nomeSoggetto =  null;
		String tipoServizio = null; String nomeServizio =  null; Integer versioneServizio =  1;
		// servizio
		if(input.contains(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA)){
			String[] split = input.split(" \\(");
			String tipoNomeServizio = split[0];

			String tipoNomeSoggetto = split[1].substring(0, split[1].indexOf(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA));

			tipoSoggetto = ParseUtility.parseTipoSoggetto(tipoNomeSoggetto);
			nomeSoggetto = ParseUtility.parseNomeSoggetto(tipoNomeSoggetto);

			tipoServizio = ParseUtility.parseTipoSoggetto(tipoNomeServizio);
			nomeServizio = ParseUtility.parseNomeSoggetto(tipoNomeServizio);
			versioneServizio = ParseUtility.parseVersione(nomeServizio);

		} else { 
			// entrambe info servizio e soggetto obbligatorie nella forma servizio (soggetto)
			throw new CoreException("Input format ["+input+"] errato; atteso input nella forma 'tipo/nomeServizio (tipo/nomeSoggetto)'");
		}
		
		return IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(tipoServizio, nomeServizio, tipoSoggetto, nomeSoggetto, versioneServizio);
	}
	
	public static String convertToServizioSoggetto(IDServizio idServizio) throws CoreException{
		StringBuilder uri = new StringBuilder();
		if(idServizio.getNome() == null){ // servizio
			// entrambe info servizio e soggetto obbligatorie nella forma servizio (soggetto)
			throw new CoreException("Input format ["+idServizio+"] errato; atteso input per generare informazione 'tipo/nomeServizio (tipo/nomeSoggetto)'");
		}
		uri.append(idServizio.getTipo());
		uri.append(Costanti.SEPARATORE_TIPO_NOME);
		uri.append(idServizio.getNome());
		uri.append(Costanti.SEPARATORE_VERSIONE);
		uri.append(idServizio.getVersione());
		uri.append(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA);
		uri.append(idServizio.getSoggettoErogatore().toString()); // soggetto
		uri.append(Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA);
		
		return uri.toString();
	}
	
}
