UPDATE porte_delegate porte set porte.id_soggetto_erogatore=(select id from soggetti s where s.tipo_soggetto=porte.tipo_soggetto_erogatore AND s.nome_soggetto=porte.nome_soggetto_erogatore) where porte.id_soggetto_erogatore<=0;
UPDATE porte_delegate porte set porte.id_servizio=(select id from servizi s where s.tipo_servizio=porte.tipo_servizio AND s.nome_servizio=porte.nome_servizio AND s.versione_servizio=porte.versione_servizio AND s.id_soggetto=porte.id_soggetto_erogatore ) where porte.id_servizio<=0;

UPDATE porte_applicative porte set porte.id_servizio=(select id from servizi s where s.tipo_servizio=porte.tipo_servizio AND s.nome_servizio=porte.servizio AND s.versione_servizio=porte.versione_servizio AND s.id_soggetto=porte.id_soggetto ) where porte.id_servizio<=0;
