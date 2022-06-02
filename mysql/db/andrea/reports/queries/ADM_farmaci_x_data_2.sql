select ADM_ID, MWP_ID, ADM_PAT_ID, MWPD_MDSR_ID, MDSR_CODE, MDSR_DESC, MWP_DATE, MWP_WRD_ID_A, MWP_BLL_ID, MWP_DIS_ID_A, MWP_PRS_ID,
 ADM_DATE_ADM, ADM_ADMT_ID_A_ADM, ADM_IN_DIS_ID_A, ADM_OUT_DIS_ID_A, ADM_OUT_DIS_ID_A_2, ADM_OUT_DIS_ID_A_3,
ADM_DATE_DIS, ADM_DIST_ID_A
from 
MEDICALDSR
join

(
select ADM_ID, MWP_ID, ADM_PAT_ID, MWPD_MDSR_ID, MWP_DATE, MWP_WRD_ID_A, MWP_BLL_ID, MWP_DIS_ID_A, MWP_PRS_ID,
 ADM_DATE_ADM, ADM_ADMT_ID_A_ADM, ADM_IN_DIS_ID_A, ADM_OUT_DIS_ID_A, ADM_OUT_DIS_ID_A_2, ADM_OUT_DIS_ID_A_3,
ADM_DATE_DIS, ADM_DIST_ID_A
from 
MEDICALDSRWARDPRESCRIPTIONDETAIL
join 
(

select ADM_ID, MWP_ID, ADM_PAT_ID, MWP_DATE, MWP_WRD_ID_A, MWP_BLL_ID, MWP_DIS_ID_A, MWP_PRS_ID,
 ADM_DATE_ADM, ADM_ADMT_ID_A_ADM, ADM_IN_DIS_ID_A, ADM_OUT_DIS_ID_A, ADM_OUT_DIS_ID_A_2, ADM_OUT_DIS_ID_A_3,
ADM_DATE_DIS, ADM_DIST_ID_A
  from MEDICALDSRWARDPRESCRIPTION
join 
(
select ADM_ID, ADM_PAT_ID, ADM_DATE_ADM, ADM_ADMT_ID_A_ADM, ADM_IN_DIS_ID_A, ADM_OUT_DIS_ID_A, ADM_OUT_DIS_ID_A_2, ADM_OUT_DIS_ID_A_3,
ADM_DATE_DIS, ADM_DIST_ID_A from ADMISSION
 WHERE DATE_FORMAT(ADM_DATE_DIS, '%Y-%m-&d') >= '2014-01-01' and DATE_FORMAT(ADM_DATE_DIS, '%Y-%m-&d') <= '2014-07-01' 
  and ADM_DATE_DIS != 'NULL' AND ADM_DELETED='N'
) as B
on MWP_PAT_ID=ADM_PAT_ID and MWP_DATE >= ADM_DATE_ADM and MWP_DATE <= ADM_DATE_DIS

order by ADM_ID, MWP_ID, ADM_PAT_ID
) as C

on MWPD_ID = MWP_ID

) as D

on MWPD_MDSR_ID=MDSR_ID