import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {

    private static final String BASE_URL = "https://petstore.swagger.rv-school.ru/api/v3";

    @Test
    public void testDeleteNonexistentPet() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .when()
                .delete(BASE_URL + "/pet/9999");

        // Тело ответа как текст - получаем один раз и переиспользуем
        String responseBody = response.getBody().asString();

        assertEquals(200, response.getStatusCode(),
                "Код ответа не совпал с ожидаемым. Ответ: " + responseBody);

        assertEquals("Pet deleted", responseBody,
                "Текст ответа не совпал с ожидаемым. Получен: " + responseBody);
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