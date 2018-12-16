import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import us.jcole.opencv.GoldMineralLocator;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ImageLocatorTest {
    final private static String VIDEO_WINDOW = "Video Window";

    private static String testImagesPath = "testImages";
    private static String testImagesAnnotatedPath = "testImagesAnnotated";

    private static Size IMAGE_SIZE = new Size(864, 480);

    private static Scalar BLACK = new Scalar(0, 0, 0);
    private static Scalar WHITE = new Scalar(255, 255, 255);
    private static Scalar GREEN = new Scalar(0, 255, 0);
    private static Scalar RED = new Scalar(255, 0, 0);

    private static void centeredLabel(Mat image, String label, Scalar color, int lineNumber) {
        final int paddingSize = 6;
        final int fontFace = 0;
        final double fontScale = 0.6;

        Size labelSize = Imgproc.getTextSize(label, fontFace, fontScale, 1, null);
        Imgproc.putText(image, label,
                new Point(image.width()/2 - labelSize.width/2 - 2,
                        labelSize.height * (lineNumber+1) + paddingSize * (lineNumber) + 3 - 1),
                fontFace, fontScale, BLACK, 1);
        Imgproc.putText(image, label,
                new Point(image.width()/2 - labelSize.width/2,
                        labelSize.height * (lineNumber+1) + paddingSize * (lineNumber) + 3),
                fontFace, fontScale, color, 1);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        File[] imageFiles = new File(testImagesPath).listFiles(pathname -> pathname.getName().endsWith(".jpg"));
        Arrays.sort(imageFiles);

        GoldMineralLocator goldMineralLocator = new GoldMineralLocator();
        goldMineralLocator.setSearchArea(new Rect(
                new Point((IMAGE_SIZE.width-1) * 0.0,
                        (IMAGE_SIZE.height-1) * 0.1),
                new Point((IMAGE_SIZE.width-1) * 1.0,
                        (IMAGE_SIZE.height-1) * 0.4)));

        System.out.printf("Testing %d images from %s...\n\n",
                imageFiles.length,
                testImagesPath);

        System.out.printf("%-40s%-10s%-10s%-10s\n", "Image", "Expected", "Found", "Result");

        int testCountTotal=0, testCountPassed=0;
        for (File imageFile : imageFiles) {
            testCountTotal++;

            String[] imageFileParts = imageFile.getName().split("\\.")[0].split("_");
            String imageFileMineralLocation = imageFileParts[imageFileParts.length-1].toUpperCase();
            GoldMineralLocator.MineralPosition expectedGoldMineralLocation =
                    GoldMineralLocator.MineralPosition.valueOf(imageFileMineralLocation);

            Mat rgbaImage = new Mat();
            Imgproc.resize(Imgcodecs.imread(imageFile.getAbsolutePath()), rgbaImage, IMAGE_SIZE);
            Imgproc.cvtColor(rgbaImage, rgbaImage, Imgproc.COLOR_BGR2RGBA, 4);

            Mat grayImage = new Mat();
            Imgproc.cvtColor(rgbaImage, grayImage, Imgproc.COLOR_RGBA2GRAY);

            goldMineralLocator.locate(rgbaImage, grayImage);

            boolean testPassed = expectedGoldMineralLocation == goldMineralLocator.getCurrentGoldMineralPosition();

            System.out.printf("%-40s%-10s%-10s%-10s\n",
                    imageFile.getName(),
                    expectedGoldMineralLocation,
                    goldMineralLocator.getCurrentGoldMineralPosition(),
                    testPassed ? "PASS" : "FAIL");

            if (testPassed)
                testCountPassed++;

                Mat annotatedImage = goldMineralLocator.getAnnotatedImage();
                centeredLabel(annotatedImage, imageFile.getName(), WHITE, 0);
                centeredLabel(annotatedImage,
                        String.format(Locale.US, "Expected: %s, Found: %s",
                                expectedGoldMineralLocation,
                                goldMineralLocator.getCurrentGoldMineralPosition()),
                        WHITE, 1);
                centeredLabel(annotatedImage,
                        "Result: " + (testPassed ? "PASS" : "FAIL"),
                        testPassed ? GREEN : RED, 12);

            Mat bgrImage = new Mat();
            Imgproc.cvtColor(annotatedImage, bgrImage, Imgproc.COLOR_RGBA2BGR, 3);

            if (true || !testPassed) {
                String annotatedImageFile = testImagesAnnotatedPath + "/" + imageFile.getName();
                Imgcodecs.imwrite(annotatedImageFile, bgrImage);
            }

            if (false) {
                HighGui.imshow(VIDEO_WINDOW, bgrImage);
                HighGui.waitKey(0);
            }
        }

        System.out.println();
        System.out.printf("Test complete, %d/%d (%.2f%%) passed.\n",
                testCountPassed, testCountTotal,
                100.0 * ((double)testCountPassed / (double)testCountTotal));

        System.exit(0);
    }
}
