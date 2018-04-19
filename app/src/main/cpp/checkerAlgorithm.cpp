#include <jni.h>



#include <time.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <string>

using namespace cv;
using namespace std;

bool compareLines(const Vec2f & line1, const Vec2f & line2) {
    return line1[0] < line2[0];
}

bool comparePointsX(const Point& pt1, const Point& pt2) {
    return pt1.x < pt2.x;
}

bool comparePointsY(const Point& pt1, const Point& pt2) {
    return pt1.y < pt2.y;
}

string type2str(int type) {
    string r;

    uchar depth = type & CV_MAT_DEPTH_MASK;
    uchar chans = 1 + (type >> CV_CN_SHIFT);

    switch (depth) {
        case CV_8U:  r = "8U"; break;
        case CV_8S:  r = "8S"; break;
        case CV_16U: r = "16U"; break;
        case CV_16S: r = "16S"; break;
        case CV_32S: r = "32S"; break;
        case CV_32F: r = "32F"; break;
        case CV_64F: r = "64F"; break;
        default:     r = "User"; break;
    }

    r += "C";
    r += (chans + '0');

    return r;
}

/* Helper function to find median */
double _median(Mat channel) {
    double m = (channel.rows*channel.cols) / 2;
    int bin = 0;
    double med = -1.0;

    int histSize = 256;
    float range[] = { 0, 256 };
    const float* histRange = { range };
    bool uniform = true;
    bool accumulate = false;
    cv::Mat hist;
    cv::calcHist(&channel, 1, 0, cv::Mat(), hist, 1, &histSize, &histRange, uniform, accumulate);

    for (int i = 0; i < histSize && med < 0.0; ++i)
    {
        bin += cvRound(hist.at< float >(i));
        if (bin > m && med < 0.0)
            med = i;
    }

    return med;
}

/* Print board function */
void printBoard(const vector<vector<int> >& Board) {
    cout << string(Board[0].size() * 4 + 1, '-') << endl;
    for (int row = 0; row < Board.size(); ++row) {
        cout << "| ";
        for (int col = 0; col < Board[row].size(); ++col) {
            cout << Board[row][col] << " | ";
        }
        cout << endl;
        // int length = Board[row].size() * 4;
        // string filler = string(Board[row].size() * 4 + 1, '-');
        cout << string(Board[row].size() * 4 + 1, '-') << endl;
    }
}

void autoCanny(Mat& image, float sigma, int& lower, int& upper) {
    double med = _median(image);
    lower = int(max(0.0, (1.0 - sigma) * med));
    upper = int(min(255.0, (1.0 + sigma) * med));
}

double euclideanDistance(Point2f& a, Point2f& b) {
    Point diff = a - b;
    return sqrt(pow(diff.x, 2) + pow(diff.y, 2));
}

// Resize the image if it's too large. Processing will take too long and don't need that much resolution
void resizeImage(Mat& image_) {
    int maxDimension = 1088;
    // Resize the image if it's too large. Processing will take too long and don't need that much resolution
    if (image_.size().width > maxDimension) {
        resize(image_, image_, Size(maxDimension, maxDimension * image_.size().height / image_.size().width));
    } else if(image_.size().height > maxDimension) {
        resize(image_, image_, Size(maxDimension * image_.size().width / image_.size().height, maxDimension));
    }
}

// Preprocessing image variable
// returns the processed ratio
float preprocessing(Mat& processedImage, Mat& gray, Mat& image) {
    cvtColor(image, processedImage, CV_BGR2GRAY);
    int d = 8;
    double sigmaColor = 17, sigmaSpace = 17;
    float desiredHeight = 800.0;
    float ratio = image.size().height / desiredHeight;

    Mat intermediate;
    resize(processedImage, intermediate, cv::Size(), 1 / ratio, 1 / ratio);
    bilateralFilter(intermediate, image, d, sigmaColor, sigmaSpace);
    return ratio;
}

