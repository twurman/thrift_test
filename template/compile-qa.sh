# NOTE ABOUT CLASSPATHS:
# Classpaths contain jar files and paths to the TOP of package hierarchies.
# For example, say a program imports the class info.ephyra.OpenEphyra
# Now javac knows to look for the class info.ephyra.OpenEphyra in the directory info/ephyra/
# However, javac still needs the classpath to the package.

printdivision()
{
	echo -e "\n"
	for i in `seq 1 70`; do
		echo -n "#";
	done
	echo -e "\n"
}

# Generate thrift files
echo -e "./compile-qa.sh: `pwd`"
echo -e "./compile-qa.sh: Compiling thrift source code..."
thrift --gen java qaservice.thrift
printdivision

thrift=~/thrift-0.9.2
JAVA_CLASS_PATH=$thrift/lib/java/build/libthrift-0.9.2.jar:$thrift/lib/java/build/lib/slf4j-api-1.5.8.jar:$thrift/lib/java/build/lib/slf4j-log4j12-1.5.8.jar:$thrift/lib/java/build/lib/log4j-1.2.14.jar:httpcomponents-core/lib/httpcore-4.4.1.jar

# Use cp flag to avoid cluttering up the CLASSPATH environment variable
echo -e "javac -cp $JAVA_CLASS_PATH QADaemon.java QAServiceHandler.java gen-java/edu/umich/clarity/thrift/QAService.java\n\n"
javac -cp $JAVA_CLASS_PATH -Xlint:unchecked -Xlint:deprecation QADaemon.java QAServiceHandler.java gen-java/edu/umich/clarity/thrift/QAService.java

printdivision
echo -e "javac -cp $JAVA_CLASS_PATH QADaemonSimple.java QAServiceHandler.java gen-java/edu/umich/clarity/thrift/QAService.java\n\n"
javac -cp $JAVA_CLASS_PATH QADaemonSimple.java QAServiceHandler.java gen-java/edu/umich/clarity/thrift/QAService.java