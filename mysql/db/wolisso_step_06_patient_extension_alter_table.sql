ALTER TABLE PATIENT ADD COLUMN PAT_PCODE VARCHAR(10) DEFAULT '' AFTER PAT_TIMESTAMP; 
ALTER TABLE PATIENT ADD COLUMN PAT_ESTA_NAME VARCHAR(50) DEFAULT '' AFTER PAT_PCODE;