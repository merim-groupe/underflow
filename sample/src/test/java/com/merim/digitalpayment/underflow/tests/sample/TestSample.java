package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.test.StartupArgs;
import com.merim.digitalpayment.underflow.test.UnderflowTest;
import com.merim.digitalpayment.underflow.tests.sample.form.LoginForm;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;

/**
 * TestSample.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
@UnderflowTest(value = TestAppImpl.class,
        args = @StartupArgs({"-foo", "-bar"}))
public class TestSample {

    /**
     * Test.
     */
    @Test
    public void test() {
        final ValidatableResponse validatableResponse = given()
                .when()
                .get("/test-text")
                .then();

        validatableResponse
                .extract()
                .response()
                .body()
                .prettyPrint();

        validatableResponse
                .body(Matchers.containsString("OK"));

        final LoginForm loginForm = Mockito.mock(LoginForm.class);
        Mockito.when(loginForm.getName())
                .thenReturn("mockito test !");

        Assertions.assertEquals("mockito test !", loginForm.getName());

        Assertions.assertEquals(Mode.TEST, Application.getMode());
    }
}
