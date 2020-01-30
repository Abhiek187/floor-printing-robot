#!/usr/bin/env python

# Import required modules
import time
import RPi.GPIO as GPIO

# Declare the GPIO settings
GPIO.setmode(GPIO.BOARD)

# Set up GPIO pins
GPIO.setup(7, GPIO.OUT) # connected to PWMA
GPIO.setup(11, GPIO.OUT) # connected to AIN2
GPIO.setup(12, GPIO.OUT) # connected to AIN1
GPIO.setup(13, GPIO.OUT) # connected to STBY
GPIO.setup(15, GPIO.OUT) # connected to BIN1
GPIO.setup(16, GPIO.OUT) # connected to BIN2
GPIO.setup(18, GPIO.OUT) # connected to PWMB

# Drive the motor clockwise
# Motor A:
GPIO.output(12, GPIO.HIGH) # set AIN1
GPIO.output(11, GPIO.LOW) # set AIN2
# Motor B:
GPIO.output(15, GPIO.HIGH) # set BIN1
GPIO.output(16, GPIO.LOW) # set BIN2

# Set the motor speed
# Motor A:
GPIO.output(7, GPIO.HIGH) # set PWMA
# Motor B:
GPIO.output(18, GPIO.HIGH) # set PWMB

# Disable STBY (standby)
GPIO.output(13, GPIO.HIGH)

# Wait 5 seconds
time.sleep(5)

# Drive the motor counterclockwise
# Motor A:
GPIO.output(12, GPIO.LOW) # set AIN1
GPIO.output(11, GPIO.HIGH) # set AIN2
# Motor B:
GPIO.output(15, GPIO.LOW) # set BIN1
GPIO.output(16, GPIO.HIGH) # set BIN2

# Set the motor speed
# Motor A:
GPIO.output(7, GPIO.HIGH) # set PWMA
# Motor B:
GPIO.output(18, GPIO.HIGH) # set PWMB

# Disable STBY (standby)
GPIO.output(13, GPIO.HIGH)

# Wait 5 seconds
time.sleep(5)

# Reset all the GPIO pins by setting them to LOW
GPIO.output(12, GPIO.LOW) # set AIN1
GPIO.output(11, GPIO.LOW) # set AIN2
GPIO.output(7, GPIO.LOW) # set PWMA
GPIO.output(13, GPIO.LOW) # set STBY
GPIO.output(15, GPIO.LOW) # set BIN1
GPIO.output(16, GPIO.LOW) # set BIN2
GPIO.output(18, GPIO.LOW) # set PWMB
