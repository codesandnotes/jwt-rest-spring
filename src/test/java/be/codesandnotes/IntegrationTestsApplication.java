package be.codesandnotes;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Prepares the Integration tests' context.<br/>
 * <br/>
 * We do exclude the scan of Application.class, so that we can specify an alternative Spring Boot Application context
 * for these tests, while preventing Spring Boot from loading the original Application context.
 */
@ComponentScan(
        basePackages = "be.codesandnotes",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Application.class)}
)
@Configuration
@EnableAutoConfiguration
@PropertySource({
        "classpath:security.test.properties"
})
public class IntegrationTestsApplication {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder().detectRequestFactory(false).requestFactory(SimpleClientHttpRequestFactory.class);
    }
}
