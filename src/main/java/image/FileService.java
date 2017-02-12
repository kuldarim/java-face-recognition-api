package image;

import config.CONFIG;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

@Service
public class FileService {

  public void writeImagesToFile(ArrayList<Mat> images, String folder, String prefix) {
    System.out.println("@writeImagesToFile writing " + images.size() + " to "
      + "src/main/resources/" + folder + "/"+ prefix + "index" + ".png");
    int index = 1;
    for (Mat image: images) {
      Highgui.imwrite("src/main/resources/database/" + folder + "/"+ prefix + index + ".png", image);
      index++;
    }
  }

  public ArrayList<Mat> readImages(String folder, String imageNamePrefix, int imageCount) {
    ArrayList<Mat> images = new ArrayList<>();
    System.out.println("@readImages reading images from src/main/resources/database/"
      + folder + "/" + imageNamePrefix + "index" + ".png");

    for(int i = 1; i <= imageCount; i++) {
      Mat image = Highgui.imread("src/main/resources/database/" + folder + "/" + imageNamePrefix + i + ".png",
        Highgui.CV_LOAD_IMAGE_GRAYSCALE);
      images.add(image);
    }

    System.out.println("@readImages read images " + images.size());

    return images;
  }

  public ArrayList<Mat> readImagesTrueColor(String folder, String imageNamePrefix, int imageCount) {
    ArrayList<Mat> images = new ArrayList<>();
    System.out.println("@readImages reading images from src/main/resources/database/"
      + folder + "/" + imageNamePrefix + "index" + ".png");

    for(int i = 1; i <= imageCount; i++) {
      Mat image = Highgui.imread("src/main/resources/database/" + folder + "/" + imageNamePrefix + i + ".png");
      images.add(image);
    }

    System.out.println("@readImages read images " + images.size());

    return images;
  }

  public void storeMatInFile(String fileName, Double[][] mat) {
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

  public void storeMatFile(String fileName, Double[][] mat) {
    try(
      FileOutputStream f = new FileOutputStream("src/main/resources/database/" + fileName);
      ObjectOutput s = new ObjectOutputStream(f)
    ) {
      s.writeObject(mat);
      System.out.println(fileName + " stored succesfully!");
    } catch (Exception ex) {
      System.out.println(ex.toString());
    } finally {

    }
  }

  public void storeNetInFile(ArrayList<String> list) {
    try(
      FileOutputStream f = new FileOutputStream("src/main/resources/calculated/result/net.txt");
      ObjectOutput s = new ObjectOutputStream(f)
    ) {
      s.writeObject(list);
      System.out.println("net stored succesfully!");
    } catch (Exception ex) {
      System.out.println(ex.toString());
    } finally {

    }
  }

  public ArrayList<String>  readNetFromFile() {
    try(FileInputStream in = new FileInputStream("src/main/resources/calculated/result/net.txt");
        ObjectInputStream s = new ObjectInputStream(in)) {
      ArrayList<String>  list = (ArrayList<String> )s.readObject();
      return list;
    } catch (Exception ex) {
      System.out.println(ex.toString());
    } finally {

    }
    return null;
  }

  public void createCalculatedDirectories() {
    new File("src/main/resources/calculated").mkdir();
    new File("src/main/resources/calculated/mean").mkdir();
    new File("src/main/resources/calculated/mean/self").mkdir();
    new File("src/main/resources/calculated/mean/others").mkdir();
    new File("src/main/resources/calculated/result").mkdir();
    new File("src/main/resources/calculated/variance").mkdir();
    new File("src/main/resources/calculated/distance").mkdir();
  }

  public ArrayList<Double[][]> readMatsByPerson(String person) {
    ArrayList<Double[][]> varianceMats = new ArrayList<>();

    for (int i = 1; i<= CONFIG.NUMBER_OF_IMAGES; i++) {
      Double[][] mat = this.readMatFromFile("variance/" + person + i + ".txt");
      varianceMats.add(mat);
    }

    return varianceMats;
  }

  public Double[][] readMatFromFile(String fileName) {
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
