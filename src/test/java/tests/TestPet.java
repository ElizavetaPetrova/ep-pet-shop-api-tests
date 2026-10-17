package tests;

import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {

    private static final String BASE_URL = "https://petstore.swagger.rv-school.ru/api/v3";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("elizaveta petrova")
    @Description("Проверка удаления несуществующего питомца")
    @Tag("pet")
    public void testDeleteNonExistentPet() {
        // Шаг 1: отправляем запрос и сохраняем ответ
        Response response = step("Отправить DELETE-запрос на удаление несуществующего питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .delete(BASE_URL + "/pet/999"));

        // Тело ответа получаем один раз и дальше используем переменную
        String responseBody = response.getBody().asString();

        // Шаг 2: проверяем код ответа
        step("Проверить, что статус-код ответа == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        // Шаг 3: проверяем текст ответа
        step("Проверить, что текст ответа 'Pet deleted'", () ->
                assertEquals("Pet deleted", responseBody,
                        "Текст ответа не совпал с ожидаемым. Получен: " + responseBody));
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("elizaveta petrova")
    public void testUpdateNonexistentPet() {
        // 1. Готовим данные: питомец с ID, которого точно нет
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existent Pet");
        pet.setStatus("available");

        Pet.Category category = new Pet.Category();
        category.setId(1);
        category.setName("Dogs");
        pet.setCategory(category);

        Pet.Tag tag = new Pet.Tag();
        tag.setId(1);
        tag.setName("friendly");
        pet.setTags(List.of(tag));                 // import java.util.List;
        pet.setPhotoUrls(List.of("http://example.com/photo.jpg"));

        // 2. Отправляем PUT с объектом в теле - REST Assured сам сделает из него JSON
        Response response = step("Отправить PUT запрос на обновление несуществующего питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        // 3. Проверяем код и текст
        step("Проверить, что статус-код ответа == 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверить, что текст ответа 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Текст ответа не совпал с ожидаемым. Получен: " + responseBody));
    }

    @Test
    public void testGetNonexistentPet() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .when()
                .get(BASE_URL + "/pet/9999");

        // Тело ответа как текст - получаем один раз и переиспользуем
        String responseBody = response.getBody().asString();

        assertEquals(404, response.getStatusCode(),
                "Код ответа не совпал с ожидаемым. Ответ: " + responseBody);

        assertEquals("Pet not found", responseBody,
                "Текст ответа не совпал с ожидаемым. Получен: " + responseBody);

    }
}