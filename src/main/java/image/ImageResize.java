//comment these out to be able to run spring boot application
//package image;
//
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.highgui.Highgui;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//
//import java.util.ArrayList;
//
//public class ImageResize {
//  static {
//    // Load OpenCv library
//    nu.pattern.OpenCV.loadShared();
//    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
//  }
//
//  public static void main( String[] args ) {
//    try {
//
//      ArrayList<Mat> images = readImages("cropped", 6);
//      ArrayList<Mat> resized = resizeImages(images);
//      writeImagesToFile(resized, "resized");
//
//    } catch (Exception e) {
//      System.out.println("Error: " + e.getMessage());
//    }
//  }
//
//  private static ArrayList<Mat> resizeImages(ArrayList<Mat> images) {
//    ArrayList<Mat> resized = new ArrayList<>();
//
//    for (Mat image: images) {
//      Mat resizedImage = new Mat(image.rows(), image.cols(), image.type());
//      Imgproc.resize(image, resizedImage, new Size(800.0, 600.0));
//      resized.add(resizedImage);
//    }
//
//    return resized;
//  }
//
//  private static ArrayList<Mat> readImages(String imageNamePrefix, int imageCount) {
//    ArrayList<Mat> images = new ArrayList<>();
//
//    for(int i = 1; i <= imageCount; i++) {
//      Mat image = Highgui.imread("src/main/resources/cropped/" + imageNamePrefix + i + ".jpg");
//      images.add(image);
//    }
//
//    return images;
//  }
//
//  private static void writeImagesToFile(ArrayList<Mat> images, String prefix) {
//    int index = 0;
//    for (Mat image: images) {
//      Highgui.imwrite("src/main/resources/resized/" + prefix + "_" + index + ".jpg", image);
//      index++;
//    }
//  }
//}
