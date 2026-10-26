package com.example.opencodetutorial.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the {@link OpenAPI} bean consumed by springdoc to render the API
 * metadata (title, version, contact, license, external docs) on Swagger UI
 * and in the {@code /v3/api-docs} spec.
 *
 * <p>The bean lives in the infrastructure layer because the OpenAPI model is
 * a third-party concern — it never crosses the application or domain boundaries.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the global {@link OpenAPI} metadata for the opencode-tutorial API.
     * Contact, license and external-docs fields are left as placeholders so the
     * project owner can fill them in without changing code.
     *
     * @return the OpenAPI specification metadata
     */
    @Bean
    public OpenAPI opencodeTutorialOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("opencode-tutorial API")
                        .version("0.1.0")
                        .description("""
                                CRUD API for an educational center: Centers, Courses, Groups, Students and Persons.

                                The hierarchy is `Center -> Course -> Group`, and every resource is
                                reachable through its canonical nested URL:

                                - `/api/v1/centers` and `/api/v1/centers/{centerId}`
                                - `/api/v1/centers/{centerId}/courses[/{courseId}]`
                                - `/api/v1/centers/{centerId}/courses/{courseId}/groups[/{groupId}]`
                                - `/api/v1/centers/{centerId}/students[/{studentId}]`
                                - `/api/v1/persons[/{id}]` (persons are global, not scoped to a center)

                                Enrolling and detaching a student are sub-resources of the group:
                                `POST|DELETE .../groups/{groupId}/students/{studentId}`.

                                A student is associated with exactly one person, belongs to exactly one
                                center, and may be enrolled in zero, one or many groups — but at most one
                                per course. That last rule is enforced twice: in the domain (409 with a
                                readable message) and in the database, through a unique constraint on the
                                `student_group_link (student_id, course_id)` junction.

                                The aggregate `Group` is persisted to a table called `student_group`
                                because `GROUP` is a reserved SQL keyword.
                                """)
                        .contact(new Contact()
                                .name("opencode-tutorial maintainers")
                                .url("https://example.com/opencode-tutorial")
                                .email("maintainers@example.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://example.com/opencode-tutorial/license")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project documentation")
                        .url("https://example.com/opencode-tutorial/docs"));
    }
}
