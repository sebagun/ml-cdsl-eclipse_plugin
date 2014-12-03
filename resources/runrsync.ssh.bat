REM Stop Resin
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -t -l %1 %2 sh /data1/resin/bin/httpd.sh stop
REM Setup SSH transport tunnel
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -N -L 873:localhost:1873 -l %1 %2
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 2
REM Iterate through filenames in synclist.txt and rsync them
for /F "tokens=1,2 delims=;" %%i in (%4) do rsync -avz --del %%i 127.0.0.1::%%j
REM Start Resin
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -t -l %1 %2 sh /data1/resin/bin/httpd.sh start
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 2
REM Kill opened windows
TASKKILL /T /F /FI "WINDOWTITLE eq PLINK_FOR_RSYNC*"
REM Done!
exit
