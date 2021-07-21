package com.github.ct.configuration;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.Response;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;

@EnableOpenApi
@Configuration
@Profile({"dev", "unittest"})
public class OpenAPIConfig {

  private static final String TITLE = "Init project Demo";
  private static final String DESCRIPTION = "Quickly complete the initial project setup";
  private static final String VERSION = "1.0.1";
  private static final String TERMS_OF_SERVICE_URL = "";
  private static final String LICENSE = "Apache 2.0";
  private static final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";
  private static final String CONTACT_NAME = "sam";
  private static final String CONTACT_URL = "https://blog.samzhu.dev";
  private static final String CONTACT_EMAIL = "spike19820318@gmail.com";
  private static final String BASIC_AUTH_NAME = "BasicAuth";
  private static final String BEARER_TOKEN_NAME = "BearerToken";

  @Bean
  public Docket api() {
    // @formatter:off
    return new Docket(DocumentationType.SWAGGER_2)
        // 為了顯示 CompletableFuture 內的 DTO 描述
        .genericModelSubstitutes(ResponseEntity.class, CompletableFuture.class)
        // .securityContexts(this.securityContext())
        // .securitySchemes(this.securitySchemes())
        // .useDefaultResponseMessages(false)
        // .additionalModels(new TypeResolver().resolve(ErrorMsg.class))
        // .globalResponses(HttpMethod.GET, this.getGlobalResonseMessage())
        // .globalResponses(HttpMethod.POST, this.getGlobalResonseMessage())
        // .globalResponses(HttpMethod.PUT, this.getGlobalResonseMessage())
        // .globalResponses(HttpMethod.DELETE, this.getGlobalResonseMessage())
        .apiInfo(this.apiInfo())
        .select()
        // .apis(RequestHandlerSelectors.basePackage("com.example.demo"))
        .apis(RequestHandlerSelectors.any())
        // .paths(PathSelectors.any())
        // 濾掉預設的與監控路徑
        .paths(
            path -> {
              return !(path.matches("/error")
                  || path.matches("/actuator+(.*)")
                  || path.matches("/profile"));
            })
        .build();
    // @formatter:on
  }

  private ApiInfo apiInfo() {
    // @formatter:off
    return new ApiInfoBuilder()
        .title(TITLE)
        .description(DESCRIPTION)
        .version(VERSION)
        .termsOfServiceUrl(TERMS_OF_SERVICE_URL)
        .contact(this.contact())
        .license(LICENSE)
        .licenseUrl(LICENSE_URL)
        .build();
    // @formatter:on
  }

