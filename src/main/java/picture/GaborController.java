package picture;

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

    @RequestMapping("/gabor")
    public String gobor() {
        Mat greyscaleImage = Highgui.imread("src/main/resources/example.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat gabor = gaborService.calculateGaborMat(greyscaleImage);
        
        return matService.matToJSON(gabor);
    }
}
