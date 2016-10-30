package recognition;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

@Service
public class FaceService {

  public Mat cropFace(Mat img, String filename) {
    CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haars/haarcascade_frontalface_default.xml");

    MatOfRect faceDetections = new MatOfRect();
    faceDetector.detectMultiScale(img, faceDetections);

    Mat cropped;
    if (!faceDetections.empty()) {
      cropped = new Mat(img, faceDetections.toArray()[0]);
      Highgui.imwrite("src/main/resources/cropped/" + filename + ".png", cropped);
    } else {
      cropped = new Mat();
    }

    return cropped;
  }
}
