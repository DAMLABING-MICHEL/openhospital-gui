-- this procedure have to be ran after wolisso_step_37_admission_bills.sql
ALTER TABLE BILLS ADD COLUMN BLL_CHECKED TINYINT(1) NULL AFTER BLL_ADM_ID;

DROP PROCEDURE IF EXISTS setAdmissionsFromBills;
DELIMITER //

CREATE PROCEDURE setAdmissionsFromBills()
BEGIN
    DECLARE v_adm_id INT;
	DECLARE v_bll_id INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT ADM_ID, BLL_ID -- , BLL_ID_PAT, ADM_PAT_ID, BLL_DATE, ADM_DATE_ADM, ADM_DATE_DIS 
							FROM BILLS LEFT JOIN ADMISSION ON (BLL_ID_PAT = ADM_PAT_ID AND  ADM_DELETED = 'N' AND (BLL_DATE BETWEEN ADM_DATE_ADM AND ADM_DATE_DIS OR ADM_DATE_ADM < BLL_DATE AND ADM_DATE_DIS IS NULL))
							WHERE BLL_CHECKED IS NULL
                            AND BLL_DATE >= '2017-01-01 00:00:00' AND BLL_DATE < '2017-03-24 09:52:01'; -- <-- !!!JUST FOR TESTING!!!! NEW YEAR NEW LIFE?
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_adm_id, v_bll_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
UPDATE BILLS 
SET 
    BLL_ADM_ID = v_adm_id,
    BLL_CHECKED = 1
WHERE
    BLL_ID = v_bll_id;
    END LOOP;
  CLOSE cur;

END; //
DELIMITER ;

CALL setAdmissionsFromBills();

