#script di migrazione dalla 1.6.0
source step_26_communication.sql;
-- source step_27_new_buttons_bill_and_receipt.sql; --already there
-- source step_28_other_prices_extension.sql; --already there
source step_29_add_log_method_and_user.sql;
source step_30_help_manual.sql
source step_31_alter_tables_innodb.sql; -- alreday contains one statement, no problem
source step_32_convert_birthdate_to_date.sql;
source step_33_grants_on_patientfolder.sql;