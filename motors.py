#!/usr/bin/env python

# Import required modules
from time import sleep
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

# p, q, a, b = GPIO.PWM(pin, 20)
# p, q, a, b.start(0)

# stop(): Stops both motors
"""def stop():
	p.ChangeDutyCycle(0)
	q.ChangeDutyCycle(0)
	a.ChangeDutyCycle(0)
	b.ChangeDutyCycle(0)"""

# forward(speed): Sets both motors to move forward at speed. 0 <= speed <= 100
"""def forward(speed):
	p.ChangeDutyCycle(speed)
	q.ChangeDutyCycle(0)
	a.ChangeDutyCycle(speed)
	b.ChangeDutyCycle(0)
	p.ChangeFrequency(speed + 5)
	a.ChangeFrequency(speed + 5)"""

# reverse(speed): Sets both motors to reverse at speed. 0 <= speed <= 100
"""def reverse(speed):
	p.ChangeDutyCycle(0)
	q.ChangeDutyCycle(speed)
	a.ChangeDutyCycle(0)
	b.ChangeDutyCycle(speed)
	q.ChangeFrequency(speed + 5)
	b.ChangeFrequency(speed + 5)"""

# spinLeft(speed): Sets motors to turn opposite directions at speed. 0 <= speed <= 100
"""def spinLeft(speed):
	p.ChangeDutyCycle(0)
	q.ChangeDutyCycle(speed)
	a.ChangeDutyCycle(speed)
	b.ChangeDutyCycle(0)
	q.ChangeFrequency(speed + 5)
	a.ChangeFrequency(speed + 5)"""

# spinRight(speed): Sets motors to turn opposite directions at speed. 0 <= speed <= 100
"""def spinRight(speed):
	p.ChangeDutyCycle(speed)
	q.ChangeDutyCycle(0)
	a.ChangeDutyCycle(0)
	b.ChangeDutyCycle(speed)
	p.ChangeFrequency(speed + 5)
	b.ChangeFrequency(speed + 5)"""

print("Starting the motors...")
# Declare the GPIO settings
GPIO.setmode(GPIO.BOARD)

# Set up GPIO pins
for pin in Pin:
	GPIO.setup(pin.value, GPIO.OUT)

# Run the following continuously until we interrupt
try:
	while True:
		# Drive the motor clockwise
		print("Moving clockwise...")
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
		sleep(5)

		# Drive the motor counterclockwise
		print("Moving counterclockwise...")
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
		sleep(5)

except KeyboardInterrupt:
	# Reset all the GPIO pins by setting them to LOW
	for pin in Pin:
		GPIO.output(pin.value, GPIO.LOW)

	# Clean up pins
	GPIO.cleanup()
