package image;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ImageResizeService {

  public static ArrayList<Mat> resizeImages(ArrayList<Mat> images) {
    ArrayList<Mat> resized = new ArrayList<>();

    for (Mat image: images) {
      Mat resizedImage = new Mat(image.rows(), image.cols(), image.type());
      Imgproc.resize(image, resizedImage, new Size(800.0, 600.0));
      resized.add(resizedImage);
    }

    return resized;
  }
}
