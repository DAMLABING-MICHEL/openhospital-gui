-- this procedure is needed to fix double OPDs made by Chronic Module and Pharmacy when not collaborating
ALTER TABLE OPD ADD COLUMN OPD_CHECKED TINYINT(1) NULL;

DROP PROCEDURE IF EXISTS mergeChronicVisits;
DELIMITER //

CREATE PROCEDURE mergeChronicVisits()
BEGIN
    DECLARE v_opd_id INT;
    DECLARE v_opd_date_vis DATE;
    DECLARE v_opd_pat_id INT;
    DECLARE v_opd_dis_id_a VARCHAR(10);
    DECLARE v_opd_id2 INT;
    DECLARE cur1done INT DEFAULT FALSE;
    DECLARE cur1 CURSOR FOR 
    SELECT 
		OPD_ID, OPD_DATE_VIS, OPD_PAT_ID, OPD_DIS_ID_A
	FROM
		OPD JOIN OPD_CHRONIC ON OPD_ID = CV_OPD_ID
	WHERE
		DATE(OPD_DATE_VIS) > STR_TO_DATE('2017-10-01','%Y-%m-%d') -- !!! GO BACKWARD TIME AFTER TIME (REDUCED WORKLOAD)
		AND OPD_CHECKED IS NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET cur1done = 1;

    OPEN cur1;
    read_loop1: LOOP
        FETCH cur1 INTO v_opd_id, v_opd_date_vis, v_opd_pat_id, v_opd_dis_id_a;
        IF cur1done THEN
        	CLOSE cur1;
            LEAVE read_loop1;
        END IF;
	  
        BLOCK2: BEGIN
	    DECLARE cur2done INT DEFAULT FALSE;
		DECLARE cur2 CURSOR FOR SELECT OPD_ID FROM OPD WHERE OPD_US_ID_A = 'Pharmacy' AND OPD_DATE_VIS = v_opd_date_vis AND OPD_PAT_ID = v_opd_pat_id AND OPD_DIS_ID_A = v_opd_dis_id_a;	        
	    DECLARE CONTINUE HANDLER FOR NOT FOUND SET cur2done = 1;
	    OPEN cur2;
	    read_loop2: LOOP
	        FETCH cur2 INTO v_opd_id2;
	        IF cur2done THEN
	        	CLOSE cur2;
	            LEAVE read_loop2;
	        END IF;
		
			UPDATE MEDICALDSRWARDPRESCRIPTION SET MWP_OPD_ID = v_opd_id WHERE MWP_OPD_ID = v_opd_id2;
			UPDATE OPD SET OPD_CHECKED = 1 WHERE OPD_ID = v_opd_id;
            -- UPDATE OPD SET OPD_CHECKED = 0 WHERE OPD_ID = v_opd_id2;
			DELETE FROM OPD WHERE OPD_ID = v_opd_id2;
	
	    	END LOOP read_loop2;
	    END BLOCK2;
		END LOOP read_loop1;
		
END; //
DELIMITER ;

CALL mergeChronicVisits();

-- After the above procedure is completed the OPD_CHECKED column can be dropped:
-- ALTER TABLE OPD DROP COLUMN OPD_CHECKED;