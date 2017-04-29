Class and method prioritization tool

Class Level
Main Theory: Create a Two-Run prioritization tool.
First Run, record statements covered by each test; then return a list of test classes sorted and arranged by Its statement coverage formed on the Total strategy (TS) and Additional strategy (AS) of the test prioritization. A java file containing a test suite which based on the TS or AS will be automatically created under the test folder of the target project.
Second Run, maven test the modified pom.xml file to manipulate the class testing order. 
Steps to achieve: Step to achieve First Run. 
First call “premain” method inside JaveAgent class, then override “visitLine” method in “MethodTransformVisitor” class. 

Method Level (Just for bonus)
Main Theory: Manipulate the order of the test method tested inside of each test class, to better improve the bug detecting speed, with a Two-Run process
First Run, return two lists of test classes and methods based on TS and AS and stored in total-cov.txt and additional-cov.txt respectively under the root directory of the target project. Then run Rename.jar under the test directory to add custom annotation for each method.
Second Run, modify pom.xml file to manipulate the class testing order as well as the method testing order under each class.
