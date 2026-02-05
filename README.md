# JSCOPE: FundGoodDeeds

We are **JSCOPE** "Creating **J**ava **S**olutions **C**oded and **O**ptimized for **P**rogress and **E**fficiency."

**FundGoodDeeds** is a Java-based financial tracking application designed to enhance user resilience by monitoring daily expenses, funding sources, and budget thresholds through an intuitive MVC-structured interface. The system features a secure multi-user authentication subsystem that ensures data privacy by isolating personal financial records into encrypted, user-specific CSV directories.

## JSCOPE Team & Roles

- Connor Bashaw (Team Coordinator)
- Oliver Gomes Jr (Configuration Coordinator)
- Jonathan Ho (Requirements Coordinator)
- Patrick LaBeau (Quality Assurance Coordinator))

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

## Startup Instructions

Details on how to run our project are provided by the team in the **doc/SETUP.md** file prior to each major release. Link [here](https://github.com/RIT-SWEN-383/term-project-rit-swen-383-01-1a/blob/master/doc/SETUP.md)

## Disclaimers

## Resolved defects

* Implementation of user sign in/out/up and persistence of data across users
* Ledger log gets the funding sources data
  * No longer asks user to input cost of a funding source
  * Checks existing funding sources (only allowing exisiting funding sources when adding a funding income)

## License

MIT License

See LICENSE for details.
