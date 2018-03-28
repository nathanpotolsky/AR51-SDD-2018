#include "stdafx.h"
#include <time.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <string>

using namespace cv;
using namespace std;

 bool compareLines(const Vec2f & line1, const Vec2f & line2) {
 	if(line1[0] <= line2[0]) return true;
 	else return false;
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


double euclideanDistance(Point2f& a, Point2f& b) {
	Point diff = a - b;
	return sqrt(pow(diff.x, 2) + pow(diff.y, 2));
}


int checker() {
	bool debug = false;
	string file = "ex1.jpeg";
	file = "TestImages/chessboard3.jpg";
	if(debug) cout << "Opening file: " << file << std::endl;
	Mat image_;
	image_ = imread(file, CV_LOAD_IMAGE_COLOR);
	if (!image_.data) {
		cerr << "could not open or find the image" << endl;
		return 0;
	}

	UMat image = image_.getUMat(ACCESS_READ);
	UMat orig = image.clone();

	// Preprocessing Begins
	// Resizing
	UMat canvasImage = orig.clone();

	//Grayscale Conversion
	cvtColor(image, image, CV_BGR2GRAY);
	int d = 11;
	double sigmaColor = 17, sigmaSpace = 17;
	UMat processedImage;
	bilateralFilter(image, processedImage, d, sigmaColor, sigmaSpace);
	if (debug) imwrite("./DebugImages/bilateral.png", processedImage);
	float desiredHeight = 300.0;
	float ratio = image.size().height / desiredHeight;
	resize(processedImage, image, cv::Size(), 1 / ratio, 1 / ratio);
	// Preprocessing Ends

	// Contour Detection Begins
	Canny(image, image, 30, 100);
	if (debug) imwrite("./DebugImages/Canny.png", image);
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	findContours(image, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);
	// Contour Detection Ends

	// Contour sorting begins
	vector<Point> approx, boardApprox;
	double maxArea = 200.0;
	int maxContourIndex = -1;

	for (int i = 0; i<contours.size(); ++i) {
		double area = contourArea(contours[i]);

		approxPolyDP(contours[i], approx, arcLength(Mat(contours[i]), true) * 0.01, true);
		if (approx.size() == 4 && area>maxArea) {
			maxArea = area;
			maxContourIndex = i;
			boardApprox = approx;
		}
	}
	if (maxContourIndex == -1) cerr << "Can't find any square contours" << endl;
	int thickness = 4;
	Scalar color = (0, 255, 255);
	if (debug) drawContours(canvasImage, contours, maxContourIndex, color, thickness);
	// Contour Sorting Ends

	// Perspective Transform Begins
	if (debug) cout << "Board Approx\n" << boardApprox << endl;
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
	if (debug) for (int i = 0; i < rect.size(); ++i) { cout << rect[i] << endl; }
	if (debug) for (int i = 0; i < dstRect.size(); ++i) { cout << dstRect[i] << endl; }

	Mat transformMatrix = getPerspectiveTransform(rect, dstRect);
	UMat warp;
	Size size(maxWidth, maxHeight);
	if (debug) cout << type2str(orig.type()) << endl;
	if (debug) cout << type2str(warp.type()) << endl;
	warpPerspective(processedImage, warp, transformMatrix, size);
	//Perspective Transform Ends
	
	// Point detection begins
	Mat hMask = Mat::zeros(warp.size(), CV_8U);
	Mat vMask = Mat::zeros(warp.size(), CV_8U);

	// not sure if worth the conversion to UMat, test for time later
	// intersectionMask.getUMat(ACCESS_READ);

	// check if edge detection on whole image first is faster than canny on small
	// and canny on warp
	UMat boardEdges;
	vector<Vec2f> lines;
	Canny(warp, boardEdges, 30, 150);
	float PI = 3.1415926;

	HoughLines(boardEdges, lines, 1, PI / 180, 200);
	sort(lines.begin(), lines.end(), compareLines);
	int minDist = 20;
	int prevVDist = 0, prevHDist = 0;
	int numCols = 0;
	for(int i = 0; i < lines.size(); ++i) {
		float rho = lines[i][0], theta = lines[i][1];
		Point pt1, pt2;
		double a = cos(theta), b = sin(theta);
		double x0 = a*rho, y0 = b*rho;
		pt1.x = cvRound(x0 + 1000*(-b));
		pt1.y = cvRound(y0 + 1000*(a));
		pt2.x = cvRound(x0 - 1000*(-b));
		pt2.y = cvRound(y0 - 1000*(a));

		int thetaDeg = int(theta * 180 / PI);
		if (debug) cout << thetaDeg << endl;
		if(-22.5 < thetaDeg  % 180 && thetaDeg % 180 < 22.5) {
			// cout << rho - prevVDist <<endl;
		  	if(abs(rho - prevVDist) > minDist) {
		  		line(vMask , pt1, pt2, Scalar(255), 1);
		  		prevVDist = rho;
		  	}
		} else if(67.5 <  thetaDeg  % 180 && thetaDeg % 180 < 112.5) {
			if(abs(rho - prevHDist) > minDist) {
		  		line(hMask , pt1, pt2, Scalar(255), 1);
		  		prevHDist = rho;
		  		numCols += 1;
		  	}
		}
	}
	Mat intersectionMask;
	bitwise_and(hMask, vMask, intersectionMask);
	vector<Point> points;
	vector<vector<Point> > pointsSorted;
	findNonZero(intersectionMask, points);
	int oldHeight = -1000;
	vector<Point>::const_iterator rowBegin = points.begin(), rowEnd = rowBegin + numCols;
	for(int i = 0; i< points.size()/numCols; ++i) {
		vector<Point> temp(rowBegin, rowEnd);
		// cout<<"Row "<< i <<": "<<temp<<endl;
		pointsSorted.push_back(temp);
		rowBegin = rowEnd;
		rowEnd = rowBegin + numCols;
	}
	// Point Detection Ends


	// imshow("checkerboard", processedImage);
	// imshow("warp", warp);
	// imshow("Board edges", boardEdges);
	imshow("intersection", intersectionMask);
	waitKey(0);

	return 0;
}

int main() {
	checker();

	int sum = 0;
	int n = 0;
	for (int i = 0; i < n; ++i) {
		clock_t t = clock();
		checker();
		t = clock() - t;
		sum += t;
	}
	cout << "Average time: " << float(sum) * 1000/ (n * CLOCKS_PER_SEC) << "ms" << endl;
}