package KalendarUspomena.Util;

import java.nio.charset.Charset;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

public class CustomContainsMatcher implements ResultMatcher {

  private final String expectedSubstring;

  public CustomContainsMatcher(String expectedSubstring) {
    this.expectedSubstring = expectedSubstring;
  }

  @Override
  public void match(MvcResult result) throws Exception {
    String responseContent = result.getResponse().getContentAsString(Charset.defaultCharset());
    if (!responseContent.contains(expectedSubstring)) {
      throw new AssertionError("Expected response to contain substring: \"" + expectedSubstring +
          "\", but was: \"" + responseContent + "\"");
    }
  }

  public static ResultMatcher containsString(String substring) {
    return new CustomContainsMatcher(substring);
  }
}
