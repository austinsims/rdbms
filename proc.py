import	re
import	sys

infile	= open(sys.argv[1],'r')
s = infile.read()
infile.close()
s = s.replace('"','')
s = re.sub(r'[ ]{2,}','\t',s)

out = open(sys.argv[1], 'w')
lines = s.split('\n')
lines = list(filter(lambda a: a != '',lines))
i = 0
for line in lines:
    out.write('"' + line + r'\n"')
    if i == len(lines) - 1:
        out.write(',')
    else:
        out.write('+')
    out.write('\n')
    i=i+1
out.close()
