# TERM-PROJECT:  JSCOPE

We are **JSCOPE** "Creating **J**ava **S**olutions **C**oded and **O**ptimized for **P**rogress and **E**fficiency."

An online application to be developed in Java 11=>**17** for the purpose of...  >**_replace-with-description_**<

## JSCOPE Team & Roles

- Connor Bashaw, Team Coordinator
- Oliver Gomes Jr, Configuration Coordinator
- Jonathan Ho
- Patrick LaBeau

## Team Additional Information

- **Meeting times** -> https://www.when2meet.com/?32438348-t5TOZ
- **Google Drive link** -> https://drive.google.com/drive/u/1/folders/0ALWbc479021SUk9PVA
- **Trello Workspace link** -> https://trello.com/w/userworkspace76225060
- **Slack:**
  - **General channel** -> https://rit.enterprise.slack.com/archives/C09GCM0J5MF
  - **Team Contract channel** -> https://rit.enterprise.slack.com/archives/C09G3DPDZ9A
  - **Virtual Standups channel** -> https://rit.enterprise.slack.com/archives/C09FJ33L9C7

## Prerequisites

- Java **17=>21**
- VSCode IDE - as developer platform
- Maven
- Chocolatey

## Instructions
Details on how to run our project are provided by the team in the **doc/SETUP.md** and updated to each major release.

## Fund Good Deeds

### Design Choice
- Users will NOT be able to have fractional values for bundle component quantities such as (Monthly Rent, 2.5) as a team we agreed this doesn't make any sense for the system to process but a logical error on the user. If the cost is 2.5x for your "Monthly Rent", that should be reflected in the price not the quantities. If you pay for two people and a dog then the acceptable values are whole, positive integer values. Example: (Monthly Rent, 2) with the dog fee listed a fee. 

### Disclaimers
- When you do donation/fulfillment, there isn't any error checking for an existing need/bundle. Please use existing needs/bundles only.
- "If no funding goal has ever been set, the system will use a default goal of $200.00, not the documented default of $2000.00."
- The application expects the ledger file name as a command-line argument (java FundGoodDeedsApp.java <ledger-csv-file>). The requirements, however, mandate a specific file name: log.csv
- "This application requires the path to your ledger file as a startup argument. It does not automatically use log.csv. Furthermore, when saving, the application writes ledger data to ledger-new.csv, not the original file, so your changes will not be loaded next time."
- The application currently does not load any existing data from the ledger file (log.csv). All previous entries will be ignored at startup, and saving will overwrite them.

### Shortcomings
- When a new donation is entered, there is no functionality to delete needs from inventory. Although, each donation is tracked in the ledger and can be exported on exit.
- The available funds and and goal are not tracked, nor visible on the front-end CLI.
- The requirements specify a default funding goal of 2000.0. Your LedgerRepository class has a DEFAULT_GOAL constant, but it is not used in the getGoalForDate method, which instead returns a hardcoded value of 200.0 from the old findGoal method.
- The loadLog() method in LedgerRepository is almost empty. It reads the CSV data but never processes it or adds it to the logEntries list. As a result, any previously saved ledger entries are ignored when the application starts
- The saveLogEntries method in LedgerRepository writes to a new file named ledger-new.csv instead of overwriting the original ledger file. This means user changes are not persisted in the correct file.
- 
### Known Bugs 
- When entering the date for donations (dd) if dd is 1 instead of 01, the code breaks and exits the program.

## License
MIT License

See LICENSE for details.
