# NOTE ABOUT CLASSPATHS:
# Classpaths contain jar files and paths to the TOP of package hierarchies.
# For example, say a program imports the class info.ephyra.OpenEphyra
# Now javac knows to look for the class info.ephyra.OpenEphyra in the directory info/ephyra/
# However, javac still needs the classpath to the package.

# Add my classes to class path
# (These class paths are only necessary when running the program.)
thrift=~/thrift-0.9.2
JAVA_CLASS_PATH=$thrift/lib/java/build/libthrift-0.9.2.jar:$thrift/lib/java/build/lib/slf4j-api-1.5.8.jar:$thrift/lib/java/build/lib/slf4j-log4j12-1.5.8.jar:$thrift/lib/java/build/lib/log4j-1.2.14.jar:httpcomponents-core/lib/httpcore-4.4.1.jar

export JAVA_CLASS_PATH=$JAVA_CLASS_PATH:`pwd`:`pwd`/gen-java

# Rather than forking a subshell, execute all commands
# in java-config.sh in the current shell.
# cd ../common
# 	source ./qa-runtime-config.sh
# NOTE: this script starts in ../common/question-answer
if [ "$1" == "-simple" ]; then
	echo "starting simple server"
	java -cp $JAVA_CLASS_PATH -Djava.library.path=lib/search/ -server -Xms1024m -Xmx2048m QADaemonSimple $2
fi
if [ "$1" == "-http" ]; then
	echo "starting http server"
	java -cp $JAVA_CLASS_PATH -Djava.library.path=lib/search/ -server -Xms1024m -Xmx2048m QADaemon $2
fi
if [ "$1" == "-cc" ]; then
	echo "starting command center server"
	java -cp $JAVA_CLASS_PATH -Djava.library.path=lib/search/ -server -Xms1024m -Xmx2048m CommandCenterDaemon $2
fi

# Use cp flag to avoid cluttering up the CLASSPATH environment variable

