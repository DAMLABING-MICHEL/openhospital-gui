UPDATE BILLITEMS SET BLI_ID_PRICE = CONCAT('MED', BLI_ID_PRICE) WHERE BLI_ID_PRICE REGEXP '^-?[0-9]+$';