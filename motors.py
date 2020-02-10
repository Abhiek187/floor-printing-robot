#!/usr/bin/env python

# Import required modules
import time
from enum import Enum
import RPi.GPIO as GPIO

# Constants for the pins
class Pin(Enum):
	PWMA = 7
	AIN2 = 11
	AIN1 = 12
	STBY = 13
	BIN1 = 15
	BIN2 = 16
	PWMB = 18

# Declare the GPIO settings
GPIO.setmode(GPIO.BOARD)

# Set up GPIO pins
for pin in Pin:
	GPIO.setup(pin.value, GPIO.OUT)

# Drive the motor clockwise
# Motor A:
GPIO.output(Pin.AIN1.value, GPIO.HIGH)
GPIO.output(Pin.AIN2.value, GPIO.LOW)
# Motor B:
GPIO.output(Pin.BIN1.value, GPIO.HIGH)
GPIO.output(Pin.BIN2.value, GPIO.LOW)

# Set the motor speed
# Motor A:
GPIO.output(Pin.PWMA.value, GPIO.HIGH)
# Motor B:
GPIO.output(Pin.PWMB.value, GPIO.HIGH)

# Disable STBY (standby)
GPIO.output(Pin.STBY.value, GPIO.HIGH)

# Wait 5 seconds
time.sleep(5)

# Drive the motor counterclockwise
# Motor A:
GPIO.output(Pin.AIN1.value, GPIO.LOW)
GPIO.output(Pin.AIN2.value, GPIO.HIGH)
# Motor B:
GPIO.output(Pin.BIN1.value, GPIO.LOW)
GPIO.output(Pin.BIN2.value, GPIO.HIGH)

# Set the motor speed
# Motor A:
GPIO.output(Pin.PWMA.value, GPIO.HIGH)
# Motor B:
GPIO.output(Pin.PWMB.value, GPIO.HIGH)

# Disable STBY (standby)
GPIO.output(Pin.STBY.value, GPIO.HIGH)

# Wait 5 seconds
time.sleep(5)

# Reset all the GPIO pins by setting them to LOW
for pin in Pin:
	GPIO.output(pin.value, GPIO.LOW)

# Clean up pins
GPIO.cleanup()
