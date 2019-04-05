package {{packageName}};

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Map;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.github.robwin.swagger.loader.Swagger20Parser;
import io.github.robwin.swagger.test.SwaggerAssertions;
import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT, printOnlyOnFailure = false)
@TestPropertySource(properties = { "debug=true" })
public class SwaggerDocumentationGenerator {
	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	public static void beforeAll() {
		// Hack because JsonSlurper uses a TreeMap when parsing the json, so the structure gets re-ordered alphabetically
		// http://www.it1me.com/it-answers?id=33018236&ttl=How+to+maintain+JSON%26%2339%3Bs+order+in+Groovy%26%2339%3Bs+JsonSlurper%3F
		System.setProperty("jdk.map.althashing.threshold", "512");
	}

	@AfterAll
	public static void afterAll() {
		System.clearProperty("jdk.map.althashing.threshold");
	}

	@Test
	@Disabled("We haven't yet defined our API. Remove this annotation once the api.yml is complete")
	public void validateImplementationFitsDesignSpec() throws UnsupportedEncodingException, IOException, Exception {
		SwaggerAssertions.assertThat(Swagger20Parser.parse(getJson())).satisfiesContract(new ClassPathResource("api.yml").getPath());
	}

	@Test
	public void generateApiJson() throws Exception {
		String apiTargetDir = getSystemProperty("apiTargetDir", "target/api-docs");
		String apiJsonFile = getSystemProperty("apiJsonFile", String.format("%s/api.json", apiTargetDir));

		new File(apiTargetDir).mkdirs();

		try (FileWriter writer = new FileWriter(new File(apiJsonFile))) {
			writer.write(JsonOutput.prettyPrint(getJson()));
		}
	}

	@Test
	public void generateSwaggerAsciidoc() throws Exception {
	  String outputLocation = getSystemProperty("asciidocRawDir", "target/asciidoc/generated/raw");
		File outputDir = new File(outputLocation);
		outputDir.mkdirs();

		try (FileWriter writer = new FileWriter(new File(outputDir, "index.adoc"))) {
			writer.write(String.format("include::{generated}/overview.adoc[]%sinclude::{generated}/paths.adoc[]%sinclude::{generated}/definitions.adoc[]", System.lineSeparator(), System.lineSeparator(), System.lineSeparator()));
		}

		Swagger2MarkupConfig markupConfig = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(MarkupLanguage.ASCIIDOC)
				.withPathsGroupedBy(GroupBy.AS_IS)
				.withGeneratedExamples()
				.build();

			Swagger2MarkupConverter.from(getJson())
				.withConfig(markupConfig)
				.build()
				.toFolder(Paths.get(outputLocation));
	}

	private String getJson() throws UnsupportedEncodingException, Exception {
		return clenseJson(this.mockMvc.perform(MockMvcRequestBuilders.get("/api").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString());
	}

	private static String clenseJson(String jsonString) {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonObj = (Map<String, Object>) new JsonSlurper().parseText(jsonString);
		jsonObj.remove("host");
		jsonObj.remove("basePath");

		return JsonOutput.toJson(jsonObj);
	}

	private static String getSystemProperty(String propertyName, String defaultIfNotFound) {
		String value = System.getProperty(propertyName);

		if (StringUtils.isBlank(value)) {
			value = defaultIfNotFound;
		}

		return value;
	}
}