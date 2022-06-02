--
-- Tables for the pregnancy care module
--
CREATE TABLE PREGNANCY (
 PREG_ID int(11) NOT NULL AUTO_INCREMENT COMMENT 'The Pregnancy ID',
 PREG_PAT_ID int(11) NOT NULL COMMENT 'The PATIENT this pregnancy refers to',
 PREG_GRAVIDA int(2) DEFAULT 1 COMMENT 'The number of the Pregnancy',
 PREG_PARITY int(2) DEFAULT 0 COMMENT 'The parity of the patient',
 PREG_CHILDALIVE int(2) NOT NULL COMMENT 'The number of children alive',
 PREG_LMP DATETIME COMMENT 'The date of last menstrual period',
 PREG_CALC_DELIVERY DATETIME COMMENT 'The estimated date of delivery',
 PREG_REAL_DELIVERY DATETIME COMMENT 'The real date of delivery',
 PREG_ACTIVE varchar(1) NOT NULL DEFAULT 'Y' COMMENT 'Y-currently pregnant, N-delivered, D-deleted',
 INDEX ( PREG_PAT_ID ),
 PRIMARY KEY (PREG_ID)
) ENGINE=InnoDB;

CREATE TABLE PREGNANCYDELIVERY (
 PDEL_ID int(11) NOT NULL AUTO_INCREMENT COMMENT 'The id of Delivery',
 PDEL_PREG_ID int(11) NOT NULL COMMENT 'the PREGNANCY this Delivery refers to',
 PDEL_ADM_ID int(11) NOT NULL COMMENT 'The ADMISSION this Delivery refers to',
 PDEL_DIS_ID_A VARCHAR(15) DEFAULT NULL COMMENT 'The DISEASE the ADMISSION was for',
 PDEL_ESTIMATED_GA int(2) COMMENT 'The Estimated Gestational Age (if missing from LMP)',
 PDEL_DATE_DEL DATETIME DEFAULT NULL COMMENT 'The date of this Delivery',
 PDEL_ROBSON VARCHAR(2) COMMENT 'The Robson Classification (1-10)',
 PDEL_DLT_ID_A char(1) DEFAULT NULL COMMENT 'The DELIVERYTYPE of this Delivery',
 PDEL_DRT_ID_A char(1) DEFAULT NULL COMMENT 'The DELIVERYRESULTTYPE of thi Delivery',
 PDEL_WEIGHT int NOT NULL COMMENT 'The weight of the newborn (g)',
 PDEL_HEIGHT int NOT NULL COMMENT 'The height of the newborn (cm)',
 PDEL_HEAD float NOT NULL COMMENT 'The head circunference of the newborn',
 PDEL_SEX char(1) NOT NULL DEFAULT 'F' COMMENT 'The gender of the newborn',
 PDEL_APGAR varchar(6) COMMENT 'The APGAR score of the newborn',
 PDEL_CHILD_NAME varchar(45) COMMENT 'The child''s name',
 PDEL_RMPT VARCHAR(10) COMMENT 'Type of removal placenta: AMTSL | Manual',
 PDEL_MANAGEMENT VARCHAR(12) COMMENT 'Management: Induction | Augmentation',
 PDEL_COMP_APH tinyint(1) COMMENT 'Complication APH - Antepartum Hemorragy',
 PDEL_COMP_PPH tinyint(1) COMMENT 'Complication PPH - Postpartum Hemorragy',
 PDEL_COMP_CP tinyint(1) COMMENT 'Complication CP - Cord Prolapse',
 PDEL_PMTCT_PLACE VARCHAR(50) COMMENT 'Place for HIV test: Hospital | Health Center | Unknown',
 PDEL_PMTCT_EXAM char(1) COMMENT 'HIV test result: P | N | U',
 PDEL_PMTCT_PARTNER char(1) COMMENT 'HIV test result (partner): P | N | U',
 PDEL_PA_MIN int DEFAULT 0 COMMENT 'Blood Pressure MIN',
 PDEL_PA_MAX int DEFAULT 0 COMMENT 'Blood Pressure MAX',
 PDEL_ANC VARCHAR(7) COMMENT 'Antenatal Clinic (if done): Unknown | 4+ | 1-3 | 0',
 PDEL_MWH char(1) COMMENT 'Mother Waiting Home (if done): Y | N',
 PDEL_NOTE VARCHAR(250) COMMENT 'Remarsk (prolonged labour, meconium grade, etc...)',
 PDEL_US_ID_A VARCHAR(50) COMMENT 'The logged user',
 INDEX ( PDEL_ADM_ID ),
 INDEX ( PDEL_DLT_ID_A ),
 INDEX ( PDEL_DRT_ID_A ),
 PRIMARY KEY (PDEL_ID)
) ENGINE=InnoDB;

