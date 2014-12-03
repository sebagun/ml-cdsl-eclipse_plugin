REM restart Resin
START /MIN "PLINK_FOR_RESTART_RESIN" plink -i %3 -v -t -l %1 %2 sh /root/restartresin
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 10
REM Kill opened windows
TASKKILL /T /F /FI "WINDOWTITLE eq PLINK_FOR_RESTART_RESIN*"
REM Done!
exit
