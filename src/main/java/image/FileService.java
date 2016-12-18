package image;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class FileService {

  public void writeImagesToFile(ArrayList<Mat> images, String folder, String prefix) {
    System.out.println("@writeImagesToFile writing " + images.size() + " to "
      + "src/main/resources/" + folder + "/"+ prefix + "index" + ".jpg");
    int index = 1;
    for (Mat image: images) {
      Highgui.imwrite("src/main/resources/database/" + folder + "/"+ prefix + index + ".jpg", image);
      index++;
    }
  }

  public ArrayList<Mat> readImages(String folder, String imageNamePrefix, int imageCount) {
    ArrayList<Mat> images = new ArrayList<>();
    System.out.println("@readImages reading images from src/main/resources/database/"
      + folder + "/" + imageNamePrefix + "index" + ".jpg");

    for(int i = 1; i <= imageCount; i++) {
      Mat image = Highgui.imread("src/main/resources/database/" + folder + "/" + imageNamePrefix + i + ".jpg",
        Highgui.CV_LOAD_IMAGE_GRAYSCALE);
      images.add(image);
    }

    System.out.println("@readImages read images " + images.size());

    return images;
  }

}
