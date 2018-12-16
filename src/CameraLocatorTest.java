import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import us.jcole.opencv.GoldMineralLocator;

public class CameraLocatorTest {
    final private static String VIDEO_WINDOW = "Video Window";

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture vc = new VideoCapture();
        vc.open(0);

        Mat cameraImage = new Mat();
        Mat rgbaImage = new Mat();
        Mat grayImage = new Mat();
        Mat bgrImage = new Mat();

        vc.read(cameraImage);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        GoldMineralLocator goldMineralLocator = new GoldMineralLocator();
        goldMineralLocator.setSearchArea(new Rect(
                new Point((cameraImage.width()-1) * 0.0,
                        (cameraImage.height()-1) * 0.3),
                new Point((cameraImage.width()-1) * 1.0,
                        (cameraImage.height()-1) * 0.7)));
        do {
            vc.read(cameraImage);
            Imgproc.cvtColor(cameraImage, rgbaImage, Imgproc.COLOR_BGR2RGBA, 4);
            Imgproc.cvtColor(cameraImage, grayImage, Imgproc.COLOR_BGR2GRAY);

            goldMineralLocator.locate(rgbaImage, grayImage);

            Imgproc.cvtColor(goldMineralLocator.getAnnotatedImage(), bgrImage, Imgproc.COLOR_RGBA2BGR, 3);
            HighGui.imshow(VIDEO_WINDOW, bgrImage);
        } while (HighGui.waitKey(5) != 'q');

        System.exit(0);
    }
}
