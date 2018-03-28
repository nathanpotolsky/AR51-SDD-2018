#!/bin/bash

g++ Checker\ Algorithm.cpp -o main.exe -std=c++11 `pkg-config --cflags --libs opencv`
#./main.exe
