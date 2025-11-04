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
- Chocolately

## Instructions
Details on how to run our project are provided by the team in the **doc/SETUP.md** file prior to each major release. 

## Disclaimers

*   **Integer-Only Bundle Quantities:** When creating or editing a bundle, component quantities must be whole numbers (e.g., 1, 2, 3). While the system stores these as floating-point numbers (e.g., 1.0) in `needs.csv` per file format requirements, any fractional parts entered by the user (e.g., 1.5) will be ignored. This is a design choice to ensure that partial fulfillments are handled through cost adjustments in the basic need itself, not through fractional quantities in a bundle.
*   **Case-Insensitive Naming:** Need and bundle names are treated as case-insensitive (e.g., 'Phone Plan' is the same as 'phone plan'). You cannot create two items that differ only by capitalization.
*   **Observer Alerts in Console:** When data is saved or loaded, an `[ALERT]` message is printed to the console. This is a feature of the Observer pattern implementation to confirm that data models have been updated and is not an error message.

## Limitations

*   **No Spending Visualization:** The application does not currently implement the daily spending visualization feature mentioned in the overview ("...generate visualizations showing the distribution of spending across fixed costs, variable costs, and fees."). While all the necessary data is being calculated and stored, there is no menu option to display this breakdown.
*   **No "On Track" Goal Indicator:** The overview mentions, "The program should indicate whether the user is on track to meet their goal...". While the main menu now helpfully displays `Today's Goal: $Current/$Target`, it does not explicitly state if the user is "on track," "underfunded," or has a "surplus."
*   **No Editing or Deleting:** The application's interface supports adding new needs, bundles, and ledger entries. However, there is no functionality to edit or delete existing items. To remove an item, the user must manually edit the `needs.csv` or `log.csv` files.

## Known Issues and Edge Cases

*   **Bundle Definition Order on Save:** The `saveNeedsCatalog` method writes items to `needs.csv` in the order they exist in memory. If the application were ever modified to allow reordering of needs/bundles, it could potentially save a parent bundle before a sub-bundle, violating the "no forward reference" rule for the CSV file. The current implementation is safe, but this is a point of fragility.
*   **Error Handling for Malformed CSVs:** The application's CSV parsing logic assumes the CSV files are well-formed. If a user manually edits `log.csv` or `needs.csv` and enters non-numeric text in a cost or count field (e.g., `2025,11,05,f,abc`), the application will crash on startup with a `NumberFormatException`.
*   **Performance with Very Large Files:** The application loads the entire `needs.csv` and `log.csv` into memory at startup. If these files were to grow extremely large (e.g., hundreds of thousands of entries), the application could experience slow startup times and high memory consumption.

## License
MIT License

See LICENSE for details.
