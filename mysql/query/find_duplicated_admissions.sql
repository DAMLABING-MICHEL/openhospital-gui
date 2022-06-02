UPDATE ADMISSION SET ADM_DELETED = '-' WHERE ADM_ID IN (
    SELECT ADM_ID FROM (
        SELECT * FROM ADMISSION WHERE ADM_PAT_ID IN (
            SELECT ADM_PAT_ID FROM (
                select ADM_PAT_ID, ADM_DATE_ADM, count(*) cnt 
                from ADMISSION 
                group by ADM_PAT_ID, ADM_DATE_ADM 
                having cnt > 1
                order by cnt asc
            ) AS DUPLICATES
        ) ORDER BY ADM_PAT_ID
    ) AS FULLROWS
    WHERE ADM_DATE_DIS IS NULL
)