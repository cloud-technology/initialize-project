package com.example.demo;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.ct.DemoApplication;
import com.github.ct.shareddomain.dto.CartInfoDto;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import javax.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@ActiveProfiles("unittest")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
class DemoApplicationTests {
  @Value("${apiDocPath}")
  private String apiDocPath;

  @Container
  private static final PostgreSQLContainer database =
      new PostgreSQLContainer("postgres:13.3").withDatabaseName("testdb");

  @Container
  public static final GenericContainer mockGoods =
      new GenericContainer(DockerImageName.parse("stoplight/prism:4"))
          .withExposedPorts(4010)
          .withFileSystemBind(
              "./specs/openapi-goods.yml", "/tmp/openapi-goods.yml", BindMode.READ_ONLY)
          .withCommand("mock -h 0.0.0.0 /tmp/openapi-goods.yml");

  @Container
  public static final GenericContainer mockOrders =
      new GenericContainer(DockerImageName.parse("stoplight/prism:4"))
          .withExposedPorts(4010)
          .withFileSystemBind(
              "./specs/openapi-orders.yml", "/tmp/openapi-orders.yml", BindMode.READ_ONLY)
          .withCommand("mock -h 0.0.0.0 /tmp/openapi-orders.yml");

  @Autowired private TestRestTemplate testRestTemplate;

  @DynamicPropertySource
  static void mysqlProperties(DynamicPropertyRegistry registry) {
    log.debug(
        "url={}, username={}, password={}",
        database.getJdbcUrl(),
        database.getUsername(),
        database.getPassword());
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("spring.datasource.password", database::getPassword);
  }

  @Test
  @DisplayName("Specification to html generator 😱")
  public void openapi() throws Exception {
    //
    Path openapiFile = Paths.get(apiDocPath);
    // Path openapiFile = Paths.get("swagger", "api.json");
    openapiFile.toFile().getParentFile().mkdirs();
    //
    UriComponents uriComponents = UriComponentsBuilder.fromUriString("/v2/api-docs").build();
    ResponseEntity<String> responseEntity = null;

    // HttpHeaders headers = new HttpHeaders();
    // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      responseEntity =
          testRestTemplate.exchange(uriComponents.getPath(), HttpMethod.GET, null, String.class);
    } catch (Exception e) {
      log.error("uri={}, responseEntity={}", uriComponents.getPath(), responseEntity, e);
    }
    try (BufferedWriter writer = Files.newBufferedWriter(openapiFile, StandardCharsets.UTF_8)) {
      writer.write(responseEntity.getBody());
    }
    // 將 openapi 輸出到 build/asciidoc/swagger.json

    // Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
    // // .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
    // // .withOutputLanguage(Language.EN)
    // // .withPathsGroupedBy(GroupBy.TAGS).withGeneratedExamples()
    // // .withoutInlineSchema()
    // .build();
    // System.out.println(config);
    // System.out.println("isBasePathPrefixEnabled=" +
    // config.isBasePathPrefixEnabled());
    // System.out.println("isFlatBodyEnabled=" + config.isFlatBodyEnabled());
    // System.out.println("isGeneratedExamplesEnabled=" +
    // config.isGeneratedExamplesEnabled());
    // System.out.println("isInlineSchemaEnabled=" +
    // config.isInlineSchemaEnabled());
    // System.out.println("isInterDocumentCrossReferencesEnabled="
    // + config.isInterDocumentCrossReferencesEnabled());
    // System.out.println("isListDelimiterEnabled=" +
    // config.isListDelimiterEnabled());
    // System.out.println("isPathSecuritySectionEnabled=" +
    // config.isPathSecuritySectionEnabled());
    // System.out.println("isSeparatedDefinitionsEnabled=" +
    // config.isSeparatedDefinitionsEnabled());
    // System.out.println("isSeparatedOperationsEnabled=" +
    // config.isSeparatedOperationsEnabled());
    // // // 轉成 swagger.adoc
    // Swagger2MarkupConverter converter =
    // Swagger2MarkupConverter.from(openapiTempFile).withConfig(config).build();
    // converter.toFile(Paths.get("build/generated-snippets/index.adoc"));
    // AsciiDocBuilder b = new AsciiDocBuilder()

    // log.info("openapi={}", responseEntity.getBody());
    assertNotNull(responseEntity.getBody());
  }

  @Test
  @DisplayName("testGoodsAPI 😱")
  public void testGoodsAPI() throws Exception {
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromHttpUrl(
            "http://localhost:" + mockGoods.getMappedPort(4010) + "/appName");
    UriComponents uriComponents = uriComponentsBuilder.build();
    ResponseEntity<String> responseEntity = null;
    try {
      RestTemplate restTemplate = new RestTemplate();
      responseEntity =
          restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null, String.class);
      log.info("uri={}, responseBody={}", uriComponents.toUri(), responseEntity.getBody());
    } catch (Exception e) {
      log.error("uri={}, responseEntity={}", uriComponents.toUri(), responseEntity, e);
    }
    assertNotNull(responseEntity.getBody());
  }

  @Test
  @DisplayName("testOrdersAPI 😱")
  public void testOrdersAPI() throws Exception {
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromHttpUrl(
            "http://localhost:" + mockOrders.getMappedPort(4010) + "/appName");
    UriComponents uriComponents = uriComponentsBuilder.build();
    ResponseEntity<String> responseEntity = null;
    try {
      RestTemplate restTemplate = new RestTemplate();
      responseEntity =
          restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null, String.class);
      log.info("uri={}, responseBody={}", uriComponents.toUri(), responseEntity.getBody());
    } catch (Exception e) {
      log.error("uri={}, responseEntity={}", uriComponents.toUri(), responseEntity, e);
    }
    assertNotNull(responseEntity.getBody());
  }

  // @Test
  public void get_cart_test() throws Exception {
    log.debug("some input ex cartId={}", "001");
    UriComponents uriComponents = UriComponentsBuilder.fromUriString("/carts/{cartId}").build();
    uriComponents = uriComponents.expand(Collections.singletonMap("cartId", "001"));
    ResponseEntity<CartInfoDto> responseEntity = null;
    try {
      responseEntity =
          testRestTemplate.exchange(
              uriComponents.getPath(), HttpMethod.GET, null, CartInfoDto.class);
    } catch (Exception e) {
      log.error("uri={}, responseEntity={}", uriComponents.getPath(), responseEntity, e);
    }
    log.info(
        "uri={}, statusCode={}, CartInfoDto={}",
        uriComponents.getPath(),
        responseEntity.getStatusCode(),
        responseEntity.getBody());
    Assert.assertEquals(responseEntity.getBody().getCustomerName(), "sam");
  }

  // @Test
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
        .optionalLayer("dto")
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
    // layer 必要定義 且不會為空
    // optionalLayer 可能為空的
  }
}
