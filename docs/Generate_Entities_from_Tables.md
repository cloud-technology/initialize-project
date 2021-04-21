# 使用 jooq 產生 JPA
下載 [jooq](https://www.jooq.org/download/) 解壓後 把 jOOQ-lib 內的 jar 檔放到 jooq/jooq-lib  
  
下載對應的 JDBC 驅動 放到 jooq/db-lib  
  
下載額外的依賴  
[JAXB API » 2.3.1](https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api/2.3.1) 放到 jooq/3rd-lib

## 起動需要的 Mysql
``` bash
docker-compose -f docker/docker-compose.yml up -d
```

## 啟動APP
透過 flyway 建立 DB Table 結束後停止 application

## 執行 jooq 產生器
``` bash
java -cp jooq/jooq-lib/*:jooq/3rd-lib/*:jooq/db-lib/* org.jooq.codegen.GenerationTool jooq/jooq-config.xml
```
產出的資料夾在此  
jooq/generator/com/example/demo/boundedcontext/domain  
最主要 Entity 在這邊  
jooq/generator/com/example/demo/boundedcontext/domain/tables/pojos  

## 拿出 Entity 並移除用不到的 Jooq 程式碼
``` bash
mv jooq/generator/com/example/demo/boundedcontext/domain/tables/pojos jooq/pojos
rm -rf jooq/generator
```

## 補強 Entity

### 啟動 ubuntu
Mac 的 sed 跟 Linux 有點差異, 還是起一個 Ubuntu 比較標準  
``` bash
docker run -it -v ${PWD}/jooq/pojos:/pojos ubuntu bash
```

### 處理 Entity
移除 schema
``` bash
sed -i '/schema =/d' pojos/*.java
```
引入套件
``` bash
sed -i '/package/ a\import com.fasterxml.jackson.annotation.JsonIgnoreProperties;' pojos/*.java
sed -i '/public class/ i\@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "fieldHandler"})' pojos/*.java
```
調整 package
``` bash
sed -i 's/com.example.demo.boundedcontext.domain.tables.pojos/com.example.demo.cartms.domain.projecttions/g' pojos/*.java
```
跳出 container
``` bash
exit
```
搬到 src 內(如果有其他功能想引入可以一起做完再搬, 參考 其他 JPA 功能)
``` bash
export BasePackage=src/main/java/com/example/demo
export BoundedContext=cartms
mkdir -p ${BasePackage}/${BoundedContext}/domain/projecttions
mv jooq/pojos/*.java ${BasePackage}/${BoundedContext}/domain/projecttions
```

刪除產生的資料夾
```
rm -rf jooq/pojos
```

## 其他 JPA 功能

### 增加 Event 發佈能力
``` bash
sed -i '/package/ a\import org.springframework.data.domain.AbstractAggregateRoot;' pojos/*.java
sed -i 's/implements Serializable/extends AbstractAggregateRoot implements Serializable/g' pojos/*.java
```

### 增加 Auditing 紀錄
``` bash
sed -i '/package/ a\import javax.persistence.EntityListeners;' pojos/*.java
sed -i '/package/ a\import org.springframework.data.jpa.domain.support.AuditingEntityListener;' pojos/*.java
sed -i '/package/ a\import org.springframework.data.annotation.CreatedBy;' pojos/*.java
sed -i '/package/ a\import org.springframework.data.annotation.CreatedDate;' pojos/*.java
sed -i '/package/ a\import org.springframework.data.annotation.LastModifiedBy;' pojos/*.java
sed -i '/package/ a\import org.springframework.data.annotation.LastModifiedDate;' pojos/*.java
sed -i '/public class/ i\@EntityListeners({AuditingEntityListener.class})' pojos/*.java
sed -i '/getCreatedBy/ i\    @CreatedBy' pojos/*.java
sed -i '/getCreatedDate/ i\    @CreatedDate' pojos/*.java
sed -i '/getLastModifiedBy/ i\    @LastModifiedBy' pojos/*.java
sed -i '/getLastModifiedDate/ i\    @LastModifiedDate' pojos/*.java
```

# 設定 Repository & Spring Data Rest
build.gradle 需要 data-rest 此套件支援
``` groovy
implementation 'org.springframework.boot:spring-boot-starter-data-rest'
```

## 新增 Repository
建立資料夾
``` bash
export BasePackage=src/main/java/com/example/demo
export BoundedContext=cartms
mkdir -p ${BasePackage}/${BoundedContext}/infrastructure/repositories
```

新增 CartRepository.java
``` bash
cat << EOF > ${BasePackage}/${BoundedContext}/infrastructure/repositories/CartRepository.java
package com.example.demo.cartms.infrastructure.repositories;

import com.example.demo.cartms.domain.projecttions.Cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestResource
public interface CartRepository extends JpaRepository<Cart, String> {

    // @RestResource(exported = false)
    // Page<Cart> findAll(Pageable pageable);

    @ApiOperation("find by name")
    Page<Cart> findByCustomer(@ApiParam(value="客戶") @Param("customer") String customer, Pageable pageable);

}
EOF
```