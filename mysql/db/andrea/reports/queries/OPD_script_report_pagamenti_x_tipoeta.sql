SELECT DATE as MESE, AGE, ROUND(MIN(AMOUNT),2) AS MINIMO, ROUND(AVG(AMOUNT),2) AS MEDIA, ROUND(MAX(AMOUNT),2) AS MASSIMO, 
 count(amount) as NUMBER FROM
(
select DISTINCTROW OPD_ID, BLL_ID, BLL_ID_PAT, BLP_ID, DATE_FORMAT(BLP_DATE,"%Y-%m-%d"), 
    DATE_FORMAT(BLP_DATE, "%m") AS DATE, BLP_AMOUNT AS AMOUNT, 
    if(PAT_AGE<10,"UNDER TEN","OVER TEN") AS AGE 
    FROM OPD inner JOIN BILLS 
    ON DATE_FORMAT(BLL_DATE,"%Y-%m-%d")=DATE_FORMAT(OPD_DATE,"%Y-%m-%d") AND BLL_ID_PAT=OPD_PAT_ID 
    JOIN BILLPAYMENTS
    ON BLP_ID_BILL = BLL_ID
    JOIN PATIENT
    ON BLL_ID_PAT = PAT_ID
    WHERE DATE_FORMAT(BLP_DATE,"%Y-%m-%d") BETWEEN "2014-01-01" AND "2014-08-01"
	AND PAT_DELETED = 'N'
    GROUP BY BLL_ID
) AS G
GROUP BY MESE, AGE
