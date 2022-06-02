-- this procedure is needed to align the MEDICALDSR table with the MEDICASLDSRSTOCKMOV one 
-- in terms of INPUT and OUTPUT quantities that may have been broken in unsafe transactions  

DROP PROCEDURE IF EXISTS updateMEDICALDSRfromMEDICALDSRSTOCKMOV;
DELIMITER //

CREATE PROCEDURE updateMEDICALDSRfromMEDICALDSRSTOCKMOV()
BEGIN
    DECLARE v_mdsr_id INT;
    DECLARE v_mdsr_input INT;
    DECLARE v_mdsr_output INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur1 CURSOR FOR SELECT MDSR_ID, SUM(IF(MMV_MMVT_ID_A = 'charge',MMV_QTY,0)) AS INPUT, SUM(IF(MMV_MMVT_ID_A = 'discharge', MMV_QTY, 0)) AS OUTPUT		
			FROM MEDICALDSR
			LEFT JOIN MEDICALDSRSTOCKMOV ON MDSR_ID = MMV_MDSR_ID
			WHERE MMV_MDSR_ID IS NOT NULL
			GROUP BY MDSR_ID;

	DECLARE cur2 CURSOR FOR SELECT MDSR_ID, 0 AS INPUT, 0 AS OUTPUT		
			FROM MEDICALDSR
			LEFT JOIN MEDICALDSRSTOCKMOV ON MDSR_ID = MMV_MDSR_ID
			WHERE MMV_MDSR_ID IS NULL
			GROUP BY MDSR_ID;
			
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur1;
    read_loop: LOOP
        FETCH cur1 INTO v_mdsr_id, v_mdsr_input, v_mdsr_output;
        IF done THEN
            LEAVE read_loop;
        END IF;
	UPDATE MEDICALDSR 
	SET 
	    MDSR_IN_QTI = v_mdsr_input,
	    MDSR_OUT_QTI = v_mdsr_output
	WHERE
	    MDSR_ID = v_mdsr_id;
	    END LOOP;
	  CLOSE cur1;
	  
    OPEN cur2;
    read_loop: LOOP
        FETCH cur2 INTO v_mdsr_id, v_mdsr_input, v_mdsr_output;
        IF done THEN
            LEAVE read_loop;
        END IF;
	UPDATE MEDICALDSR 
	SET 
	    MDSR_IN_QTI = v_mdsr_input,
	    MDSR_OUT_QTI = v_mdsr_output
	WHERE
	    MDSR_ID = v_mdsr_id;
	    END LOOP;
	  CLOSE cur2;

END; //
DELIMITER ;

CALL updateMEDICALDSRfromMEDICALDSRSTOCKMOV();

