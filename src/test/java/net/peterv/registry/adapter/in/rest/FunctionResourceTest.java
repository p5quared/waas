package net.peterv.registry.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.peterv.registry.adapter.out.stub.InMemoryFunctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class FunctionResourceTest {

    private static final String BASE_PATH = "/api/v1/functions";
    private static final byte[] SAMPLE_WASM = {0x00, 0x61, 0x73, 0x6D};
    private static final String SAMPLE_WASM_BASE64 = Base64.getEncoder().encodeToString(SAMPLE_WASM);

    @Inject
    InMemoryFunctionRepository repository;

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void createReturns201WithLocationHeader() {
        given()
            .contentType("application/json")
            .body(Map.of(
                    "name", "hello",
                    "description", "A test function",
                    "wasmBytes", SAMPLE_WASM_BASE64
            ))
        .when()
            .post(BASE_PATH)
        .then()
            .statusCode(201)
            .header("Location", notNullValue())
            .body("id", notNullValue())
            .body("name", is("hello"))
            .body("contentHash", notNullValue())
            .body("timeoutSeconds", is(30))
            .body("memoryLimitBytes", is(67108864));
    }

    @Test
    void getReturns200() {
        String id = createFunction("getter");

        given()
        .when()
            .get(BASE_PATH + "/" + id)
        .then()
            .statusCode(200)
            .body("id", is(id))
            .body("name", is("getter"));
    }

    @Test
    void getNotFoundReturns404() {
        given()
        .when()
            .get(BASE_PATH + "/00000000-0000-0000-0000-000000000000")
        .then()
            .statusCode(404)
            .body("error", notNullValue());
    }

    @Test
    void listReturnsFunctions() {
        createFunction("listA");
        createFunction("listB");

        given()
        .when()
            .get(BASE_PATH)
        .then()
            .statusCode(200)
            .body("$", hasSize(2));
    }

    @Test
    void updateReturns200() {
        String id = createFunction("updatable");

        given()
            .contentType("application/json")
            .body(Map.of(
                    "description", "updated description"
            ))
        .when()
            .put(BASE_PATH + "/" + id)
        .then()
            .statusCode(200)
            .body("id", is(id))
            .body("name", is("updatable"));
    }

    @Test
    void updateNotFoundReturns404() {
        given()
            .contentType("application/json")
            .body(Map.of(
                    "description", "nope"
            ))
        .when()
            .put(BASE_PATH + "/00000000-0000-0000-0000-000000000000")
        .then()
            .statusCode(404)
            .body("error", notNullValue());
    }

    @Test
    void deleteReturns204() {
        String id = createFunction("deletable");

        given()
        .when()
            .delete(BASE_PATH + "/" + id)
        .then()
            .statusCode(204);
    }

    @Test
    void deleteNotFoundReturns404() {
        given()
        .when()
            .delete(BASE_PATH + "/00000000-0000-0000-0000-000000000000")
        .then()
            .statusCode(404);
    }

    @Test
    void createWithInvalidNameReturns400() {
        given()
            .contentType("application/json")
            .body(Map.of(
                    "name", "123invalid",
                    "wasmBytes", SAMPLE_WASM_BASE64
            ))
        .when()
            .post(BASE_PATH)
        .then()
            .statusCode(400)
            .body("error", notNullValue());
    }

    @Test
    void createWithMissingNameReturns400() {
        given()
            .contentType("application/json")
            .body(Map.of(
                    "wasmBytes", SAMPLE_WASM_BASE64
            ))
        .when()
            .post(BASE_PATH)
        .then()
            .statusCode(400)
            .body("error", notNullValue());
    }

    private String createFunction(String name) {
        return given()
            .contentType("application/json")
            .body(Map.of(
                    "name", name,
                    "description", "test",
                    "wasmBytes", SAMPLE_WASM_BASE64
            ))
        .when()
            .post(BASE_PATH)
        .then()
            .statusCode(201)
            .extract()
            .path("id");
    }
}
