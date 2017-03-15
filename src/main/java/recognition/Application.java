package recognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"image", "recognition", "config", "analytics"})
@EnableAutoConfiguration
public class Application {

  static {
    // Load OpenCv library
    nu.pattern.OpenCV.loadShared();
    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