CREATE TABLE PREGNANCYVISIT (
 PVIS_ID int(11) NOT NULL AUTO_INCREMENT COMMENT 'The Pregnancy Visit ID',
 PVIS_PREG_ID int(11) NOT NULL COMMENT 'The PREGNANCY this Pregnancy Visit refers to',
 PVIS_DATE DATETIME NOT NULL COMMENT 'The Pregnancy Visit date',
 PVIS_NEXT_DATE DATETIME DEFAULT NULL COMMENT 'The next Pregnancy Visit date',
 PVIS_PTT_ID_A varchar(10) DEFAULT NULL COMMENT 'The PREGNANTTREATMENTTYPE used in this Pregnancy Visit',
 PVIS_NOTE text COMMENT 'Remarks',
 PVIS_TYPE tinyint(2) NOT NULL DEFAULT '-1' COMMENT 'The type of the Pregnancy visit: -1 Prenatal, 1 Postnatal',
 INDEX ( PVIS_PREG_ID ),
 PRIMARY KEY (PVIS_ID)
) ENGINE=InnoDB;

CREATE TABLE PREGNANCYEXAM (
 PREGEX_ID varchar(10) NOT NULL COMMENT 'The ID of the Pregnancy Exam',
 PREGEX_DESC varchar(100) NOT NULL COMMENT 'The description of the Pregnancy Exam',
 PREGEX_TYPE tinyint(2) NOT NULL DEFAULT '-1' COMMENT 'The type the exam is used for (-1 = prenatal, 1= postnatal, 0= both)',
 PREGEX_DEFAULT varchar(50) DEFAULT NULL COMMENT 'The default value of the Pregnancy Exam',
 PREGEX_VALUES varchar(255) DEFAULT NULL COMMENT 'The allowed results (semicolon separated)',
 PRIMARY KEY (PREGEX_ID)
) ENGINE=InnoDB;

CREATE TABLE PREGNANCYEXAMRESULT (
 PEXRES_ID int(11) NOT NULL AUTO_INCREMENT COMMENT 'The ID of the Pregnancy Exam result',
 PEXRES_PVIS_ID int(11) NOT NULL COMMENT 'The PREGNANCYVISIT this Pregnancy Exam result refers to',
 PEXRES_PREGEX_ID varchar(10) NOT NULL COMMENT 'The PREGNANCYEXAM this Pregnancy Exam result refers to',
 PEXRES_OUTCOME varchar(50) NOT NULL COMMENT 'The PREGNANCYEXAM result',
 INDEX ( PEXRES_PVIS_ID ),
 INDEX ( PEXRES_PREGEX_ID ),
 PRIMARY KEY (PEXRES_ID)
) ENGINE=InnoDB;

--
-- Foreign keys
--
ALTER TABLE PREGNANCY 
	ADD CONSTRAINT FK_PREGNANCY_PATIENT 
	FOREIGN KEY (PREG_PAT_ID) 
	REFERENCES PATIENT (PAT_ID);

ALTER TABLE PREGNANCYDELIVERY 
	ADD CONSTRAINT FK_PREGNANCYDELIVERY_ADMISSION
	FOREIGN KEY (PDEL_ADM_ID) 
	REFERENCES ADMISSION (ADM_ID);
	
ALTER TABLE PREGNANCYDELIVERY 
	ADD CONSTRAINT FK_PREGNANCYDELIVERY_PREGNANCY
	FOREIGN KEY (PDEL_PREG_ID) 
	REFERENCES PREGNANCY (PREG_ID);
	
ALTER TABLE PREGNANCYDELIVERY 
	ADD CONSTRAINT FK_PREGNANCYDELIVERY_DISEASE
	FOREIGN KEY (PDEL_DIS_ID_A) 
	REFERENCES DISEASE (DIS_ID_A);

ALTER TABLE PREGNANCYDELIVERY 
	ADD CONSTRAINT FK_PREGNANCYDELIVERY_DELIVERYRESULTTYPE
	FOREIGN KEY (PDEL_DRT_ID_A) 
	REFERENCES DELIVERYRESULTTYPE (DRT_ID_A);
	
ALTER TABLE PREGNANCYDELIVERY 
	ADD CONSTRAINT FK_PREGNANCYDELIVERY_DELIVERYTYPE
	FOREIGN KEY (PDEL_DLT_ID_A) 
	REFERENCES DELIVERYTYPE (DLT_ID_A);
	
ALTER TABLE PREGNANCYDELIVERY 
	ADD CONSTRAINT FK_PREGNANCYDELIVERY_USER
	FOREIGN KEY (PDEL_US_ID_A) 
	REFERENCES USER (US_ID_A);

ALTER TABLE PREGNANCYEXAMRESULT 
	ADD CONSTRAINT FK_PREGNANCYEXAMRESULT_PREGNANCYEXAM
	FOREIGN KEY (PEXRES_PREGEX_ID) 
	REFERENCES PREGNANCYEXAM (PREGEX_ID);

ALTER TABLE PREGNANCYEXAMRESULT 
	ADD CONSTRAINT FK_PREGNANCYEXAMRESULT_PREGNANCYVISIT
	FOREIGN KEY (PEXRES_PVIS_ID) 
	REFERENCES PREGNANCYVISIT (PVIS_ID);

