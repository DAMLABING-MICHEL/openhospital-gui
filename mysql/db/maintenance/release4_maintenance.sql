START TRANSACTION;

-- STEP 0) There are several bills with wrong balance, fix them with the script:
source wolisso_01_procedure_accounting_balances.sql;


-- STEP 0) There are several bills (73) that should have not been in STATUS = 'C' because BALANCE <> 0, that should be RE-OPEN, with the query
UPDATE BILLS SET BLL_STATUS = 'O'
WHERE BLL_STATUS = 'C'
AND BLL_BALANCE <> 0;


-- STEP 1) Bills with no items (398)
-- DELETE empty bills (no items) regardless they are Open, PayLater or Closed with the query:
UPDATE BILLS SET BLL_STATUS = 'D' WHERE BLL_ID IN (
	SELECT BLL_ID FROM (
		SELECT *, COUNT(BLI_ID) AS ITEMS FROM BILLS LEFT JOIN BILLITEMS ON BLL_ID = BLI_ID_BILL
		-- WHERE BLL_STATUS IN ('O', 'L', 'C')
		GROUP BY (BLL_ID)
		HAVING ITEMS = 0 
	) AS EMPTY_BILLS
);


-- STEP 2) Bills with amount = 0 (138)
-- CLOSE all Open and PayLater prescription bills with amount = 0 (free given drugs) with the query:
-- (in the future such bills should be closed automatically)
UPDATE BILLS SET BLL_STATUS = 'C' WHERE BLL_ID IN (
	SELECT BLL_ID FROM (
		SELECT BLL_ID FROM BILLS RIGHT JOIN MEDICALDSRWARDPRESCRIPTION ON BLL_ID = MWP_BLL_ID
		WHERE BLL_STATUS IN ('O','L') AND BLL_AMOUNT = 0
	) AS FREE_PRESCRIPTIONS
);


-- STEP 3) Exempted bills (kept open) (1034)
-- CLOSE all Open and PayLater bills that have exempted services and the balance is zero, with the query:
-- (in the future such bills should be closed automatically)
UPDATE BILLS SET BLL_STATUS = 'C' WHERE BLL_ID IN (
	SELECT BLL_ID FROM (
		SELECT * FROM BILLS RIGHT JOIN BILLITEMS ON BLL_ID = BLI_ID_BILL
		WHERE (BLI_ID_PRICE IN ('EXE','STAFF','FREE','COFF')
		OR BLI_ITEM_DESC LIKE 'EXE%'
		OR BLI_ITEM_DESC LIKE 'STAFF%'
		OR BLI_ITEM_DESC LIKE 'FREE%'
		OR BLI_ITEM_DESC LIKE 'COFF%')
		AND BLL_STATUS IN ('O','L')
		AND BLL_BALANCE = 0
	) AS FREE_PRESCRIPTIONS
);


-- STEP 4) Prescription bills not closed (but dispensed and BALANCE = 0) (3):
-- There are some prescription bills that have been finished (dispensed) but not closed, fix them with the query:
UPDATE BILLS SET BLL_STATUS = 'C' WHERE BLL_ID IN (
	SELECT BLL_ID FROM (
		SELECT * FROM BILLS RIGHT JOIN MEDICALDSRWARDPRESCRIPTION ON BLL_ID = MWP_BLL_ID
		WHERE BLL_STATUS IN ('O')
		AND MWP_STATUS = 1
		AND BLL_BALANCE = 0
	) AS BILLSTOBECLOSED
);


-- STEP 5) Prescription bills not claimed (6024)
-- There are many prescription bills still open but never claimed (not dispensed also) that can be deleted or set to 'X' status, fix with the query:
UPDATE BILLS
        RIGHT JOIN
    MEDICALDSRWARDPRESCRIPTION ON BLL_ID = MWP_BLL_ID 
SET 
    BLL_STATUS = 'X'
WHERE
    BLL_STATUS = 'O' AND MWP_STATUS = 0;


-- STEP 6) OPEN bills related to CLOSED admissions with BALANCE = 0:
-- There are many bills related to closed ADMISSION with BALANCE = 0. Find them with the query:
-- SELECT * FROM BILLS RIGHT JOIN ADMISSION ON BLL_ID_PAT = ADM_PAT_ID
-- WHERE BLL_STATUS IN ('O')
-- AND DATE(BLL_DATE) = DATE(ADM_DATE_ADM)
-- AND ADM_IN = 0
-- AND ADM_DELETED = 'N'
-- AND BLL_BALANCE = 0
-- They are so divided:
-- 2013	12
-- 2014	288
-- 2015	399
-- 2016	202
-- and can be CLOSED at the date of the DISCHARGE date with the script:
source wolisso_02_procedure_closing_bills_from_admissions.sql;


-- STEP 7) CLOSED bills related to OPEN admission with BALANCE = 0:
-- These bills are closed but the admission is still pending and have to be closed a the same BILL UPDATE date, but with which DISEASE OUT and OUTCOME?
-- SELECT * FROM BILLS RIGHT JOIN ADMISSION ON BLL_ID_PAT = ADM_PAT_ID
-- WHERE BLL_STATUS IN ('C')
-- AND DATE(BLL_DATE) = DATE(ADM_DATE_ADM)
-- AND ADM_IN = 1
-- AND ADM_DELETED = 'N'
-- The are so divided:
-- 2013	68
-- 2014	539
-- 2015	893
-- 2016	632
-- and can be closed with this script, configured:
source wolisso_03_procedure_closing_admissions_from_bills.sql;


-- STEP 8)
-- dispense all the drugs related to the PayLater bills (java org.isf.utils.db.maintenance.TestPayLaterDispense and check OPD Pharmacy)


-- STEP 9)
-- set as PAID (with payment) all the bills whose companies have no longer outstading balances, starting from the full statement list, with this query:

-- STEPS TO FIX MANUALLY IN "Pending Bills Analysis Fix Manually.txt"

COMMIT;
