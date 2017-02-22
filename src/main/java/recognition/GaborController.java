package recognition;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import config.CONFIG;
import image.FileService;
import image.Person;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;


@RestController
public class GaborController {

  @Autowired
  MatService matService;

  @Autowired
  GaborService gaborService;

  @Autowired
  FileService fileService;

  @RequestMapping("/far")
  public String far() {
    ArrayList<Double> selfNorms = this.fileService.readDistanceFromFile("self-distance.txt");
    ArrayList<Double> othersNorms = this.fileService.readDistanceFromFile("others-distance.txt");

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

    return "yey";
  }

  @RequestMapping("/store-distances")
  public String storeDistances() {
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
            File[] personPhotos1 = new File(path + "/" + directoryName1 + "/net").listFiles();
            File[] personPhotos2 = new File(path + "/" + directoryName2 + "/net").listFiles();

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

      this.fileService.storeSelfDistanceInFile(selfNorms);
      this.fileService.storeOthersDistanceInFile(othersNorms);

      System.out.println(selfNorms.size());
      System.out.println(othersNorms.size());

      return "yey";

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return "ney";
  }

  @RequestMapping("/recognise")
  public Map<String, String> resize(@RequestBody String p) {
    System.out.println(p);

    String fileName = p.replaceAll("resized-ui", "net");
    String path = "src/main/resources/database";

    Mat vectorToCompare = Highgui.imread(fileName);

    //TODO norms should be hashmap instead of array because p10 is second in directories after p1
    ArrayList<Double> norms = new ArrayList<>();

    // Sum all norms for each person
    // Just dont add the same picture norm, because it will be 0
    // Calculate vidurkis of each person and return lowest norm.
    // Should divide by 5 or 6 depending if the person is the same or different.

    try {
      File[] personDirectories = new File(path).listFiles();

      for (File directory: personDirectories) {

        String directoryName = directory.getName();
        boolean mathched = false;
        if (!directoryName.equalsIgnoreCase(".DS_Store")) {
          File[] personPhotos = new File(path + "/" + directoryName + "/net").listFiles();

          Double sum = 0.0;

          for (File photo: personPhotos) {
            if (!photo.getName().equalsIgnoreCase(".DS_Store")) {
              if (photo.getPath().toString().equalsIgnoreCase(p)) {
                mathched = true;
              } else {
                Mat comparatorVector = Highgui.imread(photo.getPath().toString());
                Double norm = Core.norm(vectorToCompare, comparatorVector, Core.NORM_L2);
                System.out.println(photo.getPath().toString() + " " + norm);
                sum += norm;
              }

            }
          }

          norms.add(sum / (mathched ? CONFIG.NUMBER_OF_PERSONS - 1 : CONFIG.NUMBER_OF_PERSONS));
        }
      }

      int index = 0;
      int smallest = 0;
      for (Double norm: norms) {
        System.out.println("yey: " + norm);
        if (norms.get(smallest) > norm) {
          smallest = index;
        }
        index++;
      }


      Map<String, String> data = new HashMap<>();
      data.put("person", String.valueOf("p" + (smallest + 1)));

      return data;

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  @RequestMapping("/create/from/stored/net")
  public String createFromStoredNet() {

    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      new File("src/main/resources/database/" + person + "/net").mkdir();
    }

    ArrayList<String> net = this.fileService.readNetFromFile();

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

              String vectorPath = photo.getPath().toString().replaceAll("resized", "net");

              System.out.println(vectorPath);

              Highgui.imwrite( vectorPath, vector);
            }
          }
        }

      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return "yey";
  }

  @RequestMapping("/store")
  public String store(@RequestBody ArrayList<String> net) {

    this.fileService.storeNetInFile(net);

    for (int i = 1; i <= CONFIG.NUMBER_OF_PERSONS; i++) {
      String person = "p" + i;
      new File("src/main/resources/database/" + person + "/net").mkdir();
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

              String vectorPath = photo.getPath().toString().replaceAll("resized", "net");

              System.out.println(vectorPath);

              Highgui.imwrite( vectorPath, vector);
            }
          }
        }

      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return "yey";
  }

  @RequestMapping("/distance")
  public String distance() {
    Double[][] distance = this.fileService.readMatFromFile("/distance/self-others.txt");

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

  @RequestMapping("/euclidean")
  public String euclidean() {
    Mat image1 = Highgui.imread("src/main/resources/database/p1/original/p1_1.jpg");
    Mat image2 = Highgui.imread("src/main/resources/database/p1/original/p1_2.jpg");

    //calculate the distance between matrices
    return String.valueOf(Core.norm(image1, image2, Core.NORM_L2));
  }

  @RequestMapping("/gabor")
  public String gobor() {
    Mat greyscaleImage = Highgui.imread("src/main/resources/database/p1/resized/p1_1.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
    gaborService.calculateGarborMats(greyscaleImage);

    return "gabor calculated " + String.valueOf(Math.random());
  }

  @RequestMapping("/print/self")
   public String printSelf() {
    Double[][] self = this.fileService.readMatFromFile("/result/self-mean.txt");

    this.printMatToConsole(self);

    return "printed";
  }

  @RequestMapping("/print/others")
  public String printOthers() {
    Double[][] self = this.fileService.readMatFromFile("/result/others-mean.txt");

    this.printMatToConsole(self);

    return "printed";
  }

  @RequestMapping("/do/everything")
  public String doEverything() {
    this.fileService.createCalculatedDirectories();

    this.matService.createVarianceMats();
    this.matService.createOthersVarianceMats();
    this.matService.createSelfVarianceMats();
    this.matService.createMeanOthers();
    this.matService.createMeanSelf();
    this.matService.calculateDistance();

    return "done";
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
