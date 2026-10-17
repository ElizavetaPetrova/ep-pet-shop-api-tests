import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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