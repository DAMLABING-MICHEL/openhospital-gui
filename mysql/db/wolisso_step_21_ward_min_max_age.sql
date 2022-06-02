ALTER TABLE WARD
ADD COLUMN WRD_MIN_AGE INT NULL DEFAULT 0 AFTER WRD_IS_FREE,
ADD COLUMN WRD_MAX_AGE INT NULL DEFAULT 0 AFTER WRD_MIN_AGE;

UPDATE WARD SET WRD_MAX_AGE='1' WHERE WRD_ID_A='N';
UPDATE WARD SET WRD_MAX_AGE='60', WRD_MIN_AGE='1' WHERE WRD_ID_A='P';
UPDATE WARD SET WRD_MIN_AGE='120' WHERE WRD_ID_A='G';
UPDATE WARD SET WRD_MIN_AGE='120' WHERE WRD_ID_A='M';