int contourSorting(Mat& image, Mat& contourImage, Mat*& warpedImage,
                   vector<vector<Point> > &contours, vector<Point> &approx, vector<Point> &boardApprox) {
    int maxContourIndex = -1;
    double maxArea = 200.0;
    for (int i = 0; i < contours.size(); ++i) {
        double area = contourArea(contours[i]);
        approxPolyDP(contours[i], approx, arcLength(Mat(contours[i]), true) * 0.05, true);
        if (approx.size() == 4 && area > maxArea) {
            maxArea = area;
            maxContourIndex = i;
            boardApprox = approx;
        }
    }
    if (maxContourIndex == -1) cerr << "Can't find any square contours" << endl;
    int thickness = 4;
    Scalar color = (0, 255, 255);
    if (maxArea / (image.size().height * image.size().width) < 0.3) {
        cout << "maxArea too small" << endl;
        drawContours(contourImage, contours, maxContourIndex, Scalar(0, 255, 0), thickness);
        contourImage.copyTo(*warpedImage);
        return -1;
    }
}


Mat* perspectiveTransform(Mat& warp, Mat& warpColored, Mat& processedImage, Mat& orig, vector<Point> &boardApprox, float ratio) {
    array<Point2f,4> rect;
    int minSum = 10000, maxSum = -1;
    for (int i = 0; i < boardApprox.size(); ++i) {
        int sum = boardApprox[i].x + boardApprox[i].y;
        if (sum < minSum) {
            minSum = sum;
            rect[0] = Point(boardApprox[i].x*ratio, boardApprox[i].y*ratio);
        }
        if (sum > maxSum) {
            maxSum = sum;
            rect[2] = Point(boardApprox[i].x*ratio, boardApprox[i].y*ratio);
        }
    }

    minSum = 10000, maxSum = -1;
    for (int i = 0; i < boardApprox.size(); ++i) {
        int diff = boardApprox[i].y - boardApprox[i].x;
        if (diff < minSum) {
            minSum = diff;
            rect[1] = Point(boardApprox[i].x*ratio, boardApprox[i].y*ratio);
        }
        if (diff > maxSum) {
            maxSum = diff;
            rect[3] = Point(boardApprox[i].x*ratio, boardApprox[i].y*ratio);
        }
    }

    int topWidth, botWidth, leftHeight, rightHeight;
    topWidth = euclideanDistance(rect[0], rect[1]);
    botWidth = euclideanDistance(rect[2], rect[3]);
    int maxWidth = int(max(topWidth, botWidth));
    leftHeight = euclideanDistance(rect[0], rect[3]);
    rightHeight = euclideanDistance(rect[1], rect[2]);
    int maxHeight = int(max(leftHeight, rightHeight));

    array<Point2f, 4> dstRect = { Point(0,0), Point(maxWidth - 1,0), Point(maxWidth - 1,maxHeight - 1),Point(0,maxHeight - 1) };

    Mat transformMatrix = getPerspectiveTransform(rect, dstRect);
    Size size(maxWidth, maxHeight);
    warpPerspective(processedImage, warp, transformMatrix, size);
    warpPerspective(orig, warpColored, transformMatrix, size);
    Mat* normalized = new Mat();
    *normalized = warpColored.clone();
    return normalized;
}

