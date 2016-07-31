package picture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

@Service
public class MatService {
  /**
   * TODO add decode method in node js side
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

}
