import java.net.URI;
import java.io.File;

public class TestUriLFI {
    public static void main(String[] args) throws Exception {
        String cacheDirPath = "/data/user/0/com.app/cache";
        String url1 = "file:///data/user/0/com.app/cache/webarchive-123.mht";
        String url2 = "file:///data/user/0/com.app/shared_prefs/prefs.xml";
        String url3 = "file:///data/user/0/com.app/cache/../shared_prefs/prefs.xml";
        String url4 = "file:///android_asset/about.html";
        String url5 = "file:///android_asset/../data/user/0/com.app/shared_prefs/prefs.xml";

        System.out.println("URL1 valid: " + isValid(url1, cacheDirPath));
        System.out.println("URL2 valid: " + isValid(url2, cacheDirPath));
        System.out.println("URL3 valid: " + isValid(url3, cacheDirPath));
        System.out.println("URL4 valid: " + isValid(url4, cacheDirPath));
        System.out.println("URL5 valid: " + isValid(url5, cacheDirPath));
    }

    private static boolean isValid(String url, String cacheDirPath) throws Exception {
        if (!url.toLowerCase().startsWith("file://")) return false;

        // Android URI.parse behaves similarly to java.net.URI
        String path = new URI(url).getPath();

        if (path == null) {
            return false;
        }

        // Allow android_asset
        if (path.startsWith("/android_asset/")) {
            return !path.contains("..");
        }

        File file = new File(path);
        String canonicalPath = file.getCanonicalPath();
        String canonicalCacheDirPath = new File(cacheDirPath).getCanonicalPath() + File.separator;

        return canonicalPath.startsWith(canonicalCacheDirPath);
    }
}
