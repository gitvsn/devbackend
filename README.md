#                        VSN Project  BE 


## Project dependencies
``Versions at the start of the project``
| Dep. Name | Version |
| :---         |     ---:      |  
| Java    | 11      |
| io.jsonwebtoken    | 0.8.0      |
| spring boot starter mail     | 2.0.1.RELEASE       |
| org.powermock     | 2.7.0       |
| MySQL    | 5.7 Release      | 
| net.coobird     | 0.4.11       |
| web3j     | 5.0.0       |
| de.taimos     | 1.0.0       |





[Flexy exchange © 2020](https://flexy.exchange/) 



## Build project core file
| Command | Text |
| :---         |     ---:      |  
| git clone https://github.com/gitvsnwallet/devbackend.git    | clone project      |
| cd devbackend    |  (project directory)      |
| mvn clean package -Dmaven.test.skip=true   |  (build command)      |
| cd ./target   | (ROOT.war -- main file )     |





 

## Install Tomcat server (back-end server)
| Command | Text |
| :---         |     ---:      |  
| wget https://apache.paket.ua/tomcat/tomcat-9/v9.0.39/bin/apache-tomcat-9.0.39.tar.gz.   | download server "Tomcat"  |
| tar -xvf  apache-tomcat-9.0.39.tar.gz   |  (unarchive)   |
| mv ROOT.war /apache-tomcat-9.0.39/webapps  |  move file to webapps directory   |
| cd /apache-tomcat-9.0.39/bin   |  go to run server directory    |
| sh catalina.sh start   |   run server    |


 


