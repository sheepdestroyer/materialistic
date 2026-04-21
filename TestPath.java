public class TestPath {
  public static void main(String[] args) throws Exception {
    // Mocking URI for standard Java
    String url1 = "file:///android_asset/test.html";
    String url2 = "file:///data/user/0/com.app/cache/webarchive-123.mht";

    System.out.println("URL1 path: " + new java.net.URI(url1).getPath());
    System.out.println("URL2 path: " + new java.net.URI(url2).getPath());
  }
}
