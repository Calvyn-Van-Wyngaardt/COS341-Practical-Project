JAVAC = javac
JAVA = java
JAR = jar
SRC_DIR = src
MAIN_CLASS = Main
JAR_FILE = COS341_Semester_Project.jar

make:
	$(JAVAC) $(SRC_DIR)/*.java

jar: make
	$(JAR) cfe $(JAR_FILE) $(MAIN_CLASS) -C $(SRC_DIR) .

run:
	$(JAVA) -cp $(SRC_DIR) $(MAIN_CLASS) $(FILE)

runjar: jar
	$(JAVA) -jar $(JAR_FILE) $(FILE)

clean:
	rm -f $(SRC_DIR)/*.class $(JAR_FILE)
	