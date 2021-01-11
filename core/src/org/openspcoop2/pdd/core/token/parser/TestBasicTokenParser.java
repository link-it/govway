/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.token.parser;

import java.util.Date;

import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.SorgenteInformazioniToken;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * TestBasicTokenParser
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TestBasicTokenParser {

	public static void main(String[] args) throws Exception {
		
		TipologiaClaims tipoTest = null;
		if(args!=null && args.length>0){
			tipoTest = TipologiaClaims.valueOf(args[0]);
		}
		
		
		// Test 1
		if(tipoTest==null || TipologiaClaims.GOOGLE.equals(tipoTest))
		{
			System.out.println("Test BasicTokenParser, access token google dopo introspection ...");
			String cliendId = "18192.apps.googleusercontent.com";
			String audience = "23223.apps.googleusercontent.com";
			String subject = "10623542342342323";
			String s1 = "https://www.googleapis.com/auth/userinfo.email";
			String s2 = "https://www.googleapis.com/auth/userinfo.profile";
			String s3 = "openid";
			String mail = "example@gmail.com";
			String rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \"1602666852\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			BasicTokenParser tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			InformazioniToken info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			if(!info.isValid()) {
				throw new Exception("Atteso token valido");
			}
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=9) {
				throw new Exception("Claims attesi 9, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("azp")){
				throw new Exception("Atteso azp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("aud")){
				throw new Exception("Atteso aud non presente tra i claims");
			}
			if(!info.getClaims().containsKey("sub")){
				throw new Exception("Atteso sub non presente tra i claims");
			}
			if(!info.getClaims().containsKey("scope")){
				throw new Exception("Atteso scope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("exp")){
				throw new Exception("Atteso exp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("email")){
				throw new Exception("Atteso email non presente tra i claims");
			}
			
			if(!cliendId.equals(info.getClientId())){
				throw new Exception("Atteso azp '"+cliendId+"' trovato '"+info.getClientId()+"'");
			}
			
			if(info.getAud()==null) {
				throw new Exception("Audience attesi non trovato");
			}
			if(info.getAud().size()!=1) {
				throw new Exception("Audience attesi 1, trovati '"+info.getAud().size()+"'");
			}
			boolean find = false;
			for (String s : info.getAud()) {
				if(audience.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience+"' non trovato");
			}
			
			if(!subject.equals(info.getSub())){
				throw new Exception("Atteso subject '"+subject+"' trovato '"+info.getSub()+"'");
			}
			
			if(info.getScopes()==null) {
				throw new Exception("Scope attesi non trovato");
			}
			if(info.getScopes().size()!=3) {
				throw new Exception("Scope attesi 3, trovati '"+info.getScopes().size()+"'");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s1.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s1+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s2+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s3.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s3+"' non trovato");
			}
			
			Date d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			Date now = DateManager.getDate();
			now = new Date(now.getTime() + (1000*60*60*2));
			if(now.before(d)){
				throw new Exception("Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			if(info.getIat()!=null) {
				throw new Exception("Claim iat non atteso");
			}
			if(info.getIss()!=null) {
				throw new Exception("Claim iss non atteso");
			}
			if(info.getNbf()!=null) {
				throw new Exception("Claim nbf non atteso");
			}
			if(info.getRoles()!=null) {
				throw new Exception("Ruoli non attesi");
			}
			if(info.getUsername()!=null) {
				throw new Exception("Claim username non atteso");
			}
			
			if(info.getUserInfo()!=null) {
				if(!mail.equals(info.getUserInfo().getEMail())){
					throw new Exception("Atteso mail '"+mail+"' trovato '"+info.getUserInfo().getEMail()+"'");
				}
				if(info.getUserInfo().getFamilyName()!=null) {
					throw new Exception("Claim family name non atteso");
				}
				if(info.getUserInfo().getFirstName()!=null) {
					throw new Exception("Claim first name non atteso");
				}
				if(info.getUserInfo().getFullName()!=null) {
					throw new Exception("Claim full name non atteso");
				}
				if(info.getUserInfo().getMiddleName()!=null) {
					throw new Exception("Claim middle name non atteso");
				}
			}
			else {
				throw new Exception("Atteso UserInfo");
			}
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection ok");
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection (check date) ...");
			
			long data = (DateManager.getDate().getTime() + (1000*30)) / 1000; // scade tra 30 secondi
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \""+data+"\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*20));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection (check date) ok");
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection (check date con expires con max long value) ...");
			
			data = Long.MAX_VALUE/1000;
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \""+data+"\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection (check date con expires con max long value) ok");

			System.out.println("Test BasicTokenParser, access token google dopo introspection (check date con expires con overflow long value) ...");
			
			data = Long.MAX_VALUE/1000;
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \"111111111"+data+"\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection (check date con expires con overflow long value) ok");
						
			System.out.println("Test BasicTokenParser, access token google dopo introspection (non valido) ...");
			
			data = Long.MAX_VALUE/1000;
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \""+data+"\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			try {
				info = new InformazioniToken(500, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
				if(info.isValid()) {
					throw new Exception("Attes token non valido (trasporto)");
				}
			}catch(Exception e) {
				if(!e.getMessage().startsWith("Connessione terminata con errore (codice trasporto: 500)")) {
					throw e;
				}
			}
			
			rawResponse = "{\"error\": \"errore\", \"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \""+data+"\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			info = new InformazioniToken(400, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			if(info.isValid()) {
				throw new Exception("Attes token non valido (claim error)");
			}
			
			rawResponse = "{\"error_description\": \"errore\", \"azp\": \""+cliendId+"\", \"aud\": \""+audience+"\", \"sub\": \""+subject+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"exp\": \""+data+"\",  \"expires_in\": \"3113\",  \"email\": \""+mail+"\",  \"email_verified\": \"true\",  \"access_type\": \"offline\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			info = new InformazioniToken(401, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			if(info.isValid()) {
				throw new Exception("Attes token non valido (claim error_description)");
			}
			
			System.out.println("Test BasicTokenParser, access token google dopo introspection (non valido) ok");
			
		}
		
		
		
		if(tipoTest==null || TipologiaClaims.GOOGLE.equals(tipoTest))
		{
			System.out.println("Test BasicTokenParser, access token google dopo userinfo ...");
			String nomeCognome = "Paolo Rossi";
			String nome = "Paolo";
			String cognome = "Rossi";
			String subject = "10623542342342323";
			String mail = "example@gmail.com";
			String rawResponse = "{\"sub\": \""+subject+"\", \"name\": \""+nomeCognome+"\",\n  \"given_name\": \""+nome+"\",\n  \"family_name\": \""+cognome+"\",\n "+
					" \"picture\": \"https://lh6.googleusercontent.com/-5-xqlP4VKB8/AAAAAAAAA/photo.jpg\",\n "+
					" \"email\": \""+mail+"\",\n  \"email_verified\": true,\n  \"locale\": \"it\"\n}";
			BasicTokenParser tokenParser = new BasicTokenParser(TipologiaClaims.GOOGLE);
			InformazioniToken info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=8) {
				throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("sub")){
				throw new Exception("Atteso sub non presente tra i claims");
			}
			if(!info.getClaims().containsKey("name")){
				throw new Exception("Atteso name non presente tra i claims");
			}
			if(!info.getClaims().containsKey("given_name")){
				throw new Exception("Atteso given_name non presente tra i claims");
			}
			if(!info.getClaims().containsKey("family_name")){
				throw new Exception("Atteso scope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("family_name")){
				throw new Exception("Atteso exp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("email")){
				throw new Exception("Atteso email non presente tra i claims");
			}
			
			if(!subject.equals(info.getSub())){
				throw new Exception("Atteso subject '"+subject+"' trovato '"+info.getSub()+"'");
			}
			
			if(info.getUserInfo()!=null) {
				if(!nomeCognome.equals(info.getUserInfo().getFullName())){
					throw new Exception("Atteso nome e cognome '"+nomeCognome+"' trovato '"+info.getUserInfo().getFullName()+"'");
				}
				if(!nome.equals(info.getUserInfo().getFirstName())){
					throw new Exception("Atteso nome '"+nome+"' trovato '"+info.getUserInfo().getFirstName()+"'");
				}
				if(!cognome.equals(info.getUserInfo().getFamilyName())){
					throw new Exception("Atteso cognome '"+cognome+"' trovato '"+info.getUserInfo().getFamilyName()+"'");
				}
				if(!mail.equals(info.getUserInfo().getEMail())){
					throw new Exception("Atteso mail '"+mail+"' trovato '"+info.getUserInfo().getEMail()+"'");
				}
				if(info.getUserInfo().getMiddleName()!=null) {
					throw new Exception("Claim middle name non atteso");
				}
			}
			else {
				throw new Exception("Atteso UserInfo");
			}
			
			if(info.getAud()!=null) {
				throw new Exception("Audience non atteso");
			}
						
			if(info.getScopes()!=null) {
				throw new Exception("Scope non attesi");
			}
						
			Date d = info.getExp();
			if(d!=null) {
				throw new Exception("Data di scadenza non attesa");
			}
					
			if(info.getIat()!=null) {
				throw new Exception("Claim iat non atteso");
			}
			if(info.getIss()!=null) {
				throw new Exception("Claim iss non atteso");
			}
			if(info.getNbf()!=null) {
				throw new Exception("Claim nbf non atteso");
			}
			if(info.getRoles()!=null) {
				throw new Exception("Ruoli non attesi");
			}
			if(!nomeCognome.equals(info.getUsername())){
				throw new Exception("Atteso username '"+nomeCognome+"' trovato '"+info.getUsername()+"'");
			}
			
			System.out.println("Test BasicTokenParser, access token google dopo userinfo ok");
			
		}
		
		
		
		// Test 3
		if(tipoTest==null || TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662.equals(tipoTest))
		{
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662'...");
			String cliendId = "18192.apps";
			String audience = "23223.apps";
			String audience2 = "7777.apps";
			String issuer = "testAuthEnte";
			String username = "Utente di Prova";
			String subject = "10623542342342323";
			String s1 = "https://userinfo.email";
			String s2 = "https://userinfo.profile";
			String s3 = "altro";
			String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			Date now = DateManager.getDate();
			Date iat = new Date( (now.getTime()/1000)*1000);
			Date nbf = new Date(iat.getTime() - (1000*20));
			Date exp = new Date(iat.getTime() + (1000*60));
			String rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			BasicTokenParser tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			InformazioniToken info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=10) {
				throw new Exception("Claims attesi 10, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("client_id")){
				throw new Exception("Atteso client_id non presente tra i claims");
			}
			if(!info.getClaims().containsKey("aud")){
				throw new Exception("Atteso aud non presente tra i claims");
			}
			if(!info.getClaims().containsKey("sub")){
				throw new Exception("Atteso sub non presente tra i claims");
			}
			if(!info.getClaims().containsKey("iss")){
				throw new Exception("Atteso iss non presente tra i claims");
			}
			if(!info.getClaims().containsKey("username")){
				throw new Exception("Atteso username non presente tra i claims");
			}
			if(!info.getClaims().containsKey("scope")){
				throw new Exception("Atteso scope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("iat")){
				throw new Exception("Atteso iat non presente tra i claims");
			}
			if(!info.getClaims().containsKey("nbf")){
				throw new Exception("Atteso nbf non presente tra i claims");
			}
			if(!info.getClaims().containsKey("exp")){
				throw new Exception("Atteso exp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("jti")){
				throw new Exception("Atteso jti non presente tra i claims");
			}
			
			if(!cliendId.equals(info.getClientId())){
				throw new Exception("Atteso client_id '"+cliendId+"' trovato '"+info.getClientId()+"'");
			}
			
			if(info.getAud()==null) {
				throw new Exception("Audience attesi non trovato");
			}
			if(info.getAud().size()!=2) {
				throw new Exception("Audience attesi 2, trovati '"+info.getAud().size()+"'");
			}
			boolean find = false;
			for (String s : info.getAud()) {
				if(audience.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience+"' non trovato");
			}
			find = false;
			for (String s : info.getAud()) {
				if(audience2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience2+"' non trovato");
			}
			
			if(!subject.equals(info.getSub())){
				throw new Exception("Atteso subject '"+subject+"' trovato '"+info.getSub()+"'");
			}
			if(!issuer.equals(info.getIss())){
				throw new Exception("Atteso issuer '"+issuer+"' trovato '"+info.getIss()+"'");
			}
			if(!username.equals(info.getUsername())){
				throw new Exception("Atteso username '"+username+"' trovato '"+info.getUsername()+"'");
			}
			
			if(info.getScopes()==null) {
				throw new Exception("Scope attesi non trovato");
			}
			if(info.getScopes().size()!=3) {
				throw new Exception("Scope attesi 3, trovati '"+info.getScopes().size()+"'");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s1.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s1+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s2+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s3.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s3+"' non trovato");
			}
			
			Date d = info.getIat();
			if(d==null) {
				throw new Exception("Data non trovata");
			}
			if(!d.equals(iat)) {
				throw new Exception("Claim iat differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(iat)+" iat="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getNbf();
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			if(!d.equals(nbf)) {
				throw new Exception("Claim nbf differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			if(now.before(d)){
				throw new Exception("Token non utilizzabile, non atteso now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(exp)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			if(!now.before(d)){
				throw new Exception("Token scaduto non atteso now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			if(!jti.equals(tokenParser.getJWTIdentifier())){
				throw new Exception("Atteso jti '"+jti+"' trovato '"+tokenParser.getJWTIdentifier()+"'");
			}
			
			if(info.getRoles()!=null) {
				throw new Exception("Ruoli non attesi");
			}
			
			if(info.getUserInfo()!=null) {
				if(info.getUserInfo().getFamilyName()!=null) {
					throw new Exception("Claim family name non atteso");
				}
				if(info.getUserInfo().getFirstName()!=null) {
					throw new Exception("Claim first name non atteso");
				}
				if(info.getUserInfo().getFullName()!=null) {
					throw new Exception("Claim full name non atteso");
				}
				if(info.getUserInfo().getMiddleName()!=null) {
					throw new Exception("Claim middle name non atteso");
				}
				if(info.getUserInfo().getEMail()!=null) {
					throw new Exception("Claim mail non atteso");
				}
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date(iat.getTime() + (1000*60));
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*20));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getNbf();
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			now = DateManager.getDate();
			if(now.before(d)){
				throw new Exception("(1) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() - (1000*20));
			if(now.before(d)){
				throw new Exception("(2) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() - (1000*60));
			if(!now.before(d)){
				throw new Exception("(3) Atteso token non utilizzabile, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date con expires con max long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date((Long.MAX_VALUE/1000)*1000);
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date con expires con max long value) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date con expires con overflow long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date((Long.MAX_VALUE/1000)*1000);
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date con expires con overflow long value) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date con nbf con max long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date((Long.MAX_VALUE/1000)*1000);
			exp = new Date(iat.getTime() + (1000*60));
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			
			d = info.getNbf();
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			if(!d.equals(nbf)) {
				throw new Exception("Claim nbf differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (check date con nbf con max long value) ok");
			
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (non valido) ...");
			
			rawResponse = "{\"client_id\":\"nonImportante\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			try {
				info = new InformazioniToken(500, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
				if(info.isValid()) {
					throw new Exception("Attes token non valido (trasporto)");
				}
			}catch(Exception e) {
				if(!e.getMessage().startsWith("Connessione terminata con errore (codice trasporto: 500)")) {
					throw e;
				}
			}
			
			rawResponse = "{\"active\": \"true\", \"client_id\":\"nonImportante\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			if(!info.isValid()) {
				throw new Exception("Atteso token valido");
			}
			
			rawResponse = "{\"active\": \"false\", \"client_id\":\"nonImportante\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			if(info.isValid()) {
				throw new Exception("Atteso token non valido");
			}
			
			rawResponse = "{\"active\": \"valoreNonBooleano\", \"client_id\":\"nonImportante\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.INTROSPECTION_RESPONSE_RFC_7662);
			info = new InformazioniToken(200, SorgenteInformazioniToken.INTROSPECTION, rawResponse, tokenParser);
			if(info.isValid()) {
				throw new Exception("Atteso token non valido (not boolean value)");
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'INTROSPECTION_RESPONSE_RFC_7662' (non valido) ok");
			
		}
		
		
		
		
		
		
		// Test 4
		if(tipoTest==null || TipologiaClaims.JSON_WEB_TOKEN_RFC_7519.equals(tipoTest))
		{
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519'...");
			String cliendId = "18192.apps";
			String audience = "23223.apps";
			String audience2 = "7777.apps";
			String issuer = "testAuthEnte";
			String username = "Utente di Prova";
			String subject = "10623542342342323";
			String s1 = "https://userinfo.email";
			String s2 = "https://userinfo.profile";
			String s3 = "altro";
			String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			Date now = DateManager.getDate();
			Date iat = new Date( (now.getTime()/1000)*1000);
			Date nbf = new Date(iat.getTime() - (1000*20));
			Date exp = new Date(iat.getTime() + (1000*60));
			String rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			BasicTokenParser tokenParser = new BasicTokenParser(TipologiaClaims.JSON_WEB_TOKEN_RFC_7519);
			InformazioniToken info = new InformazioniToken(200, SorgenteInformazioniToken.JWT, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=10) {
				throw new Exception("Claims attesi 10, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("client_id")){
				throw new Exception("Atteso client_id non presente tra i claims");
			}
			if(!info.getClaims().containsKey("aud")){
				throw new Exception("Atteso aud non presente tra i claims");
			}
			if(!info.getClaims().containsKey("sub")){
				throw new Exception("Atteso sub non presente tra i claims");
			}
			if(!info.getClaims().containsKey("iss")){
				throw new Exception("Atteso iss non presente tra i claims");
			}
			if(!info.getClaims().containsKey("username")){
				throw new Exception("Atteso username non presente tra i claims");
			}
			if(!info.getClaims().containsKey("scope")){
				throw new Exception("Atteso scope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("iat")){
				throw new Exception("Atteso iat non presente tra i claims");
			}
			if(!info.getClaims().containsKey("nbf")){
				throw new Exception("Atteso nbf non presente tra i claims");
			}
			if(!info.getClaims().containsKey("exp")){
				throw new Exception("Atteso exp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("jti")){
				throw new Exception("Atteso jti non presente tra i claims");
			}
			
			// non supportato
			/*if(!cliendId.equals(info.getClientId())){
				throw new Exception("Atteso client_id '"+cliendId+"' trovato '"+info.getClientId()+"'");
			}*/
			if(info.getClientId()!=null) {
				throw new Exception("ClientId non atteso");
			}
			
			if(info.getAud()==null) {
				throw new Exception("Audience attesi non trovato");
			}
			if(info.getAud().size()!=2) {
				throw new Exception("Audience attesi 2, trovati '"+info.getAud().size()+"'");
			}
			boolean find = false;
			for (String s : info.getAud()) {
				if(audience.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience+"' non trovato");
			}
			find = false;
			for (String s : info.getAud()) {
				if(audience2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience2+"' non trovato");
			}
			
			if(!subject.equals(info.getSub())){
				throw new Exception("Atteso subject '"+subject+"' trovato '"+info.getSub()+"'");
			}
			if(!issuer.equals(info.getIss())){
				throw new Exception("Atteso issuer '"+issuer+"' trovato '"+info.getIss()+"'");
			}
			
			// non supportato
			/*
			if(!username.equals(info.getUsername())){
				throw new Exception("Atteso username '"+username+"' trovato '"+info.getUsername()+"'");
			}
			*/
			if(info.getUsername()!=null) {
				throw new Exception("Username non atteso");
			}
			
			// non supportato
			if(info.getScopes()!=null) {
				throw new Exception("Scope non attesi");
			}
			/*
			if(info.getScopes()==null) {
				throw new Exception("Scope attesi non trovato");
			}
			if(info.getScopes().size()!=3) {
				throw new Exception("Scope attesi 3, trovati '"+info.getScopes().size()+"'");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s1.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s1+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s2+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s3.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s3+"' non trovato");
			}
			*/
			
			Date d = info.getIat();
			if(d==null) {
				throw new Exception("Data non trovata");
			}
			if(!d.equals(iat)) {
				throw new Exception("Claim iat differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(iat)+" iat="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getNbf();
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			if(!d.equals(nbf)) {
				throw new Exception("Claim nbf differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			if(now.before(d)){
				throw new Exception("Token non utilizzabile, non atteso now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(exp)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			if(!now.before(d)){
				throw new Exception("Token scaduto non atteso now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			if(!jti.equals(tokenParser.getJWTIdentifier())){
				throw new Exception("Atteso jti '"+jti+"' trovato '"+tokenParser.getJWTIdentifier()+"'");
			}
			
			if(info.getRoles()!=null) {
				throw new Exception("Ruoli non attesi");
			}
			
			if(info.getUserInfo()!=null) {
				if(info.getUserInfo().getFamilyName()!=null) {
					throw new Exception("Claim family name non atteso");
				}
				if(info.getUserInfo().getFirstName()!=null) {
					throw new Exception("Claim first name non atteso");
				}
				if(info.getUserInfo().getFullName()!=null) {
					throw new Exception("Claim full name non atteso");
				}
				if(info.getUserInfo().getMiddleName()!=null) {
					throw new Exception("Claim middle name non atteso");
				}
				if(info.getUserInfo().getEMail()!=null) {
					throw new Exception("Claim mail non atteso");
				}
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date(iat.getTime() + (1000*60));
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.JSON_WEB_TOKEN_RFC_7519);
			info = new InformazioniToken(200, SorgenteInformazioniToken.JWT, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*20));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getNbf();
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			now = DateManager.getDate();
			if(now.before(d)){
				throw new Exception("(1) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() - (1000*20));
			if(now.before(d)){
				throw new Exception("(2) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() - (1000*60));
			if(!now.before(d)){
				throw new Exception("(3) Atteso token non utilizzabile, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date con expires con max long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date((Long.MAX_VALUE/1000)*1000);
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.JSON_WEB_TOKEN_RFC_7519);
			info = new InformazioniToken(200, SorgenteInformazioniToken.JWT, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date con expires con max long value) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date con expires con overflow long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date((Long.MAX_VALUE/1000)*1000);
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.JSON_WEB_TOKEN_RFC_7519);
			info = new InformazioniToken(200, SorgenteInformazioniToken.JWT, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date con expires con overflow long value) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date con nbf con max long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date((Long.MAX_VALUE/1000)*1000);
			exp = new Date(iat.getTime() + (1000*60));
			rawResponse = "{\"client_id\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.JSON_WEB_TOKEN_RFC_7519);
			info = new InformazioniToken(200, SorgenteInformazioniToken.JWT, rawResponse, tokenParser);
			
			d = info.getNbf();
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			if(!d.equals(nbf)) {
				throw new Exception("Claim nbf differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (check date con nbf con max long value) ok");
			
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (non valido) ...");
			
			rawResponse = "{\"client_id\":\"nonImportante\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.JSON_WEB_TOKEN_RFC_7519);
			try {
				info = new InformazioniToken(500, SorgenteInformazioniToken.JWT, rawResponse, tokenParser);
				if(info.isValid()) {
					throw new Exception("Attes token non valido (trasporto)");
				}
			}catch(Exception e) {
				if(!e.getMessage().startsWith("Connessione terminata con errore (codice trasporto: 500)")) {
					throw e;
				}
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'JSON_WEB_TOKEN_RFC_7519' (non valido) ok");
			
		}
		
		
		
		
		
		// Test 5
		if(tipoTest==null || TipologiaClaims.OIDC_ID_TOKEN.equals(tipoTest))
		{
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN'...");
			String cliendId = "18192.apps";
			String audience = "23223.apps";
			String audience2 = "7777.apps";
			String issuer = "testAuthEnte";
			String username = "Utente di Prova";
			String subject = "10623542342342323";
			String s1 = "https://userinfo.email";
			String s2 = "https://userinfo.profile";
			String s3 = "altro";
			String jti = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			Date now = DateManager.getDate();
			Date iat = new Date( (now.getTime()/1000)*1000);
			Date nbf = new Date(iat.getTime() - (1000*20));
			Date exp = new Date(iat.getTime() + (1000*60));
			String rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"preferred_username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			BasicTokenParser tokenParser = new BasicTokenParser(TipologiaClaims.OIDC_ID_TOKEN);
			InformazioniToken info = new InformazioniToken(200, SorgenteInformazioniToken.USER_INFO, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=10) {
				throw new Exception("Claims attesi 10, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("azp")){
				throw new Exception("Atteso azp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("aud")){
				throw new Exception("Atteso aud non presente tra i claims");
			}
			if(!info.getClaims().containsKey("sub")){
				throw new Exception("Atteso sub non presente tra i claims");
			}
			if(!info.getClaims().containsKey("iss")){
				throw new Exception("Atteso iss non presente tra i claims");
			}
			if(!info.getClaims().containsKey("preferred_username")){
				throw new Exception("Atteso preferred_username non presente tra i claims");
			}
			if(!info.getClaims().containsKey("scope")){
				throw new Exception("Atteso scope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("iat")){
				throw new Exception("Atteso iat non presente tra i claims");
			}
			if(!info.getClaims().containsKey("nbf")){
				throw new Exception("Atteso nbf non presente tra i claims");
			}
			if(!info.getClaims().containsKey("exp")){
				throw new Exception("Atteso exp non presente tra i claims");
			}
			if(!info.getClaims().containsKey("jti")){
				throw new Exception("Atteso jti non presente tra i claims");
			}
			
			if(!cliendId.equals(info.getClientId())){
				throw new Exception("Atteso client_id '"+cliendId+"' trovato '"+info.getClientId()+"'");
			}
			
			if(info.getAud()==null) {
				throw new Exception("Audience attesi non trovato");
			}
			if(info.getAud().size()!=2) {
				throw new Exception("Audience attesi 2, trovati '"+info.getAud().size()+"'");
			}
			boolean find = false;
			for (String s : info.getAud()) {
				if(audience.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience+"' non trovato");
			}
			find = false;
			for (String s : info.getAud()) {
				if(audience2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Audience atteso '"+audience2+"' non trovato");
			}
			
			if(!subject.equals(info.getSub())){
				throw new Exception("Atteso subject '"+subject+"' trovato '"+info.getSub()+"'");
			}
			if(!issuer.equals(info.getIss())){
				throw new Exception("Atteso issuer '"+issuer+"' trovato '"+info.getIss()+"'");
			}
			
			if(!username.equals(info.getUsername())){
				throw new Exception("Atteso username '"+username+"' trovato '"+info.getUsername()+"'");
			}
			
			// non supportato
			if(info.getScopes()!=null) {
				throw new Exception("Scope non attesi");
			}
			/*
			if(info.getScopes()==null) {
				throw new Exception("Scope attesi non trovato");
			}
			if(info.getScopes().size()!=3) {
				throw new Exception("Scope attesi 3, trovati '"+info.getScopes().size()+"'");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s1.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s1+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s2.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s2+"' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if(s3.equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso '"+s3+"' non trovato");
			}
			*/
			
			Date d = info.getIat();
			if(d==null) {
				throw new Exception("Data non trovata");
			}
			if(!d.equals(iat)) {
				throw new Exception("Claim iat differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(iat)+" iat="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getNbf();
			if(info.getNbf()!=null) {
				throw new Exception("Data nbf non attesa");
			}
			// non supportata
			/*
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			if(!d.equals(nbf)) {
				throw new Exception("Claim nbf differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			if(now.before(d)){
				throw new Exception("Token non utilizzabile, non atteso now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			*/
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(exp)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			if(!now.before(d)){
				throw new Exception("Token scaduto non atteso now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			// non supportato
			/*
			if(!jti.equals(tokenParser.getJWTIdentifier())){
				throw new Exception("Atteso jti '"+jti+"' trovato '"+tokenParser.getJWTIdentifier()+"'");
			}
			*/
			if(tokenParser.getJWTIdentifier()!=null) {
				throw new Exception("jti non atteso");
			}
			
			if(info.getRoles()!=null) {
				throw new Exception("Ruoli non attesi");
			}
			
			if(info.getUserInfo()!=null) {
				if(info.getUserInfo().getFamilyName()!=null) {
					throw new Exception("Claim family name non atteso");
				}
				if(info.getUserInfo().getFirstName()!=null) {
					throw new Exception("Claim first name non atteso");
				}
				if(info.getUserInfo().getFullName()!=null) {
					throw new Exception("Claim full name non atteso");
				}
				if(info.getUserInfo().getMiddleName()!=null) {
					throw new Exception("Claim middle name non atteso");
				}
				if(info.getUserInfo().getEMail()!=null) {
					throw new Exception("Claim mail non atteso");
				}
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (check date) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date(iat.getTime() + (1000*60));
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"preferred_username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.OIDC_ID_TOKEN);
			info = new InformazioniToken(200, SorgenteInformazioniToken.USER_INFO, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*20));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getNbf();
			if(d!=null) {
				throw new Exception("Data nbf non attesa");
			}
			// non supportato
			/*
			if(d==null) {
				throw new Exception("Data nbf non trovata");
			}
			now = DateManager.getDate();
			if(now.before(d)){
				throw new Exception("(1) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() - (1000*20));
			if(now.before(d)){
				throw new Exception("(2) Token non utilizzabile non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() - (1000*60));
			if(!now.before(d)){
				throw new Exception("(3) Atteso token non utilizzabile, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" nbf="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			*/
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (check date) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (check date con expires con max long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date((Long.MAX_VALUE/1000)*1000);
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"preferred_username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.OIDC_ID_TOKEN);
			info = new InformazioniToken(200, SorgenteInformazioniToken.USER_INFO, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (check date con expires con max long value) ok");
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (check date con expires con overflow long value) ...");
			
			iat = new Date( (now.getTime()/1000)*1000);
			nbf = new Date(iat.getTime() - (1000*60));
			exp = new Date((Long.MAX_VALUE/1000)*1000);
			rawResponse = "{\"azp\": \""+cliendId+"\", \"aud\": [ \""+audience+"\" , \""+audience2+"\" ], \"sub\": \""+subject+"\",  \"iss\": \""+issuer+"\", \"preferred_username\": \""+username+"\","+
					" \"scope\": \""+s1+" "+s2+" "+s3+"\","+
					"  \"iat\": \""+(iat.getTime()/1000)+"\", \"nbf\": \""+(nbf.getTime()/1000)+"\", \"exp\": \""+(exp.getTime()/1000)+"\",  "
					+ "\"jti\": \""+jti+"\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.OIDC_ID_TOKEN);
			info = new InformazioniToken(200, SorgenteInformazioniToken.USER_INFO, rawResponse, tokenParser);
			
			d = info.getExp();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			if(!d.equals(exp)) {
				throw new Exception("Claim exp differente da quello atteso atteso="+DateUtils.getOldSimpleDateFormatMs().format(nbf)+" exp="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(Long.MAX_VALUE-1001);
			if(!now.before(d)){
				throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (check date con expires con overflow long value) ok");		
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (non valido) ...");
			
			rawResponse = "{\"client_id\":\"nonImportante\"}";
			tokenParser = new BasicTokenParser(TipologiaClaims.OIDC_ID_TOKEN);
			try {
				info = new InformazioniToken(500, SorgenteInformazioniToken.USER_INFO, rawResponse, tokenParser);
				if(info.isValid()) {
					throw new Exception("Attes token non valido (trasporto)");
				}
			}catch(Exception e) {
				if(!e.getMessage().startsWith("Connessione terminata con errore (codice trasporto: 500)")) {
					throw e;
				}
			}
			
			System.out.println("Test BasicTokenParser, access token dopo introspection 'OIDC_ID_TOKEN' (non valido) ok");
			
		}
	}

}
