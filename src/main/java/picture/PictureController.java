package picture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PictureController {

    static {
        // Load OpenCv library
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    /**
     *
     * @return Base64 encoded returns Json representation of org.opencv.core.Mat
     */
    @RequestMapping("/grayscale")
    public String grayscale() {
        Mat exampleGreyscaleMat = Highgui.imread("src/main/resources/example.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);

        return matToJSON(exampleGreyscaleMat);
    }

    private String matToJSON(Mat mat) {
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
}
