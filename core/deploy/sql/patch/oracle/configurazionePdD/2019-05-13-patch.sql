ALTER TABLE ct_config_policy ADD policy_built_in NUMBER NOT NULL; 
ALTER TABLE ct_config_policy MODIFY policy_built_in DEFAULT 0;

UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeGiornaliero';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeMinuti';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-ControlloRealtimeOrario';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='NumeroRichieste-RichiesteSimultanee';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='OccupazioneBanda-ControlloRealtimeOrario';
UPDATE ct_config_policy SET policy_built_in=true WHERE policy_id='TempoMedioRisposta-ControlloRealtimeOrario'; 

UPDATE ct_config_policy SET policy_id='NumeroRichiesteSimultanee' where policy_id='NumeroRichieste-RichiesteSimultanee';
UPDATE ct_active_policy set policy_id ='NumeroRichiesteSimultanee' where policy_id='NumeroRichieste-RichiesteSimultanee';


-- Aggiunti criteri di ordinamento e valutazione alle politiche

ALTER TABLE ct_active_policy ADD policy_posizione NUMBER;
UPDATE ct_active_policy SET policy_posizione=id;
ALTER TABLE ct_active_policy MODIFY (policy_posizione NOT NULL);

ALTER TABLE ct_active_policy ADD policy_continue NUMBER NOT NULL;
ALTER TABLE ct_active_policy MODIFY policy_continue DEFAULT 0;


-- Criterio nel filtro permette molteplici azione

-- ALTER TABLE ct_active_policy MODIFY filtro_azione CLOB;
ALTER TABLE ct_active_policy ADD temp CLOB; 
UPDATE ct_active_policy SET temp=filtro_azione; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_azione ;
ALTER TABLE ct_active_policy RENAME COLUMN temp TO filtro_azione;


-- Raggruppamento per identificativo autenticato e per token claim

ALTER TABLE ct_active_policy ADD group_id_autenticato NUMBER NOT NULL;
ALTER TABLE ct_active_policy MODIFY group_id_autenticato DEFAULT 0;

ALTER TABLE ct_active_policy ADD group_token CLOB;
