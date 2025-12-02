Budget App Using MVC
	* Tabs to switch between panels to show Needs, Income, Log
	* Buttons for CRUD operations for each panel
	* Date header atop to always know what current date is selected

Four directories (app, model, view, controller) holding
the respective classes with FundGoodDeedsApp holding our main class. 

To try it out (from the root directory)
	mvn clean compile
  exec:java -Dexec.args="swing"


MORE DETAILS

The doc directory
=================
Contains design documentation in Word and PDF formats.

FUNCTIONS NOT COMPLETED

	* Panel for user login and persistent storage for each of their respective data. 
	* Pie/Bar Chart to visually represent user expense history over time. 
