package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {

    final int defermentDays = 3;

    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @Test
    void shouldSubmitDeliveryCardRequestHappy() {
        open("http://localhost:9999");
        val user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id='city'] input").setValue(user.getCity());
        val dateEvent = DataGenerator.generateDate(defermentDays);
        val dateInput = $("[data-test-id='date'] input");
        dateInput.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        dateInput.setValue(dateEvent);
        $("[data-test-id='name'] input").setValue(user.getName());
        $("[data-test-id='phone'] input").setValue(user.getPhone());
        $("[data-test-id='agreement']").click();
        val sendButton = $$("button").find(Condition.text("Запланировать"));
        sendButton.click();
        $("[data-test-id='success-notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(Condition.matchText(dateEvent), Duration.ofSeconds(15));
        val rand = new Random();
        val newDateEvent = DataGenerator.generateDate(defermentDays * (rand.nextInt(10) + 1));
        dateInput.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        dateInput.setValue(newDateEvent);
        sendButton.click();
        $("[data-test-id='replan-notification']").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='replan-notification'] button").click();
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(Condition.matchText(newDateEvent), Duration.ofSeconds(15));
    }
}
