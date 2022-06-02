DELIMITER //

DROP PROCEDURE IF EXISTS closeAdmissionFromBill;
CREATE PROCEDURE closeAdmissionFromBill()
BEGIN
    DECLARE v_id INT;
	DECLARE v_date DATETIME;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT BLL_ID, ADM_DATE_DIS FROM BILLS RIGHT JOIN ADMISSION ON BLL_ID_PAT = ADM_PAT_ID
							WHERE BLL_STATUS IN ('O')
							AND DATE(BLL_DATE) = DATE(ADM_DATE_ADM)
							AND ADM_IN = 0
							AND ADM_DELETED = 'N'
							AND BLL_BALANCE = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_id, v_date;
        IF done THEN
            LEAVE read_loop;
        END IF;
        UPDATE BILLS SET BLL_STATUS = 'C' AND BLL_UPDATE = v_date WHERE BLL_ID = v_id;
    END LOOP;
  CLOSE cur;

END; //
DELIMITER ;

CALL closeAdmissionFromBill();

