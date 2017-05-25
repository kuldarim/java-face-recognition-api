package recognition;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import config.CONFIG;
import image.FileService;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

@RestController
public class GaborParamsTestController {

  String GABOR_INDEX = "gabor22";
  int NUMBER_OF_PERSONS = 3;

  @Autowired
  FileService fileService;

  @Autowired
  GaborService gaborService;

  @RequestMapping("/gabor-tester")
  public String gaborTester() {

    // TODO change gabor1 to another thing
    new File("src/main/resources/gabor/" + GABOR_INDEX + "/").mkdirs();
    new File("src/main/resources/gabor/" + GABOR_INDEX + "/mean").mkdirs();
    new File("src/main/resources/gabor/" + GABOR_INDEX +"/mean/self").mkdirs();
    new File("src/main/resources/gabor/" + GABOR_INDEX +"/mean/others").mkdirs();
    new File("src/main/resources/gabor/" + GABOR_INDEX +"/result").mkdirs();
    new File("src/main/resources/gabor/" + GABOR_INDEX +"/variance").mkdirs();
    new File("src/main/resources/gabor/" + GABOR_INDEX +"/distance").mkdirs();


    // VARIANCE MATS
    for (int i = 1; i <= NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      ArrayList<Mat> images = this.fileService.readImages(person + "/resized", person + "_", CONFIG.NUMBER_OF_IMAGES);

      System.out.println("@variance " + person + "started");


      int index = 1;
      for (Mat image : images) {
        Double[][] variance = gaborService.createGaborVarianceMatForImage(image);
        this.fileService.storeVarianceInFile(GABOR_INDEX + "/variance/" + person + "_" + index + ".txt", variance);
        index++;
      }
      System.out.println("@CreateGabor " + person + "done");
    }

//     VARIANCE SELF
    for (int i = 1; i <= NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      ArrayList<Double[][]> varianceMat = this.fileService.readVarianceMatsByPerson(GABOR_INDEX, person + "_");

      System.out.println("@createSelfVarianceMats " + person + "started");
      Double[][] variance = gaborService.calculateMeanForVarianceMats(varianceMat);
      System.out.println("@createSelfVarianceMats " + person + "done");

//      this.fileService.storeMatInFile("/mean/self/"+ person+ ".txt", variance);
      this.fileService.storeVarianceInFile(GABOR_INDEX + "/mean/self/" + person + ".txt", variance);
    }

    // VARIANCE OTHERS
    ArrayList<ArrayList<Double[][]>> varianceMats = new ArrayList<>();

    for (int i = 1; i <= NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      ArrayList<Double[][]> varianceMat = this.fileService.readVarianceMatsByPerson(GABOR_INDEX, person + "_");
      varianceMats.add(varianceMat);
    }

    for (int i = 1; i < NUMBER_OF_PERSONS; i++) {
      String personI = "p" + i;
      for (int j = i; j <= NUMBER_OF_PERSONS; j++) {
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

        this.fileService.storeVarianceInFile(GABOR_INDEX + "/mean/others/" + personI + personJ + ".txt", variance);
      }
    }

//     MEAN SELF
    ArrayList<Double[][]> self = new ArrayList<>();

    for (int i = 1; i <= NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      Double[][] mat = this.fileService.readMatFromFile("gabor/" + GABOR_INDEX + "/mean/self/" + person + ".txt");
      self.add(mat);
    }

    System.out.println("@createMeanSelf started");
    Double[][] selfMean = gaborService.calculateMeanForVarianceMats(self);
    System.out.println("@createMeanSelf done");

    this.fileService.storeMatInFile("/result/self-mean.txt", selfMean);
    this.fileService.storeVarianceInFile(GABOR_INDEX + "/result/self-mean.txt", selfMean);

    // MEAN OTHERS

    ArrayList<Double[][]> others = new ArrayList<>();

    for (int i = 1; i < NUMBER_OF_PERSONS; i++) {
      String personI = "p" + i;
      for (int j = i; j <= NUMBER_OF_PERSONS; j++) {
        if (i == j) {
          continue;
        }
        String personJ = "p" + j;
        Double[][] mat = this.fileService.readMatFromFile("gabor/" + GABOR_INDEX + "/mean/others/" + personI + personJ + ".txt");
        others.add(mat);
      }
    }

    System.out.println("@createMeanOthers started");
    Double[][] othersMean = gaborService.calculateMeanForVarianceMats(others);
    System.out.println("@createMeanOthers done");

    this.fileService.storeMatInFile("/result/others-mean.txt", othersMean);
    this.fileService.storeVarianceInFile(GABOR_INDEX + "/result/others-mean.txt", othersMean);

    // CALCULATE DISTANCE

    Double[][] selfD = this.fileService.readMatFromFile("gabor/" + GABOR_INDEX + "/result/self-mean.txt");
    Double[][] othersD = this.fileService.readMatFromFile("gabor/" + GABOR_INDEX + "/result/others-mean.txt");

    Double[][] distance = this.gaborService.calculateDistanceBetweenMatrixes(selfD, othersD);

    this.fileService.storeVarianceInFile(GABOR_INDEX + "/distance/self-others.txt", distance);

    Double[][] distanceFromFile = this.fileService.readMatFromFile("gabor/" + GABOR_INDEX + "/distance/self-others.txt");

    JsonArray parentJsonArray = new JsonArray();

    for (Double[] row: distanceFromFile) {
      JsonArray childJsonArray = new JsonArray();
      for (Double cell: row) {
        childJsonArray.add(cell);
      }
      parentJsonArray.add(childJsonArray);
    }

    Gson gson = new Gson();
    return gson.toJson(parentJsonArray);
  }

