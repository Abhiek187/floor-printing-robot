#!/usr/bin/env python

# Import required modules
from time import sleep
import RPi.GPIO as GPIO

# Constants for the pins
class Pin():
	def __init__(self):
		self.PWMA = 7
		self.AIN2 = 11
		self.AIN1 = 12
		self.STBY = 13
		self.BIN1 = 15
		self.BIN2 = 16
		self.PWMB = 18

# p (L1), q (L2), a (R1), b (R2) = GPIO.PWM(pin, 20)
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
pins = Pin()

# Set up GPIO pins
for pin in vars(pins).values():
	GPIO.setup(pin, GPIO.OUT)

# Run the following continuously until we interrupt
try:
	while True:
		# Drive the motor clockwise
		print("Moving clockwise...")
		# Motor A:
		GPIO.output(pins.AIN1, GPIO.HIGH)
		GPIO.output(pins.AIN2, GPIO.LOW)
		# Motor B:
		GPIO.output(pins.BIN1, GPIO.HIGH)
		GPIO.output(pins.BIN2, GPIO.LOW)

		# Set the motor speed
		# Motor A:
		GPIO.output(pins.PWMA, GPIO.HIGH)
		# Motor B:
		GPIO.output(pins.PWMB, GPIO.HIGH)

		# Disable STBY (standby)
		GPIO.output(pins.STBY, GPIO.HIGH)

		# Wait 5 seconds
		sleep(5)

		# Drive the motor counterclockwise
		print("Moving counterclockwise...")
		# Motor A:
		GPIO.output(pins.AIN1, GPIO.LOW)
		GPIO.output(pins.AIN2, GPIO.HIGH)
		# Motor B:
		GPIO.output(pins.BIN1, GPIO.LOW)
		GPIO.output(pins.BIN2, GPIO.HIGH)

		# Set the motor speed
		# Motor A:
		GPIO.output(pins.PWMA, GPIO.HIGH)
		# Motor B:
		GPIO.output(pins.PWMB, GPIO.HIGH)

		# Disable STBY (standby)
		GPIO.output(pins.STBY, GPIO.HIGH)

		# Wait 5 seconds
		sleep(5)

except KeyboardInterrupt:
	print("Stopping the motors...")
	# Reset all the GPIO pins by setting them to LOW
	for pin in vars(pins).values():
		GPIO.output(pin, GPIO.LOW)

	# Clean up pins
	GPIO.cleanup()