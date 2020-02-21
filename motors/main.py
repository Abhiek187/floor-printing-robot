#!/usr/bin/python

from PCA9685 import PCA9685
from time import sleep

Dir = [
    'forward',
    'backward',
]
pwm = PCA9685(0x40, debug=False)
pwm.setPWMFreq(50) # freq range: 40 - 1000

class MotorDriver():
    def __init__(self):
        self.PWMA = 0
        self.AIN1 = 1
        self.AIN2 = 2
        self.PWMB = 5
        self.BIN1 = 3
        self.BIN2 = 4

    def MotorRun(self, motor, index, speed):
        if speed > 100:
            return
        if motor == 0:
            pwm.setDutycycle(self.PWMA, speed)
            if index == Dir[0]:
                pwm.setLevel(self.AIN1, 0)
                pwm.setLevel(self.AIN2, 1)
            else:
                pwm.setLevel(self.AIN1, 1)
                pwm.setLevel(self.AIN2, 0)
        else:
            pwm.setDutycycle(self.PWMB, speed)
            if index == Dir[0]:
                pwm.setLevel(self.BIN1, 0)
                pwm.setLevel(self.BIN2, 1)
            else:
                pwm.setLevel(self.BIN1, 1)
                pwm.setLevel(self.BIN2, 0)

    def MotorStop(self, motor):
        if motor == 0:
            pwm.setDutycycle(self.PWMA, 0)
        else:
            pwm.setDutycycle(self.PWMB, 0)

"""
Notes:
Front of bot = side away from pi
Motor 1 moves the opposite of command.
Speed range: 0 - 100
"""

def move_forward(speed):
    Motor.MotorRun(0, 'forward', speed)
    Motor.MotorRun(1, 'backward', speed)

def move_backwards(speed):
    Motor.MotorRun(0, 'backward', speed)
    Motor.MotorRun(1, 'forward', speed)

def turn_left(speed):
    Motor.MotorRun(0, 'backward', speed)
    Motor.MotorRun(1, 'backward', speed)

def turn_right(speed):
    Motor.MotorRun(0, 'forward', speed)
    Motor.MotorRun(1, 'forward', speed)

def stop():
    Motor.MotorStop(0)
    Motor.MotorStop(1)

print("Testing the motors...")
Motor = MotorDriver()

try:
    while True:
        print("Moving forward...")
        move_forward(30)
        sleep(1)

        print("Moving backwards...")
        move_backwards(30)
        sleep(1)

        print("Turning right...")
        turn_right(30)
        sleep(1)

        print("Turning left...")
        turn_left(30)
        sleep(1)

except IOError as e:
    print(e)

except KeyboardInterrupt:
    print("Stopping the motors...")
    stop()

