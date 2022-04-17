#! /bin/bash
for dire in ./testovi/*
do
	#echo $dire
#ls $dire
difs="$(java MinDka < $dire/t.ul | diff $dire/t.iz -)"
#java lab2 < $dire/t.ul
		
	if [ "$difs" = "" ];
	then
		echo "$dire : [OK]"
	else
		
		echo "$dire : "
		echo $difs
	fi	

done

