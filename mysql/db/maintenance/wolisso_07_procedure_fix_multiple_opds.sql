-- this procedure is needed to fix multiple OPDs made by Pharmacy for Emergency patients
DROP PROCEDURE IF EXISTS deletePharmacyDuplicates;
DELIMITER //

CREATE PROCEDURE deletePharmacyDuplicates()
BEGIN
    DECLARE v_opd_id INT;
    DECLARE v_opd_date_vis DATE;
    DECLARE v_opd_pat_id INT;
    DECLARE v_opd_dis_id_a VARCHAR(10);
    DECLARE cur1done INT DEFAULT FALSE;
    DECLARE cur1 CURSOR FOR 
    SELECT 
	    OPD_ID, OPD_DATE_VIS, OPD_PAT_ID, OPD_DIS_ID_A
	FROM
	    OPD
	WHERE
	    OPD_US_ID_A IN ('Pharmacy')
	GROUP BY DATE(OPD_DATE_VIS), OPD_PAT_ID, OPD_DIS_ID_A
	HAVING COUNT(*) > 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET cur1done = 1;

    OPEN cur1;
    read_loop1: LOOP
        FETCH cur1 INTO v_opd_id, v_opd_date_vis, v_opd_pat_id, v_opd_dis_id_a;
        IF cur1done THEN
        	CLOSE cur1;
            LEAVE read_loop1;
        END IF;
        
        DELETE FROM OPD 
		WHERE
		    OPD_DATE_VIS = v_opd_date_vis
		    AND OPD_PAT_ID = v_opd_pat_id
		    AND OPD_DIS_ID_A = v_opd_dis_id_a
		    AND OPD_ID <> v_opd_id; -- REMAINS ONLY ONE OPD FOR THE SAME DATE, PATIENT AND DIAGNOSIS
	  
		END LOOP read_loop1;
END; //
DELIMITER ;

CALL deletePharmacyDuplicates();


