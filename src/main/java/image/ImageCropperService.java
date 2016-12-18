package image;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ImageCropperService {

  public ArrayList<Mat> getCroppedFaces(CascadeClassifier faceDetector, Mat img) {
    MatOfRect faceDetections = new MatOfRect();
    faceDetector.detectMultiScale(img, faceDetections);

    ArrayList<Mat> cropped = new ArrayList<>();

    for (Rect rect : faceDetections.toArray()) {
      cropped.add(new Mat(img, rect));
    }

    return cropped;
  }
}
