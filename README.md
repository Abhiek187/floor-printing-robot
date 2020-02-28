# floor-printing-robot
Capstone project: A robot that can draw images on a surface

## img_info.py

A Python script that converts an image into its basic colors

## motors/ (only works on a device with I2C)

Scripts that run the motors on the raspberry pi. You must enable I2C under Interfacing Options in raspi-config to get them to work.

- **encoder.py**: powers the motor encoder to move the printer up and down in a rack and pinion system
- **main.py**: powers the wheels, allowing the bot to maneuver with the image
- **PCA9685.py**: library code to handle the wheel motors using smbus
- **ultrasonic.py**: runs the ultrasonic sensor to detect obstacles and restrain the printing area

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
`python3 img_info.py [image_file]` where _image_file_ can be any image

To preview the image on Linux: `fbi -a [new_image_file]` (No cops will be called!)

**Provided Images (may be too small to see normally)**

**small.png**: a collection of colored pixels

**square.png**: a small, black square

## LinuxTest

An Android app that lets you draw or upload images and print them to a raspberry pi

### Requirements
- Android Studio
- A Raspberry Pi connected to the internet (need to enter the username, password, and IP address in settings)
