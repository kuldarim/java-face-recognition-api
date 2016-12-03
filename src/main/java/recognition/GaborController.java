package recognition;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class GaborController {

  @Autowired
  MatService matService;

  @Autowired
  GaborService gaborService;

  @Autowired
  FaceService faceService;

  @RequestMapping("/gabor")
  public String gobor() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/image.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    Mat gabor = gaborService.calculateGaborMat(greyscaleImage);

    return matService.matToJSON(gabor);
  }

  @RequestMapping("/variance")
   public String variance() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/image.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    Double[][] varianceMat = gaborService.createGaborVarianceMatForImage(greyscaleImage);

    //Highgui.imwrite("src/main/resources/image_test.png", gabor);

    return varianceMat.toString();
  }

  @RequestMapping("/crop")
  public String crop() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/faces/p4.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    Mat cropped = faceService.cropFace(greyscaleImage, "subject01");

    return matService.matToJSON(cropped);
  }

  @RequestMapping("/test")
  public String test() {
    ArrayList<Mat> originalImages = readImages("p1_", 6);

    ArrayList<Double[][]> varianceMats = new ArrayList<>();

    for (Mat original: originalImages) {
      Double[][] variance = gaborService.createGaborVarianceMatForImage(original);
      varianceMats.add(variance);
    }

    gaborService.calculateMeanForVarianceMats(varianceMats);


    return "yey";
  }

  private ArrayList<Mat> readImages(String imageNamePrefix, int imageCount) {
    ArrayList<Mat> images = new ArrayList<>();

    for(int i = 1; i <= imageCount; i++) {
      Mat image = Highgui.imread("src/main/resources/database/p1/resized/" + imageNamePrefix + i + ".jpg");
      images.add(image);
    }

    return images;
  }
}
