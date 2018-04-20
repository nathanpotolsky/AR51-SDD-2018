// Comment out stdafx.h if not using Visual Studio
#include "stdafx.h"
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
void resizeImage(Mat& image_, int maxDimension) {
	if (image_.size().width > maxDimension) {
		resize(image_, image_, Size(maxDimension, maxDimension * image_.size().height / image_.size().width));
	}
	else if (image_.size().height > maxDimension) {
		resize(image_, image_, Size(maxDimension * image_.size().width / image_.size().height, maxDimension));
	}
}

// Preprocessing image variable
// returns the processed ratio
float preprocessing(Mat& src, float desiredHeight) {
	Mat intermediate;
	cvtColor(src, src, CV_BGR2GRAY);
	float ratio = src.size().height / desiredHeight;

	resize(src, intermediate, cv::Size(), 1 / ratio, 1 / ratio);
	int d = 8;	double sigmaColor = 17, sigmaSpace = 17;
	bilateralFilter(intermediate, src, d, sigmaColor, sigmaSpace);
	return ratio;
}

void detectContours(Mat& src, vector<vector<Point> >& contours, vector<Vec4i>& hierarchy) {
	int lower, upper;
	autoCanny(src, 0.33, lower, upper);
	Canny(src, src, lower, upper);
	findContours(src, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);
}
vector<Point> findLargestQuadralateral(vector<vector<Point> > &contours) {
	int maxContourIndex = -1;
	double maxArea = 200.0;
	vector<Point> boardApprox, approx;
	for (int i = 0; i < contours.size(); ++i) {
		double area = contourArea(contours[i]);
		approxPolyDP(contours[i], approx, arcLength(Mat(contours[i]), true) * 0.05, true);
		if (approx.size() == 4 && area > maxArea) {
			maxArea = area;
			maxContourIndex = i;
			boardApprox = approx;
		}
	}
	return boardApprox;
}

void orderVertices(vector<Point>& boardApprox, array<Point2f, 4>& rect, array<Point2f, 4>& dstRect, float ratio) {
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
	dstRect = { Point(0,0), Point(maxWidth - 1,0), Point(maxWidth - 1,maxHeight - 1),Point(0,maxHeight - 1) };

}

void perspectiveTransform(Mat& input, Mat& dst, array<Point2f, 4>& rect, array<Point2f, 4>& dstRect) {
	Mat transformMatrix = getPerspectiveTransform(rect, dstRect);

	int width = dstRect[1].x - dstRect[0].x + 1;
	int height = dstRect[3].y - dstRect[0].y + 1;
	// perspective transform(rect, dstrect, processedimage, warp, warpcolored)
	Size size(width, height);
	warpPerspective(input, dst, transformMatrix, size);
}