vector<vector<Point> > pointDection(Mat& warpColored, Mat& warp, Mat*& warpedImage, int lower, int upper){
    Mat hMask = Mat::zeros(warp.size(), CV_8U);
    Mat vMask = Mat::zeros(warp.size(), CV_8U);

    Mat boardEdges;
    vector<vector<Point> > pointsSorted;
    vector<Vec2f> lines;
    int boardEdgesLower, boardEdgesUpper;
    autoCanny(warp, 0.33, boardEdgesLower, boardEdgesUpper);
    Canny(warp, boardEdges, boardEdgesLower, boardEdgesUpper);
    float PI = 3.1415926;
    HoughLines(boardEdges, lines, 1, PI / 180, 200);
    if (lines.size() <= 1) {
        warpColored.copyTo(*warpedImage);
        return pointsSorted;
    }
    // sort lines by rho
    sort(lines.begin(), lines.end(), compareLines);
    int minDist = 20;
    int prevVDist = -1000, prevHDist = -1000;
    int numCols = 0;
    for (int i = 0; i < lines.size(); ++i) {
        float rho = lines[i][0], theta = lines[i][1];
        Point pt1, pt2;
        double a = cos(theta), b = sin(theta);
        double x0 = a*rho, y0 = b*rho;
        pt1.x = cvRound(x0 + 1000*(-b));
        pt1.y = cvRound(y0 + 1000*(a));
        pt2.x = cvRound(x0 - 1000*(-b));
        pt2.y = cvRound(y0 - 1000*(a));

        int thetaDeg = int(theta * 180 / PI);
        float tol = 5;
        if (-tol < thetaDeg  % 180 && thetaDeg % 180 < tol) {

            if (abs(rho - prevVDist) > minDist) {
                pt2.x = (pt1.x + pt2.x) / 2;
                pt1.x = pt2.x;

                line(vMask , pt1, pt2, Scalar(255), 1);
                prevVDist = rho;
                numCols += 1;
            }
        }
        else if (90 - tol < thetaDeg % 180 && thetaDeg % 180 < 90 + tol) {
            if (abs(rho - prevHDist) > minDist) {
                pt2.y = (pt1.y + pt2.y) / 2;
                pt1.y = pt2.y;
                line(hMask , pt1, pt2, Scalar(255), 1);
                prevHDist = rho;
            }
        }
    }
    Mat intersectionMask;
    bitwise_and(hMask, vMask, intersectionMask);
    vector<Point> points;
    waitKey(0);

    findNonZero(intersectionMask, points);
    vector<Point>::const_iterator rowBegin = points.begin(), rowEnd = rowBegin + numCols;
    for (int i = 0; i < points.size() / numCols; ++i) {
        rowBegin = points.begin() + i * numCols;
        rowEnd = rowBegin + numCols;
        vector<Point> temp(rowBegin, rowEnd);
        pointsSorted.push_back(temp);
    }
    return pointsSorted;
}

