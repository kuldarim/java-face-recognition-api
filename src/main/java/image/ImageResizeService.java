package image;

import config.CONFIG;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ImageResizeService {

  public ArrayList<Mat> resizeImages(ArrayList<Mat> images) {
    ArrayList<Mat> resized = new ArrayList<>();

    for (Mat image: images) {
      Mat resizedImage = new Mat(image.rows(), image.cols(), image.type());
      Imgproc.resize(image, resizedImage, new Size(CONFIG.CROPPED_WIDTH, CONFIG.CROPPED_HEIGTH));
      resized.add(resizedImage);
    }

    return resized;
  }
}
