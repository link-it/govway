package org.openspcoop2.pdd.core.jmx;


public class InformazioniStatoPorta {

	public String formatStatoPorta(String versionePdD, 
			String versioneBaseDati,
			String confDir, String versioneJava,
			String infoDatabase, String infoProtocolli,
			InformazioniStatoPortaCache ... cache){
		
		StringBuffer bf = new StringBuffer();
		
		// informazioni generali
		
		bf.append("\n");
		bf.append("===========================\n");
		bf.append("Informazioni Generali\n");
		bf.append("===========================\n");
		bf.append("\n");
		format(bf, versionePdD, "Versione PdD");
		bf.append("\n");
		format(bf, versioneBaseDati, "Versione BaseDati");
		bf.append("\n");
		format(bf, confDir, "Directory Configurazione");
		bf.append("\n");
		format(bf, versioneJava, "Versione Java");
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Database\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(infoDatabase);
		bf.append("\n");
		
		bf.append("===========================\n");
		bf.append("Informazioni Protocolli\n");
		bf.append("===========================\n");
		bf.append("\n");
		bf.append(infoProtocolli);
		bf.append("\n");
		bf.append("\n");
		
		if(cache!=null){
			for (int i = 0; i < cache.length; i++) {
				bf.append("===========================\n");
				bf.append("Cache "+cache[i].getNomeCache()+"\n");
				bf.append("===========================\n");
				bf.append("\n");
				format(bf, cache[i].isEnabled()+"", "Abilitata");
				if(cache[i].getStatoCache()!=null){
					bf.append(cache[i].getStatoCache());
				}
				bf.append("\n");
				bf.append("\n");
			}
		}
		
		return bf.toString();
	}
	
	private void format(StringBuffer bf,String v,String label){
		if(v==null || "".equals(v)){
			bf.append(label+": informazione non disponibile\n");
		}
		else{
			if(v.contains("\n")){
				bf.append(label+": \n");
				String [] tmp = v.split("\n");
				for (int i = 0; i < tmp.length; i++) {
					bf.append("\t- "+tmp[i]+"\n");
				}
			}else{
				bf.append(label+": "+v+"\n");
			}
		}
	}
}