vector<vector<Point> > pointDetection(Mat& warp, int& boardEdgesLower, int& boardEdgesUpper) {
	Mat hMask = Mat::zeros(warp.size(), CV_8U);
	Mat vMask = Mat::zeros(warp.size(), CV_8U);

	Mat boardEdges;
	vector<vector<Point> > pointsSorted;
	vector<Vec2f> lines;
	autoCanny(warp, 0.33, boardEdgesLower, boardEdgesUpper);
	Canny(warp, boardEdges, boardEdgesLower, boardEdgesUpper);
	float PI = 3.1415926;
	HoughLines(boardEdges, lines, 1, PI / 180, 200);
	if (lines.size() <= 1) {
		cerr << "not enough lines detected" << endl;
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
		double x0 = a * rho, y0 = b * rho;
		pt1.x = cvRound(x0 + 1000 * (-b));
		pt1.y = cvRound(y0 + 1000 * (a));
		pt2.x = cvRound(x0 - 1000 * (-b));
		pt2.y = cvRound(y0 - 1000 * (a));

		int thetaDeg = int(theta * 180 / PI);
		float tol = 5;
		if (-tol < thetaDeg % 180 && thetaDeg % 180 < tol) {

			if (abs(rho - prevVDist) > minDist) {
				pt2.x = (pt1.x + pt2.x) / 2;
				pt1.x = pt2.x;

				line(vMask, pt1, pt2, Scalar(255), 1);
				prevVDist = rho;
				numCols += 1;
			}
		}
		else if (90 - tol < thetaDeg % 180 && thetaDeg % 180 < 90 + tol) {
			if (abs(rho - prevHDist) > minDist) {
				pt2.y = (pt1.y + pt2.y) / 2;
				pt1.y = pt2.y;
				line(hMask, pt1, pt2, Scalar(255), 1);
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

void sharpenImage(Mat& src, Mat& dst, double sigma, double threshold, double amount) {
	Mat blurred;
	GaussianBlur(src, blurred, Size(), sigma, sigma);
	Mat lowContrastMask = abs(src - blurred) < threshold;
	dst = src * (1 + amount) + blurred * (-amount);
	src.copyTo(dst, lowContrastMask);
}

Scalar determineTileColor(Mat& src, Vec3f circle) {
	Point2f center(cvRound(circle[0]), cvRound(circle[0]));
	int radius = cvRound(circle[2]);
	int width = radius / sqrt(2);
	int checkerX = center.x - width, checkerY = center.y - width;
	Rect insideChecker = Rect(checkerX, checkerY, width, width);
	return mean(src(insideChecker));
}

void tileDetection(Mat& warp, Mat& warpColored, vector<vector<Point> > pointsSorted,
	Mat*& team1, Mat*& team2, const vector<Scalar>& colors, vector<vector<int> >& Board, int& boardEdgesUpper) {
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
			if ((float(w) / h > 1.4) || (float(h) / w > 1.4) || (float(w) / h <= 0) || (w * h < area / 200)) continue;
			float buffer = 0.05;
			int roiX = int(x + buffer * w);
			int roiY = int(y + buffer * h);
			Rect roi = Rect(roiX, roiY, int((1 - 2 * buffer) * w), int((1 - 2 * buffer) * h));
			Mat tile = warp(roi);
			Mat coloredTile = warpColored(roi);

			// Circle Detection Begins
			vector<Vec3f> circles;
			Mat sharpened;
			double sigma = 1, threshold = 5, amount = 1;
			sharpenImage(tile, sharpened, sigma, threshold, amount);
			int dp = 1, minDist = w, cannyThresh = boardEdgesUpper, accumulator = 30, minRadius = w / 4, maxRadius = 0.5*w;
			HoughCircles(sharpened, circles, CV_HOUGH_GRADIENT, dp, minDist, cannyThresh, accumulator, minRadius, maxRadius);

			if (circles.size() == 0) tempRow.push_back(0);
			for (int i = 0; i < circles.size(); ++i) {
				// Color detection begins				
				float minColorDist = FLT_MAX;
				int minIndex = -1;
				Point2f middle = Point2f(x + w / 2, y + h / 2);

				Point2f center(cvRound(circles[i][0]), cvRound(circles[i][0]));
				int distCenter = int(euclideanDistance(center, middle));
				Mat* tileCopy = new Mat();
				
				Scalar tileColor = determineTileColor(coloredTile, circles[i]);
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
							coloredTile.copyTo(*team1);
							foundT1 = true;
						}
					}
					else {
						if (distCenter < minDistCenterT2) {
							minDistCenterT2 = distCenter;
							coloredTile.copyTo(*team2);
							foundT2 = true;
						}

					}
				}
				tempRow.push_back(minIndex + 1);
			}
		}
		if (tempRow.size() > 0) { Board.push_back(tempRow); }
	}
}


// Takes in Mat pointers and returns by reference pointers to :image of the checkerboard, image of team 1 and team 2 pieces
// Also returns the Board state by reference, where 0: no piece, 1: colors[0] piece, 2: colors[1] piece
int checker(Mat* imageRef, Mat *&normalized, Mat *&team1, Mat *&team2, const vector<Scalar>& colors, vector<vector<int> >& Board) {
	// dereference the image and check to see if it is a valid image in memory
	Mat image = *imageRef;
	if (!image.data) { return 0; }
	
	// Shrink the image if width or height > maxDimension
	int maxDimension = 1088;
	resizeImage(image, maxDimension);

	Mat orig = image.clone();

	// Apply grayscale conversion and noise filtering and steps image size down to 800px
	float desiredHeight = 800.0;
	float ratio = preprocessing(image, desiredHeight);

	// Contour Detection
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	detectContours(image, contours, hierarchy);


	// Contour sorting
	vector<Point> boardApprox = findLargestQuadralateral(contours);
	int imageArea = image.size().height * image.size().width;
	if (contourArea(boardApprox) / imageArea < 0.3) { return -1; }
	
	// Perspective Transform
	array<Point2f, 4> rect, dstRect;
	orderVertices(boardApprox, rect, dstRect, ratio);
	Mat warp, warpColored, gray;
	cvtColor(orig, gray, CV_BGR2GRAY);
	perspectiveTransform(orig, warpColored, rect, dstRect);
	perspectiveTransform(gray, warp, rect, dstRect);
	warpColored.copyTo(*normalized);

	// Point detection
	int boardEdgesLower, boardEdgesUpper;
	vector<vector<Point> > pointsSorted = pointDetection(warp, boardEdgesLower, boardEdgesUpper);
	if (!pointsSorted.size()) return 0;

	// Tile detection begins
	tileDetection(warp, warpColored, pointsSorted, team1, team2, colors, Board, boardEdgesUpper);

	return 1;
}



int main() {
	// Test cases

	vector<vector<int> > Board;
	vector<Scalar> colors1;
	colors1.push_back(Scalar(0, 0, 255));
	colors1.push_back(Scalar(0, 0, 0));
	Mat image = imread("TestImages/ex1.jpeg");
	Mat* imageRef = &image;
	Mat* team1 = new Mat();
	Mat* team2 = new Mat();
	Mat* normalized = new Mat();
	int ret = checker(imageRef, normalized, team1, team2, colors1, Board);
	cout << "return value: " << ret << endl;
	//imshow("Team 1", *team1);
	//imshow("Team 2", *team2);
	//imshow("normalized", *normalized);
	if (ret) printBoard(Board);
}