void tileDetection(Mat& warp, Mat& warpColored,vector<vector<Point> > pointsSorted,
                   Mat*& team1, Mat*& team2, int lower, int upper,
                    const vector<Scalar>& colors, vector<vector<int> >& Board){
    int area = warp.size().height * warp.size().width;
    int minDistCenterT1 = warp.size().width;
    int minDistCenterT2 = warp.size().width;

    bool foundT1 = false;
    bool foundT2 = false;
    for (int row = 0; row < (pointsSorted.size() - 1); ++row) {
        sort(pointsSorted[row].begin(), pointsSorted[row].end(), comparePointsX);
        vector<int> tempRow;

        for (int col = 0; col < (pointsSorted[row].size() - 1); ++col) {
            int x, y, w, h;
            x = pointsSorted[row][col].x, y = pointsSorted[row][col].y;
            w = pointsSorted[row + 1][col + 1].x - x, h = pointsSorted[row + 1][col + 1].y - y;
            if ((float(w) / h > 1.4) || (float(h) / w > 1.4) || (float(w)/h <= 0) || (w * h < area / 200)) continue;
            float buffer = 0.05;
            int roiX = int(x + buffer * w);
            int roiY = int(y + buffer * h);
            Rect roi = Rect(roiX, roiY, int((1 - 2 * buffer) * w), int((1 - 2 * buffer) * h));
            Mat tile = warp(roi);
            Mat coloredTile = warpColored(roi);

            // Circle Detection Begins
            vector<Vec3f> circles;
            Mat blurred; double sigma = 1, threshold = 5, amount = 1;
            GaussianBlur(tile, blurred, Size(), sigma, sigma);
            Mat lowContrastMask = abs(tile - blurred) < threshold;
            Mat sharpened = tile*(1+amount) + blurred*(-amount);
            tile.copyTo(sharpened, lowContrastMask);
            autoCanny(sharpened, 0.5, lower, upper);
            int dp = 1, minDist = w, cannyThresh = boardEdgesUpper, accumulator=30, minRadius=w/4, maxRadius=0.5*w;
            HoughCircles(sharpened, circles, CV_HOUGH_GRADIENT, dp, minDist, cannyThresh, accumulator, minRadius, maxRadius);

            if (circles.size() == 0) tempRow.push_back(0);
            for (int i = 0; i < circles.size(); ++i) {
                Point2f center(cvRound(circles[i][0]), cvRound(circles[i][0]));
                int radius = cvRound(circles[i][2]);
                circle(tile, center, radius, Scalar(255), 2);
                Point centerShifted(x + center.x, y + center.y);
                int width = radius / sqrt(2);
                int checkerX = center.x - width, checkerY = center.y - width;
                Rect insideChecker = Rect(checkerX, checkerY, width, width);
                Scalar tileColor = mean(coloredTile(insideChecker));
                float minColorDist = FLT_MAX;
                int minIndex = -1;
                Point2f middle = Point2f(x + w/2, y + h/2);
                int distCenter = int(euclideanDistance(center, middle));
                Mat* tileCopy = new Mat();
                // *tileCopy = coloredTile.clone();
                for (int c = 0; c < colors.size(); ++c) {
                    float colorDistance = cv::norm(tileColor, colors[c]);
                    if (colorDistance < minColorDist) {
                        minColorDist = colorDistance;
                        minIndex = c;
                    }
                }
                if (minIndex > -1) {
                    if (minIndex == 0) {
                        if (distCenter < minDistCenterT1) {
                            minDistCenterT1 = distCenter;
                            //team1 = tileCopy;
                            coloredTile.copyTo(*team1);
                            foundT1 = true;

                        }
                    } else {
                        if (distCenter < minDistCenterT2) {
                            minDistCenterT2 = distCenter;
                            //team2 = tileCopy;
                            coloredTile.copyTo(*team2);
                            foundT2 = true;
                        }

                    }
                }
                tempRow.push_back(minIndex + 1);

                circle(warpColored, centerShifted, radius, tileColor, -1);
            }
        }
        if (tempRow.size() > 0) { Board.push_back(tempRow); }
    }
    waitKey(0);
}

    // imshow("checkerboard", processedImage);
    // imshow("Board edges", boardEdges);
    // imshow("intersection", intersectionMask);
    if (debug) {
        imshow("BoardGuess", warpColored);
        waitKey(0);
    }

    // Resizing image
    resizeImage(image_);

    // Preprocessing
    Mat image = image_;
    Mat orig = image.clone();
    Mat processedImage, gray;
    float ratio = preprocessing(processedImage, gray, image);

    // Contour Detection
    int lower = 0, upper = 0;
    autoCanny(image, 0.33, lower, upper);
    Canny(image, image, lower, upper);
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    findContours(image, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);

    // Contour sorting
    vector<Point> approx, boardApprox;
    Mat contourImage = orig.clone();
    resize(contourImage, contourImage, cv::Size(), 1 / ratio, 1 / ratio);
    if (contourSorting(image, contourImage, warpedImage, contours, approx, boardApprox) == -1) return 0;

    // Perspective Transform
    Mat warp, warpColored;
    warpedImage = perspectiveTransform(warp, warpColored, processedImage, orig, boardApprox, ratio);

    // Point detection
    vector<vector<Point> > pointsSorted = pointDection(warpColored, warp, warpedImage, lower, upper);
    if (!pointsSorted.size()) return 0;

    // Tile detection begins
    tileDetection(warp, warpColored, pointsSorted, team1, team2, lower, upper, colors, Board);

    return 1;
}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_nathan_myapplication_BoardDetectionActivity_convertPicture(JNIEnv *env, jobject, jlong mRaw, jlong mNormalized, jlong mTeam1, jlong mTeam2) {
    //convert long args to Mat pointers
    Mat* raw = (Mat*)mRaw;
    Mat* normalized = (Mat*)mNormalized;
    Mat* team1 = (Mat*)mTeam1;
    Mat* team2 = (Mat*)mTeam2;

    //get our board data
    vector<vector <int>> initBoard;
    vector<Scalar> colors;
    colors.push_back(Scalar(0, 0, 255));
    colors.push_back(Scalar(0, 0, 0));
    int arr = checker(raw, normalized, team1, team2, colors, initBoard);

    //initialize array lengths

    //Get int class
    jclass intClass = env->FindClass("[I");
    jobjectArray board = env->NewObjectArray((jsize) initBoard.size(), intClass, NULL);

    for (int i=0; i < initBoard.size(); i++) {
        int temp[initBoard[i].size()];
        for (int j = 0; j < initBoard[i].size(); j++) {
            temp[j] = initBoard[i][j];
        }
        jintArray intArray = env->NewIntArray(initBoard[i].size());
        env->SetIntArrayRegion(intArray, (jsize) 0, (jsize) initBoard[i].size(), (jint*) temp);
        env->SetObjectArrayElement(board, (jsize) i, intArray);
    }

    return board;
}

int main() {

}