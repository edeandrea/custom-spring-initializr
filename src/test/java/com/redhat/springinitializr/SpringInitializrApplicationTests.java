package com.redhat.springinitializr;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	properties = {
		"amazon.aws.access-key=blah",
		"amazon.aws.secret-key=blah"
	}
)
@ActiveProfiles("local")
public class SpringInitializrApplicationTests {
	@Test
	public void contextLoads() {

	}
}
