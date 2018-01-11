#!/bin/bash
 
./droit.sh jhipsters



for i in `seq $1 $2`; 
do
 echo "**************************************************************************"
 echo "*                 Test de la Configutation N° $i                         *"
 echo "**************************************************************************"

timeout 60m    java -jar launchTest.jar  $i
 
#sudo rm -r jhipsters/jhipster$j 
 
j=$(($j + 1))

done
#docker rmi uaa
cd ~