  @RequestMapping("/gabor-tester-store")
  public String gaborTesterStore(@RequestBody ArrayList<String> net) {

    this.fileService.storeNetInFile(net, GABOR_INDEX + "_net.txt");

    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      new File("src/main/resources/database/" + person + "/" + GABOR_INDEX + "net").mkdirs();
    }

    String path = "src/main/resources/database";

    try {

      File[] personDirectories = new File(path).listFiles();


      for (File directory: personDirectories) {

        String directoryName = directory.getName();

        if (!directoryName.equalsIgnoreCase(".DS_Store")) {
          File[] personPhotos = new File(path + "/" + directoryName + "/resized").listFiles();

          for (File photo: personPhotos) {
            if (!photo.getName().equalsIgnoreCase(".DS_Store")) {
              Mat image = Highgui.imread(photo.getPath().toString());
              System.out.println(photo.getPath().toString());
              Mat vector = new Mat(1, net.size() - 1, CvType.CV_8UC1);
              int i = 0;
              for (String coordinatesString : net) {
                String[] coordinates = coordinatesString.split("-");
                int x = Integer.valueOf(coordinates[0]);
                int y = Integer.valueOf(coordinates[1]);

                double[] value = image.get(x, y);
                System.out.println(x + " " + y);
                System.out.println(i + " " + value[0]);
                vector.put(0, i, value[0]);
                i++;
              }

              String vectorPath = photo.getPath().toString().replaceAll("resized", GABOR_INDEX + "net");

              System.out.println(vectorPath);

              Highgui.imwrite( vectorPath, vector);
            }
          }
        }

      }


      // TODO now its time to calculate statistics for this net
      this.storeDistance(GABOR_INDEX + "net");
      this.calculatePrecision(GABOR_INDEX + "net-self-distance.txt", GABOR_INDEX + "net-others-distance.txt");

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return "yey";
  }

  private void calculatePrecision(String selfName, String othersName) {
    ArrayList<Double> selfNorms = this.fileService.readDistanceFromFile(selfName);
    ArrayList<Double> othersNorms = this.fileService.readDistanceFromFile(othersName);

    System.out.println(selfNorms.size());
    System.out.println(othersNorms.size());

    int selfSize = selfNorms.size();
    int othersSize = othersNorms.size();


    Double selftPercentage;
    Double otherPercentage;

    int indexSelf = 0;
    int indexOthers = 0;

    for (int i = 0; i < selfSize; i++) {
      for (int j = 0; j < othersSize; j++) {
        Double a = selfNorms.get(i);
        Double b = othersNorms.get(j);
        if ( Double.compare(a, b) > 0 ) {
          indexSelf = i;
          indexOthers++;
        }
      }

      selftPercentage = (double) indexSelf / selfSize * 100;
      otherPercentage = (double) (othersSize - indexOthers) / othersSize * 100;

      double result = selftPercentage - otherPercentage;
      indexOthers = 0;

      if (result > 0 && result < 4.0) {
        System.out.println(selftPercentage + " % " + otherPercentage);
        System.out.println(selfNorms.get(i));
        break;
      }
    }
  }

