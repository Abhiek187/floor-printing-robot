import RPi.GPIO as GPIO
from time import sleep
from math import pi

class Pin():
	def __init__(self):
		self.PWMA = 32
		self.AIN1 = 12
		self.AIN2 = 11
		self.OUT1 = 16
		self.OUT2 = 18

def move_gear(speed):
	counts = 3591.84 # counts per revolution
	circum = 4*pi # circumference of gear (4 mm diameter)
	dist_per_cycle = circum / counts # about 3.5 um
	pwma.ChangeDutyCycle(speed)
	pwma.ChangeFrequency(speed)

def stop_gear():
	# Reset all the GPIO pins by setting them to LOW
	pwma.ChangeDutyCycle(0)
	GPIO.output(pin.AIN1, GPIO.LOW)
	GPIO.output(pin.AIN2, GPIO.LOW)
	GPIO.output(pin.OUT1, GPIO.LOW)
	GPIO.output(pin.OUT2, GPIO.LOW)

print("Testing the encoder...")
GPIO.setmode(GPIO.BOARD)
pins = Pin()

# Set up GPIO pins
pwma = GPIO.PWM(pin.PWMA, 20) # freq = 20
pwma.start(0) # duty cycle = 0
GPIO.setup(pin.AIN1, GPIO.OUT)
GPIO.setup(pin.AIN2, GPIO.OUT)
GPIO.setup(pin.OUT1, GPIO.OUT)
GPIO.setup(pin.OUT2, GPIO.OUT)

# Run the following continuously until we interrupt
try:
	while True:
		GPIO.output(pins.AIN1, GPIO.HIGH)
		GPIO.output(pins.AIN2, GPIO.LOW)
		move_gear(10)
		sleep(3)
		stop_gear()

		GPIO.output(pins.AIN1, GPIO.LOW)
		GPIO.output(pins.AIN2, GPIO.HIGH)
		move_gear(10)
		sleep(3)
		stop_gear()

except KeyboardInterrupt:
	print("Stopping the encoder...")
	GPIO.cleanup()
