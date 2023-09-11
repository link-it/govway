update users set tipo_interfaccia='AVANZATA', protocolli=null, protocollo_pddconsole=null, protocollo_pddmonitor=null, soggetto_pddconsole=null, soggetto_pddmonitor=null;
update users set  soggetti_all=1, servizi_all=1 where login='operatore';

