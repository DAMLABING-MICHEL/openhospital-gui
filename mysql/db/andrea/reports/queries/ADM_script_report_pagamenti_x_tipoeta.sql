SELECT DATE as MESE, WRD_NAME, if(PAT_AGE >= 5, if(PAT_AGE<10,"FROM 5 TO 10","OVER 10"),"UNDER 5") AS AGE , round(AVG(AMOUNT),2)  AS AVG,
    round(SUM(AMOUNT),2) as TOTALE, COUNT(ADM_ID) AS NUMERO_RICOVERI, round(MIN(AMOUNT),2) AS MINIMO, 
    round(MAX(AMOUNT),2) AS MASSIMO, round(AVG(PAT_AGE),2) AS AVG_AGE, DATE_N 
FROM
(
select ADM_ID, DATE_FORMAT(BLP_DATE, "%M") AS DATE, PAT_AGE, WRD_NAME, SUM(BLP_AMOUNT) AS AMOUNT, DATE_FORMAT(BLP_DATE, "%m") as DATE_N
 FROM PATIENT JOIN ADMISSION ON PAT_ID = ADM_PAT_ID
 JOIN WARD ON WRD_ID_A = ADM_WRD_ID_A
 JOIN BILLS ON BLL_ID_PAT = PAT_ID
 JOIN BILLPAYMENTS ON BLL_ID = BLP_ID_BILL
 WHERE DATE_FORMAT(BLP_DATE,"%Y-%m-%d") BETWEEN "2014-01-01" AND "2014-08-01"
 AND ADM_DELETED = 'N'
 AND ADM_DATE_DIS IS NOT NULL
 AND ADM_DATE_ADM >= "2014-01-01"
 AND ADM_DATE_DIS <= "2014-08-01"
 AND PAT_DELETED = 'N'
 GROUP BY ADM_ID
) AS A
GROUP BY DATE_N, WRD_NAME, AGE
ORDER BY DATE_N, WRD_NAME, AVG_AGE