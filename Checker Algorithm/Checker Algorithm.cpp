﻿// Comment out stdafx.h if not using Visual Studio
#include "stdafx.h"
#include <time.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <string>

using namespace cv;
using namespace std;

 // Binary function sorting function
 // sorts line objects based on rho value
bool compareLines(const Vec2f & line1, const Vec2f & line2) {
	return line1[0] < line2[0];
}

 // Binary function sorting function
 // sorts Point objects based on x value
bool comparePointsX(const Point& pt1, const Point& pt2) {
	return pt1.x < pt2.x;
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
		cout << string(Board[row].size() * 4 + 1, '-') << endl;
	}
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


double median(Mat channel) {
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


// Zero parameter Canny threshold tuner
// determines Canny threshold values based on median image value
// Example:
//		int lower, upper;
//		autoCanny(image, lower, upper);
//		Canny(image, image, lower, upper);
void autoCanny(Mat& src, float sigma, int& lower, int& upper) {
	double med = median(src);
	lower = int(max(0.0, (1.0 - sigma) * med));
	upper = int(min(255.0, (1.0 + sigma) * med));
}

double euclideanDistance(Point2f& a, Point2f& b) {
	Point diff = a - b;
	return sqrt(pow(diff.x, 2) + pow(diff.y, 2));
}

// Resizes an image if dimensions exceed maxDimension
void resizeImage(Mat& src, int maxDimension) {
	if (src.size().width > maxDimension) {
		resize(src, src, Size(maxDimension, maxDimension * src.size().height / src.size().width));
	}
	else if (src.size().height > maxDimension) {
		resize(src, src, Size(maxDimension * src.size().width / src.size().height, maxDimension));
	}
}

// convert a Mat to grayscale, shrink image for faster processing, and apply bilateral filtering
// returns ratio of old image to shrunk image
float preprocessing(Mat& src, float desiredHeight) {

	// convert to single channel from 3 channel for faster processing
	cvtColor(src, src, CV_BGR2GRAY);

	float ratio = src.size().height / desiredHeight;
	Mat intermediate;
	resize(src, intermediate, cv::Size(), 1 / ratio, 1 / ratio);

	// Noise removal
	// a small neighborhood and sigma values chosen to maintain edges as much as possible
	int d = 8;	double sigmaColor = 17, sigmaSpace = 17;
	bilateralFilter(intermediate, src, d, sigmaColor, sigmaSpace);
	return ratio;
}


// find contours in Mat src
void detectContours(Mat& src, vector<vector<Point> >& contours, vector<Vec4i>& hierarchy) {
	// determine edge detection thresholds automatically
	int lower, upper;
	autoCanny(src, 0.33, lower, upper);

	// apply edge detection
	Canny(src, src, lower, upper);
	findContours(src, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);
}

// Finds largest contour with four vertices within a list of contours and returns the index
int findLargestQuadrilateral(vector<vector<Point> > &contours, vector<Point>& boardApprox) {
	int maxContourIndex = -1;
	double maxArea = 200.0;
	vector<Point> approx;

	// Process contours to find a four corner contour with max area
	for (int i = 0; i < contours.size(); ++i) {
		double area = contourArea(contours[i]);

		// fit the contour to a polygon with epsilon precision
		// approx stores the vertex coordinates of said polygon
		double epsilon = arcLength(Mat(contours[i]), true) * 0.05;
		approxPolyDP(contours[i], approx, epsilon, true);

		if (approx.size() == 4 && area > maxArea) {
			maxArea = area;
			maxContourIndex = i;
			boardApprox = approx;
		}
	}
	return maxContourIndex;
}

// reorient the vertices of a quadrilateral so that they are stored clockwise starting with the top left vertex
// Example:
//		boardApprox = vector<Point>(Point(10, 20), Point(20, 30), Point(10, 30), Point(20, 20));
//		array<Point2f, 4> rect, dstRect;
//		orderVertices(boardApprox, rect, dstRect);
//		rect: array<Point2f, 4>(Point(10, 20), Point(20, 20), Point(20, 30), Point(10, 30))
//		dstRect: array<Point2f, 4>(Point(0, 0), Point(10, 0), Point(10, 10), Point(0, 10))
void orderVertices(vector<Point>& boardApprox, array<Point2f, 4>& rect, array<Point2f, 4>& dstRect, float ratio) {
	// sum x and y values of each point
	// smallest sum is top left corner, largest sum is bottom right
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

	// subtract x and y values of each point
	// smallest sum is top right point, largest sum is bottom left
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
	// dstRect stores the output shape of the normalized image
	dstRect = { Point(0,0), Point(maxWidth - 1,0), Point(maxWidth - 1,maxHeight - 1),Point(0,maxHeight - 1) };
}

// applies perspective transform to get normalized view of the checkerboard
void perspectiveTransform(Mat& input, Mat& dst, array<Point2f, 4>& rect, array<Point2f, 4>& dstRect) {
	// determine transformation relationship between original and normalized image
	Mat transformMatrix = getPerspectiveTransform(rect, dstRect);

	int width = dstRect[1].x - dstRect[0].x + 1;
	int height = dstRect[3].y - dstRect[0].y + 1;
	Size size(width, height);
	warpPerspective(input, dst, transformMatrix, size);
}

// find internal corners of checkerboard
vector<vector<Point> > pointDetection(Mat& src, int& boardEdgesLower, int& boardEdgesUpper) {
	// create two binary matrices that will store the horizontal and vertical lines found in the image
	Mat hMask = Mat::zeros(src.size(), CV_8U);
	Mat vMask = Mat::zeros(src.size(), CV_8U);

	// run edge detection on src image
	Mat boardEdges;
	autoCanny(src, 0.33, boardEdgesLower, boardEdgesUpper);
	Canny(src, boardEdges, boardEdgesLower, boardEdgesUpper);

	// perform line detection on edge image
	// we are looking for the grid that defines the checkerboard
	vector<Vec2f> lines;
	vector<vector<Point> > pointsSorted;
	float PI = 3.1415926;
	HoughLines(boardEdges, lines, 1, PI / 180, 200);
	if (lines.size() <= 1) { return pointsSorted; }

	// sort lines from closest to furthest away
	sort(lines.begin(), lines.end(), compareLines);
	int minDist = 20;
	int prevVDist = -1000, prevHDist = -1000;
	int numCols = 0;
	for (int i = 0; i < lines.size(); ++i) {
		// determine endpoints of line
		float rho = lines[i][0], theta = lines[i][1];
		Point pt1, pt2;
		double a = cos(theta), b = sin(theta);
		double x0 = a * rho, y0 = b * rho;
		pt1.x = cvRound(x0 + 1000 * (-b));
		pt1.y = cvRound(y0 + 1000 * (a));
		pt2.x = cvRound(x0 - 1000 * (-b));
		pt2.y = cvRound(y0 - 1000 * (a));

		int thetaDeg = int(theta * 180 / PI);	// convert line angle to degrees

												// vertical lines are within 5 degrees of 0 degrees
												// horizontal lines are within 5 degrees of 90 degrees
		float tol = 5;
		if (-tol < thetaDeg % 180 && thetaDeg % 180 < tol) {
			// ignore lines which are too close to each other. every physical line on checkerboard
			// will be detected as two lines due to thickness of the physical line
			if (abs(rho - prevVDist) > minDist) {
				// draw the line in the binary image created earlier
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

	// applying bitwise_and to the two images of vertical and horizontal lines finds intersections of
	// the lines of the checkerboard grid
	// intersections represent internal corners of checkerboard grid
	Mat intersectionMask;
	bitwise_and(hMask, vMask, intersectionMask);

	// all intersections are nonZero values in binary image
	vector<Point> points;
	findNonZero(intersectionMask, points);	// stores the x,y coordinates of every intersection

											// convert the 1 dimensional points vector into a 2 dimensional vector representing the checkerboard grid
	vector<Point>::const_iterator rowBegin = points.begin(), rowEnd = rowBegin + numCols;
	for (int i = 0; i < points.size() / numCols; ++i) {
		rowBegin = points.begin() + i * numCols;
		rowEnd = rowBegin + numCols;
		vector<Point> temp(rowBegin, rowEnd);
		pointsSorted.push_back(temp);
	}
	return pointsSorted;
}

// sharpens an image by blurring an image and subtracting the blur from the original image
void sharpenImage(Mat& src, Mat& dst, double sigma, double threshold, double amount) {
	Mat blurred;
	GaussianBlur(src, blurred, Size(), sigma, sigma);
	Mat lowContrastMask = abs(src - blurred) < threshold;
	dst = src * (1 + amount) + blurred * (-amount);
	src.copyTo(dst, lowContrastMask);
}

// returns the color of a found checker piece
// simply averages the color of the largest square we can inscribe in the circle
Scalar determineTileColor(Mat& src, Vec3f circle) {
	Point2f center(cvRound(circle[0]), cvRound(circle[0]));
	int radius = cvRound(circle[2]);
	int width = radius / sqrt(2);
	int checkerX = center.x - width, checkerY = center.y - width;
	Rect insideChecker = Rect(checkerX, checkerY, width, width);
	return mean(src(insideChecker));
}

// determines checkerboard state from a normalized checkerboard image and the points representing the internal checkboard corners
// stores the image of a tile with a team1 piece on it and the image of a tile with a team2 piece on it
void pieceDetection(Mat& warp, Mat& warpColored, vector<vector<Point> > pointsSorted,
	Mat*& team1, Mat*& team2, const vector<Scalar>& colors, vector<vector<int> >& Board, int& boardEdgesUpper) {
	int area = warp.size().height * warp.size().width;

	int minDistCenterT1 = warp.size().width;
	int minDistCenterT2 = warp.size().width;

	// iterate over each internal corner of the checkerboard and check the tile defined by it
	// and the point diagonally down and right of it for a piece
	for (int row = 0; row < (pointsSorted.size() - 1); ++row) {
		// sort the points in the row because they may not necessarily be in order of ascending x values
		sort(pointsSorted[row].begin(), pointsSorted[row].end(), comparePointsX);
		vector<int> tempRow;	// stores the state of each row. 0 is no piece, 1 is team1, 2 is team2
		for (int col = 0; col < (pointsSorted[row].size() - 1); ++col) {
			// calculate width, height of each tile and determine the ratio of its sides
			// if the tile is too rectangular (w / h > 1.4) then it is noise and it can be ignored
			// these "tiles" are the edge of the checkerboard but not part of the grid
			int x, y, w, h;
			x = pointsSorted[row][col].x, y = pointsSorted[row][col].y;
			w = pointsSorted[row + 1][col + 1].x - x, h = pointsSorted[row + 1][col + 1].y - y;
			if ((float(w) / h > 1.4) || (float(h) / w > 1.4) || (float(w) / h <= 0) || (w * h < area / 200)) continue;

			// Define the tile subimage
			//	Because the found grid does not perfectly represent the physical grid, pixels from
			//  neighboring tiles may invade our tile. Choosing an area slightly within the actual tile
			//	reduces noise from neighboring tiles
			float buffer = 0.05;
			int roiX = int(x + buffer * w);
			int roiY = int(y + buffer * h);
			Rect roi = Rect(roiX, roiY, int((1 - 2 * buffer) * w), int((1 - 2 * buffer) * h));
			Mat tile = warp(roi);
			Mat coloredTile = warpColored(roi);

			// sharpen tile image to remove some blur generated from noise removal
			Mat sharpened;
			double sigma = 1, threshold = 5, amount = 1;
			sharpenImage(tile, sharpened, sigma, threshold, amount);

			// look for a checker piece by finding circles within the tile
			vector<Vec3f> circles;
			int dp = 1, minDist = w, cannyThresh = boardEdgesUpper, accumulator = 30, minRadius = w / 4, maxRadius = 0.5*w;
			HoughCircles(sharpened, circles, CV_HOUGH_GRADIENT, dp, minDist, cannyThresh, accumulator, minRadius, maxRadius);

			if (circles.size() == 0) tempRow.push_back(0);	// no circles means it was an empty tile
			for (int i = 0; i < circles.size(); ++i) {
				float minColorDist = FLT_MAX;
				int minIndex = -1;
				Scalar tileColor = determineTileColor(coloredTile, circles[i]);

				// compare the color of a found checkerpiece to the reference palette
				// associate the checkerpiece with the team it is closest in color to
				for (int c = 0; c < colors.size(); ++c) {
					float colorDistance = norm(tileColor, colors[c]);
					if (colorDistance < minColorDist) {
						minColorDist = colorDistance;
						minIndex = c;
					}
				}
				tempRow.push_back(minIndex + 1);	// store the team number of the piece in the row state

													// determine the "best" image of a team1 piece and team2 piece we can find
													// "best" is defined as piece whose center is closest to middle of tile
													// we only keep the "best" image and show them on the team selection page
				Point2f middle = Point2f(x + w / 2, y + h / 2);
				Point2f center(cvRound(circles[i][0]), cvRound(circles[i][0]));
				int distCenter = int(euclideanDistance(center, middle));
				if (minIndex == 0 && distCenter < minDistCenterT1) {
					minDistCenterT1 = distCenter;
					coloredTile.copyTo(*team1);
				}
				else if (minIndex == 1 && distCenter < minDistCenterT2) {
					minDistCenterT2 = distCenter;
					coloredTile.copyTo(*team2);
				}

			}
		}
		if (tempRow.size() > 0) { Board.push_back(tempRow); }	// store the row state in Board state
	}
}


// Takes in Mat pointers and returns by reference pointers to :image of the checkerboard, image of team 1 and team 2 pieces
// Also returns the Board state by reference, where 0 = no piece, 1 = team1 piece, 2 = team2 piece
int checker(Mat* imageRef, Mat *&normalized, Mat *&team1, Mat *&team2, const vector<Scalar>& colors, vector<vector<int> >& Board) {
	Mat image = *imageRef;
	if (!image.data) { return 0; }

	// Shrink the image if width or height > maxDimension
	int maxDimension = 1088;
	resizeImage(image, maxDimension);

	Mat orig = image.clone();

	// Apply grayscale conversion, noise filtering, and step image size down to 800px for faster processing
	float desiredHeight = 800.0;
	float ratio = preprocessing(image, desiredHeight);

	// generate list of contours
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	detectContours(image, contours, hierarchy);

	// find largest contour with 4 corners within found contours
	vector<Point> boardApprox;
	int maxContourIndex = findLargestQuadrilateral(contours, boardApprox);

	// contours that are too small are false positives
	int imageArea = image.size().height * image.size().width;
	if (contourArea(boardApprox) / imageArea < 0.3) {
		// if a false positive is returned, draw it so the user has some feedback on what happened
		drawContours(orig, contours, maxContourIndex, Scalar(0, 255, 0), 4);
		orig.copyTo(*normalized);
		return -1;
	}

	// reorder the order of board corners coordinates to be clockwise starting with the top left corner
	array<Point2f, 4> rect, dstRect;
	orderVertices(boardApprox, rect, dstRect, ratio);

	Mat warp, warpColored;
	// get a normalized image of the board and a grayscale copy
	perspectiveTransform(orig, warpColored, rect, dstRect);
	cvtColor(warpColored, warp, CV_BGR2GRAY);

	// save the normalized version of the board
	warpColored.copyTo(*normalized);

	// Detect for internal corners of checkerboard
	int boardEdgesLower, boardEdgesUpper;
	vector<vector<Point> > pointsSorted = pointDetection(warp, boardEdgesLower, boardEdgesUpper);
	if (!pointsSorted.size()) return 0;

	// determine checkerboard state and store in Board
	pieceDetection(warp, warpColored, pointsSorted, team1, team2, colors, Board, boardEdgesUpper);

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
