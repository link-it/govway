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

package org.openspcoop2.pdd.monitor.driver;

import org.openspcoop2.pdd.monitor.Filtro;

/**
*
* FilterSearch
* 
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
* 
*/
public class FilterSearch extends Filtro {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long limit = -1;
	private long offset = -1;

	
	public long getOffset() {
		return this.offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public long getLimit() {
		return this.limit;
	}
	public void setLimit(long limit) {
		this.limit = limit;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.getBusta()!=null){
			if(this.getBusta().getMittente()!=null){
				if(this.getBusta().getMittente().getTipo()!=null && !"".equals(this.getBusta().getMittente().getTipo()) &&
						this.getBusta().getMittente().getNome()!=null && !"".equals(this.getBusta().getMittente().getNome())){
					bf.append("Mittente(");
					bf.append(this.getBusta().getMittente().getTipo()+"/"+this.getBusta().getMittente().getNome());
					bf.append(")");
				}else{
					if(this.getBusta().getMittente().getTipo()!=null && !"".equals(this.getBusta().getMittente().getTipo())){
						bf.append("TipoMittente(");
						bf.append(this.getBusta().getMittente().getTipo());
						bf.append(")");
					}
					if(this.getBusta().getMittente().getNome()!=null && !"".equals(this.getBusta().getMittente().getNome())){
						if(bf.length()>0){
							bf.append(" ");
						}
						bf.append("Mittente(");
						bf.append(this.getBusta().getMittente().getNome());
						bf.append(")");
					}
				}
			}
			if(bf.length()>0){
				bf.append(" ");
			}
			if(this.getBusta().getDestinatario()!=null){
				if(this.getBusta().getDestinatario().getTipo()!=null && !"".equals(this.getBusta().getDestinatario().getTipo()) &&
						this.getBusta().getDestinatario().getNome()!=null && !"".equals(this.getBusta().getDestinatario().getNome())){
					if(bf.length()>0){
						bf.append(" ");
					}
					bf.append("Destinatario(");
					bf.append(this.getBusta().getDestinatario().getTipo()+"/"+this.getBusta().getDestinatario().getNome());
					bf.append(")");
				}else{
					if(this.getBusta().getDestinatario().getTipo()!=null && !"".equals(this.getBusta().getDestinatario().getTipo())){
						if(bf.length()>0){
							bf.append(" ");
						}
						bf.append("TipoDestinatario(");
						bf.append(this.getBusta().getDestinatario().getTipo());
						bf.append(")");
					}
					if(this.getBusta().getDestinatario().getNome()!=null && !"".equals(this.getBusta().getDestinatario().getNome())){
						if(bf.length()>0){
							bf.append(" ");
						}
						bf.append("Destinatario(");
						bf.append(this.getBusta().getDestinatario().getNome());
						bf.append(")");
					}
				}
			}
			if(this.getBusta().getServizio()!=null){
				if(this.getBusta().getServizio().getTipo()!=null && !"".equals(this.getBusta().getServizio().getTipo()) &&
						this.getBusta().getServizio().getNome()!=null && !"".equals(this.getBusta().getServizio().getNome())){
					if(bf.length()>0){
						bf.append(" ");
					}
					bf.append("Servizio(");
					bf.append(this.getBusta().getServizio().getTipo()+"/"+this.getBusta().getServizio().getNome());
					bf.append(")");
				}else{
					if(this.getBusta().getServizio().getTipo()!=null && !"".equals(this.getBusta().getServizio().getTipo())){
						if(bf.length()>0){
							bf.append(" ");
						}
						bf.append("TipoServizio(");
						bf.append(this.getBusta().getServizio().getTipo());
						bf.append(")");
					}
					if(this.getBusta().getServizio().getNome()!=null && !"".equals(this.getBusta().getServizio().getNome())){
						if(bf.length()>0){
							bf.append(" ");
						}
						bf.append("Servizio(");
						bf.append(this.getBusta().getServizio().getNome());
						bf.append(")");
					}
				}
			}
			if(this.getBusta().getAzione()!=null && !"".equals(this.getBusta().getAzione())){
				if(bf.length()>0){
					bf.append(" ");
				}
				bf.append("Azione(");
				bf.append(this.getBusta().getAzione());
				bf.append(")");
			}
			if(this.getBusta().getProfiloCollaborazione()!=null && !"".equals(this.getBusta().getProfiloCollaborazione())){
				if(bf.length()>0){
					bf.append(" ");
				}
				bf.append("ProfiloCollaborazione(");
				bf.append(this.getBusta().getProfiloCollaborazione());
				bf.append(")");
			}		
			if(this.getBusta().getCollaborazione()!=null && !"".equals(this.getBusta().getCollaborazione())){
				if(bf.length()>0){
					bf.append(" ");
				}
				bf.append("Collaborazione(");
				bf.append(this.getBusta().getCollaborazione());
				bf.append(")");
			}
			if(this.getBusta().getRiferimentoMessaggio()!=null && !"".equals(this.getBusta().getRiferimentoMessaggio())){
				if(bf.length()>0){
					bf.append(" ");
				}
				bf.append("RiferimentoMessaggio(");
				bf.append(this.getBusta().getRiferimentoMessaggio());
				bf.append(")");
			}
		}
		if(this.getSoggettoList()!=null && this.getSoggettoList().size()>0){
			for(int i=0; i<this.getSoggettoList().size(); i++){
				if(bf.length()>0){
					bf.append(" ");
				}
				bf.append("FiltroSoggetto["+i+"](");
				bf.append(this.getSoggettoList().get(i).getTipo()+"/"+this.getSoggettoList().get(i).getNome());
				bf.append(")");
			}
		}
		if(this.getIdMessaggio()!=null && !"".equals(this.getIdMessaggio())){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("IDMessaggio(");
			bf.append(this.getIdMessaggio());
			bf.append(")");
		}
		if(this.getMessagePattern()!=null && !"".equals(this.getMessagePattern())){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Pattern(");
			bf.append(this.getMessagePattern());
			bf.append(")");
		}
		if(this.getSoglia()>0){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Soglia(");
			bf.append(this.getSoglia());
			bf.append(")");
		}
		if(this.getStato()!=null && !"".equals(this.getStato())){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Stato(");
			bf.append(this.getStato());
			bf.append(")");
		}
		if(this.getTipo()!=null && !"".equals(this.getTipo())){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("Tipo(");
			bf.append(this.getTipo());
			bf.append(")");
		}

		if(bf.length()>255){
			return bf.substring(0, 250) +"...";
		}
		else{
			return bf.toString();
		}
	}
}
