package recognition;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.CONFIG;
import image.FileService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MatService {

  @Autowired
  FileService fileService;

  @Autowired
  GaborService gaborService;
  /**
   * Read more here http://answers.opencv.org/question/8873/best-way-to-store-a-mat-object-in-android/?answer=28608#post-id-28608
   *
   * @param mat
   * @return
   */
  public String matToJSON(Mat mat) {
    if (mat.isContinuous()) {
      int cols = mat.cols();
      int rows = mat.rows();
      int elemSize = (int) mat.elemSize();

      byte[] data = new byte[cols * rows * elemSize];

      mat.get(0, 0, data);

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("rows", mat.rows());
      jsonObject.addProperty("cols", mat.cols());
      jsonObject.addProperty("type", mat.type());

      // We cannot set binary data to a json object, so:
      // Encoding data byte array to Base64.
      String dataString = new String(Base64.encodeBase64(data));

      jsonObject.addProperty("data", dataString);

      Gson gson = new Gson();
      String json = gson.toJson(jsonObject);

      return json;
    } else {
      System.out.println("Mat not continuous.");
    }
    return "{}";
  }

  public void createVarianceMats() {
    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      ArrayList<Mat> images = this.fileService.readImages(person + "/resized", person + "_", CONFIG.NUMBER_OF_IMAGES);

      System.out.println("@createVarianceMats " + person + "started");
      this.storeVarianceMatsForImages(images, "variance/" + person + "_");
      System.out.println("@createVarianceMats " + person + "done");
    }

  }

  public void createOthersVarianceMats() {
    ArrayList<ArrayList<Double[][]>> varianceMats = new ArrayList<>();

    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      ArrayList<Double[][]> varianceMat = this.fileService.readMatsByPerson(person + "_");
      varianceMats.add(varianceMat);
    }

    for (int i = 1; i < CONFIG.NUMBER_OF_PERSONS; i++) {
      String personI = "p" + i;
      for (int j = i; j <= CONFIG.NUMBER_OF_PERSONS; j++) {
        if (i == j) {
          continue;
        }
        String personJ = "p" + j;
        ArrayList<Double[][]> varianceMatsPIPJ = new ArrayList<>();
        varianceMatsPIPJ.addAll(varianceMats.get(i - 1));
        varianceMatsPIPJ.addAll(varianceMats.get(j - 1));

        System.out.println("@createOthersVarianceMats "  + personI + personJ + " started");
        Double[][] variance = gaborService.calculateMeanForVarianceMats(varianceMatsPIPJ);
        System.out.println("@createOthersVarianceMats "  + personI + personJ + " done");

        this.fileService.storeMatInFile("/mean/others/" + personI + personJ + ".txt", variance);
      }
    }
  }

  public void createSelfVarianceMats() {
    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      ArrayList<Double[][]> varianceMat = this.fileService.readMatsByPerson(person + "_");

      System.out.println("@createSelfVarianceMats " + person + "started");
      Double[][] variance = gaborService.calculateMeanForVarianceMats(varianceMat);
      System.out.println("@createSelfVarianceMats " + person + "done");

      this.fileService.storeMatInFile("/mean/self/"+ person+ ".txt", variance);
    }
  }

  public void createMeanOthers() {

    ArrayList<Double[][]> others = new ArrayList<>();

    for (int i = 1; i < CONFIG.NUMBER_OF_PERSONS; i++) {
      String personI = "p" + i;
      for (int j = i; j <= CONFIG.NUMBER_OF_PERSONS; j++) {
        if (i == j) {
          continue;
        }
        String personJ = "p" + j;
        Double[][] mat = this.fileService.readMatFromFile("calculated/mean/others/" + personI + personJ + ".txt");
        others.add(mat);
      }
    }

    System.out.println("@createMeanOthers started");
    Double[][] othersMean = gaborService.calculateMeanForVarianceMats(others);
    System.out.println("@createMeanOthers done");

    this.fileService.storeMatInFile("/result/others-mean.txt", othersMean);
  }

  public void createMeanSelf() {
    ArrayList<Double[][]> self = new ArrayList<>();

    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      Double[][] mat = this.fileService.readMatFromFile("calculated/mean/self/" + person + ".txt");
      self.add(mat);
    }

    System.out.println("@createMeanSelf started");
    Double[][] selfMean = gaborService.calculateMeanForVarianceMats(self);
    System.out.println("@createMeanSelf done");

    this.fileService.storeMatInFile("/result/self-mean.txt", selfMean);
  }

  public void calculateDistance() {
    Double[][] self = this.fileService.readMatFromFile("calculated/result/self-mean.txt");
    Double[][] others = this.fileService.readMatFromFile("calculated/result/others-mean.txt");

    Double[][] distance = this.gaborService.calculateDistanceBetweenMatrixes(self,others);

    this.fileService.storeMatInFile("/distance/self-others.txt", distance);
  }

  private void storeVarianceMatsForImages(ArrayList<Mat> images, String fileName) {
    int i = 1;
    for (Mat original: images) {
      Double[][] variance = gaborService.createGaborVarianceMatForImage(original);
      this.fileService.storeMatInFile(fileName + i + ".txt", variance);
      i++;
    }
  }

}
