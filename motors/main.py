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
Motor 1 moves the opposite of command and is slower than motor 0.
Speed range: 0 - 100
"""

def move_forward(speed, duration=0):
    Motor.MotorRun(0, 'forward', speed*0.85)
    Motor.MotorRun(1, 'backward', speed)
    sleep(duration)

def move_backwards(speed, duration=0):
    Motor.MotorRun(0, 'backward', speed*0.85)
    Motor.MotorRun(1, 'forward', speed)
    sleep(duration)

def turn_left(speed, duration=0):
    Motor.MotorRun(0, 'backward', speed)
    Motor.MotorRun(1, 'backward', speed)
    sleep(duration)

def turn_right(speed, duration=0):
    Motor.MotorRun(0, 'forward', speed)
    Motor.MotorRun(1, 'forward', speed)
    sleep(duration)

def stop(duration=0):
    Motor.MotorStop(0)
    Motor.MotorStop(1)
    sleep(duration)

#print("Testing the motors...")
Motor = MotorDriver()

"""try:
    while True:
        print("Moving forward...")
        move_forward(20, 1)

        print("Moving backwards...")
        move_backwards(20, 1)

        print("Turning right...")
        turn_right(40, 1)

        print("Turning left...")
        turn_left(40, 1)

except IOError as e:
    print(e)

except KeyboardInterrupt:
    print("Stopping the motors...")
    stop()"""
