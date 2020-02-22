import sys
sys.path.append("motors")

import numpy as np
from PIL import Image
from math import sqrt
from webcolors import name_to_rgb, rgb_to_name
from motors.main import move_forward, turn_left, turn_right, stop
from time import sleep
import signal

# List of avaiable colors (use color name from CSS)
"""COLORS = [name_to_rgb('red'),name_to_rgb('orange'),name_to_rgb('yellow'),name_to_rgb('green'),name_to_rgb('blue'),\
	name_to_rgb('purple'),name_to_rgb('black'),name_to_rgb('brown'),name_to_rgb('white')]"""
COLORS = [name_to_rgb("black"), name_to_rgb("white")]
FSPEED = 20 # forward speed
TSPEED = 40 # turn speed

def stop_robot(signum, frame):
	# Stop the motors upon SIGINT
	print("Stopping the print job...")
	stop()
	sys.exit(0)

def closest_color(color):
	# Find the color a pixel closely matches to
	res = "???"
	min_dist = sys.maxsize
	r1, g1, b1, *a1 = color # might get rgba values, in that case extract the *a* values

	for ref_c in COLORS:
		r2, g2, b2, *a2 = ref_c
		dist = sqrt((r1-r2)**2 + (g1-g2)**2 + (b1-b2)**2)

		if (dist < min_dist):
			min_dist = dist
			res = rgb_to_name(ref_c)

	return res

def check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color):
	# Look at the next pixel (excluding last one)
	if x < width and y < height:
		color = closest_color(pix[x,y])
		rgb_arr[y][x] = list(name_to_rgb(color))
		print(f"({x},{y}): Print {color}")

		if prev_color != color:
			#print(f"Switching to {color}...")
			pass
		prev_color = color

def check_progress(progress):
	# Show progress
	if (progress == int(round(total*0.1))):
		print("10% done", flush=True)
	elif (progress == int(round(total*0.2))):
		print("20% done", flush=True)
	elif (progress == int(round(total*0.3))):
		print("30% done", flush=True)
	elif (progress == int(round(total*0.4))):
		print("40% done", flush=True)
	elif (progress == int(round(total*0.5))):
		print("50% done", flush=True)
	elif (progress == int(round(total*0.6))):
		print("60% done", flush=True)
	elif (progress == int(round(total*0.7))):
		print("70% done", flush=True)
	elif (progress == int(round(total*0.8))):
		print("80% done", flush=True)
	elif (progress == int(round(total*0.9))):
		print("90% done", flush=True)

argc = len(sys.argv)
if (argc == 1):
	raise IOError("Please supply an image file.")
elif (argc > 2):
	raise IOError("Only 1 image file please. Thank you!")

file = sys.argv[1]
image = Image.open(file) # will throw an error if image file is invalid
pix = image.load()
width, height = image.size
total = width * height
rgb_arr = np.zeros((height, width, 3), dtype="uint8") # RGB x width x height int array
x = y = 0
progress = 0
state = "right" # robot starts at top left moving right; possible states: left, right, down
print("Starting print job...", flush=True)
signal.signal(signal.SIGINT, stop_robot) # stop in case of an emergency

# Print the first pixel
prev_color = "white" # surface color
check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color) # really check current pixel this time

while x < width and y < height:
	# Check state of robot
	if (state == "left"):
		# Robot is moving left; if at the edge, need to turn left and move down
		if (x == 0):
			state = "down"
			y += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			#print("Turn left, move down")
			turn_left(TSPEED)
			sleep(1)
			move_forward(FSPEED)
			sleep(1)
		else:
			x -= 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			#print("Continue left")
			move_forward(FSPEED)
			sleep(1)
	elif (state == "right"):
		# Robot is moving right; if at the edge, need to turn right and move down
		if (x == width - 1):
			state = "down"
			y += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			#print("Turn right, move down")
			turn_right(TSPEED)
			sleep(1)
			move_forward(FSPEED)
			sleep(1)
		else:
			x += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			#print("Continue right")
			move_forward(FSPEED)
			sleep(1)
	else:
		# Robot moved down, need to turn in the right direction
		if (x == 0):
			state = "right"
			x += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			#print("Turn left, move right")
			turn_left(TSPEED)
			sleep(1)
			move_forward(FSPEED)
			sleep(1)
		else:
			state = "left"
			x -= 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			#print("Turn right, move left")
			turn_right(TSPEED)
			sleep(1)
			move_forward(FSPEED)
			sleep(1)

	progress += 1
	check_progress(progress)
	stop()
	sleep(1)

# See how the image looks with basic colors
new_img = Image.fromarray(rgb_arr) # note: parameter must be array, not list
img_name = f"new_{file.split('/')[-1]}"
new_img.save(img_name)
print(f"Done! Preview print job at {img_name}", flush=True)