  private void storeDistance(String netName) {
    String path = "src/main/resources/database";

    ArrayList<Double> selfNorms = new ArrayList<>();
    ArrayList<Double> othersNorms = new ArrayList<>();

    try {
      File[] personDirectories = new File(path).listFiles();

      for (File directory1: personDirectories) {

        for (File directory2: personDirectories) {

          String directoryName1 = directory1.getName();
          String directoryName2 = directory2.getName();

          if (!directoryName1.equalsIgnoreCase(".DS_Store") && !directoryName2.equalsIgnoreCase(".DS_Store")) {
            File[] personPhotos1 = new File(path + "/" + directoryName1 + "/" + netName).listFiles();
            File[] personPhotos2 = new File(path + "/" + directoryName2 + "/" + netName).listFiles();

            for (File photo1: personPhotos1) {
              for (File photo2: personPhotos2) {
                if (!photo1.getName().equalsIgnoreCase(".DS_Store") && !photo2.getName().equalsIgnoreCase(".DS_Store")) {

                  Mat comparatorVector = Highgui.imread(photo1.getPath().toString());
                  Mat vectorToCompare = Highgui.imread(photo2.getPath().toString());
                  Double norm = Core.norm(vectorToCompare, comparatorVector, Core.NORM_L2);
                  System.out.println(photo1.getPath().toString() + " " + photo2.getPath().toString() + " " + norm);
                  if (directoryName1.equalsIgnoreCase(directoryName2)) {
                    selfNorms.add(norm);
                  } else {
                    othersNorms.add(norm);
                  }
                }
              }
            }
          }

        }
      }

      Collections.sort(selfNorms);
      Collections.sort(othersNorms);

      this.fileService.storeDistanceInFile(selfNorms, netName + "-self-distance.txt");
      this.fileService.storeDistanceInFile(othersNorms, netName + "-others-distance.txt");

      System.out.println(selfNorms.size());
      System.out.println(othersNorms.size());

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void storeNet(ArrayList<String> net, String vectorFolderName) {
    String path = "src/main/resources/database";
    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;

      new File("src/main/resources/database/" + person + "/" + vectorFolderName).mkdir();
    }
    try {

      File[] personDirectories = new File(path).listFiles();


      for (File directory: personDirectories) {

        String directoryName = directory.getName();

        if (!directoryName.equalsIgnoreCase(".DS_Store")) {
          File[] personPhotos = new File(path + "/" + directoryName + "/resized").listFiles();

          for (File photo: personPhotos) {
            if (!photo.getName().equalsIgnoreCase(".DS_Store")) {
              Mat image = Highgui.imread(photo.getPath().toString());
              System.out.println(photo.getPath().toString());
              Mat vector = new Mat(1, net.size() - 1, CvType.CV_8UC1);
              int i = 0;
              for (String coordinatesString : net) {
                String[] coordinates = coordinatesString.split("-");
                int x = Integer.valueOf(coordinates[0]);
                int y = Integer.valueOf(coordinates[1]);

                double[] value = image.get(x, y);
                System.out.println(x + " " + y);
                System.out.println(i + " " + value[0]);
                vector.put(0, i, value[0]);
                i++;
              }

              String vectorPath = photo.getPath().toString().replaceAll("resized", vectorFolderName);

              System.out.println(vectorPath);

              Highgui.imwrite( vectorPath, vector);
            }
          }
        }

      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void storeNetArrayString(String[] net, String vectorFolderName) {
    String path = "src/main/resources/database";
    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;

      new File("src/main/resources/database/" + person + "/" + vectorFolderName).mkdir();
    }
    try {

      File[] personDirectories = new File(path).listFiles();


      for (File directory: personDirectories) {

        String directoryName = directory.getName();

        if (!directoryName.equalsIgnoreCase(".DS_Store")) {
          File[] personPhotos = new File(path + "/" + directoryName + "/resized").listFiles();

          for (File photo: personPhotos) {
            if (!photo.getName().equalsIgnoreCase(".DS_Store")) {
              Mat image = Highgui.imread(photo.getPath().toString());
              System.out.println(photo.getPath().toString());
              Mat vector = new Mat(1, net.length - 1, CvType.CV_8UC1);
              int i = 0;
              for (String coordinatesString : net) {
                String[] coordinates = coordinatesString.split("-");
                int x = Integer.valueOf(coordinates[0]);
                int y = Integer.valueOf(coordinates[1]);

                double[] value = image.get(x, y);
                System.out.println(x + " " + y);
                System.out.println(i + " " + value[0]);
                vector.put(0, i, value[0]);
                i++;
              }

              String vectorPath = photo.getPath().toString().replaceAll("resized", vectorFolderName);

              System.out.println(vectorPath);

              Highgui.imwrite( vectorPath, vector);
            }
          }
        }

      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
