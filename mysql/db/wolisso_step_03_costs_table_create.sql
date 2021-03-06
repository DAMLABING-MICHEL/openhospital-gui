DROP TABLE IF EXISTS MEDICALDSRCOST;
CREATE TABLE MEDICALDSRCOST (
	MDC_ID INT NOT NULL AUTO_INCREMENT ,
	MDC_MDSR_ID INT(11) NOT NULL ,
	MDC_COST DOUBLE NOT NULL ,
	MDC_TIMESTAMP TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
	PRIMARY KEY (MDC_ID)
) ENGINE = INNODB;

ALTER TABLE MEDICALDSRCOST 
	ADD CONSTRAINT FK_MEDICALDSRCOST_MEDICALDSR
		FOREIGN KEY (MDC_MDSR_ID )
		REFERENCES MEDICALDSR (MDSR_ID )
			ON DELETE CASCADE
			ON UPDATE CASCADE, 
	ADD INDEX FK_MEDICALDSRCOST_MEDICALDSR (MDC_MDSR_ID ASC);