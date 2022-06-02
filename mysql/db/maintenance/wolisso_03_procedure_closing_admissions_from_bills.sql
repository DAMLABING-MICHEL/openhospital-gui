DELIMITER //

DROP PROCEDURE IF EXISTS closeBillFromAdmissions;
CREATE PROCEDURE closeBillFromAdmissions()
BEGIN
    DECLARE v_id INT;
	DECLARE v_date DATETIME;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT ADM_ID, BLL_UPDATE FROM BILLS RIGHT JOIN ADMISSION ON BLL_ID_PAT = ADM_PAT_ID
							WHERE BLL_STATUS IN ('C')
							AND DATE(BLL_DATE) = DATE(ADM_DATE_ADM)
							AND ADM_IN = 1
							AND ADM_DELETED = 'N';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_id, v_date;
        IF done THEN
            LEAVE read_loop;
        END IF;
        UPDATE ADMISSION SET ADM_DATE_DIS = v_date, ADM_DIST_ID_A = 'I', ADM_OUT_DIS_ID_A = '137.3', ADM_IN = 0	WHERE ADM_ID = v_id;
    END LOOP;
  CLOSE cur;

END; //
DELIMITER ;

CALL closeBillFromAdmissions();

