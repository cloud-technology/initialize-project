package com.example.demo;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.example.demo.shareddomain.dto.CartInfoDto;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import java.util.Collections;
import javax.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("unittest")
@Testcontainers(disabledWithoutDocker = true)
class DemoApplicationTests {
  @Container
  public static MySQLContainer db = new MySQLContainer("mysql:5.7.22").withDatabaseName("testdb");

  @Autowired private TestRestTemplate restTemplate;

  @DynamicPropertySource
  static void mysqlProperties(DynamicPropertyRegistry registry) {
    log.debug(
        "url={}, username={}, password={}", db.getJdbcUrl(), db.getUsername(), db.getPassword());
    registry.add("spring.datasource.url", db::getJdbcUrl);
    registry.add("spring.datasource.username", db::getUsername);
    registry.add("spring.datasource.password", db::getPassword);
  }

  @Test
  public void get_cart_test() throws Exception {
    log.debug("some input ex cartId={}", "001");
    UriComponents uriComponents = UriComponentsBuilder.fromUriString("/carts/{cartId}").build();
    uriComponents = uriComponents.expand(Collections.singletonMap("cartId", "001"));
    ResponseEntity<CartInfoDto> responseEntity = null;
    try {
      responseEntity =
          restTemplate.exchange(uriComponents.getPath(), HttpMethod.GET, null, CartInfoDto.class);
    } catch (Exception e) {
      log.error("uri={}, responseEntity={}", uriComponents.getPath(), responseEntity, e);
    }
    log.info(
        "uri={}, statusCode={}, CartInfoDto={}",
        uriComponents.getPath(),
        responseEntity.getStatusCode(),
        responseEntity.getBody());
    Assert.assertEquals(responseEntity.getBody().getCustomer(), "sam");
  }

  @Test
  public void file_name_and_package_name_and_architecture_rule() {
    JavaClasses importedClasses =
        new ClassFileImporter()
            // exclude test
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            // exclude jar
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            // exclude shareddomain
            .withImportOption(
                new ImportOption() {
                  @Override
                  public boolean includes(Location location) {
                    return !location.contains("/shareddomain/");
                  }
                })
            // package source
            .importPackages("com.example.demo");
    // package & file name rule
    classes()
        .that()
        .resideInAPackage("..commandgateways")
        .should()
        .haveSimpleNameEndingWith("CommandService")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..querygateways")
        .should()
        .haveSimpleNameEndingWith("QueryService")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..commands")
        .should()
        .haveSimpleNameEndingWith("Command")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..events")
        .should()
        .haveSimpleNameEndingWith("Event")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..aggregates")
        .should()
        .haveSimpleNameEndingWith("Aggregate")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..valueobjects")
        .should()
        .haveSimpleNameEndingWith("Vo")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..queries")
        .should()
        .haveSimpleNameEndingWith("Query")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..queryhandlers")
        .should()
        .haveSimpleNameEndingWith("QueryHandler")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..eventhandlers")
        .should()
        .haveSimpleNameEndingWith("EventHandler")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..dto")
        .should()
        .haveSimpleNameEndingWith("Dto")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..rest")
        .should()
        .haveSimpleNameEndingWith("Controller")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..transform")
        .should()
        .haveSimpleNameEndingWith("Assembler")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..configuration")
        .should()
        .haveSimpleNameEndingWith("Config")
        .check(importedClasses);
    classes()
        .that()
        .resideInAPackage("..exceptions")
        .should()
        .haveSimpleNameEndingWith("Exception")
        .check(importedClasses);
    // repositories should use @RestResource
    classes()
        .that()
        .resideInAPackage("..repositories")
        .should()
        .haveSimpleNameEndingWith("Repository")
        .andShould()
        .beAnnotatedWith(RestResource.class)
        .check(importedClasses);
    // projecttions should use @Entity
    classes()
        .that()
        .resideInAPackage("..projecttions")
        .should()
        .beAnnotatedWith(Entity.class)
        .check(importedClasses);

    // @formatter:off
    layeredArchitecture()
        .layer("app")
        .definedBy("com.example.demo")
        .optionalLayer("rest")
        .definedBy("..rest")
        .layer("dto")
        .definedBy("..dto")
        .optionalLayer("application")
        .definedBy("..application..")
        .optionalLayer("domain")
        .definedBy("..domain..")
        .optionalLayer("infrastructure")
        .definedBy("..infrastructure..")
        .optionalLayer("interfaces")
        .definedBy("..interfaces..")
        .whereLayer("rest")
        .mayNotBeAccessedByAnyLayer()
        .whereLayer("infrastructure")
        .mayOnlyBeAccessedByLayers("rest", "application")
        .whereLayer("application")
        .mayOnlyBeAccessedByLayers("domain", "rest")
        .whereLayer("dto")
        .mayOnlyBeAccessedByLayers("domain", "application", "rest", "interfaces", "app")
        .check(importedClasses);
    // @formatter:on
  }
}
