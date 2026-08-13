package io.github.sheepdestroyer.materialisheep.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class HackerNewsClientTest {

    @Mock
    RestServiceFactory restServiceFactory;

    @Mock
    SessionManager sessionManager;

    @Mock
    FavoriteManager favoriteManager;

    @Mock
    HackerNewsClient.RestService restService;

    @Mock
    Call<HackerNewsItem> mockCall;

    private HackerNewsClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restServiceFactory.rxEnabled(true)).thenReturn(restServiceFactory);
        when(restServiceFactory.create(HackerNewsClient.BASE_API_URL, HackerNewsClient.RestService.class)).thenReturn(restService);
        client = new HackerNewsClient(restServiceFactory, sessionManager, favoriteManager);
    }

    @Test
    public void testGetItem_IOExceptionReturnsNull() throws IOException {
        when(restService.item(anyString())).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Test exception"));

        Item item = client.getItem("1", ItemManager.MODE_DEFAULT);

        assertNull("getItem should return null when execute throws IOException", item);
    }
}
