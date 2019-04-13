===================
   REQUIREMENTS
===================
Latest version of Java 8 - https://java.com


===================
      INSTALL
===================
Place the WorkLog.jar-file somewhere convenient and run it.


================================================
    UPGRADE FROM A VERSION BELOW 0.6 TO 0.6+
================================================
There is a new update procedure to version 0.6 because the file structure has changed. There is no longer files
being created alongside the jarfile. The database and log files is now located in its own folder somewhere in the
userhome folder.

>> YOU NEED TO DO THESE FOLLOWING STEPS TO KEEP YOUR DATABASE <<

1. Turn off any running instances of WorkLog.
2. Open your previous WorkLog-folder, where the jar-file and DB-folder is located.
3. Open this location in another window:
    Windows: C:\Users\<username>\AppData\Roaming\WorkLog
    Linux: /home/<username>/.WorkLog
4. In the WorkLog folder you opened in step 4, rename the DB-folder if it exists to 'DB_old'.
   If there is no data or you do not care about the data, you can just delete the DB folder.
5. Move the DB-folder from step 2 into the folder you opened in step 3.
6. You can now start WorkLog 0.6+.
