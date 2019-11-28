from PIL import Image
import sys
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
	r1, g1, b1, *a1 = color

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

for y in range(height):
	for x in range(width):
		# Tell robot to print this color
		print(f'({x},{y}): {closestColor(pix[x,y])}')
