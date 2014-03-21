:: remove existed .class files
::rm ziqiangyeah\experiment\RST\*.class
::rm mstparser\*.class
::rm mstparser\io\*.class
::javac mstparser\*.java
::javac mstparser\io\*.java
:: extract dependency information
::javac ziqiangyeah\experiment\RST\*.java
::java ziqiangyeah.experiment.RST.Main input:.\\traindata\\
::java ziqiangyeah.experiment.RST.Main input:.\\testdata\\
rm wl\*.class
::rm wl\PDTB\*.class
javac wl\*.java
::javac wl\PDTB\*.java
:: extract features
java wl.Main train:.\\traindata\\ test:.\\testdata\\
:: generate train data file and test data file
::java wl.DataGenerator labeled:false coarse:false train:true > .\data\mytrain.ulab
::java wl.DataGenerator labeled:false coarse:false train:false > .\data\mytest.ulab
::java wl.DataGenerator labeled:true coarse:false train:true > .\data\mytrain110.lab
::java wl.DataGenerator labeled:true coarse:false train:false > .\data\mytest110.lab
::java wl.DataGenerator labeled:true coarse:true train:true > .\data\mytrain18.lab
::java wl.DataGenerator labeled:true coarse:true train:false > .\data\mytest18.lab
