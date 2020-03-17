import sys
sys.path.append("motors")

import numpy as np
from PIL import Image
from math import sqrt
from webcolors import name_to_rgb, rgb_to_name
from motors.main import move_forward, turn_left, turn_right, stop
from motors.ultrasonic import get_distance, get_dimensions
from signal import signal, SIGINT
from time import time
from threading import Thread

# List of available colors (use color name from CSS)
"""COLORS = [name_to_rgb('red'),name_to_rgb('orange'),name_to_rgb('yellow'),name_to_rgb('green'),name_to_rgb('blue'),\
	name_to_rgb('purple'),name_to_rgb('black'),name_to_rgb('brown'),name_to_rgb('white')]"""
COLORS = [name_to_rgb("black"), name_to_rgb("white")]
FSPEED = 20 # forward speed
TSPEED = 30 # turn speed

def stop_robot(signum, frame):
	# Stop the motors upon SIGINT
	print("Stopping the print job...")
	stop()
	sys.exit(0)

def closest_color(color):
	# Find the color a pixel closely matches to
	res = "???"
	min_dist = sys.maxsize
	r1, g1, b1, *a1 = color # might get RGBA values, in that case extract the *A* values

	for ref_c in COLORS:
		r2, g2, b2, *a2 = ref_c
		dist = sqrt((r1-r2)**2 + (g1-g2)**2 + (b1-b2)**2)

		if dist < min_dist:
			min_dist = dist
			res = rgb_to_name(ref_c)

	return res

def check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color):
	# Look at the next pixel (excluding last one)
	if x < width and y < height:
		color = closest_color(pix[x,y])
		rgb_arr[y][x] = list(name_to_rgb(color))
		#print(f"({x},{y}): Print {color}")

		if prev_color != color:
			#print(f"Switching to {color}...")
			pass
		prev_color = color

def check_progress(progress):
	# Show progress
	if progress == int(round(total*0.1)):
		print("10% done", flush=True)
	elif progress == int(round(total*0.2)):
		print("20% done", flush=True)
	elif progress == int(round(total*0.3)):
		print("30% done", flush=True)
	elif progress == int(round(total*0.4)):
		print("40% done", flush=True)
	elif progress == int(round(total*0.5)):
		print("50% done", flush=True)
	elif progress == int(round(total*0.6)):
		print("60% done", flush=True)
	elif progress == int(round(total*0.7)):
		print("70% done", flush=True)
	elif progress == int(round(total*0.8)):
		print("80% done", flush=True)
	elif progress == int(round(total*0.9)):
		print("90% done", flush=True)

argc = len(sys.argv)
if argc == 1:
	raise IOError("Please supply an image file.")
elif argc > 2:
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
signal(SIGINT, stop_robot) # stop in case of an emergency

# Perform calibration to determine turn speeds
"""input("Calibration is needed to turn the robot 90\u00B0. Press enter to continue...") # let the user read the message
start_time = time()
turn_right(TSPEED)
input("Turning right, press enter to continue...")
stop()
TR = time() - start_time
input(f"Turning right for {TR} seconds, press enter to continue...")

start_time = time()
turn_left(TSPEED)
input("Turning left, press enter to continue...")
stop()
TL = time() - start_time
input(f"Turning left for {TL} seconds, press enter to continue...")"""
TR, TL, w_dist, h_dist = get_dimensions(TSPEED)

# Determine how far to move each cycle (s/px)
#w_dist = 14 # cm wide of printing area
pix_width = w_dist/width # cm/px wide
real_speed = 0.8*FSPEED - 6
TFW = pix_width/real_speed # time forward width (s/px)
#h_dist = 20 # cm tall of printing area
pix_height = h_dist/height # cm/px tall
TFH = pix_height/real_speed # time forward height (s/px)

# Start checking for obstacles
ultra_thread = Thread(target=get_distance, daemon=True) # when script finishes, kill thread
ultra_thread.start()

print("Starting print job...", flush=True)
# Print the first pixel
prev_color = "white" # surface color
check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color) # really check current pixel this time

while x < width and y < height:
	# Check state of robot
	if state == "left":
		# Robot is moving left; if at the edge, need to turn left and move down
		if x == 0:
			state = "down"
			y += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			# Turn left, move down
			turn_left(TSPEED, TL)
			move_forward(FSPEED, TFH)
		else:
			x -= 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			# Continue left
			move_forward(FSPEED, TFW)
	elif state == "right":
		# Robot is moving right; if at the edge, need to turn right and move down
		if x == width - 1:
			state = "down"
			y += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			# Turn right, move down
			turn_right(TSPEED, TR)
			move_forward(FSPEED, TFH)
		else:
			x += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			# Continue right
			move_forward(FSPEED, TFW)
	else:
		# Robot moved down, need to turn in the right direction
		if x == 0:
			state = "right"
			x += 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			# Turn left, move right
			turn_left(TSPEED, TL)
			move_forward(FSPEED, TFW)
		else:
			state = "left"
			x -= 1
			check_next_pixel(x, y, width, height, pix, rgb_arr, prev_color)
			# Turn right, move left
			turn_right(TSPEED, TR)
			move_forward(FSPEED, TFW)

	progress += 1
	check_progress(progress)
	stop(0.1)

# See how the image looks with basic colors
new_img = Image.fromarray(rgb_arr) # note: parameter must be array, not list
img_name = f"new_{file.split('/')[-1]}"
new_img.save(img_name)
print(f"Done! Preview print job at {img_name}", flush=True)
