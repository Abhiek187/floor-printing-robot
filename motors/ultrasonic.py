#!/usr/bin/env python

# Import required modules
from time import sleep, time
import RPi.GPIO as GPIO
from main import stop

# Assign global variable for count
count = 5
timeout = 1

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
        self.Trigger = 0
        self.Echo = 2

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
GPIO.setmode(GPIO.BCM) # use pin numbers 1-40 instead of GPIO N
pins = Pin()

# Set up GPIO pins
"""for pin in vars(pins).values():
    GPIO.setup(pin, GPIO.OUT)"""

# Assign Trigger and Echo to GPIO status
GPIO.setup(pins.Trigger, GPIO.OUT)
GPIO.setup(pins.Echo, GPIO.IN)

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
        # Set Trigger to Low
        GPIO.output(pins.Trigger, GPIO.LOW)

        # Sensor calibration (let it settle for a second, sleep mode...:())
        sleep(1)

        # Set Trigger to High
        GPIO.output(pins.Trigger, GPIO.HIGH)
        sleep(0.00001)
        GPIO.output(pins.Trigger, GPIO.LOW)

        # Condition to set start/stop time based on echo (timeout if taking too long)
        print("Echo = 0")
        pulse_start = time()
        max_time = pulse_start + timeout
        while GPIO.input(pins.Echo) == 0 and pulse_start < max_time:
            pulse_start = time()

        if GPIO.input(pins.Echo) == 0:
            if count == 0:
                print("Ight imma head out...")
                break
            else:
                print(f"That took too long! ({count} {'attempt' if count == 1 else 'attempts'} left)")
                count -= 1
                continue # try again

        print("Echo = 1")
        pulse_end = time()
        max_time = pulse_end + timeout
        while GPIO.input(pins.Echo) == 1 and pulse_end < max_time:
            pulse_end = time()

        if GPIO.input(pins.Echo) == 1:
            if count == 0:
                print("Ight imma head out...")
                break
            else:
                print(f"That took too long! ({count} {'attempt' if count == 1 else 'attempts'} left)")
                count -= 1
                continue

        # Calculate distance: take 34300 cm/s and divide it by 2 for the one-way trip
        pulse_duration = pulse_end - pulse_start
        # Round to 2 decimal places
        distance = round(pulse_duration * 17150, 2)

        # Checks to see if the bot detects anything within 50 cm
        if distance <= 50:
            if count > 0:
                print(f"Obstacle {distance} cm away. Trying again in 5 seconds, {count} {'attempt' if count == 1 else 'attempts'} left...")
                count -= 1
                #stop(5)
            else:
                print("Ight imma head out...")
                break
        else:
            print(f"Distance: {distance} cm")

except KeyboardInterrupt:
    print("Stopping the ultrasonic sensor...")
    # Reset all the GPIO pins by setting them to LOW
    """for pin in vars(pins).values():
        GPIO.output(pin, GPIO.LOW)"""

finally:
    # Stop the bot and clean up the pins
    print("Cleaning up the pins...")
    stop()
    GPIO.cleanup()
