CREATE
    TABLE
        cart(
            cart_number VARCHAR(60) NOT NULL COMMENT '購物車編號',
            customer VARCHAR(60) NOT NULL COMMENT '客戶名稱',
            amount INT NOT NULL COMMENT '金額',
            created_by VARCHAR(60) NOT NULL,
            created_date datetime NOT NULL,
            last_modified_by VARCHAR(60) NOT NULL,
            last_modified_date datetime NOT NULL,
            PRIMARY KEY(cart_number)
        ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '購物車資料';

CREATE
    TABLE
        cart_product(
            id INT NOT NULL AUTO_INCREMENT COMMENT '流水編號',
            cart_number VARCHAR(60) NOT NULL COMMENT '購物車編號',
            product_id VARCHAR(10) NOT NULL COMMENT '商品編號',
            product_name VARCHAR(10) NOT NULL COMMENT '商品編號',
            amount INT NOT NULL COMMENT '單價金額',
            created_by VARCHAR(60) NOT NULL,
            created_date datetime NOT NULL,
            last_modified_by VARCHAR(60) NOT NULL,
            last_modified_date datetime NOT NULL,
            PRIMARY KEY(id)
        ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '購物車商品資料';

ALTER TABLE
    cart_product ADD FOREIGN KEY(cart_number) REFERENCES cart(cart_number);