package io.github.sheepdestroyer.materialisheep.accounts;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import io.reactivex.rxjava3.schedulers.Schedulers;

import org.junit.Before;
import org.junit.Test;

public class UserServicesClientTest {
    private UserServicesClient mUserServicesClient;

    @Before
    public void setUp() {
        Call.Factory callFactory = mock(Call.Factory.class);
        mUserServicesClient = new UserServicesClient(callFactory, Schedulers.trampoline());
    }

    @Test
    public void testParseLoginError_ioExceptionReturnsNull() throws Exception {
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);

        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenThrow(new IOException("Mock IO Exception"));

        // Access the private method using reflection since we want to test it specifically
        java.lang.reflect.Method method = UserServicesClient.class.getDeclaredMethod("parseLoginError", Response.class);
        method.setAccessible(true);

        String result = (String) method.invoke(mUserServicesClient, response);
        assertNull(result);
    }
}
