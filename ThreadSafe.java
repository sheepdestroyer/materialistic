import android.os.Handler;
import android.os.Looper;

public class ThreadSafe {
  public static void runOnMain(Runnable runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      runnable.run();
    } else {
      new Handler(Looper.getMainLooper()).post(runnable);
    }
  }
}
