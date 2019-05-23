ALTER TABLE ct_config_policy ADD COLUMN policy_built_in BOOLEAN NOT NULL;
ALTER TABLE ct_config_policy ALTER COLUMN policy_built_in SET DEFAULT false;

UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeGiornaliero';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeMinuti';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeOrario';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-RichiesteSimultanee';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='OccupazioneBanda-ControlloRealtimeOrario';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='TempoMedioRisposta-ControlloRealtimeOrario'; 

UPDATE ct_config_policy SET policy_id='NumeroRichiesteSimultanee' where policy_id='NumeroRichieste-RichiesteSimultanee';
UPDATE ct_active_policy set policy_id ='NumeroRichiesteSimultanee' where policy_id='NumeroRichieste-RichiesteSimultanee';


-- Aggiunti criteri di ordinamento e valutazione alle politiche

ALTER TABLE ct_active_policy ADD COLUMN policy_posizione INT;
UPDATE ct_active_policy SET policy_posizione=id;
ALTER TABLE ct_active ALTER COLUMN policy_posizione SET NOT NULL;

ALTER TABLE ct_active_policy ADD COLUMN policy_continue BOOLEAN NOT NULL;
ALTER TABLE ct_active_policy ALTER COLUMN policy_continue SET DEFAULT false;


-- Criterio nel filtro permette molteplici azione, applicativi e soggetti.

ALTER TABLE ct_active_policy ADD COLUMN temp VARCHAR(65535); 
UPDATE ct_active_policy SET temp=filtro_nome_fruitore; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_nome_fruitore; 
ALTER TABLE ct_active_policy ALTER COLUMN temp RENAME TO filtro_nome_fruitore;

ALTER TABLE ct_active_policy ADD COLUMN temp VARCHAR(65535); 
UPDATE ct_active_policy SET temp=filtro_tipo_fruitore; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_tipo_fruitore; 
ALTER TABLE ct_active_policy ALTER COLUMN temp RENAME TO filtro_tipo_fruitore;

ALTER TABLE ct_active_policy ADD COLUMN temp VARCHAR(65535); 
UPDATE ct_active_policy SET temp=filtro_sa_fruitore; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_sa_fruitore; 
ALTER TABLE ct_active_policy ALTER COLUMN temp RENAME TO filtro_sa_fruitore;

ALTER TABLE ct_active_policy ADD COLUMN temp VARCHAR(65535); 
UPDATE ct_active_policy SET temp=filtro_azione; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_azione; 
ALTER TABLE ct_active_policy ALTER COLUMN temp RENAME TO filtro_azione;


-- Raggruppamento per identificativo autenticato e per token claim

ALTER TABLE ct_active_policy ADD COLUMN group_id_autenticato BOOLEAN NOT NULL;
ALTER TABLE ct_active_policy ALTER COLUMN group_id_autenticato SET DEFAULT false;
ALTER TABLE ct_active_policy ADD COLUMN group_token VARCHAR(65535);
