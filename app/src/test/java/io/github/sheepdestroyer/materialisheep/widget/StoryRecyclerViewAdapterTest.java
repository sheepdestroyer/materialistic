package io.github.sheepdestroyer.materialisheep.widget;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.ContextWrapper;
import io.github.sheepdestroyer.materialisheep.ApplicationComponent;
import io.github.sheepdestroyer.materialisheep.MaterialisticApplication;
import io.github.sheepdestroyer.materialisheep.MultiPaneListener;
import io.github.sheepdestroyer.materialisheep.data.Item;
import io.github.sheepdestroyer.materialisheep.data.ItemManager;
import io.github.sheepdestroyer.materialisheep.data.SessionManager;
import io.github.sheepdestroyer.materialisheep.data.WebItem;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class StoryRecyclerViewAdapterTest {

  private StoryRecyclerViewAdapter adapter;
  private SessionManager sessionManager;
  private ItemManager itemManager;
  private Context context;

  // Helper abstract class to combine ContextWrapper and MultiPaneListener
  public abstract static class TestContext extends ContextWrapper implements MultiPaneListener {
    public TestContext(Context base) {
      super(base);
    }
  }

  @Before
  public void setUp() {
    final MaterialisticApplication mockApp = mock(MaterialisticApplication.class);
    mockApp.applicationComponent = mock(ApplicationComponent.class);
    when(mockApp.getApplicationContext()).thenReturn(mockApp);

    // Wrap the real Robolectric context to return our mock Application and implement
    // MultiPaneListener
    context =
        new TestContext(RuntimeEnvironment.application) {
          @Override
          public Context getApplicationContext() {
            return mockApp;
          }

          @Override
          public void onItemSelected(WebItem item) {}

          @Override
          public WebItem getSelectedItem() {
            return null;
          }

          @Override
          public boolean isMultiPane() {
            return false;
          }
        };

    adapter = new StoryRecyclerViewAdapter(context);
    sessionManager = mock(SessionManager.class);
    itemManager = mock(ItemManager.class);

    adapter.mSessionManager = sessionManager;
    adapter.mItemManager = itemManager;
  }

  @Test
  public void testMarkAsViewed_callsSessionManagerView() {
    Item item = mock(Item.class);
    when(item.getId()).thenReturn("123");
    when(item.getLongId()).thenReturn(123L);
    when(item.getLocalRevision()).thenReturn(1);
    when(item.isViewed()).thenReturn(false);

    adapter.mItems.add(item);

    // Call markAsViewed for the item at position 0
    adapter.markAsViewed(0);

    verify(sessionManager, times(1)).view("123");
  }

  @Test
  public void testMarkAsViewed_optimized() {
    Item item = mock(Item.class);
    when(item.getId()).thenReturn("123");
    when(item.getLongId()).thenReturn(123L);
    when(item.getLocalRevision()).thenReturn(1);

    AtomicBoolean isViewed = new AtomicBoolean(false);
    when(item.isViewed()).thenAnswer(invocation -> isViewed.get());
    doAnswer(
            invocation -> {
              isViewed.set(invocation.getArgument(0));
              return null;
            })
        .when(item)
        .setIsViewed(anyBoolean());

    adapter.mItems.add(item);

    // Call markAsViewed the first time
    adapter.markAsViewed(0);

    // Should call view()
    verify(sessionManager, times(1)).view("123");
    verify(item, times(1)).setIsViewed(true);

    // Call markAsViewed again
    adapter.markAsViewed(0);

    // Verify it is NOT called again
    verify(sessionManager, times(1)).view("123");
    // Verify setIsViewed was NOT called again (since it returned early)
    verify(item, times(1)).setIsViewed(true);
  }
}
