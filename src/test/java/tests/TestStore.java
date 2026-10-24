package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Store")                     // одна аннотация на весь класс
public class TestStore {

    private static final String BASE_URL = "https://petstore.swagger.rv-school.ru/api/v3";

    @ParameterizedTest(name = "Оформление заказа со статусом: {0}")
    @ValueSource(strings = {"approved", "placed", "delivered"})
    @Severity(SeverityLevel.CRITICAL)
    @Owner("elizaveta petrova")
    public void testPlaceOrder(String status) {
        Order order = new Order();
        order.setId(1);
        order.setPetId(10);
        order.setQuantity(1);
        order.setShipDate("2026-09-10T10:00:00.000Z");
        order.setStatus(status);
        order.setComplete(false);

        Response response = step("Отправить POST запрос на оформление заказа", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(order)
                        .when()
                        .post(BASE_URL + "/store/order"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус-код ответа == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверить параметры созданного заказа", () -> {
            Order createdOrder = response.as(Order.class);
            assertEquals(order.getId(), createdOrder.getId(), "id заказа не совпадает");
            assertEquals(order.getPetId(), createdOrder.getPetId(), "petId не совпадает");
            assertEquals(order.getQuantity(), createdOrder.getQuantity(), "quantity не совпадает");
            assertEquals(order.getStatus(), createdOrder.getStatus(), "status не совпадает");
            assertEquals(order.isComplete(), createdOrder.isComplete(), "complete не совпадает");
        });
    }
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Owner("elizaveta petrova")
    public void testGetOrderById() {
        // Предусловие: создаём заказ, который будем запрашивать
        Order order = new Order();
        order.setId(2);
        order.setPetId(10);
        order.setQuantity(2);
        order.setStatus("approved");
        order.setComplete(true);

        step("Предусловие: создать заказ", () ->
                given()
                        .contentType(ContentType.JSON)
                        .body(order)
                        .when()
                        .post(BASE_URL + "/store/order"));

        Response response = step("Отправить GET запрос на получение заказа по id", () ->
                given()
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "/store/order/" + order.getId()));

        step("Проверить код ответа и параметры заказа", () -> {
            assertEquals(200, response.getStatusCode(), "Код ответа не совпал. Ответ: " + response.getBody().asString());
            Order receivedOrder = response.as(Order.class);
            assertEquals(order.getId(), receivedOrder.getId(), "id заказа не совпадает");
            assertEquals(order.getStatus(), receivedOrder.getStatus(), "status заказа не совпадает");
        });
    }
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Owner("elizaveta petrova")
    public void testDeleteOrder() {
        Order order = new Order();
        order.setId(3);
        order.setPetId(10);
        order.setQuantity(1);
        order.setStatus("placed");

        step("Предусловие: создать заказ", () ->
                given().contentType(ContentType.JSON).body(order)
                        .when().post(BASE_URL + "/store/order"));

        Response deleteResponse = step("Отправить DELETE запрос на удаление заказа", () ->
                given().header("Accept", "application/json")
                        .when().delete(BASE_URL + "/store/order/" + order.getId()));

        step("Проверить, что статус-код ответа == 200", () ->
                assertEquals(200, deleteResponse.getStatusCode(),
                        "Код ответа не совпал. Ответ: " + deleteResponse.getBody().asString()));

        step("Проверить, что заказ больше не находится", () -> {
            Response getResponse = given().header("Accept", "application/json")
                    .when().get(BASE_URL + "/store/order/" + order.getId());
            assertEquals(404, getResponse.getStatusCode(),
                    "Заказ всё ещё существует. Ответ: " + getResponse.getBody().asString());
        });
    }
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("elizaveta petrova")
    public void testGetNonexistentOrder() {
        Response response = step("Отправить GET запрос на получение несуществующего заказа", () ->
                given()
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "/store/order/9999"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус-код ответа == 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверить, что текст ответа 'Order not found'", () ->
                assertEquals("Order not found", responseBody,
                        "Текст ответа не совпал с ожидаемым. Получен: " + responseBody));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("elizaveta petrova")
    public void testDeleteNonexistentOrder() {
        Response response = step("Отправить DELETE запрос на удаление несуществующего заказа", () ->
                given()
                        .header("Accept", "application/json")
                        .when()
                        .delete(BASE_URL + "/store/order/9999"));

        String responseBody = response.getBody().asString();

        step("Проверить код и текст ответа", () -> {
            assertEquals(404, response.getStatusCode(),
                    "Код ответа не совпал с ожидаемым. Ответ: " + responseBody);
            assertEquals("Order not found", responseBody,
                    "Текст ответа не совпал с ожидаемым. Получен: " + responseBody);
        });
    }
    @Test
    @Severity(SeverityLevel.MINOR)
    @Owner("elizaveta petrova")
    public void testGetInventory() {
        Response response = step("Отправить GET запрос на получение инвентаря", () ->
                given()
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "/store/inventory"));

        step("Проверить код ответа и наличие статусов", () -> {
            assertEquals(200, response.getStatusCode());
            // Ответ вида {"available": 5, "pending": 2, "sold": 1}
            Integer approved = response.jsonPath().getInt("approved");
            assertTrue(approved >= 0, "Количество approved не может быть отрицательным");
        });
    }
}