  private Contact contact() {
    return new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL);
  }

  /**
   * 全域的錯誤訊息格式
   *
   * @return
   */
  private List<Response> getGlobalResonseMessage() {
    List<Response> responseList = new ArrayList<>();
    // responseList.add(this.createResponse(HttpStatus.BAD_REQUEST,
    // HttpStatus.BAD_REQUEST.getReasonPhrase()));
    // responseList.add(this.createResponse(HttpStatus.UNAUTHORIZED,
    // HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    // responseList.add(this.createResponse(HttpStatus.FORBIDDEN,
    // HttpStatus.FORBIDDEN.getReasonPhrase()));
    // responseList.add(this.createResponse(HttpStatus.NOT_FOUND,
    // HttpStatus.NOT_FOUND.getReasonPhrase()));
    // responseList.add(this.createResponse(HttpStatus.CONFLICT,
    // HttpStatus.CONFLICT.getReasonPhrase()));
    // responseList.add(this.createResponse(HttpStatus.INTERNAL_SERVER_ERROR,
    // HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    return responseList;
  }

  /**
   * 建立共用的錯誤訊息格式
   *
   * @param httpStatus
   * @param description
   * @return springfox.documentation.service.Response
   */
  private Response createResponse(HttpStatus httpStatus, String description) {
    // @formatter:off
    return new ResponseBuilder()
        .code(String.valueOf(httpStatus.value()))
        .description(description)
        .representation(MediaType.APPLICATION_JSON)
        .apply(
            pepresentationBuilder ->
                pepresentationBuilder.model(
                    modelSpecificationBuilder ->
                        modelSpecificationBuilder
                            .name("ErrorResponse")
                            .referenceModel(
                                referenceModelSpecificationBuilder ->
                                    referenceModelSpecificationBuilder
                                        .key(
                                            modelKeyBuilder ->
                                                modelKeyBuilder
                                                    .isResponse(true)
                                                    .qualifiedModelName(
                                                        qualifiedModelNameBuilder ->
                                                            qualifiedModelNameBuilder
                                                                .name("ErrorMsg")
                                                                .namespace(
                                                                    "com.cathay.wmsp.shareddomain.dto")
                                                                .build())
                                                    .build())
                                        .build())
                            .build()))
        .build();
    // @formatter:on
  }

  private List<SecurityScheme> securitySchemes() {
    GrantType grantType = new ResourceOwnerPasswordCredentialsGrant("/oauth/token");

    List<SecurityScheme> securitySchemes = new ArrayList();

    OAuth oAuth =
        new OAuthBuilder()
            .name(BEARER_TOKEN_NAME)
            .grantTypes(Collections.singletonList(grantType))
            .scopes(Arrays.asList(scopes()))
            .build();
    securitySchemes.add(oAuth);

    securitySchemes.add(new BasicAuth(BASIC_AUTH_NAME));

    return securitySchemes;
  }

  private AuthorizationScope[] scopes() {
    List<AuthorizationScope> scopeList = new ArrayList();
    scopeList.add(
        new AuthorizationScope(
            "calendar",
            "See, edit, share, and permanently delete all the calendars you can access using Calendar"));
    scopeList.add(new AuthorizationScope("calendar.readonly", "View your calendars"));
    scopeList.add(
        new AuthorizationScope("calendar.events", "View and edit events on all your calendars"));
    scopeList.add(
        new AuthorizationScope("calendar.events.readonly", "View events on all your calendars"));
    AuthorizationScope[] scopeArray = scopeList.stream().toArray(n -> new AuthorizationScope[n]);
    return scopeArray;
  }

  private List<SecurityContext> securityContext() {
    List<SecurityContext> securityContexts = new ArrayList();
    securityContexts.add(this.securityContextLogin());
    securityContexts.add(createSecurityContext(BEARER_TOKEN_NAME, "/book", RequestMethod.POST));
    return securityContexts;
  }

  private SecurityContext securityContextLogin() {
    return new SecurityContextBuilder()
        .securityReferences(
            Collections.singletonList(
                new SecurityReference.SecurityReferenceBuilder()
                    .reference(BASIC_AUTH_NAME)
                    .scopes(new AuthorizationScope[0])
                    .build()))
        .operationSelector(
            operationContext ->
                operationContext.requestMappingPattern().equals("/oauth/token")
                    && operationContext.httpMethod().equals(RequestMethod.POST.toString()))
        .build();
  }

  private SecurityContext createSecurityContext(
      String securityReferenceName, String uri, RequestMethod requestMethod) {
    return new SecurityContextBuilder()
        .securityReferences(
            Collections.singletonList(
                new SecurityReference.SecurityReferenceBuilder()
                    .reference(securityReferenceName)
                    .scopes(new AuthorizationScope[0])
                    .build()))
        .operationSelector(
            operationContext ->
                operationContext.requestMappingPattern().equals(uri)
                    && operationContext.httpMethod().equals(requestMethod.toString()))
        .build();
  }

  // private boolean selector(OperationContext operationContext) {
  // String url = operationContext.requestMappingPattern();
  // System.out.println(operationContext.httpMethod() + ", " + url);
  // // 這里可以寫URL過濾規則
  // return true;
  // }
}
