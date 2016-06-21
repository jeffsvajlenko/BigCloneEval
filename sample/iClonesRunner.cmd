@echo off
CALL C:\Users\jeffs\Desktop\Database\iclones\iclones -input %* > clones
java -jar C:\Users\jeffs\Desktop\Database\iclones\Convert.jar clones
del clones