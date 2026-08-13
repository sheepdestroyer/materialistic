package io.github.sheepdestroyer.materialisheep.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AlgoliaClientTest {

    @Mock
    RestServiceFactory restServiceFactory;

    @Mock
    AlgoliaClient.RestService restService;

    @Mock
    ItemManager hackerNewsClient;

    @Mock
    Call<AlgoliaClient.AlgoliaHits> mockCall;

    private AlgoliaClient algoliaClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restServiceFactory.rxEnabled(true)).thenReturn(restServiceFactory);
        when(restServiceFactory.create(anyString(), eq(AlgoliaClient.RestService.class))).thenReturn(restService);
        algoliaClient = new AlgoliaClient(restServiceFactory, hackerNewsClient, Schedulers.trampoline());
    }

    @Test
    public void testGetStories_IOException() throws IOException {
        when(restService.search(anyString(), isNull())).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));

        AlgoliaClient.sSortByTime = false;
        Item[] result = algoliaClient.getStories("filter", ItemManager.MODE_DEFAULT);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    public void testGetStoriesByDate_IOException() throws IOException {
        when(restService.searchByDate(anyString(), isNull())).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));

        AlgoliaClient.sSortByTime = true;
        Item[] result = algoliaClient.getStories("filter", ItemManager.MODE_DEFAULT);

        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
