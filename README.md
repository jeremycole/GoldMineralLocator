# GoldMineralLocator

This is a Java class using OpenCV to locate blobs of yellow/gold colored objects in an area of interest within a scene, and determining whether the object is in the left, center, or right portion of the area of interest. Its primary purpose is to locate the Gold Mineral in [FIRST Tech Challenge](https://www.firstinspires.org/robotics/ftc) 2018-2019 season "Rover Ruckus".

When combined with [EnderCV](https://github.com/guineawheek/endercv) by creating an [OpenCVPipeline such as my GoldMineralPipeline](https://github.com/Team11574/TeamCode_2018/blob/master/src/main/java/us/jcole/opencvpipeline/GoldMineralPipeline.java) images can be fed in real time from the Android camera to this class. Team 11574's example [TestGoldMineralLocator OpMode](https://github.com/Team11574/TeamCode_2018/blob/master/src/main/java/us/ftcteam11574/teamcode2018/TestGoldMineralLocator.java) shows how it can be used from an OpMode.

# Gold Mineral Locating Strategy

The following strategy is used to locate the Gold Mineral:

1. Convert the RGBA image provided by the Android camera to HSV color space.
2. Mask out non-searched areas of the image.
3. Search for gold-colored pixels and create a mask of matching pixels.
4. Convert the blobs of matched pixels to polygon contours which surround them.
5. Find the maximum-area contour; call it the "selected contour".
6. Check if the selected contour's area is greater than the minimum area configured – if so, it is a match, determine which region of the area of interest it lies in (left, center, or right).
7. Produce bounding boxes for all contours greater than half of the configured minimum area, and store them as candidates (for more effective debugging later).
8. Produce an annotated image which can be used for real time display as well as debugging after the fact.

In our robot's implementation, we actually disable the OpenCVPipeline as soon as "Start" is pressed, and use the last image that the camera saw as the match started, so that OpenCV does not continue to run during our actual Autonomous driving. See our [AutonomousLandSampleClaim OpMode](https://github.com/Team11574/TeamCode_2018/blob/master/src/main/java/us/ftcteam11574/teamcode2018/AutonomousLandSampleClaim.java) for more information.
