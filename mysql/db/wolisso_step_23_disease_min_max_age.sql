ALTER TABLE DISEASE
ADD COLUMN DIS_MALE TINYINT(1) NOT NULL DEFAULT 1 AFTER DIS_IPD_OUT_INCLUDE,
ADD COLUMN DIS_FEMALE TINYINT(1) NOT NULL DEFAULT 1 AFTER DIS_MALE,
ADD COLUMN DIS_MIN_AGE INT NOT NULL DEFAULT 0 AFTER DIS_FEMALE,
ADD COLUMN DIS_MAX_AGE INT NOT NULL DEFAULT 0 AFTER DIS_MIN_AGE;

UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='127';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='128';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='129';
UPDATE DISEASE SET DIS_MAX_AGE='0' WHERE DIS_ID_A='130';
UPDATE DISEASE SET DIS_MAX_AGE='0' WHERE DIS_ID_A='131';
UPDATE DISEASE SET DIS_MAX_AGE='0' WHERE DIS_ID_A='132.1';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='CHD';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='CLP';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='CLUB';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='CON';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='CIO';
UPDATE DISEASE SET DIS_MAX_AGE='120' WHERE DIS_ID_A='HIP';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='LBW';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='LBW2';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='NEO';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='NEON';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='PREB';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='RESP';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='SPI';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='130';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='131';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='132.1';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='132.2';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='132.3';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='133';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='134';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='135.1';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='135.2';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='BIR';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='BPI';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='FET';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='IUGR';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='JAU';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='ONP';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='PER';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='PRT';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='RES';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='VLBW';
UPDATE DISEASE SET DIS_MAX_AGE='1' WHERE DIS_ID_A='006';
UPDATE DISEASE SET DIS_FEMALE='0' WHERE DIS_ID_A='112';
UPDATE DISEASE SET DIS_FEMALE='0' WHERE DIS_ID_A='114.2';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='113';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='114.3';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='114.4';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='114.5';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='051';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='052';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='053';
UPDATE DISEASE SET DIS_FEMALE='0' WHERE DIS_ID_A='054';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='057.2';
UPDATE DISEASE SET DIS_FEMALE='0' WHERE DIS_ID_A='057.3';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='060.1';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='060.2';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='060.3';
UPDATE DISEASE SET DIS_MALE='0' WHERE DIS_ID_A='ADN';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='115';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='116.1';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='116.2';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='116.3';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='117.1';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='118';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='119';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='120.1';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='120.2';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='120.3';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='120.4';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='120.5';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='120.6';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='ACT1';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='ACT2';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='APH';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='BRE';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='ECL';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='ECT';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='FAL';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='INC';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='IUFD';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='OTH';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='PPH';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='PRE';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='SCAR';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='PRO';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='PUE';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='RET';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='SVD';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='THR';
UPDATE DISEASE SET DIS_MALE='0', DIS_MIN_AGE='120' WHERE DIS_ID_A='UTE';
UPDATE DISEASE SET DIS_MIN_AGE='600' WHERE DIS_ID_A='136';

