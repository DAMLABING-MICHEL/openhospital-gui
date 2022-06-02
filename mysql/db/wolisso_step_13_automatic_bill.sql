CREATE TABLE MEDICALDSRWARDPRESCRIPTION (
  MWP_ID int(11) NOT NULL AUTO_INCREMENT,
  MWP_DATE datetime NOT NULL,
  MWP_PAT_ID int(11) NOT NULL,
  MWP_WRD_ID_A varchar(1) NOT NULL,
  MWP_BLL_ID int(11) NULL,
  MWP_STATUS TINYINT NOT NULL DEFAULT 0,
  MWP_IPD tinyint(1) NOT NULL DEFAULT '0',
  MWP_WRD_ID_B varchar(1) DEFAULT NULL,
  MWP_DIS_ID_A varchar(10) DEFAULT NULL,
  PRIMARY KEY (MWP_ID),
  KEY FK_PRESCRIPTION_BILL (MWP_BLL_ID),
  CONSTRAINT FK_PRESCRIPTION_BILL FOREIGN KEY (MWP_BLL_ID) REFERENCES BILLS (BLL_ID) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE MEDICALDSRWARDPRESCRIPTIONDETAIL (
  MWPD_ID int(11) NOT NULL AUTO_INCREMENT,
  MWPD_MWP_ID int(11) NOT NULL,
  MWPD_MDSR_ID varchar(100) NOT NULL,
  MWPD_MDSR_QTY double NOT NULL,
  MWPD_MDSR_UNITS varchar(10) NOT NULL,
  PRIMARY KEY (MWPD_ID),
  KEY FK_PRESCRIPTIONDETAIL_PRESCRIPTION (MWPD_MWP_ID),
  CONSTRAINT FK_PRESCRIPTIONDETAIL_PRESCRIPTION FOREIGN KEY (MWPD_MWP_ID) REFERENCES MEDICALDSRWARDPRESCRIPTION (MWP_ID) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB;