ALTER TABLE PREGNANCYVISIT
	ADD CONSTRAINT FK_PREGNANCYVISIT_PREGNANCY
	FOREIGN KEY (PVIS_PREG_ID) 
	REFERENCES PREGNANCY (PREG_ID);

ALTER TABLE PREGNANCYVISIT
	ADD CONSTRAINT FK_PREGNANCYVISIT_PREGNANTTREATMENTTYPE
	FOREIGN KEY (PVIS_PTT_ID_A) 
	REFERENCES PREGNANTTREATMENTTYPE (PTT_ID_A);


--
-- Dumping data for table 'MENUITEM'
--
INSERT INTO MENUITEM (MNI_ID_A,MNI_BTN_LABEL,MNI_LABEL,MNI_TOOLTIP,MNI_SHORTCUT,MNI_SUBMENU,MNI_CLASS,MNI_IS_SUBMENU,MNI_POSITION) VALUES 
 ('pregnancy','angal.menu.btn.pregnancy','angal.menu.pregnancy','x','P','main','org.isf.pregnancy.gui.PregnancyCareBrowser','N',5),
 ('btnpregnewpat','angal.menu.btn.btnpregnewpat','angal.menu.btnpregnewpat','x','N','pregnancy','none','N',0),
 ('btnpregeditpat','angal.menu.btn.btnpregeditpat','angal.menu.btnpregeditpat','x','N','pregnancy','none','N',0),
 ('btnpregdelpat','angal.menu.btn.btnpregdelpat','angal.menu.btnpregdelpat','x','N','pregnancy','none','N',0),
 ('btnpregpatvac','angal.menu.btn.btnpregpatvac','angal.menu.btnpregpatvac','x','N','pregnancy','none','N',0),
 ('btnpregnewpreg','angal.menu.btn.btnpregnewpreg','angal.menu.btnpregnewpreg','x','N','pregnancy','none','N',0),
 ('btnpregnewpren','angal.menu.btn.btnpregnewpren','angal.menu.btnpregnewpren','x','N','pregnancy','none','N',0),
 ('btnpregnewpost','angal.menu.btn.btnpregnewpost','angal.menu.btnpregnewpost','x','N','pregnancy','none','N',0),
 ('btnpregregdel','angal.menu.btn.btnpregregdel','angal.menu.btnpregregdel','x','N','pregnancy','none','N',0),
 ('btnpregedit','angal.menu.btn.btnpregedit','angal.menu.btnpregedit','x','N','pregnancy','none','N',0),
 ('btnpregdel','angal.menu.btn.btnpregdel','angal.menu.btnpregdel','x','N','pregnancy','none','N',0),
 ('btnpregupddel','angal.menu.btn.btnpregupddel','angal.menu.btnpreupdgdel','x','N','pregnancy','none','N',0),
 ('pregnancyexam','angal.menu.btn.pregnancyexam','angal.menu.pregnancyexam','x','P','generaldata','org.isf.pregnancyexam.gui.PregnancyExamBrowser','N',4);

--
-- Dumping data for table 'GROUPMENU'
--
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES 
('admin','pregnancy','Y'),
('admin','btnpregnewpat','Y'),
('admin','btnpregeditpat','Y'),
('admin','btnpregdelpat','Y'),
('admin','btnpregpatvac','Y'),
('admin','btnpregnewpreg','Y'),
('admin','btnpregnewpren','Y'),
('admin','btnpregnewpost','Y'),
('admin','btnpregregdel','Y'),
('admin','btnpregedit','Y'),
('admin','btnpregdel','Y'),
('admin','btnpregupddel','Y'),
('admin','pregnancyexam','Y');

--
-- Pregnancy exams evaluated outside the laboratory
--
INSERT INTO PREGNANCYEXAM (PREGEX_ID, PREGEX_DESC, PREGEX_TYPE, PREGEX_DEFAULT, PREGEX_VALUES) VALUES
('MW', 'Mothers weight', 0, '', ''),
('FH', 'Fundal Height', -1, '', ''),
('BP', 'Blood Pressure', -1, '', ''),
('MAV', 'Fetal Movements', -1, '', 'YES;NO'),
('FHR', 'Fetal Heart Rate', -1, '', ''),
('LIE', 'Fetal Condition', -1, '', ''),
('PRES', 'Fetal Presentaation', -1, '', ''),
('OEDEMA', 'Oedema', -1, '', '+;++;+++'),
('CONJ', 'Congiuntive', -1, '', 'Pallide;Colorate'),
('US', 'Ultrasound', -1, '', ''),
('URINE', 'Urine Analysis', -1, '', ''),
('EMO', 'Blood Analysis', -1, '', ''),
('VAG', 'Vaginal Examination', 0, '', ''),
('TREAT', 'Treatment', 0, '', ''),
('BRST', 'Breastfeeding', 1, '', ''),
('CW', 'Child Weight', 1, '', '');
