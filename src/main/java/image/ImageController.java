package image;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Controller
public class ImageController {

  @RequestMapping("/photo2")
  @ResponseBody
  public String testphoto() {

    try {
      File file = new File("src/main/resources/database/p1/original/p1_1.jpg");
      byte[] fileContent = Files.readAllBytes(file.toPath());

      String encoded = Base64.getEncoder().encodeToString(fileContent);

      return encoded;
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return "bad";
  }


}
