package recognition;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;

@RestController
public class GaborController {

  @Autowired
  MatService matService;

  @Autowired
  GaborService gaborService;

  @RequestMapping("/distance")
  public String distance() {
    Double[][] distance = this.readMatFromFile("/distance/self-others.txt");

    JsonArray parentJsonArray = new JsonArray();

    for (Double[] row: distance) {
      JsonArray childJsonArray = new JsonArray();
      for (Double cell: row) {
        childJsonArray.add(cell);
      }
      parentJsonArray.add(childJsonArray);
    }

    Gson gson = new Gson();
    return gson.toJson(parentJsonArray);
  }

  @RequestMapping("/gabor")
  public String gobor() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/database/p1/resized/p1_1.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    gaborService.calculateGarborMats(greyscaleImage);

    return "gabor calculated " + String.valueOf(Math.random());
  }

  @RequestMapping("/variance")
   public String variance() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/image.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    Double[][] varianceMat = gaborService.createGaborVarianceMatForImage(greyscaleImage);

    //Highgui.imwrite("src/main/resources/image_test.png", gabor);

    return varianceMat.toString();
  }

  @RequestMapping("/print/self")
   public String printSelf() {
    Double[][] self = this.readMatFromFile("/result/self-mean.txt");

    this.printMatToConsole(self);

    return "printed";
  }

  @RequestMapping("/print/others")
  public String printOthers() {
    Double[][] self = this.readMatFromFile("/result/others-mean.txt");

    this.printMatToConsole(self);

    return "printed";
  }

  @RequestMapping("/do/everything")
  public String doEverything() {
    new File("src/main/resources/calculated").mkdir();
    new File("src/main/resources/calculated/mean").mkdir();
    new File("src/main/resources/calculated/mean/self").mkdir();
    new File("src/main/resources/calculated/mean/others").mkdir();
    new File("src/main/resources/calculated/result").mkdir();
    new File("src/main/resources/calculated/variance").mkdir();
    new File("src/main/resources/calculated/distance").mkdir();

    this.createVarianceMats();
    this.createOthersVarianceMats();
    this.createSelfVarianceMats();
    this.createMeanOthers();
    this.createMeanSelf();
    this.calculateDistance();

    return "done";
  }

  @RequestMapping("/calculate/distance")
  public String calculateDistance() {
    Double[][] self = this.readMatFromFile("result/self-mean.txt");
    Double[][] others = this.readMatFromFile("result/others-mean.txt");

    Double[][] distance = this.gaborService.calculateDistanceBetweenMatrixes(self,others);

    this.storeMatInFile("/distance/self-others.txt", distance);

    return "calculated";
  }

  @RequestMapping("/create/self/mean/mats")
   public String createMeanSelf() {

    Double[][] p1 = this.readMatFromFile("/mean/self/p1.txt");
    Double[][] p2 = this.readMatFromFile("/mean/self/p2.txt");
    Double[][] p3 = this.readMatFromFile("/mean/self/p3.txt");

    ArrayList<Double[][]> self = new ArrayList<>();

    self.add(p1);
    self.add(p2);
    self.add(p3);

    Double[][] selfMean = gaborService.calculateMeanForVarianceMats(self);
    System.out.println("self done");
    this.storeMatInFile("/result/self-mean.txt", selfMean);

    return "yey";
  }

  @RequestMapping("/create/others/mean/mats")
  public String createMeanOthers() {

    Double[][] p1 = this.readMatFromFile("/mean/others/p1p2.txt");
    Double[][] p2 = this.readMatFromFile("/mean/others/p1p3.txt");
    Double[][] p3 = this.readMatFromFile("/mean/others/p2p3.txt");

    ArrayList<Double[][]> others = new ArrayList<>();

    others.add(p1);
    others.add(p2);
    others.add(p3);

    Double[][] othersMean = gaborService.calculateMeanForVarianceMats(others);
    System.out.println("others done");
    this.storeMatInFile("/result/others-mean.txt", othersMean);

    return "yey";
  }

  @RequestMapping("/create/self/variance/mats")
   public String createSelfVarianceMats() {

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

  @RequestMapping("/create/others/variance/mats")
  public String createOthersVarianceMats() {
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

  private void printMatToConsole(Double[][] mat) {
    for(Double[] row: mat) {
      for(Double cell: row) {
        System.out.print(String.valueOf(cell) + " ");
      }
      System.out.println();
    }
  }
}
