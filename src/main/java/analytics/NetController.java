package analytics;

import config.CONFIG;
import image.FileService;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

@RestController
public class NetController {

  @Autowired
  FileService fileService;

  @RequestMapping("/analytics/precision/{netName}")
  public String far(@PathVariable(value="netName") String netName) {
    switch (netName) {
      case "1": {
        this.calculatePrecision("netSquare-self-distance.txt", "netSquare-others-distance.txt");
        break;
      }
      case "2": {
        this.calculatePrecision("net-self-distance.txt", "net-others-distance.txt");
        break;
      }
      case "3": {
        this.calculatePrecision("netFeature-self-distance.txt", "netFeature-others-distance.txt");
      }
    }

    return "yey";
  }

  @RequestMapping("/analytics/store-distances/{netName}")
  public String storeDistances(@PathVariable(value="netName") String netName) {

    switch (netName) {
      case "1": {
        this.storeDistance("netSquare");
        break;
      }
      case "2": {
        this.storeDistance("net");
        break;
      }
      case "3": {
        this.storeDistance("netFeature");
      }
    }

    return "ney";
  }

  @RequestMapping("/analytics/apply/{netName}")
  public String applyNet(@PathVariable(value="netName") String netName) {

    ArrayList<String> netGenetic = this.fileService.readNetFromFile();
    ArrayList<String> netSquare = new ArrayList<>();
    netSquare.add("320-225");
    netSquare.add("320-226");
    netSquare.add("320-227");
    netSquare.add("320-228");
    netSquare.add("320-229");
    netSquare.add("320-230");
    netSquare.add("320-231");
    netSquare.add("320-232");
    netSquare.add("320-233");
    netSquare.add("320-234");
    netSquare.add("320-235");
    netSquare.add("320-236");
    netSquare.add("320-237");
    netSquare.add("320-238");
    netSquare.add("320-239");
    netSquare.add("320-240");
    netSquare.add("320-241");
    netSquare.add("320-242");
    netSquare.add("320-243");
    netSquare.add("320-244");
    netSquare.add("320-245");
    netSquare.add("320-246");
    netSquare.add("320-247");
    netSquare.add("320-248");
    netSquare.add("320-249");
    netSquare.add("320-250");
    netSquare.add("320-251");
    netSquare.add("320-252");
    netSquare.add("320-253");
    netSquare.add("320-254");
    netSquare.add("320-255");
    netSquare.add("320-256");
    netSquare.add("320-257");
    netSquare.add("320-258");
    netSquare.add("320-259");
    netSquare.add("320-260");
    netSquare.add("320-261");
    netSquare.add("320-262");
    netSquare.add("320-263");
    netSquare.add("320-264");
    ArrayList<String> netFeature = new ArrayList<>();
    netFeature.add("193-177");
    netFeature.add("192-176");
    netFeature.add("191-175");
    netFeature.add("190-174");
    netFeature.add("194-173");
    netFeature.add("195-172");
    netFeature.add("196-171");
    netFeature.add("197-170");
    netFeature.add("198-177");
    netFeature.add("199-178");
    netFeature.add("200-179");
    netFeature.add("199-170");
    netFeature.add("411-171");
    netFeature.add("412-172");
    netFeature.add("413-173");
    netFeature.add("414-174");
    netFeature.add("415-175");
    netFeature.add("416-176");
    netFeature.add("417-177");
    netFeature.add("418-178");
    netFeature.add("419-179");
    netFeature.add("411-177");
    netFeature.add("412-177");
    netFeature.add("413-177");
    netFeature.add("414-177");
    netFeature.add("415-177");
    netFeature.add("416-177");
    netFeature.add("261-348");
    netFeature.add("262-348");
    netFeature.add("263-348");
    netFeature.add("264-348");
    netFeature.add("265-348");
    netFeature.add("266-348");
    netFeature.add("267-348");
    netFeature.add("268-348");
    netFeature.add("269-348");
    netFeature.add("270-348");
    netFeature.add("271-348");
    netFeature.add("272-348");
    netFeature.add("273-348");

    switch (netName) {
      case "1": {
        this.storeNet(netSquare, "netSquare");
        break;
      }
      case "2": {
        this.storeNet(netGenetic, "net");
        break;
      }
      case "3": {
        this.storeNet(netFeature, "netFeature");
      }
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
}
