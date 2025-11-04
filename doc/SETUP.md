# Project Run Instructions

## Prerequisite

Make sure Maven is installed on your PC and available in your system PATH. If you don't know how to set it up. Here is a youtube [video](https://www.youtube.com/watch?v=dQw4w9WgXcQ).

## Commands to Run

All commands **must be executed from the project root folder**:  
`term-project-rit-swen-383-01-1a/`

### 1. Clean and Compile the Project
Removes previous build artifacts and compiles the source code:

```bash
mvn clean compile
```
### 2️. Run the Application

```bash
mvn exec:java -Dexec.args="ledger-new.csv"
```
