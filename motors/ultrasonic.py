#!/usr/bin/env python

# Import required modules
from time import sleep, time
import RPi.GPIO as GPIO
from main import stop, turn_right, turn_left
from atexit import register
from sys import exit

# Assign global variable for count
count = 5
timeout = 1
obstacle_detected = False # global variable to check if the bot needs to stop

# Constants for the pins
class Pin():
    def __init__(self):
        self.Trigger = 0
        self.Echo = 2

def stop_ultrasonic_sensor():
    # Stop the bot and clean up the pins
    print("Cleaning up the pins...")
    stop()
    GPIO.cleanup()

def _get_distance():
    # Secret distance function that returns a value
    # Set Trigger to Low
    GPIO.output(pins.Trigger, GPIO.LOW)

    # Sensor calibration (let it settle for a second, sleep mode...:())
    sleep(1)

    # Set Trigger to High for 10 us
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
        return -1 # device is broken

    print("Echo = 1")
    pulse_end = time()
    max_time = pulse_end + timeout
    while GPIO.input(pins.Echo) == 1 and pulse_end < max_time:
        pulse_end = time()

    if GPIO.input(pins.Echo) == 1:
        return -1 # device is broken

    # Calculate distance: take 34300 cm/s and divide it by 2 for the one-way trip
    pulse_duration = pulse_end - pulse_start
    # Round to 2 decimal places
    return round(pulse_duration * 17150, 2)

def get_dimensions(turn_speed):
    # Start by turning right and looking for a marker
    start_time = time()
    turn_right(turn_speed)
    height = _get_distance()

    while height > 50:
        # 50 cm limit on tracking distance
        height = _get_distance()

    stop()
    right_time = time() - start_time

    if height == -1:
        raise RuntimeError("The ultrasonic sensors are not working.")

    # Then turn left and look for the other marker
    start_time = time()
    turn_left(turn_speed)
    width = _get_distance()

    while width > 50:
        width = _get_distance()

    stop()
    left_time = time() - start_time

    if width == -1:
        raise RuntimeError("The ultrasonic sensors are not working.")

    return (right_time, left_time, width, height)

def get_distance():
    global count, obstacle_detected

    while count >= 0:
        while obstacle_detected:
            pass # wait until printing can continue

        # Set Trigger to Low
        GPIO.output(pins.Trigger, GPIO.LOW)

        # Sensor calibration (let it settle for a second, sleep mode...:())
        sleep(1)

        # Set Trigger to High for 10 us
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
            obstacle_detected = True

            if count > 0:
                print(f"Obstacle {distance} cm away. Trying again in 5 seconds, {count} {'attempt' if count == 1 else 'attempts'} left...")
                count -= 1
            else:
                print("Ight imma head out...")
        else:
            print(f"Distance: {distance} cm")

def get_obstacle_detected():
    return obstacle_detected

def get_count():
    return count

register(stop_ultrasonic_sensor) # call this function every time this script is terminated
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
#get_distance() # uncomment if debugging the sensors
