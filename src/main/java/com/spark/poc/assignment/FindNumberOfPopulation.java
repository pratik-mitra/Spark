package com.spark.poc.assignment;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class FindNumberOfPopulation {
	
public static void main(String[] args) {
		
		Logger.getLogger("org").setLevel(Level.ERROR);
		
		SparkConf conf = new SparkConf().setAppName("findPopulation").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sc.textFile("in/AUS_state.csv");
		JavaRDD<String> fileredDataSet = lines.filter(line -> 
							!(line.contains("Rank")||line.contains("Population")||line.contains("national")));
		JavaRDD<String> newDataSet = fileredDataSet.map(line -> 
							line.split(",")[1]+","+line.split(",")[3]+","+line.split(",")[4]);
		
		JavaPairRDD<String, Long> pairDataSet = newDataSet.mapToPair(getPairedData());
		pairDataSet = pairDataSet.filter(entry -> entry._2() > 0);
		
		Map<String, Long> resultMap = pairDataSet.collectAsMap();
		
		for(Map.Entry<String, Long> entry : resultMap.entrySet()) {
				System.out.println(entry.getKey()+"::::"+entry.getValue());
		}
	}
	
	public static PairFunction<String, String, Long> getPairedData() {
		return (PairFunction<String, String, Long>) line -> 
					new Tuple2<>(line.split(",")[0],new Long(line.split(",")[1])-new Long(line.split(",")[2]));
	}
}
