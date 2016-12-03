package recognition;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;

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

  @RequestMapping("/create/variance/mats")
  public String createVarianceMats() {
    ArrayList<Mat> originalImagesP1 = readImages("p1/resized/p1_", 6);
    ArrayList<Mat> originalImagesP2 = readImages("p2/resized/p2_", 6);
    ArrayList<Mat> originalImagesP3 = readImages("p3/resized/p3_", 6);

    ArrayList<Double[][]> varianceMatsP1 = this.getVarianceMatsForImages(originalImagesP1);
    System.out.println("varianceMatsP1 done");
    Double[][] meanP1 = gaborService.calculateMeanForVarianceMats(varianceMatsP1);
    System.out.println("meanP1 done");
    this.storeMatInFile("meanP1.txt", meanP1);

    ArrayList<Double[][]> varianceMatsP2 = this.getVarianceMatsForImages(originalImagesP2);
    System.out.println("varianceMatsP2 done");
    Double[][] meanP2 = gaborService.calculateMeanForVarianceMats(varianceMatsP2);
    System.out.println("meanP2 done");
    this.storeMatInFile("meanP2.txt", meanP2);

    ArrayList<Double[][]> varianceMatsP3 = this.getVarianceMatsForImages(originalImagesP3);
    System.out.println("varianceMatsP3 done");
    Double[][] meanP3 = gaborService.calculateMeanForVarianceMats(varianceMatsP3);
    System.out.println("meanP3 done");
    this.storeMatInFile("meanP3.txt", meanP3);

    return "yey";
  }

  @RequestMapping("/write")
  public String write() {
    Double[][] test = new Double[1][1];
    test[0][0] = 1.1;
    this.storeMatInFile("Test", test);
    return "yey";
  }

  @RequestMapping("/read")
  public String read() {
    Double[][] mat = this.readMatFromFile("test");
    return String.valueOf(mat[0][0]);
  }

  private ArrayList<Double[][]> getVarianceMatsForImages(ArrayList<Mat> images) {
    ArrayList<Double[][]> varianceMats = new ArrayList<>();

    for (Mat original: images) {
      Double[][] variance = gaborService.createGaborVarianceMatForImage(original);
      varianceMats.add(variance);
    }

    return varianceMats;
  }

  private ArrayList<Mat> readImages(String imageNamePrefix, int imageCount) {
    ArrayList<Mat> images = new ArrayList<>();

    for(int i = 1; i <= imageCount; i++) {
      Mat image = Highgui.imread("src/main/resources/database/" + imageNamePrefix + i + ".jpg");
      images.add(image);
    }

    return images;
  }

  private void storeMatInFile(String fileName, Double[][] mat) {
    try(
      FileOutputStream f = new FileOutputStream("src/main/resources/calculated/" + fileName);
      ObjectOutput s = new ObjectOutputStream(f)
    ) {
      s.writeObject(mat);
      System.out.println(fileName + " stored succesfully!");
    } catch (Exception ex) {
      System.out.println(ex.toString());
    } finally {

    }
  }

  private Double[][] readMatFromFile(String fileName) {
    try(FileInputStream in = new FileInputStream("src/main/resources/test.txt");
        ObjectInputStream s = new ObjectInputStream(in)) {
      Double[][] mat = (Double[][])s.readObject();
      return mat;
    } catch (Exception ex) {
      System.out.println(ex.toString());
    } finally {

    }
    return null;
  }
}
