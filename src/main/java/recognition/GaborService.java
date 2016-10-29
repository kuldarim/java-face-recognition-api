package recognition;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GaborService {
  /**
   * Algorithm explained
   *
   * 1. calculated gabor samples for each image [Get 40 matrices]
   * 2. calculate variance for each pixel in that matrix. [Get a matrix]
   * 3. repeat same calculation for each person [person number * matrix]
   * 4. calculate mean for each pixel for 3rd step list of matrixes
   */

  public Mat createGaborVarianceMatForImage(Mat image) {
    ArrayList<Mat> gabors = this.calculateGarborMats(image);

    Mat temp = gabors.get(0);
    int cols = temp.cols();
    int rows = temp.rows();

    Mat varianceMat = new Mat(cols, rows, temp.type());


    for (int col = 0; col < cols; col++) {

      for (int row = 0; row < rows; row++) {
        ArrayList<Double> pixels = new ArrayList<>();

        for (Mat gabor: gabors) {
          Double pixelValue = gabor.get(row, col)[0];
          pixels.add(pixelValue);
        }

        Double variance = this.variance(pixels);

        varianceMat.put(row, col, variance);
      }

    }

    return varianceMat;

  }

  public Mat calculateGaborMat(Mat image) {
    //predefine parameters for Gabor kernel
    List<Double> thetas = Arrays.asList(0.0, 23.0, 45.0, 68.0, 90.0, 113.0, 135.0, 158.0);
    List<Double> lambdas = Arrays.asList(3.0, 6.0, 13.0, 28.0, 58.0);
    Size kSize = new Size(5, 5);

    double sigma = 20;
    double gamma = 0.5;
    double psi = 0;

    /**
     * parameters:
     * ksize – Size of the filter returned.
     * sigma – Standard deviation of the gaussian envelope.
     * theta – Orientation of the normal to the parallel stripes of a Gabor function.
     * lambd – Wavelength of the sinusoidal factor.
     * gamma – Spatial aspect ratio.
     * psi – Phase offset.
     * ktype – Type of filter coefficients. It can be CV_32F or CV_64F .
     */
    // the filters kernel
    Mat kernel = Imgproc.getGaborKernel(kSize, sigma, thetas.get(1), lambdas.get(1), gamma, psi, CvType.CV_32F);
    // apply filters on my image. The result is stored in gabor
    Mat gabor = new Mat(image.width(), image.height(), CvType.CV_8UC1);
    Imgproc.filter2D(image, gabor, -1, kernel);

    return gabor;
  }

  public ArrayList<Mat> calculateGarborMats(Mat image) {

    ArrayList<Mat> gabors = new ArrayList<Mat>();

    //predefine parameters for Gabor kernel
    //TODO check if these params are really good
    List<Double> thetas = Arrays.asList(0.0, 23.0, 45.0, 68.0, 90.0, 113.0, 135.0, 158.0);
    List<Double> lambdas = Arrays.asList(3.0, 6.0, 13.0, 28.0, 58.0);
    Size kSize = new Size(5, 5);

    double sigma = 20;
    double gamma = 0.5;
    double psi =  0;

    for (Double theta: thetas) {
        for (Double lambda: lambdas) {
          /**
           * parameters:
           * ksize – Size of the filter returned.
           * sigma – Standard deviation of the gaussian envelope.
           * theta – Orientation of the normal to the parallel stripes of a Gabor function.
           * lambd – Wavelength of the sinusoidal factor.
           * gamma – Spatial aspect ratio.
           * psi – Phase offset.
           * ktype – Type of filter coefficients. It can be CV_32F or CV_64F .
           */
            // the filters kernel
            Mat kernel = Imgproc.getGaborKernel(kSize, sigma, theta, lambda, gamma, psi, CvType.CV_32F);
            // apply filters on my image. The result is stored in gabor
            Mat gabor = new Mat (image.width(), image.height(), CvType.CV_8UC1);
            Imgproc.filter2D(image, gabor, -1, kernel);

            gabors.add(gabor);
        }
    }

    return gabors;
  }

  /**
   * info http://www.mathsisfun.com/data/standard-deviation.html
   */
  private Double standartDeviation(ArrayList<Double> list) {
    Double variance = this.variance(list);
    return Math.sqrt(variance);
  }

  private Double variance(ArrayList<Double> list) {
    Double mean = this.mean(list);
    Double variance = 0.0;

    for(Double d : list){
      variance = variance + Math.pow(mean - d, 2);
    }

    return variance / list.size();
  }

  private Double mean(ArrayList<Double> list) {
    Double sum = 0.0;
    for(Double d : list){
      sum = sum + d;
    }
    return sum / list.size();
  }
}
