import java.io.File;

public class TestFileLFI {
  public static void main(String[] args) throws Exception {
    String cacheDirPath = "/data/user/0/com.app/cache";
    String url1 = "file:///data/user/0/com.app/cache/webarchive-123.mht";
    String url2 = "file:///data/user/0/com.app/shared_prefs/prefs.xml";
    String url3 = "file:///data/user/0/com.app/cache/../shared_prefs/prefs.xml";

    System.out.println("URL1 valid: " + isValidFileAccess(url1, cacheDirPath));
    System.out.println("URL2 valid: " + isValidFileAccess(url2, cacheDirPath));
    System.out.println("URL3 valid: " + isValidFileAccess(url3, cacheDirPath));
  }

  private static boolean isValidFileAccess(String url, String cacheDirPath) throws Exception {
    if (!url.toLowerCase().startsWith("file://")) return false;

    String path = url.replace("file://", "");
    File file = new File(path);
    String canonicalPath = file.getCanonicalPath();
    String canonicalCacheDirPath = new File(cacheDirPath).getCanonicalPath() + File.separator;

    return canonicalPath.startsWith(canonicalCacheDirPath);
  }
}
