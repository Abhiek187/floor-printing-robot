import RPi.GPIO as GPIO
from time import sleep

class Pin():
	def __init__(self):
		self.PWM = 6 # or 26 for PWM0

def drop_marker():
	servo.ChangeDutyCycle(12)
	sleep(0.3) # reduce jitter
	servo.ChangeDutyCycle(0)
	sleep(0.7)

def lift_marker():
	servo.ChangeDutyCycle(2)
	sleep(0.3) # reduce jitter
	servo.ChangeDutyCycle(0)
	sleep(0.7)

print("Testing the servo...")
GPIO.setmode(GPIO.BCM)
pins = Pin()

GPIO.setup(pins.PWM, GPIO.OUT)
servo = GPIO.PWM(pins.PWM, 50) # freq = 50 Hz
servo.start(0) # 0% <= duty cycle <= 100%
sleep(2)

try:
	while True:
		# Turn 180 degrees in 10 steps (2% = 0d, 12% = 180d, deg = 18*dc - 36, dc = deg/18 + 2)
		dc = 2

		while dc <= 12:
			servo.ChangeDutyCycle(dc)
			sleep(0.3) # reduce jitter
			servo.ChangeDutyCycle(0)
			sleep(0.7)
			dc += 1

		# Go back to 90 degrees
		servo.ChangeDutyCycle(7)
		sleep(0.5)
		servo.ChangeDutyCycle(0)
		sleep(1.5)

		# Return to 0 degrees
		servo.ChangeDutyCycle(2)
		sleep(0.5)
		servo.ChangeDutyCycle(0)
		sleep(1.5)
except KeyboardInterrupt:
	print("Stopping the servo...")
	servo.stop()
	GPIO.cleanup()
