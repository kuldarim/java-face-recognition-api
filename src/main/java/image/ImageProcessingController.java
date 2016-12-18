package image;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ImageProcessingController {

  @Autowired
  FileService fileService;

  @Autowired
  ImageCropperService imageCropperService;

  @Autowired
  ImageResizeService imageResizeService;

  @RequestMapping("/crop/{person}")
  public String crop(@PathVariable(value="person") String person) {
    try {
      CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haars/haarcascade_frontalface_default.xml");

      ArrayList<Mat> images = this.fileService.readImages(person + "/original", person + "_", 6);
      ArrayList<Mat> croppedFaces = new ArrayList<>();

      int i = 1;
      for (Mat image: images) {
        ArrayList<Mat> cropped = this.imageCropperService.getCroppedFaces(faceDetector, image);
        System.out.println("@" + person + " photo " + i + " cropped " + cropped.size());
        croppedFaces.add(cropped.get(cropped.size() - 1));
        i++;
      }

      this.fileService.writeImagesToFile(croppedFaces, person + "/cropped", person + "_");


      return "yey";

    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());

      return e.getMessage();
    }
  }

  @RequestMapping("/resize/{person}")
  public String resize(@PathVariable(value="person") String person) {
    try {
      ArrayList<Mat> images = this.fileService.readImages(person + "/cropped", person + "_", 6);
      ArrayList<Mat> resized = this.imageResizeService.resizeImages(images);
      this.fileService.writeImagesToFile(resized, person + "/resized", person + "_");
      return "yey";
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return e.getMessage();
    }
  }
}
