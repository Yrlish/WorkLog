==============
 REQUIREMENTS
==============
Latest version of Java 8 - https://java.com


===============
    INSTALL
===============
Place the WorkLog.jar-file somewhere convenient and run it.


===============
    UPGRADE
===============
There is a new update procedure to version 0.6 because the file structure has changed. There is no longer files
being created alongside the jarfile. The database and log files is now located in its own folder somewhere in the
userhome folder.

>> YOU NEED TO DO THESE FOLLOWING STEPS TO KEEP YOUR DATABASE <<

1. Turn off any running instances of WorkLog.
2. Open your previous WorkLog-folder, where the jar-file and DB-folder is located.
3. *COPY* the DB-folder.
4. Open this location:
    Windows: C:\Users\<username>\AppData\Roaming\WorkLog
    Linux: /home/<username>/.WorkLog
5. In the WorkLog folder you opened in step 4, rename the DB-folder if it exists to 'DB_old'.
6. *PASTE* the DB-folder from step 3 into the folder you opened in step 4.
7. You can now start WorkLog 0.6+.
