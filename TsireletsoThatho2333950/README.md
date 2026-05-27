**ProcureGov - Tender Management System**

**Ministry of Public Works, Kingdom of Lesotho**




**How to Get Started**



**Prerequisites**

1\. Java Development Kit (JDK) 17

2\. Apache Tomcat 10.1.50

3\. MySQL Server (running on port 3306)



**Setup Steps:**



1\. **Database Setup**

Create database with this name **tsireletsothatho2333950** and import `**tsireletsothatho2333950.sql**` in MySQL(XAMPP) to insert seed data.



2\. **Deploy Application**

\- Open project in NetBeans

\- Clean and Build

\- Deploy to Tomcat 10.1.50

\- Access at: **http://localhost:8080/TsireletsoThatho2333950/**



**Default User Credentials**



**Procurement Officers**

&#x20;         **Email           | Password**

&#x20;mohapi.letsie@mpw.gov.ls | mohapi123

&#x20;mamello.khosi@mpw.gov.ls | mamello123



**Evaluation Committee Members**

&#x20;         **Email           | Password**

&#x20;thabo.mokoena@mpw.gov.ls | thabo123

&#x20;lerato.sefako@mpw.gov.ls | lerato123





**NOTICE:** Tender can only be award when all evaluators have evaluated(i.e. both evaluation committee member and officer have evaluated).



**Project Structure**



TsireletsoThatho2333950/

├── web/

│ ├── META-INF/context.xml

│ ├── WEB-INF/

│ │ ├── lib/ (JAR files)

│ │ ├── views/ (JSP files)

│ │ └── web.xml

│ ├── css/style.css

│ ├── index.jsp

│ └── error.jsp

├── src/

│ ├── model/ (JavaBeans)

│ ├── dao/ (Data Access Objects)

│ ├── controller/ (Servlets)

│ ├── service/ (Business Logic)

│ ├── util/ (Utilities)

│ └── filter/ (Filters)

└── README.md



**How to run TsireletsoThatho.war file**



**Step 1:** Stop Tomcat (if running)

Go to "**apache-tomcat-10.1.50\\bin\\shutdown.bat**" and double click **"shutdown.bat"** to stop tomcat.



**Step 2:** Copy the WAR File

Go to your project's **dist** folder: "**\\TsireletsoThatho2333950\\dist\\TsireletsoThatho2333950.war**" and copy the war file.



**Step 3:** Paste into Tomcat's webapps

Go to: "**apache-tomcat-10.1.50\\apache-tomcat-10.1.50\\webapps\\**" and paste the war file here.



**Step 4:** Start Tomcat

Go to: "**apache-tomcat-10.1.50\\bin\\startup.bat**" and double click **"startup.bat"** to start tomcat.



**Step 5:** Wait for Deployment

Tomcat will automatically:

1. Detect the WAR file
2. Extract it into a folder: **webapps\\TsireletsoThatho2333950\\**
3. Deploy the application

This might take 10-30 seconds.



**Step 6:** Access the Application

Open browser and go to: **http://localhost:8080/TsireletsoThatho2333950/**



**TROUBLESHOOTING**

**Problem	                     |                Solution**

404 Error	             |  Tomcat hasn't finished deploying yet - wait 30 seconds and refresh

Port 8080 already in use     |  Stop any other Tomcat instances first

Application doesn't start    |  Check Tomcat logs at **logs\\catalina.out**

Database connection fails    |  Make sure MySQL is running and **tsireletsothatho2333950.sql** was executed

