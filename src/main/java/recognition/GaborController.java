package recognition;

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
}
