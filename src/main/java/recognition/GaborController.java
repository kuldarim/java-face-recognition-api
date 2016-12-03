package recognition;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.stream.Stream;

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

  @RequestMapping("/create/self/mean/mats")
   public String createSelfMeanMats() {

    ArrayList<Double[][]> varianceMatsP1 = this.readMatsByPerson("p1_");
    System.out.println("varianceMatsP1 done");
    Double[][] meanP1 = gaborService.calculateMeanForVarianceMats(varianceMatsP1);
    System.out.println("meanP1 done");
    this.storeMatInFile("/mean/self/p1.txt", meanP1);

    ArrayList<Double[][]> varianceMatsP2 = this.readMatsByPerson("p2_");
    System.out.println("varianceMatsP2 done");
    Double[][] meanP2 = gaborService.calculateMeanForVarianceMats(varianceMatsP2);
    System.out.println("meanP2 done");
    this.storeMatInFile("/mean/self/p2.txt", meanP2);

    ArrayList<Double[][]> varianceMatsP3 = this.readMatsByPerson("p3_");
    System.out.println("varianceMatsP3 done");
    Double[][] meanP3 = gaborService.calculateMeanForVarianceMats(varianceMatsP3);
    System.out.println("meanP3 done");
    this.storeMatInFile("/mean/self/p3.txt", meanP3);

    return "yey";
  }

  @RequestMapping("/create/others/mean/mats")
  public String createOthersMeanMats() {
    ArrayList<Double[][]> varianceMatsP1 = this.readMatsByPerson("p1_");
    ArrayList<Double[][]> varianceMatsP2 = this.readMatsByPerson("p2_");
    ArrayList<Double[][]> varianceMatsP3 = this.readMatsByPerson("p3_");

    ArrayList<Double[][]> varianceMatsP1P2 = new ArrayList<>();
    varianceMatsP1P2.addAll(varianceMatsP1);
    varianceMatsP1P2.addAll(varianceMatsP2);
    Double[][] meanP1P2 = gaborService.calculateMeanForVarianceMats(varianceMatsP1P2);
    System.out.println("meanP1P2 done");
    this.storeMatInFile("/mean/others/p1p2.txt", meanP1P2);

    ArrayList<Double[][]> varianceMatsP1P3 = new ArrayList<>();
    varianceMatsP1P3.addAll(varianceMatsP1);
    varianceMatsP1P3.addAll(varianceMatsP3);
    Double[][] meanP1P3 = gaborService.calculateMeanForVarianceMats(varianceMatsP1P3);
    System.out.println("meanP1P3 done");
    this.storeMatInFile("/mean/others/p1p3.txt", meanP1P3);

    ArrayList<Double[][]> varianceMatsP2P3 = new ArrayList<>();
    varianceMatsP2P3.addAll(varianceMatsP2);
    varianceMatsP2P3.addAll(varianceMatsP3);
    Double[][] meanP2P3 = gaborService.calculateMeanForVarianceMats(varianceMatsP2P3);
    System.out.println("meanP2P3 done");
    this.storeMatInFile("/mean/others/p2p3.txt", meanP2P3);

    return "yey";
  }

  @RequestMapping("/create/variance/mats")
  public String createVarianceMats() {
    ArrayList<Mat> originalImagesP1 = readImages("p1/resized/p1_", 6);
    ArrayList<Mat> originalImagesP2 = readImages("p2/resized/p2_", 6);
    ArrayList<Mat> originalImagesP3 = readImages("p3/resized/p3_", 6);

    System.out.println("varianceMatsP1 started");
    this.storeVarianceMatsForImages(originalImagesP1, "variance/p1_");
    System.out.println("varianceMatsP1 done");

    System.out.println("varianceMatsP2 started");
    this.storeVarianceMatsForImages(originalImagesP2, "variance/p2_");
    System.out.println("varianceMatsP2 done");

    System.out.println("varianceMatsP3 started");
    this.storeVarianceMatsForImages(originalImagesP3, "variance/p3_");
    System.out.println("varianceMatsP3 done");

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
  private void storeVarianceMatsForImages(ArrayList<Mat> images, String fileName) {
    int i = 1;
    for (Mat original: images) {
      Double[][] variance = gaborService.createGaborVarianceMatForImage(original);
      this.storeMatInFile(fileName + i +".txt", variance);
      i++;
    }
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

  private ArrayList<Double[][]> readMatsByPerson(String person) {
    ArrayList<Double[][]> varianceMats = new ArrayList<>();

    for (int i = 1; i<=6; i++) {
      Double[][] mat = this.readMatFromFile("variance/" + person + i + ".txt");
      varianceMats.add(mat);
    }

    return varianceMats;
  }

  private Double[][] readMatFromFile(String fileName) {
    try(FileInputStream in = new FileInputStream("src/main/resources/calculated/" + fileName);
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
