REM Run job
START /MIN "PLINK_FOR_JOB" plink -i %3 -v -t -l %1 %2 . $HOME/.bash_profile; ./java.ksh %4 %5
REM Sleep for 3 seconds to give plink enough time to finish
START /MIN /WAIT sleep 3
REM Kill opened windows
TASKKILL /T /F /FI "WINDOWTITLE eq PLINK_FOR_JOB*"
REM Done!
exit
