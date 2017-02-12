package image;

import config.CONFIG;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
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

      ArrayList<Mat> images = this.fileService.readImages(person + "/original", person + "_", CONFIG.NUMBER_OF_IMAGES);
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
      ArrayList<Mat> images = this.fileService.readImages(person + "/cropped", person + "_", CONFIG.NUMBER_OF_IMAGES);
      ArrayList<Mat> resized = this.imageResizeService.resizeImages(images);
      this.fileService.writeImagesToFile(resized, person + "/resized", person + "_");
      return "yey";
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return e.getMessage();
    }
  }

  @RequestMapping("/resize/for-ui/{person}")
  public String resizeForUi(@PathVariable(value="person") String person) {
    try {
      ArrayList<Mat> images = this.fileService.readImagesTrueColor(person + "/original", person + "_", CONFIG.NUMBER_OF_IMAGES);
      ArrayList<Mat> resized = this.imageResizeService.resizeImagesForUi(images);
      this.fileService.writeImagesToFile(resized, person + "/resized-ui", person + "_");
      return "yey";
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return e.getMessage();
    }
  }

  @RequestMapping("image/process")
  public String resizeForUi() {
    try {
      for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
        String person = "p" + i;
        new File("src/main/resources/database/" + person + "/cropped").mkdir();
        this.cropOne(person);
        new File("src/main/resources/database/" + person + "/resized").mkdir();
        this.resizeOne(person);
        new File("src/main/resources/database/" + person + "/resized-ui").mkdir();
        this.resizeForUiOne(person);
      }
      return "yey";
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return e.getMessage();
    }
  }

  private void cropOne(String person) {
    CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haars/haarcascade_frontalface_default.xml");

    ArrayList<Mat> images = this.fileService.readImages(person + "/original", person + "_", CONFIG.NUMBER_OF_IMAGES);
    ArrayList<Mat> croppedFaces = new ArrayList<>();

    int i = 1;
    for (Mat image: images) {
      ArrayList<Mat> cropped = this.imageCropperService.getCroppedFaces(faceDetector, image);
      System.out.println("@" + person + " photo " + i + " cropped " + cropped.size());
      croppedFaces.add(cropped.get(cropped.size() - 1));
      i++;
    }

    this.fileService.writeImagesToFile(croppedFaces, person + "/cropped", person + "_");
  }

  private void resizeOne(String person) {
    ArrayList<Mat> images = this.fileService.readImages(person + "/cropped", person + "_", CONFIG.NUMBER_OF_IMAGES);
    ArrayList<Mat> resized = this.imageResizeService.resizeImages(images);
    this.fileService.writeImagesToFile(resized, person + "/resized", person + "_");
  }

  private void resizeForUiOne(String person) {
    ArrayList<Mat> images = this.fileService.readImagesTrueColor(person + "/original", person + "_", CONFIG.NUMBER_OF_IMAGES);
    ArrayList<Mat> resized = this.imageResizeService.resizeImagesForUi(images);
    this.fileService.writeImagesToFile(resized, person + "/resized-ui", person + "_");
  }


}
