package picture;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PictureController {

  @Autowired
  MatService matService;

  /**
   * @return returns Base64 encoded Json representation of org.opencv.core.Mat
   */
  @RequestMapping("/grayscale")
  public String grayscale() {
    Mat exampleGreyscaleMat = Highgui.imread("src/main/resources/image.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE);

    return matService.matToJSON(exampleGreyscaleMat);
  }
}
