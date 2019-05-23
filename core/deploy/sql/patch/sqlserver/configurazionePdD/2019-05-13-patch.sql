ALTER TABLE ct_config_policy ADD policy_built_in BIT NOT NULL DEFAULT 'false';

UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeGiornaliero';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeMinuti';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeOrario';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-RichiesteSimultanee';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='OccupazioneBanda-ControlloRealtimeOrario';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='TempoMedioRisposta-ControlloRealtimeOrario'; 

UPDATE ct_config_policy SET policy_id='NumeroRichiesteSimultanee' where policy_id='NumeroRichieste-RichiesteSimultanee';
UPDATE ct_active_policy set policy_id ='NumeroRichiesteSimultanee' where policy_id='NumeroRichieste-RichiesteSimultanee';


-- Aggiunti criteri di ordinamento e valutazione alle politiche

ALTER TABLE ct_active_policy ADD policy_posizione INT;
UPDATE ct_active_policy SET policy_posizione=id;
ALTER TABLE ct_active_policy ALTER COLUMN policy_posizione INT NOT NULL; 

ALTER TABLE ct_active_policy ADD policy_continue BIT NOT NULL DEFAULT 'false';


-- Criterio nel filtro permette molteplici azione, applicativi e soggetti.

ALTER TABLE ct_active_policy ADD temp VARCHAR(max); 
UPDATE ct_active_policy SET temp=filtro_nome_fruitore; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_nome_fruitore ;
EXEC sp_rename  'ct_active_policy.temp' , 'filtro_nome_fruitore' , 'COLUMN'

ALTER TABLE ct_active_policy ADD temp VARCHAR(max); 
UPDATE ct_active_policy SET temp=filtro_tipo_fruitore; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_tipo_fruitore ;
EXEC sp_rename  'ct_active_policy.temp' , 'filtro_tipo_fruitore' , 'COLUMN'

ALTER TABLE ct_active_policy ADD temp VARCHAR(max); 
UPDATE ct_active_policy SET temp=filtro_sa_fruitore; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_sa_fruitore ;
EXEC sp_rename  'ct_active_policy.temp' , 'filtro_sa_fruitore' , 'COLUMN'

ALTER TABLE ct_active_policy ADD temp VARCHAR(max); 
UPDATE ct_active_policy SET temp=filtro_azione; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_azione ;
EXEC sp_rename  'ct_active_policy.temp' , 'filtro_azione' , 'COLUMN'


-- Raggruppamento per identificativo autenticato e per token claim

ALTER TABLE ct_active_policy ADD group_id_autenticato BIT NOT NULL DEFAULT 'false';
ALTER TABLE ct_active_policy ADD group_token VARCHAR(max);
