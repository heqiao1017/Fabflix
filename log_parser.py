import sys
if __name__ == '__main__':
	ts_total = 0
	tj_total = 0
	line_num = 0
	for line in open(sys.argv[1],'r'):
		line_num += 1
		tokens = line.split()
		ts_total += float(tokens[1])
		tj_total += float(tokens[3])
	print "TS: ", ts_total/line_num, " TJ: ",tj_total/line_num
