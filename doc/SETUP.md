# Project Run Instructions

## Prerequisite

Make sure Maven is installed on your PC and available in your system PATH. If you don't know how to set it up... here is a youtube [video](https://www.youtube.com/watch?v=dQw4w9WgXcQ).
OR
Chocolately has a one liner to install it:

```bash 
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))`
```

## Commands to Run

All commands **must be executed from the project root folder**:  
`term-project-rit-swen-383-01-1a/`

### 1. Clean and Compile the Project
Removes previous build artifacts and compiles the source code:

```bash
mvn clean compile
```

### 2. Run the Application

```bash
mvn exec:java -Dexec.args="ledger-new.csv"
```
