REM restart Apache
START /MIN "PLINK_FOR_RESTART_APACHE" plink -i %3 -v -t -l %1 %2 sh /root/restartapache
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 15
REM Kill opened windows
TASKKILL /T /F /FI "WINDOWTITLE eq PLINK_FOR_RESTART_APACHE*"
REM Done!
exit
