ALTER TABLE pa_transform_risp ADD temp VARCHAR2(255); 
UPDATE pa_transform_risp SET temp=to_char(return_code); 
ALTER TABLE pa_transform_risp DROP COLUMN return_code; 
ALTER TABLE pa_transform_risp RENAME COLUMN temp TO return_code; 

ALTER TABLE pd_transform_risp ADD temp VARCHAR2(255); 
UPDATE pd_transform_risp SET temp=to_char(return_code); 
ALTER TABLE pd_transform_risp DROP COLUMN return_code; 
ALTER TABLE pd_transform_risp RENAME COLUMN temp TO return_code; 
