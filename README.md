# Demo for Spring Boot WebFlux with Kotlin Coroutines

The main goal of this project is to implement a Reactive API server with high throughput and low latency utilizing Spring Boot WebFlux with Kotlin Coroutines. API server will
aggregate a lot of data from many downstream services which should be an efficient async operations and run in parallel
utilizing multithreading.

Technical scope:

- [ ] Spring Boot WebFlux API Server: Utilizing Kotlin and Coroutines for handling all asynchronous operations.
- [ ] App Containerization: Efficient packaging and deployment of the application.
- [ ] Logging: Comprehensive logging mechanisms for tracking and debugging.
- [ ] Distributed Tracing: Implementing tracing to monitor and troubleshoot distributed systems.
- [ ] Metrics and Health Checks: Tools to ensure system health and performance monitoring.
- [ ] Autogenerated OpenAPI Documentation: Interactive API documentation with Swagger UI.
- [ ] Error Handling: Standardized error responses with ApiError objects for all backend exceptions.
- [ ] Testing: Robust testing practices to ensure reliability and performance.

Business scope: 

The business requirement is defined as "Users must be able to see an overview of all assets from the investment portfolio valued in reference
currency (USD, EUR, GBP, CHF) selected by user during onboarding".

## Tech stack:

- [ ] [Kotlin 2.0.0](https://kotlinlang.org/docs/getting-started.html#install-kotlin)
  and [coroutines](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/)
- [ ] [Spring Boot WebFlux 3.2.5](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [ ] [Kotlin Coroutines Reactor](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-reactor/) (see
  also [Spring coroutines guide](https://docs.spring.io/spring-framework/reference/languages/kotlin/coroutines.html) on
  how Reactive translates to Coroutines).

## Structure

The POC app aims to follow the **Domain Driven Design** (DDD) and **Hexagonal Architecture** pattern. According to Hexagonal Architecture there is only 2 layers: business model related and infrastructure (ports and adapters) therefore only 2 corresponding packages `business` and `infra` present on the root level of the source code. The business layer contains rich `model` (domain) and `usecases` (application services). Domain model objects implements the core business logic and is opposite to anemic objects. Application Services implement the use cases of the application (user interactions in a typical web application), while Domain objects/services implement use case independent logic from the heart of the business model.

Domain services are typically used by the Application Services or other Domain Services, while Application Services are used by the Presentation Layer or Client Applications.

Application Services do cross-cutting concerns (validations, managing transaction scope, auth, client view aggregations from domain objects), get/return Data Transfer Objects (Responses). Domain object methods typically get and return the domain objects (entities, value objects). Application service is that layer which initializes and oversees interaction between the domain objects and services. The flow for app service is generally like this: get domain object (or objects) from repository, trigger an action and put the domain object back to repository if its state is changed. App service can do more - for instance it can check whether a domain object exists or not and throw exceptions accordingly. So it lets the user interact with the application (and this is probably where its name originates from) - by manipulating domain objects and services. Application services should generally represent all possible use cases.

The `infra` package contains an adapters from external world (clients) to internal services and from internal to external (providers). They can be easily implemented or replaced without affecting the core business model logic.


## Runtime requirements

- JDK 21+

## Compile and package application (tests run included)

```bash
./mvnw clean package
```

## Only test run

```bash
./mvnw clean test
```

## Build docker image

```bash
docker build -t backend-server .
```

## Run docker image

```bash
docker run -d -p 8080:8080 --name poc-springwebflux-server backend-server
```

## Logging

For the moment all server logs are written to the console and file using `ch.qos.logback.core.ConsoleAppender`
and `ch.qos.logback.core.rolling.RollingFileAppender` respectively configured in `logback.xml` file. All logs contain `traceId` value
which is implicitly populated from `org.slf4j.MDC` context and shown in the logs according to `CONSOLE_LOG_PATTERN` defined in
logback.xml.

Log record example with traceId printed right after log level `INFO`:

`2024-05-24 12:55:16.986  INFO [2695f8537e1fe05a841f0df18898e730] 1612 - [          parallel-1]  i.s.a.b.i.s.c.PortfolioController .getPortfolioOverviewStub(40) : Getting portfolioOverview stub as a temporal solution for frontend integration`

## Tracing

Distributed `traceId` is attached to every incoming request and automatically propagated to downstream threads and
requests.
Downstream treads/coroutines can access it through implicitly propagated context implemented by `micrometer-tracing`
library.
For an example of explicitly implemented propagation see `pro.misoft.poc.springreactive.kotlin.infra.spring.filter.LocaleFilter`. It
reads an `Accept-Language` header from incoming request and propagate `Locale` object to downstream context and can be
accessed in any point of time through `pro.misoft.poc.springreactive.kotlin.infra.spring.filter.LocaleThreadLocalAccessor`, e.g. used
by `pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling.RestExceptionHandler` to localize error messages shown to the end
user.
All API responses contain `X-Trace-Id` header to be able to match every http request with corresponding logs on the server side.
Example of http response header `X-Trace-Id: 7e0674227780f3226ae9a8b7d350a5ee`.

## Metrics

All maintenance endpoints are accessed by following url `http://localhost:8080/api/internal/actuator`. The list of all
app measured metrics are here `http://localhost:8080/api/internal/actuator/metrics`. For example, the number of API
calls since server start is exposed in Prometheus format
by a link `http://localhost:8080/api/internal/actuator/metrics/custom.api.calls.total` and implemented
using `io.micrometer.core.instrument.Counter` from Micrometer library.

## Health checks

Health checks are provided through Spring Boot Actuator by a link http://localhost:8080/api/internal/actuator/health

## Error handling

All thrown exceptions are handled globally by using Spring
@ControllerAdvice at class `pro.misoft.poc.springreactive.kotlin.infra.spring.errorhandling.RestExceptionHandler`. This exception handler convert exception to http response with proper http code and error body. Error body has the localized error message to be shown to the end user and unique internal code to be used by customer support team. Error body json:
```
{
   “httpStatus”: 400,
   “internalCode":"order-4002",
   “errorMessage":"Input fields contain errors",
   "traceId":"7f006775-04b5-4f81-8250-a85ffb976722",
   "subErrors":[
      {
         "objectName":"orderDto",
         "fieldName":"userName”,
         "rejectedValue”:”N”,
         "message":"size must be between 2 and 36"
      }
   ]
}
```
Error body data class:
```
data class ApiError(
 val httpStatus: Int,
    /**
     * Internal code to classify error
     *
     * pattern="${serviceNamePrefix}-${httpErrorCategory}${sequenceNumberUniqueForServiceNameAndHttpErrorCode}".
     *
     * examples=["portfolio-4001", "portfolio-4002","order-5001", "user-4001", "user-4002", "user-5001"]
     */
    val internalCode: String,
    /**
     * Human-readable localized message to display on client side
     */
    val errorMessage: String,
    /**
     * Unique identifier of user request.
     * In case of distributed architecture this identifier is passed to all downstream requests to other services.
     */
    val traceId: String,
    /**
     * Collect information about sub errors,
     * for example specific fields of forms providing human-readable error messages for each field to guide user trough out a flow
     */
    val subErrors: List<ApiSubError> = listOf()
   )
```

## Openapi documentation

`Springdoc-openapi` library is integrated to automatically generate OpenAPI documentation. Endpoint to see OpenAPI spec http://localhost:8080/api/internal/openapi. Swagger-UI is already embedded to web server and can be accessed by url http://localhost:8080/api/internal/swagger-ui. The openapi contract schema can be customized by applying swagger annotations like `io.swagger.v3.oas.annotations.media.Schema`, see example `pro.misoft.poc.springreactive.kotlin.infra.spring.controller.contract.MonetaryAmountSchema`.

## API usage

```bash
curl http://localhost:8080/api/v1/portfolio?currency=USD
```