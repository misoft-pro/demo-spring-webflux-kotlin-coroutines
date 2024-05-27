package pro.misoft.poc.springreactive.kotlin.infra.spring.controller

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext


abstract class AbstractSystemTest {

    @Autowired
    lateinit var ctx: ReactiveWebServerApplicationContext

    @BeforeEach
    fun setUpAll() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.requestSpecification = RequestSpecBuilder()
            .setBasePath("/api")
            .setPort(ctx.webServer.port)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build()
        RestAssured.responseSpecification = ResponseSpecBuilder().log(LogDetail.ALL).build()
    }
}
