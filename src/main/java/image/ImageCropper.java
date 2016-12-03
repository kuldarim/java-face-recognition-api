////comment these out to be able to run spring boot application
//package image;
//
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.highgui.Highgui;
//import org.opencv.objdetect.CascadeClassifier;
//
//import java.util.ArrayList;
//
//public class ImageCropper {
//
//  static {
//    // Load OpenCv library
//    nu.pattern.OpenCV.loadShared();
//    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
//  }
//
//  public static void main( String[] args ) {
//    try {
//      CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haars/haarcascade_frontalface_default.xml");
//
//      ArrayList<Mat> images = readImages("person3_", 6);
//
//      int index = 0;
//      for (Mat image: images) {
//        ArrayList<Mat> croppedFaces = getCroppedFaces(faceDetector, image);
//        writeImagesToFile(croppedFaces, String.valueOf(index));
//        System.out.println("cropped");
//        index++;
//      }
//
//
//    } catch (Exception e) {
//      System.out.println("Error: " + e.getMessage());
//    }
//  }
//
//  private static ArrayList<Mat> readImages(String imageNamePrefix, int imageCount) {
//    ArrayList<Mat> images = new ArrayList<>();
//
//    for(int i = 1; i <= imageCount; i++) {
//      Mat image = Highgui.imread("src/main/resources/database/p3/" + imageNamePrefix + i + ".jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//      images.add(image);
//    }
//
//    return images;
//  }
//
//  private static ArrayList<Mat> getCroppedFaces(CascadeClassifier faceDetector, Mat img) {
//    MatOfRect faceDetections = new MatOfRect();
//    faceDetector.detectMultiScale(img, faceDetections);
//
//    ArrayList<Mat> cropped = new ArrayList<>();
//
//    for (Rect rect : faceDetections.toArray()) {
//      cropped.add(new Mat(img, rect));
//    }
//
//    return cropped;
//  }
//
//  private static void writeImagesToFile(ArrayList<Mat> images, String prefix) {
//    int index = 0;
//    for (Mat image: images) {
//      Highgui.imwrite("src/main/resources/cropped/" + prefix + "_" + index + ".jpg", image);
//      index++;
//    }
//  }
//}
