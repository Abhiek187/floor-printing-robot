import RPi.GPIO as GPIO
from time import sleep

class Pin():
	def __init__(self):
		self.PWM = 25 # or 26 for PWM0

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

try:
	while True:
		# Turn 180 degrees in 10 steps (2% = 0d, 12% = 180d, deg = 18*dc - 36, dc = deg/18 + 2)
		angle = -1

		# Do robust error checking (like a good boi)
		while angle < 0 or angle > 180:
			try:
				angle = float(input("Enter angle between 0 and 180: "))
				if angle < 0 or angle > 180:
					print("Angle is not within bounds, try again.")
			except ValueError:
				print("That's not a number, try again.")

		servo.ChangeDutyCycle(angle/18 + 2)
		sleep(0.3)
		servo.ChangeDutyCycle(0) # by the time user inputs, a second has gone by

except KeyboardInterrupt:
	print("Stopping the servo...")
	servo.stop()
	GPIO.cleanup()
