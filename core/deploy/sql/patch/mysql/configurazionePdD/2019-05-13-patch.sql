ALTER TABLE ct_config_policy ADD COLUMN policy_built_in BOOLEAN NOT NULL DEFAULT false;

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
ALTER TABLE ct_active MODIFY COLUMN policy_posizione INT NOT NULL;

ALTER TABLE ct_active_policy ADD COLUMN policy_continue BOOLEAN NOT NULL DEFAULT false;


-- Criterio nel filtro permette molteplici azione

ALTER TABLE ct_active_policy ADD COLUMN temp TEXT; 
UPDATE ct_active_policy SET temp=filtro_azione; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_azione; 
ALTER TABLE ct_active_policy CHANGE COLUMN temp filtro_azione TEXT;


-- Raggruppamento per identificativo autenticato e per token claim

ALTER TABLE ct_active_policy ADD COLUMN group_id_autenticato BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE ct_active_policy ADD COLUMN group_token TEXT;
