#!/bin/bash
 
./droit.sh jhipsters
./stopDB.sh
j=0;

for i in `ls jhipsters`
do
timeout 20m    java -jar launchTest.jar  $j
 echo "**************************************************************************"
 echo "*                 Configutations N° $j testée avec succes                *"
 echo "**************************************************************************"
  j=$(($j + 1))
done
docker rmi uaa
cd ~


