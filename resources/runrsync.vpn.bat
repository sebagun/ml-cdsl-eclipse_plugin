REM Stop Resin
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -t -l %1 %2 sh /data1/resin/bin/httpd.sh stop
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 2
REM Start Rsync daemon
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -t -l %1 %2 rsync --daemon --port=80 --config=/root/rsyncd.conf
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 2
REM Iterate through filenames in synclist.txt and rsync them
for /F "tokens=1,2 delims=;" %%i in (%4) do rsync -avz --del %%i rsync://%1@%2:80/%%j
REM Sleep for 2 seconds to give rsync enough time to finish
START /MIN /WAIT sleep 2
REM Stop Rsync daemon
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -t -l %1 %2 pkill rsync
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 2
REM Start Resin
START /MIN "PLINK_FOR_RSYNC" plink -i %3 -v -t -l %1 %2 sh /data1/resin/bin/httpd.sh start
REM Sleep for 2 seconds to give plink enough time to finish
START /MIN /WAIT sleep 2
REM Kill opened windows
TASKKILL /T /F /FI "WINDOWTITLE eq PLINK_FOR_RSYNC*"
REM Done!
exit
