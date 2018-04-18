// Comment out stdafx.h if not using Visual Studio
//#include "stdafx.h"
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
	// Scalar temp = mean(image);
	// cout << "Med and temp " << med << " " << temp << endl;
	lower = int(max(0.0, (1.0 - sigma) * med));
	upper = int(min(255.0, (1.0 + sigma) * med));
}

double euclideanDistance(Point2f& a, Point2f& b) {
	Point diff = a - b;
	return sqrt(pow(diff.x, 2) + pow(diff.y, 2));
}

// Takes in the file of checkerboard image, and the team colors
// returns a 2D vector of the Board, where 0: no piece, 1: colors[0] piece, 2: colors[1] piece
int checker(Mat* imageRef, Mat* warpedImage, Mat* team1, Mat* team2, const vector<Scalar>& colors, vector<vector<int> >& Board) {
	bool debug = false;

	// if (debug) cout << "Opening file: " << file << std::endl;
	Mat image_ = *imageRef;
	// image_ = imread(file, CV_LOAD_IMAGE_COLOR);
	if (!image_.data) {
		cerr << "could not open or find the image" << endl;
		return 0;
	}

	// Resize the image if it's too large. Processing will take too long and don't need that much resolution
	if (image_.size().width > 800 || image_.size().height > 800) {
		resize(image_, image_, Size(800, 800 * image_.size().height / image_.size().width));
	}

	Mat image = image_;
	Mat orig = image.clone();

	// Preprocessing Begins
	Mat processedImage, gray;
	Mat canvasImage = orig.clone();
	cvtColor(image, processedImage, CV_BGR2GRAY);
	int d = 11;
	double sigmaColor = 17, sigmaSpace = 17;
	float desiredHeight = 800.0;
	float ratio = image.size().height / desiredHeight;
	resize(processedImage, image, cv::Size(), 1 / ratio, 1 / ratio);
	Mat intermediate;
	bilateralFilter(image, intermediate, d, sigmaColor, sigmaSpace);
	bilateralFilter(intermediate, image, d, sigmaColor, sigmaSpace);
	// Preprocessing Ends

	// Contour Detection Begins
	// Canny(image, image, 30, 100);
	int lower = 0, upper = 0;
	autoCanny(image, 0.33, lower, upper);
	// cout << "Lower, upper: " << lower << ", " << upper << endl;
	Canny(image, image, lower, upper);
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	findContours(image, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);
	// Contour Detection Ends

	// Contour sorting begins
	vector<Point> approx, boardApprox;
	double maxArea = 200.0;
	int maxContourIndex = -1;
	Mat contourImage = orig.clone();
	resize(contourImage, contourImage, cv::Size(), 1 / ratio, 1 / ratio);

	for (int i = 0; i < contours.size(); ++i) {
		if (debug) drawContours(contourImage, contours, i, Scalar(0,255,0), 1);
		double area = contourArea(contours[i]);
		approxPolyDP(contours[i], approx, arcLength(Mat(contours[i]), true) * 0.01, true);
		if (approx.size() == 4 && area > maxArea) {
			maxArea = area;
			maxContourIndex = i;
			boardApprox = approx;
		}
	}
	if (debug) {
		cout << "Max area " << maxArea << endl;
		cout << "resized image area " << image.size().height * image.size().width << endl;
	}
	if (maxContourIndex == -1) cerr << "Can't find any square contours" << endl;
	int thickness = 4;
	Scalar color = (0, 255, 255);
	if (debug) {
		drawContours(contourImage, contours, maxContourIndex, Scalar(0, 0, 255), thickness);
		imshow("CanvasImage", contourImage);
		waitKey(0);
	}
	if (maxArea / (image.size().height * image.size().width) < 0.3) {
		cout << "maxArea too small" << endl;
		return 0;
	}
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
	Mat warp, warpColored;
	Size size(maxWidth, maxHeight);
	if (debug) cout << type2str(orig.type()) << endl;
	if (debug) cout << type2str(warp.type()) << endl;
	warpPerspective(processedImage, warp, transformMatrix, size);
	warpPerspective(orig, warpColored, transformMatrix, size);
	if (debug) {
		imshow("warp initial", warp);
		// waitKey(0);
	}
	//Perspective Transform Ends
	
	// Point detection begins
	Mat hMask = Mat::zeros(warp.size(), CV_8U);
	Mat vMask = Mat::zeros(warp.size(), CV_8U);

	Mat boardEdges;
	vector<Vec2f> lines;
	// int lower, upper;
	autoCanny(warp, 0.33, lower, upper);
	Canny(warp, boardEdges, lower, upper);
	float PI = 3.1415926;
	if (debug) {
		imshow("warp initial", boardEdges);
		// waitKey();
	}
	HoughLines(boardEdges, lines, 1, PI / 180, 200);
	if (debug) { cout << "lines " << lines.size() << endl; }
	if (lines.size() <= 1) { return 0; }
	// sort lines by rho
	sort(lines.begin(), lines.end(), compareLines);
	if (debug) { cout << "lines sorted" << endl; }
	int minDist = 20;
	int prevVDist = -1000, prevHDist = -1000;
	int numCols = 0;
	for (int i = 0; i < lines.size(); ++i) {
		float rho = lines[i][0], theta = lines[i][1];
		//if (debug) { cout << "Line " << i << " data: " << rho << " " << theta << endl; }
		Point pt1, pt2;
		double a = cos(theta), b = sin(theta);
		double x0 = a*rho, y0 = b*rho;
		pt1.x = cvRound(x0 + 1000*(-b));
		pt1.y = cvRound(y0 + 1000*(a));
		pt2.x = cvRound(x0 - 1000*(-b));
		pt2.y = cvRound(y0 - 1000*(a));

		int thetaDeg = int(theta * 180 / PI);
		// if (debug) cout << thetaDeg << endl;
		float tol = 5;
		if (-tol < thetaDeg  % 180 && thetaDeg % 180 < tol) {

		  	if (abs(rho - prevVDist) > minDist) {
		  		pt2.x = (pt1.x + pt2.x) / 2;
		  		pt1.x = pt2.x;
		  		// pt2.y = vMask.size().height - 1;

		  		line(vMask , pt1, pt2, Scalar(255), 1);
		  		prevVDist = rho;
		  		numCols += 1;
		  	}
		} 
		else if (90 - tol < thetaDeg % 180 && thetaDeg % 180 < 90 + tol) {
			if (abs(rho - prevHDist) > minDist) {
		  		// pt2.x = hMask.size().width - 1;
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
	vector<vector<Point> > pointsSorted;
	if (debug) {
		imshow("intersection", intersectionMask);
	}
	waitKey(0);

	findNonZero(intersectionMask, points);
	if (debug) { cout << "num points " << points.size() << endl; }
	if (debug) { cout << "num columns " << numCols << endl; }
	int oldHeight = -1000;
	//sort(points.begin(), points.end(), comparePointsY);
	vector<Point>::const_iterator rowBegin = points.begin(), rowEnd = rowBegin + numCols;
	for (int i = 0; i < points.size() / numCols; ++i) {
		rowBegin = points.begin() + i * numCols;
		rowEnd = rowBegin + numCols;
		vector<Point> temp(rowBegin, rowEnd);
		if (debug) { cout << "Row " << i << ": " << temp << endl; }
		pointsSorted.push_back(temp);
	}
	// Point Detection Ends

	// Tile detection begins
	if (debug) { cout << "num rows " << pointsSorted.size() << endl; }
	if (debug) { cout << "num cols " << pointsSorted[0].size() << endl; }
	int area = warp.size().height * warp.size().width;
	// vector<vector<int >> Board;
	int minDistCenter = int(FLT_MAX);
	for (int row = 0; row < (pointsSorted.size() - 1); ++row) {
		sort(pointsSorted[row].begin(), pointsSorted[row].end(), comparePointsX);
		// cout << "sorted: " << pointsSorted[row] << endl;
		vector<int> tempRow;

		for (int col = 0; col < (pointsSorted[row].size() - 1); ++col) {
			int x, y, w, h;
			x = pointsSorted[row][col].x, y = pointsSorted[row][col].y;
			w = pointsSorted[row + 1][col + 1].x - x, h = pointsSorted[row + 1][col + 1].y - y;
			// cout << (float(w) / h ) << endl;
			// cout << (float(h) / w ) << endl;
			// cout << endl;
			if ((float(w) / h > 1.4) || (float(h) / w > 1.4) || (float(w)/h <= 0) || (w * h < area / 200)) continue;
			float buffer = 0.05;
			x = int(x + buffer * w);
			y = int(y + buffer * h);
			Rect roi = Rect(x, y, int((1 - 2 * buffer) * w), int((1 - 2 * buffer) * h));
			Mat tile = warp(roi);
			Mat coloredTile = warpColored(roi);

			// Circle Detection Begins
			vector<Vec3f> circles;
			autoCanny(tile, 0.33, lower, upper);
			int dp = 1, minDist = w, cannyThresh = 75, accumulator=15, minRadius=w/4, maxRadius=w/2;
			HoughCircles(tile, circles, CV_HOUGH_GRADIENT, dp, minDist, cannyThresh, accumulator, minRadius, maxRadius);
			
			if (circles.size() == 0) tempRow.push_back(0);
			for (int i = 0; i < circles.size(); ++i) {
				Point center(cvRound(circles[i][0]), cvRound(circles[i][0]));
				int radius = cvRound(circles[i][2]);
				circle(tile, center, radius, Scalar(255), 2);
				Point centerShifted(x + center.x, y + center.y);
				int width = radius / sqrt(2);
				int checkerX = center.x - width, checkerY = center.y - width;
				Rect insideChecker = Rect(checkerX, checkerY, width, width);
				Scalar tileColor = mean(coloredTile(insideChecker));
				if (debug) { cout << tileColor << endl; }
				float minColorDist = FLT_MAX;
				int minIndex;
				for (int c = 0; c < colors.size(); ++c) {
					float colorDistance = cv::norm(tileColor, colors[c]);
					if (debug) { cout << "distance" << colorDistance << endl; }
					if (colorDistance < minColorDist) {
						/*
						int distCenter = int(euclideanDistance(center, Point(x + w/2, y + h/2))
						if (distCenter < minDistCenter) {
							minDistCenter = distCenter;
							if (c == 0) {
								team1 = &(tile.clone());
							} else {
								team 2 = &(tile.clone());
							}
						}
						*/
						minColorDist = colorDistance;
						minIndex = c;
					}
				}
				tempRow.push_back(minIndex + 1);
				
				circle(warpColored, centerShifted, radius, tileColor, -1);
				// Mat roi = tile(Range(circles[i][1] - circles[i][2], circles[i][1] + circles[i][2] + 1), cv::Range(circles[i][0] - circles[i][2], circles[i][0] + circles[i][2] + 1));
			}
			if (debug) {
				Mat edgeComparison, cannyTile;
				Canny(tile, cannyTile, 100 / 2, 100);
				hconcat(warp(roi), cannyTile, edgeComparison);
				// hconcat(warp(roi), boardEdges(roi), edgeComparison);
				imshow("Comparison", edgeComparison);
				imshow("warp", warpColored);
				waitKey(0);
				destroyWindow("Comparison");
			}
		}
		if (tempRow.size() > 0) { Board.push_back(tempRow); }
	}

	// imshow("checkerboard", processedImage);
	// imshow("Board edges", boardEdges);
	// imshow("intersection", intersectionMask);
	if (debug) {imshow("BoardGuess", warpColored);}
	waitKey(0);
	return 1;
}

int main() {
	// Test cases

	vector<vector<int> > Board;
	vector<Scalar> colors1;
	colors1.push_back(Scalar(0, 0, 255));
	colors1.push_back(Scalar(0, 0, 0));
	Mat image = imread("TestImages/irl6.png");
	Mat* imageRef = &image;
	int ret = checker(imageRef,imageRef,imageRef,imageRef, colors1, Board);
	
	if (ret) printBoard(Board);

	// int sum = 0;
	// int n = 0;
	// for (int i = 0; i < n; ++i) {
	// 	clock_t t = clock();
	// 	vector<vector<int> > Board;
	// 	vector<Scalar> colors1;
	// 	colors1.push_back(Scalar(0, 0, 255));
	// 	colors1.push_back(Scalar(0, 0, 0));
	// 	checker("TestImages/WIN_20180416_14_46_01_Pro.jpg", colors1, Board);
	// 	//printBoard(Board);
	// 	t = clock() - t;
	// 	sum += t;
	// }
	// cout << "Average time: " << float(sum) * 1000 / (n * CLOCKS_PER_SEC) << "ms" << endl;
	/*vector<Scalar> colors2;
	colors2.push_back(Scalar(0, 0, 255));
	colors2.push_back(Scalar(255, 255, 255));
	checker("TestImages/chessboard4.jpg", colors2);
	checker("TestImages/empty_board_irl.jpg", colors1);
	
	string file;
	file = "TestImages/ex1.jpeg";
	file = "TestImages/empty_board_irl.jpg";
	file = "TestImages/chessboard4.jpg";
	
	int sum = 0;
	int n = 0;
	for (int i = 0; i < n; ++i) {
		clock_t t = clock();
		checker(file, colors2);
		t = clock() - t;
		sum += t;
	}
	cout << "Average time: " << float(sum) * 1000/ (n * CLOCKS_PER_SEC) << "ms" << endl;*/
}
