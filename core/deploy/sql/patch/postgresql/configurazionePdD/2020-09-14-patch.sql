ALTER TABLE pa_transform_hdr ADD COLUMN identificazione_fallita VARCHAR(255);
ALTER TABLE pa_transform_url ADD COLUMN identificazione_fallita VARCHAR(255);
ALTER TABLE pa_transform_risp_hdr ADD COLUMN identificazione_fallita VARCHAR(255);

ALTER TABLE pd_transform_hdr ADD COLUMN identificazione_fallita VARCHAR(255);
ALTER TABLE pd_transform_url ADD COLUMN identificazione_fallita VARCHAR(255);
ALTER TABLE pd_transform_risp_hdr ADD COLUMN identificazione_fallita VARCHAR(255);
