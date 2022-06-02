DELIMITER //

DROP PROCEDURE IF EXISTS calcBalance;
CREATE PROCEDURE calcBalance()
BEGIN
    DECLARE v_id INT;
	DECLARE v_balance DOUBLE;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT BLL_ID, BALANCE FROM (
							SELECT *, ROUND(SUM(AMOUNT - PAYMENTS),3) AS BALANCE FROM (
								SELECT *, IF(SUM(BLP_AMOUNT) IS NULL, 0, SUM(BLP_AMOUNT)) AS PAYMENTS FROM (
									SELECT *, SUM(BLI_ITEM_AMOUNT * BLI_QTY) AS AMOUNT  
									FROM BILLS LEFT JOIN BILLITEMS ON BLL_ID = BLI_ID_BILL
									-- WHERE BLL_STATUS IN ('C','O','L')
									GROUP BY BLL_ID) AS AMOUNTS
								LEFT JOIN BILLPAYMENTS ON BLL_ID = BLP_ID_BILL
								GROUP BY BLL_ID) AS PAYMENTS
							GROUP BY BLL_ID) AS BALANCES
							WHERE BLL_BALANCE <> BALANCE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_id, v_balance;
        IF done THEN
            LEAVE read_loop;
        END IF;
        UPDATE BILLS SET BLL_BALANCE = v_balance WHERE BLL_ID = v_id;
    END LOOP;
  CLOSE cur;

END; //
DELIMITER ;

CALL calcBalance();
