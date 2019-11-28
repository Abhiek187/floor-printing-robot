import sys
import numpy as np
from PIL import Image
from math import sqrt
from webcolors import name_to_rgb, rgb_to_name

# List of avaiable colors (use color name from CSS)
COLORS = [name_to_rgb('red'),name_to_rgb('orange'),name_to_rgb('yellow'),\
	name_to_rgb('green'),name_to_rgb('blue'),name_to_rgb('purple'),\
	name_to_rgb('black'),name_to_rgb('brown'),name_to_rgb('white')]

def closestColor(color):
	# Find the color a pixel closely matches to
	res = '???'
	min_dist = sys.maxsize
	r1, g1, b1, *a1 = color # might get rgba values, in that case extract the *a* values

	for ref_c in COLORS:
		r2, g2, b2, *a2 = ref_c
		dist = sqrt((r1-r2)**2 + (g1-g2)**2 + (b1-b2)**2)

		if (dist < min_dist):
			min_dist = dist
			res = rgb_to_name(ref_c)

	return res

argc = len(sys.argv)
if (argc == 1):
	raise IOError('Please supply an image file.')
elif (argc > 2):
	raise IOError('Only 1 image file please. Thank you!')

file = sys.argv[1]
image = Image.open(file) # will throw an error if image file is invalid
pix = image.load()
width, height = image.size
rgb_arr = np.zeros((height, width, 3), dtype='uint8') # RGB x width x height int array
x = y = 0
state = 'right' # robot starts at top left moving right; possible state: left, right, down

while x < width and y < height:
	# Print color at current pixel
	color = closestColor(pix[x,y])
	rgb_arr[y][x] = list(name_to_rgb(color))
	print(f'({x},{y}): Print {color}')

	# Check state of robot
	if (state == 'left'):
		# Robot is moving left; if at the edge, need to turn left and move down
		if (x == 0):
			print('Turn left, move down')
			state = 'down'
			y += 1
		else:
			print('Continue left')
			x -= 1
	elif (state == 'right'):
		# Robot is moving right; if at the edge, need to turn right and move down
		if (x == width - 1):
			print('Turn right, move down')
			state = 'down'
			y += 1
		else:
			print('Continue right')
			x += 1
	else:
		# Robot moved down, need to turn in the right direction
		if (x == 0):
			print('Turn left, move right')
			state = 'right'
			x += 1
		else:
			print('Turn right, move left')
			state = 'left'
			x -= 1

# See how the image looks with basic colors
new_img = Image.fromarray(rgb_arr) # note: parameter must be array, not list
new_img.save('new_img.png')
print('Done! Preview printed image at new_img.png')
