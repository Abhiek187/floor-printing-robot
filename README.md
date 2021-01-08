# Floor Printing Robot

[<img src="https://res.cloudinary.com/marcomontalbano/image/upload/v1610133191/video_to_markdown/images/youtube--GSI4W3XvddA-c05b58ac6eb4c4700831b2b3070cd403.jpg" alt="ECE 2020 Capstone Project S20-05 Demo Video: Floor Printing Robot" width="600">](https://www.youtube.com/watch?v=GSI4W3XvddA "ECE 2020 Capstone Project S20-05 Demo Video: Floor Printing Robot")

### Background

It’s the 21st century and spray painting the ground for commercial purposes such as drawing parking lots can be tedious. Automating that process will reduce the burden of road workers and allow for more complex designs in a short amount of time. This would lessen the load on the road companies to hire workers and would allow for more complex parking lot configurations to be implemented. Previous work has dealt with robots drawing on walls. Scribit is a robot that can take an image sent from an app and draw it on the wall. We want to do something similar but focus on drawing on the ground.

### Objective

The objective is to require a robot to draw and navigate using sensors/signals on the ground using cutting-edge technology. The robot can draw autonomously (small scale (~ 1:10 scale)) and our minimal viable product (MVP) is a robot that can print a simple square (5x5 pixels) using an Android app.

### Adopted approach

The adopted approach is implementing Mobile Apps and Wireless Communications technology through a raspberry pi 3B, motor controllers, and image files. The class Mobile Apps aided us in developing an Android app to tell the robot what to draw (scripts, data processing image) and the wireless communications technology is communicating from our app to the raspberry pi, which then communicates to the bot (calibrations). The raspberry pi is mounted onto the robot, and so is able to handle all data interactions from the bot to the app and vice-versa. After receiving the data file of the image to be printed from the app, a python script that controls the motors begins to run on the pi. The robot will then attempt to map out the printing area in order to determine how large the image will have to be. This is accomplished with the use of an ultrasonic sensor which will detect how close the robot is to the boundaries of the printing surface. Once it detects that it is close to the boundary the robot will then turn 90 degrees to begin mapping the other edge. This will continue until the printing area has been mapped completely. Afterward, the motor script will cause the robot to start moving, which will be when it starts to print the image using the marker that is controlled by the servo.

## img\_info.py / just\_print.py

Python scripts that convert an image into its basic colors

## motors/ (only works on a device with I2C)

Scripts that run the motors on the raspberry pi. You must enable I2C under Interfacing Options in raspi-config to get them to work.

- **servo.py**: powers the S90 servo, can be connected to a marker to move it up and down
- **main.py**: powers the wheels, allowing the bot to maneuver with the image
- **PCA9685.py**: library code to handle 12 V DC motors using smbus
- **ultrasonic.py**: runs the HC-SR04 ultrasonic sensor to detect obstacles and scale the printing area

### Requirements
**Python 3.6+ and pip 3**: (https://docs.python-guide.org/starting/install3/linux/)

**PIL**: `pip3 install pillow`

**webcolors**: `pip3 install webcolors`

**RPi.GPIO**: `pip3 install RPi.GPIO`

**smbus**: `pip3 install smbus`

(Or can type `pip3 install -r requirements.txt` to get the above modules)

**numpy**: `pip3 install numpy` or `sudo apt-get install python3-numpy`

**fbi (optional)**: `sudo apt-get install fbi`

### Syntax
Printing on a Raspberry Pi: `python3 img_info.py [image_file]` where _image_file_ can be any image

Printing purely through software: `python3 just_print.py [image_file]`

To preview the image on Linux: `fbi -a [new_image_file]` (No cops will be called!)

**Provided Images (may be too small to see normally)**

**small.png**: a collection of colored pixels

**square.png**: a small, black square

## LinuxTest

An Android app that lets you draw or upload images and print them to a raspberry pi

### Features
- A tutorial explaining how the app works the first time it is opened
- A canvas that supports black and white drawings, multiple stroke widths, and multitouch
- Save and load drawings from an SQLite database
- Load photos from the phone onto the canvas
- Settings to configure the Raspberry Pi login information
- Print to a Raspberry Pi with real-time output

### Requirements
- Android Studio
- A Raspberry Pi connected to the internet (need to enter the username, password, and IP address in settings)
