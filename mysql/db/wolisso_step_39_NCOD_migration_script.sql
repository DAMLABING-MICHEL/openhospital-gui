-- modify MEDICALDSRWARDPRESCRIPTION table (no foreign keys because of NULL values)
DROP TRIGGER IF EXISTS disease_AFTER_UPDATE;
DELIMITER $$
USE oh$$
CREATE DEFINER = CURRENT_USER TRIGGER disease_AFTER_UPDATE AFTER UPDATE ON disease FOR EACH ROW
BEGIN
	UPDATE MEDICALDSRWARDPRESCRIPTION SET MWP_DIS_ID_A = NEW.DIS_ID_A WHERE MWP_DIS_ID_A = OLD.DIS_ID_A;
END$$
DELIMITER ;

-- rename oldest codes for better isolation 
UPDATE DISEASE 
SET 
	DIS_ID_A = CONCAT("Y",DIS_ID_A), 
    DIS_DESC = CONCAT("Y",DIS_DESC)
WHERE 
	DIS_ID_A LIKE "2016%";

-- prefixing old codes and disabling them 
UPDATE DISEASE 
SET 
	DIS_ID_A = CONCAT("Y2017-",DIS_ID_A), 
    DIS_DESC = CONCAT("Y2017-",DIS_DESC),
    DIS_OPD_INCLUDE = 0,
    DIS_IPD_IN_INCLUDE = 0,
    DIS_IPD_OUT_INCLUDE = 0
WHERE 
	DIS_ID_A REGEXP '^[0-9][0-9.]*[0-9]';
	
-- reset AUTOINCREMENT
ALTER TABLE DISEASE AUTO_INCREMENT = 1 ;

	
-- add new categories
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('17', 'BLOOD, IMMUNE');
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('18', 'CONTACT WITH SERVICES');
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('19', 'EYE & ADNEXA');
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('20', 'INJURY, POISON, EXTERNAL');
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('21', 'MUSCULOSKELETAL');
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('22', 'PERINATAL PERIOD');
INSERT INTO DISEASETYPE (DCL_ID_A, DCL_DESC) VALUES ('23', 'SYMPTOMS, SIGNS, FINDINGS');

-- update old categories
UPDATE DISEASETYPE SET DCL_DESC='INFECTIOUS & PARASITIC' WHERE DCL_ID_A='1';
UPDATE DISEASETYPE SET DCL_DESC='MENTAL & BEHAVIOURAL' WHERE DCL_ID_A='4';
UPDATE DISEASETYPE SET DCL_DESC='NERVOUS SYSTEM' WHERE DCL_ID_A='5';
UPDATE DISEASETYPE SET DCL_DESC='SKIN & SUBCUTANEOUS' WHERE DCL_ID_A='11';
UPDATE DISEASETYPE SET DCL_DESC='GENITOURINARY SYSTEM' WHERE DCL_ID_A='9';
UPDATE DISEASETYPE SET DCL_DESC='PREGNANCY, BIRTH, PUERPERIUM' WHERE DCL_ID_A='10';

-- add ICD10 field
ALTER TABLE DISEASE ADD COLUMN DIS_ICD_ID VARCHAR(10) NULL AFTER DIS_IS_CHRONIC;

--
-- LOAD new diseases in DISEASE table
--
LOAD DATA LOCAL INFILE 'wolisso_step_39_NCOD_migration_script.csv' 
	INTO TABLE DISEASE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';