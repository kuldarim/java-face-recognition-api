package picture;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GaborService {

    public Mat calculateGaborMat(Mat image) {
        //predefine parameters for Gabor kernel
        List<Double> thetas = Arrays.asList(0.0, 23.0, 45.0, 68.0, 90.0, 113.0, 135.0, 158.0);
        List<Double> lambdas = Arrays.asList(3.0, 6.0, 13.0, 28.0, 58.0);
        Size kSize = new Size(5, 5);

        double sigma = 20;
        double gamma = 0.5;
        double psi =  0;

        /*
        parameters:
            ksize – Size of the filter returned.
            sigma – Standard deviation of the gaussian envelope.
            theta – Orientation of the normal to the parallel stripes of a Gabor function.
            lambd – Wavelength of the sinusoidal factor.
            gamma – Spatial aspect ratio.
            psi – Phase offset.
            ktype – Type of filter coefficients. It can be CV_32F or CV_64F .
        */
        // the filters kernel
        Mat kernel = Imgproc.getGaborKernel(kSize, sigma, thetas.get(1), lambdas.get(1), gamma, psi, CvType.CV_32F);
        // apply filters on my image. The result is stored in gabor
        Mat gabor = new Mat (image.width(), image.height(), CvType.CV_8UC1);
        Imgproc.filter2D(image, gabor, -1, kernel);

        return gabor;
    }
    //TODO implement this to return a list of Mats
//    public ArrayList<Mat> createGaborMats(Mat image) {
//        ArrayList<Mat> gabors = new ArrayList<Mat>();
//
//        //predefine parameters for Gabor kernel
//        List<Double> thetas = Arrays.asList(0.0, 23.0, 45.0, 68.0, 90.0, 113.0, 135.0, 158.0);
//        List<Double> lambdas = Arrays.asList(3.0, 6.0, 13.0, 28.0, 58.0);
//        Size kSize = new Size(5, 5);
//
//        double sigma = 20;
//        double gamma = 0.5;
//        double psi =  0;
//
//        for (Double theta: thetas) {
//            for (Double lambda: lambdas) {
//                    /*
//                    ers:
//                        ksize – Size of the filter returned.
//                        sigma – Standard deviation of the gaussian envelope.
//                        theta – Orientation of the normal to the parallel stripes of a Gabor function.
//                        lambd – Wavelength of the sinusoidal factor.
//                        gamma – Spatial aspect ratio.
//                        psi – Phase offset.
//                        ktype – Type of filter coefficients. It can be CV_32F or CV_64F .
//                     */
//                // the filters kernel
//                Mat kernel = Imgproc.getGaborKernel(kSize, sigma, theta, lambda, gamma, psi, CvType.CV_32F);
//                // apply filters on my image. The result is stored in gabor
//                Mat gabor = new Mat (image.width(), image.height(), CvType.CV_8UC1);
//                Imgproc.filter2D(image, gabor, -1, kernel);
//
//                gabors.add(gabor);
//            }
//        }
//
//        return gabors;
//    }
}
