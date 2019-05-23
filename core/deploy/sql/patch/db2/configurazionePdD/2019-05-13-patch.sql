ALTER TABLE ct_config_policy ADD policy_built_in SMALLINT NOT NULL DEFAULT 0;

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
ALTER TABLE ct_active_policy ALTER COLUMN policy_posizione SET NOT NULL;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE ct_active_policy') ;

ALTER TABLE ct_active_policy ADD policy_continue SMALLINT NOT NULL DEFAULT 0;


-- Criterio nel filtro permette molteplici azione

ALTER TABLE ct_active_policy ADD temp CLOB; 
UPDATE ct_active_policy SET temp=filtro_azione; 
ALTER TABLE ct_active_policy DROP COLUMN filtro_azione ;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE ct_active_policy') ;
ALTER TABLE ct_active_policy RENAME COLUMN temp TO filtro_azione;


-- Raggruppamento per identificativo autenticato e per token claim

ALTER TABLE ct_active_policy ADD group_id_autenticato SMALLINT NOT NULL DEFAULT 0;
ALTER TABLE ct_active_policy ADD group_token CLOB;



