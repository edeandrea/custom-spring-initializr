package {{packageName}}.configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import {{packageName}}.{{applicationName}};

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
  public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
			.directModelSubstitute(LocalDate.class, java.sql.Date.class)
			.directModelSubstitute(LocalDateTime.class, java.util.Date.class)
			.useDefaultResponseMessages(false)
			.apiInfo(apiInfo())
			.select()
				.apis(RequestHandlerSelectors.basePackage({{applicationName}}.class.getPackage().getName()))
				.paths(PathSelectors.any())
				.build();

		// This adds a 401 (Unauthorized) to all of the operations in the application
    // Uncomment this if you have authorization turned on for your application
//		Arrays.stream(RequestMethod.values()).forEach(requestMethod -> docket.globalResponseMessage(requestMethod, Arrays.asList(
//			new ResponseMessageBuilder()
//				.code(HttpStatus.UNAUTHORIZED.value())
//				.message("User isn't authorized to use application")
//				.build())));

     return docket;
  }

  private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("{{name}}")
			.description("{{description}}")
			.contact(new Contact("", "", ""))
			.version("{{version}}")
			.build();
  }
}