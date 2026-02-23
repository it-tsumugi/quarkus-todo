package com.example.todo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// @QuarkusTest でアプリ全体（DB 含む）を起動してテストする
// Dev Services が PostgreSQL コンテナを自動的に用意するため、別途 DB の準備は不要
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoResourceTest {

    // テスト間で共有する id
    static Long createdId;

    @Test
    @Order(1)
    void testCreate() {
        createdId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "牛乳を買う"}
                        """)
                .when().post("/todos")
                .then()
                .statusCode(201)
                .body("title", is("牛乳を買う"))
                .body("completed", is(false))
                .body("id", notNullValue())
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    void testList() {
        given()
                .when().get("/todos")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(3)
    void testGet() {
        given()
                .when().get("/todos/" + createdId)
                .then()
                .statusCode(200)
                .body("title", is("牛乳を買う"));
    }

    @Test
    @Order(4)
    void testUpdate() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "牛乳を買う", "completed": true}
                        """)
                .when().put("/todos/" + createdId)
                .then()
                .statusCode(200)
                .body("completed", is(true));
    }

    @Test
    @Order(5)
    void testDelete() {
        given()
                .when().delete("/todos/" + createdId)
                .then()
                .statusCode(204);

        // 削除後は 404 になることを確認
        given()
                .when().get("/todos/" + createdId)
                .then()
                .statusCode(404);
    }
}
