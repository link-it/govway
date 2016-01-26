/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.utils.id;

import org.openspcoop2.utils.id.apache.IdentifierGenerator;
import org.openspcoop2.utils.id.apache.serial.AlphanumericGenerator;
import org.openspcoop2.utils.id.apache.serial.EnumTypeGenerator;
import org.openspcoop2.utils.id.apache.serial.LongGenerator;
import org.openspcoop2.utils.id.apache.serial.NumericGenerator;
import org.openspcoop2.utils.id.apache.serial.PrefixedAlphanumericGenerator;
import org.openspcoop2.utils.id.apache.serial.PrefixedLeftPaddedNumericGenerator;
import org.openspcoop2.utils.id.apache.serial.PrefixedNumericGenerator;
import org.openspcoop2.utils.id.apache.serial.TimeBasedAlphanumericIdentifierGenerator;


/**
 * Implementazione tramite java.util.UUID
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ApacheIdentifierGenerator implements IUniqueIdentifierGenerator {

	private IdentifierGenerator identifierGenerator;
	
	@Override
	public IUniqueIdentifier newID() throws UniqueIdentifierException {
		try{
			return new BaseUniqueIdentifier(this.identifierGenerator.nextIdentifier());
		}catch(Exception e){
			throw new UniqueIdentifierException(e.getMessage(),e);
		}
	}

	@Override
	public IUniqueIdentifier convertFromString(String value)
			throws UniqueIdentifierException {
		BaseUniqueIdentifier id = new BaseUniqueIdentifier(value);
		return id;
	}

	public void initialize(ApacheGeneratorConfiguration config) throws UniqueIdentifierException {
		this.init(config);
	}
	
	@Override
	public void init(Object... o) throws UniqueIdentifierException {
		if(o==null || o.length<1){
			throw new UniqueIdentifierException("Devi indicare (tramite il primo parametro) la configurazione di ApacheGenerator tramite la classe: "+ApacheGeneratorConfiguration.class.getName());
		}
		if(!(o[0] instanceof ApacheGeneratorConfiguration)){
			throw new UniqueIdentifierException("Devi indicare (tramite il primo parametro) la configurazione di ApacheGenerator tramite la classe: "+ApacheGeneratorConfiguration.class.getName());
		}
		
		ApacheGeneratorConfiguration config = (ApacheGeneratorConfiguration) o[0];
		if(config.getType()==null){
			throw new UniqueIdentifierException("Il tipo di ApacheGenerator deve essere indicato nella configurazione");
		}
		
		try{
			switch (config.getType()) {
			case ALPHANUMERIC:
			case PREFIXED_ALPHANUMERIC:
				
				if(EnumTypeGenerator.PREFIXED_ALPHANUMERIC.equals(config.getType())){
					if(config.getPrefix()==null){
						throw new UniqueIdentifierException("Il tipo di ApacheGenerator indicato nella configurazione richiede un prefisso");
					}
				}
				
				if(config.getInitialStringValue()!=null){
					if(EnumTypeGenerator.ALPHANUMERIC.equals(config.getType())){
						this.identifierGenerator = new AlphanumericGenerator(config.isWrap(),config.getInitialStringValue());
					}
					else{
						this.identifierGenerator = new PrefixedAlphanumericGenerator(config.getPrefix(),config.isWrap(),config.getInitialStringValue());
					}
				}
				else if(config.getSize()!=null){
					if(EnumTypeGenerator.ALPHANUMERIC.equals(config.getType())){
						this.identifierGenerator = new AlphanumericGenerator(config.isWrap(),config.getSize());
					}
					else{
						this.identifierGenerator = new PrefixedAlphanumericGenerator(config.getPrefix(),config.isWrap(),config.getSize());
					}
				}
				else{
					if(EnumTypeGenerator.ALPHANUMERIC.equals(config.getType())){
						this.identifierGenerator = new AlphanumericGenerator(config.isWrap());
					}
					else{
						this.identifierGenerator = new PrefixedAlphanumericGenerator(config.getPrefix(),config.isWrap());
					}
				}
				
				if(config.getEndDigit()!=null && config.getStartDigit()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setStartEndDigit(config.getStartDigit(), config.getEndDigit());
				}
				else if(config.getEndDigit()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setEndDigit(config.getEndDigit());
				}
				else if(config.getStartDigit()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setStartDigit(config.getStartDigit());
				}
				
				if(config.getEndLetter()!=null && config.getStartLetter()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setStartEndChar(config.getStartLetter(), config.getEndLetter());
				}
				else if(config.getEndLetter()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setEndChar(config.getEndLetter());
				}
				else if(config.getStartLetter()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setStartChar(config.getStartLetter());
				}
						
				if(config.isEnableLowerCaseLetter()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setLowerChar(config.isEnableLowerCaseLetter());	
				}
				if(config.isEnableUpperCaseLetter()!=null){
					((AlphanumericGenerator)this.identifierGenerator).setUpperChar(config.isEnableUpperCaseLetter());	
				}
				
				break;
				
			case NUMERIC:
			case PREFIXED_NUMERIC:
				
				if(EnumTypeGenerator.PREFIXED_NUMERIC.equals(config.getType())){
					if(config.getPrefix()==null){
						throw new UniqueIdentifierException("Il tipo di ApacheGenerator indicato nella configurazione richiede un prefisso");
					}
				}
				if(config.getInitalLongValue()==null){
					throw new UniqueIdentifierException("Il tipo di ApacheGenerator indicato nella configurazione richiede il valore iniziale di tipo Long ('initialLongValue')");
				}
				

				if(EnumTypeGenerator.NUMERIC.equals(config.getType())){
					this.identifierGenerator = new NumericGenerator(config.isWrap(),config.getInitalLongValue());
				}
				else{
					this.identifierGenerator = new PrefixedNumericGenerator(config.getPrefix(),config.isWrap(),config.getInitalLongValue());
				}
				
				break;
	
			case PREFIXED_LEFT_PADDED_NUMERIC:
				
				if(EnumTypeGenerator.PREFIXED_ALPHANUMERIC.equals(config.getType())){
					if(config.getPrefix()==null){
						throw new UniqueIdentifierException("Il tipo di ApacheGenerator indicato nella configurazione richiede un prefisso");
					}
				}
				if(config.getSize()==null){
					throw new UniqueIdentifierException("Il tipo di ApacheGenerator indicato nella configurazione richiede la dimensione ('size')");
				}
				
				this.identifierGenerator = new PrefixedLeftPaddedNumericGenerator(config.getPrefix(),config.isWrap(),config.getSize());
				
				break;
				
			case TIME_BASED_ALPHANUMERIC:
								
				this.identifierGenerator = new TimeBasedAlphanumericIdentifierGenerator();
				
				break;
				
			case LONG:
				
				if(config.getInitalLongValue()==null){
					throw new UniqueIdentifierException("Il tipo di ApacheGenerator indicato nella configurazione richiede il valore iniziale di tipo Long ('initialLongValue')");
				}
				
				this.identifierGenerator = new LongGenerator(config.isWrap(),config.getInitalLongValue());
				
				break;
				
			default:
				break;
			}
		}catch(Exception e){
			throw new UniqueIdentifierException(e.getMessage(),e);
		}
	}

}
