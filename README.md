DiscoruseDependencyParser
==========================


This project is based on [MSTParser](http://www.seas.upenn.edu/~strctlrn/MSTParser/MSTParser.html)

see [our ACL 2014 paper](http://acl2014.org/acl2014/P14-1/pdf/P14-1003.pdf) for more details.

This is also an [online demo](http://115.28.130.129/dep/) available.


How to run
=======================

For windows user, you can run win-demo.bat for a simple demo.

For linux/unix user, you can run linux-demo.sh for a simple demo.


Running Options
=====================

You can customize your own running parameters, commonly used parameters are as follows:

in: specify the input file which contains segmented EDUs to be parsed, one EDU per line.

out: specify which file the parsing result should be saved.

fmt: specify the format of parsing result, xml and json are two currently available formats.

For example: java -classpath ".:lib/jwnl-1.3.3.jar:lib/opennlp-maxent-3.0.3.jar:lib/opennlp-tools-1.5.3.jar:libopennlp-uima-1.5.3.jar:lib/trove.jar:lib/json-20140107.jar"  -Xmx800m mstparser.DependencyParser in:sample.txt out:sample.xml fmt:json

Library Dependency
===================

Java jdk 1.6 or 1.7 are required.


