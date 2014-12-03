ls /data2/orange/jars/lib | awk '{x = x "/data2/orange/jars/lib/" $1 ":"} END { print "/data2/orange/java/docdir/WEB-INF/classes:/data2/orange/jars/diskvobject:/data2/orange/jars/ojdbcjar/ojdbc14.jar:" x "/data1/product/9i/jdbc/lib/nls_charset12.jar:/data2/orange/jars/jarsite/ml.jar:/data2/orange/oraweb/sap.jar:/data2/orange/jars/jarsap/sapjco.jar"}'

