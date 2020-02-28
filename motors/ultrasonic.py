#!/usr/bin/env python

# Import required modules
from time import sleep, time
import RPi.GPIO as GPIO
from main import stop

# Assign global variable for count
count = 5

# Constants for the pins
class Pin():
    def __init__(self):
        """self.PWMA = 7
        self.AIN2 = 11
        self.AIN1 = 12
        self.STBY = 13
        self.BIN1 = 15
        self.BIN2 = 16
        self.PWMB = 18"""
        self.Trigger = 38 #GPIO Pin Number
        self.Echo = 40 #GPIO Pin Number

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

print("Testing the ultrasonic sensor...")
# Declare the GPIO settings
GPIO.setmode(GPIO.BOARD) # use pin numbers 1-40 instead of GPIO N
pins = Pin()

# Set up GPIO pins
"""for pin in vars(pins).values():
    GPIO.setup(pin, GPIO.OUT)"""

# Run the following continuously until we interrupt
try:
    """while True:
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
        sleep(5)"""

    while True:

        #assign Trigger and Echo to GPIO status
        #GPIO.setup(False)
        GPIO.setup(pins.Trigger, GPIO.OUT)
        GPIO.setup(pins.Echo, GPIO.IN)

        #Set Trigger to Low
        GPIO.output(pins.Trigger, GPIO.LOW)

        #sensor calibration
        sleep(1.4)

        #Set Trigger to High
        GPIO.output(pins.Trigger, GPIO.HIGH)
        sleep(0.00001)
        GPIO.output(pins.Trigger, GPIO.LOW)

        #condition to set start/stop time based on echo
        while GPIO.input(pins.Echo)==0:
            pulse_start = time()
        while GPIO.input(pins.Echo)==1:
            pulse_end = time()

        #calculate distance. Assume speed of sound is 17150 cm/s
        #^Bruh it's not, the speed of sound is 343 m/s. You're just taking 343, dividing it by 2, and multiplying it by 100 to get the cm sound traveled one way...chrysnosis, am I right!
        pulse_duration = pulse_end - pulse_start
        # Round to 2 decimal places
        distance = round(pulse_duration * 17150, 2)

        #Checks to see if the bot detects anything within 50 cm
        if(distance <= 50):
            if(count > 0):
                print(f"Obstacle {distance} cm away. Trying again in 5 seconds, {count} {'attempt' if count == 1 else 'attempts'} left...")
                count -= 1
                stop(5)
            else:
                print("Ight imma head out...")
                stop() #If it detects anything within 50 cm more than 5 times then it stops for good
                GPIO.cleanup()
                break

except KeyboardInterrupt:
    print("Stopping the ultrasonic sensor...")
    # Reset all the GPIO pins by setting them to LOW
    """for pin in vars(pins).values():
        GPIO.output(pin, GPIO.LOW)"""

    # Clean up pins
    GPIO.cleanup()
