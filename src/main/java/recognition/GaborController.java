package recognition;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import image.FileService;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GaborController {

  @Autowired
  MatService matService;

  @Autowired
  GaborService gaborService;

  @Autowired
  FileService fileService;

  @RequestMapping("/distance")
  public String distance() {
    Double[][] distance = this.fileService.readMatFromFile("/distance/self-others.txt");

    JsonArray parentJsonArray = new JsonArray();

    for (Double[] row: distance) {
      JsonArray childJsonArray = new JsonArray();
      for (Double cell: row) {
        childJsonArray.add(cell);
      }
      parentJsonArray.add(childJsonArray);
    }

    Gson gson = new Gson();
    return gson.toJson(parentJsonArray);
  }

  @RequestMapping("/gabor")
  public String gobor() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/database/p1/resized/p1_1.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    gaborService.calculateGarborMats(greyscaleImage);

    return "gabor calculated " + String.valueOf(Math.random());
  }

  @RequestMapping("/variance")
   public String variance() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/image.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    Double[][] varianceMat = gaborService.createGaborVarianceMatForImage(greyscaleImage);

    //Highgui.imwrite("src/main/resources/image_test.png", gabor);

    return varianceMat.toString();
  }

  @RequestMapping("/print/self")
   public String printSelf() {
    Double[][] self = this.fileService.readMatFromFile("/result/self-mean.txt");

    this.printMatToConsole(self);

    return "printed";
  }

  @RequestMapping("/print/others")
  public String printOthers() {
    Double[][] self = this.fileService.readMatFromFile("/result/others-mean.txt");

    this.printMatToConsole(self);

    return "printed";
  }

  @RequestMapping("/do/everything")
  public String doEverything() {
    this.fileService.createCalculatedDirectories();

    this.matService.createVarianceMats();
    this.matService.createOthersVarianceMats();
    this.matService.createSelfVarianceMats();
    this.matService.createMeanOthers();
    this.matService.createMeanSelf();
    this.matService.calculateDistance();

    return "done";
  }

  private void printMatToConsole(Double[][] mat) {
    for(Double[] row: mat) {
      for(Double cell: row) {
        System.out.print(String.valueOf(cell) + " ");
      }
      System.out.println();
    }
  }
}
