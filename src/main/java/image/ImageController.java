package image;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

  @RequestMapping("/photos")
  @ResponseBody
  public  Map<String, ArrayList<Person>> photos() {

    String path = "src/main/resources/database";
    ArrayList<Person> persons = new ArrayList<>();

    try {

      File[] personDirectories = new File(path).listFiles();


      for (File directory: personDirectories) {

        String directoryName = directory.getName();

        if (!directoryName.equalsIgnoreCase(".DS_Store")){
          File[] personPhotos = new File(path + "/" + directoryName + "/resized-ui").listFiles();
          System.out.println("Directory: " + directory.getName());

          Person person = new Person();
          person.name = directoryName;
          person.images = new ArrayList<>();

          for (File photo: personPhotos) {
            System.out.println("File: " + photo.getName());

            byte[] fileContent = Files.readAllBytes(photo.toPath());
            Image image = new Image();
            image.filePath = photo.toPath().toString();
            image.encoded = Base64.getEncoder().encodeToString(fileContent);
//            image.encoded = "123";
            person.images.add(image);
          }

          persons.add(person);
        }

      }

      Map<String, ArrayList<Person>> data = new HashMap<>();
      data.put("persons", persons);

      return data;

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }




}
