:: remove existed .class files
rm ziqiangyeah\experiment\RST\*.class
rm mstparser\*.class
rm mstparser\io\*.class
javac -classpath ".;lib/jwnl-1.3.3.jar;lib/opennlp-maxent-3.0.3.jar;lib/opennlp-tools-1.5.3.jar;libopennlp-uima-1.5.3.jar;lib/trove.jar;lib/json-20140107.jar" mstparser\*.java
javac -classpath  ".;lib/jwnl-1.3.3.jar;lib/opennlp-maxent-3.0.3.jar;lib/opennlp-tools-1.5.3.jar;libopennlp-uima-1.5.3.jar;lib/trove.jar;lib/json-20140107.jar" mstparser\io\*.java
java -classpath ".;lib/jwnl-1.3.3.jar;lib/opennlp-maxent-3.0.3.jar;lib/opennlp-tools-1.5.3.jar;libopennlp-uima-1.5.3.jar;lib/trove.jar;lib/json-20140107.jar"  -Xmx800m mstparser.DependencyParser in:sample.txt out:sample.xml fmt:xml
