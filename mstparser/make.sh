#rm mstparser/*.class
#rm mstparser/io/*.class
#javac -classpath ".:lib/trove.jar" mstparser/DependencyParser.java
#java -classpath ".:lib/trove.jar" -Xmx15g mstparser.DependencyParser train train-file:data/mytrain18.lab model-name:dep18nprojNew1.model test test-file:data/mytest18.lab output-file:out18.txt eval gold-file:data/mytest18.lab format:MST decode-type:non-proj

#java -classpath ".:lib/trove.jar" -Xmx20g mstparser.DependencyParser train train-file:data/mytrain18.lab model-name:dep18projNew1.model test test-file:data/mytest18.lab output-file:out18.txt eval gold-file:data/mytest18.lab format:MST decode-type:proj


java -classpath ".:lib/trove.jar" -Xmx20g mstparser.DependencyParser train train-file:data/mytrain110.lab model-name:dep110nprojNew1.model test test-file:data/mytest110.lab output-file:out110.txt eval gold-file:data/mytest110.lab format:MST decode-type:non-proj

java -classpath ".:lib/trove.jar" -Xmx20g mstparser.DependencyParser train train-file:data/mytrain110.lab model-name:dep110projNew1.model test test-file:data/mytest110.lab output-file:out110.txt eval gold-file:data/mytest110.lab format:MST decode-type:proj
