JAVAC = javac
JAVA = java
SRC_DIR = src
MAIN_CLASS = Main

make:
    $(JAVAC) $(SRC_DIR)/*.java

run:
    $(JAVA) $(SRC_DIR)/$(MAIN_CLASS) $(ARGS)

clean:
    rm $(SRC_DIR)/*.class