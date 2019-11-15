UPDATE porte_applicative SET autenticazione='none' WHERE (autenticazione='' OR autenticazione is null) AND tipo_servizio IN ('spc','test','url','wsdl','ldap','uddi','ebXMLRegistry');

delete from users_stati ;
