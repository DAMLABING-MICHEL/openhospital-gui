ALTER TABLE MEDICALDSRLOT CHANGE COLUMN LT_PREP_DATE LT_PREP_DATE DATE NOT NULL, CHANGE COLUMN LT_DUE_DATE LT_DUE_DATE DATE NOT NULL;
ALTER TABLE MEDICALDSRLOT ADD COLUMN LT_COST DOUBLE NULL AFTER LT_DUE_DATE;

DROP TABLE MEDICALDSRCOST;

