import RPi.GPIO as GPIO
from time import sleep
from math import pi

class Pin():
	def __init__(self):
		self.PWMA = 32
		self.AIN1 = 11
		self.AIN2 = 12
		self.SIGA = 16
		self.SIGB = 18

def move_gear(speed):
	counts = 3591.84 # counts per revolution
	circum = 4*pi # circumference of gear (4 mm diameter)
	dist_per_cycle = circum / counts # about 3.5 um
	pwma.ChangeDutyCycle(speed)
	pwma.ChangeFrequency(speed)

def stop_gear():
	# Reset all the GPIO pins by setting them to LOW
	pwma.ChangeDutyCycle(0)
	GPIO.output(pins.AIN1, GPIO.LOW)
	GPIO.output(pins.AIN2, GPIO.LOW)
	GPIO.output(pins.SIGA, GPIO.LOW)
	GPIO.output(pins.SIGB, GPIO.LOW)

print("Testing the encoder...")
GPIO.setmode(GPIO.BOARD)
pins = Pin()

# Set up GPIO pins
GPIO.setup(pins.PWMA, GPIO.OUT)
pwma = GPIO.PWM(pins.PWMA, 20) # freq = 20
pwma.start(0) # duty cycle = 0
GPIO.setup(pins.AIN1, GPIO.OUT)
GPIO.setup(pins.AIN2, GPIO.OUT)
GPIO.setup(pins.SIGA, GPIO.OUT)
GPIO.setup(pins.SIGB, GPIO.OUT)

# Run the following continuously until we interrupt
try:
	while True:
		print("Clockwise?")
		GPIO.output(pins.AIN1, GPIO.HIGH)
		GPIO.output(pins.AIN2, GPIO.LOW)
		move_gear(10)
		sleep(3)

		print("Stopping...")
		stop_gear()
		sleep(3)

		print("Counterclockwise?")
		GPIO.output(pins.AIN1, GPIO.LOW)
		GPIO.output(pins.AIN2, GPIO.HIGH)
		move_gear(10)
		sleep(3)

		print("Stopping...")
		stop_gear()
		sleep(3)

except KeyboardInterrupt:
	print("Stopping the encoder...")
	GPIO.cleanup()
