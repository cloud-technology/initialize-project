SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
 -- Table cart mockdata
 -- ----------------------------
INSERT INTO cart(cart_number, customer, amount, created_by, created_date, last_modified_by, last_modified_date)
VALUES('001', 'sam', 500, 'sam', CURRENT_TIMESTAMP, 'sam', CURRENT_TIMESTAMP);

SET FOREIGN_KEY_CHECKS = 1;