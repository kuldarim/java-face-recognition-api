package hello;

import org.opencv.core.Core;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    static {
        //nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    @RequestMapping("/greeting")
    public String greeting() {
        return "Welcome to OpenCV " + Core.VERSION;
    }
}
